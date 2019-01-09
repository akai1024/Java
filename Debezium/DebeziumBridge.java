package o2o.scheduler.debezium;

import java.util.HashMap;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import o2o.scheduler.utility.Utility;

/**
 * 利用Debezium搭建的mysql與kafka橋梁
 * 
 * @author kai
 *
 */
@Service
public class DebeziumBridge {

	private static Logger logger = LoggerFactory.getLogger(DebeziumBridge.class);
	
	private static final String OFFSET_FILE_PATTERN = "debezium/%s-%d/offset.dat";
	private static final String HISTORY_FILE_PATTERN = "debezium/%s-%d/dbhistory.dat";

	@Value("${debezium.offsetFlushInterval}")
	private int offsetFlushInterval;

	@Value("${debezium.connectorName}")
	private String connectorName;

	@Value("${debezium.mysqlHost}")
	private String mysqlHost;

	@Value("${debezium.mysqlPort}")
	private int mysqlPort;

	@Value("${debezium.mysqlUser}")
	private String mysqlUser;

	@Value("${debezium.mysqlPassword}")
	private String mysqlPassword;

	@Value("${debezium.mysqlServerId}")
	private long mysqlServerId;

	@Value("${debezium.mysqlServerName}")
	private String mysqlServerName;

	@Value("${debezium.kafkaTopic}")
	private String kafkaTopic;

	@Value("${debezium.kafkaBrokers}")
	private String kafkaBrokers;

	@Value("${debezium.console.log}")
	private boolean isConsoleLog = false;
	
	/** DebeziumEngine */
	private EmbeddedEngine engine;

	/** KafkaProducer */
	private KafkaProducer<String, String> producer;

	@PostConstruct
	public void init() {
		
		// 建立kafkaProducer
		producer = new KafkaProducer<String, String>(getKafkaConfig());

		String offsetFile = String.format(OFFSET_FILE_PATTERN, mysqlHost, mysqlServerId);
		String dbHistoryFile = String.format(HISTORY_FILE_PATTERN, mysqlHost, mysqlServerId);
		
		Configuration config = Configuration.create()
				// 引擎配置
				.with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
				.with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
				.with("offset.storage.file.filename", offsetFile)
				.with("offset.flush.interval.ms", offsetFlushInterval)
				// connector配置
				.with("name", connectorName)
				.with("server.id", mysqlServerId)
				.with("database.hostname", mysqlHost)
				.with("database.port", mysqlPort)
				.with("database.user", mysqlUser)
				.with("database.password", mysqlPassword)
				.with("database.server.name", mysqlServerName)
				.with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
				.with("database.history.file.filename", dbHistoryFile)
				
				// 修正時間亂碼
				.with("database.serverTimezone", "UTC")

				// 不攔截schema異動事件
				.with("include.schema.changes", false)
				// 擷取binlog的時間間隔
				.with("poll.interval.ms", 10)
				.build();

		// 建立一個debezium嵌入引擎
		engine = EmbeddedEngine.create()
				.using(config)
				.notifying(this::checkSourceRecord)
				.build();

		// 異步執行
		Thread thread = new Thread(engine);
		thread.start();

	}

	@PreDestroy
	public void destory() {
		engine.stop();
	}
	
	/**
	 * 檢查每一個事件紀錄
	 */
	private void checkSourceRecord(SourceRecord record) {
		// 跳過value無值的事件
		if (record.value() == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("skip event:" + record.toString());
			}
			return;
		}

		// 只擷取debezium內部的kafkaValue
		Struct value = (Struct) record.value();

		try {
			
			// 把事件Struct轉成鍵值結構
			HashMap<String, Object> valueMap = parseStruct(value);
			// 再轉成jsonString
			String jsonValue = Utility.toJsonStr(valueMap);

			// 在console紀錄歷程
			if(isConsoleLog && logger.isInfoEnabled()) {
				logger.info(jsonValue);
			}
			
			// 檢查每個事件
			DebeziumEventCapture event = Utility.parseJson(jsonValue, DebeziumEventCapture.class);
			
			// 只要是新增的就送到KafkaTopic
//			if (event.isInsert()) { // 先全部都丟給kafkaTopic
				producer.send(new ProducerRecord<String, String>(kafkaTopic, jsonValue));
//			}

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("checkSourceRecord exception", e);
			}
		}

	}

	/**
	 * 將Struct轉成鍵值的結構
	 */
	private HashMap<String, Object> parseStruct(Struct struct) {
		HashMap<String, Object> structMap = new HashMap<>();
		Schema schema = struct.schema();
		for (Field field : schema.fields()) {
			String fieldName = field.name();
			Object object = struct.get(field);
			if (field.schema().type().equals(Schema.Type.STRUCT)) {
				Struct subStruct = struct.getStruct(fieldName);
				if (subStruct != null) {
					object = parseStruct(subStruct);
				}
			}
			structMap.put(fieldName, object);
		}
		return structMap;
	}

	/**
	 * 提交給kafkaTopic的配置
	 */
	private Properties getKafkaConfig() {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaBrokers);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return props;
	}
	
}
