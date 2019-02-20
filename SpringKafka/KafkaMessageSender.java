package o2o.app.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import o2o.app.utility.Utility;

@Service
public class KafkaMessageSender {

	@Autowired
	private KafkaConfig kafkaConfig;
	
	@Autowired
	private ReplyingKafkaTemplate<String, String, String> kafkaTemplate;

	/**
	 * 傳遞訊息給目標groupid後等候回應
	 */
	public String sendMessageAndGetResponse(String topic, Object message) throws Exception {
		return sendMessageAndGetResponse(topic, Utility.toJsonStr(message));
	}
	
	/**
	 * 傳遞訊息給目標groupid後等候回應
	 */
	public String sendMessageAndGetResponse(String topic, String message) throws Exception {
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, message);
		record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, kafkaConfig.getReplyTopic().getBytes()));
		RequestReplyFuture<String, String, String> sendAndReceive = kafkaTemplate.sendAndReceive(record);
		
//		SendResult<String, String> sendResult = sendAndReceive.getSendFuture().get();
//		sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));
		ConsumerRecord<String, String> consumerRecord = sendAndReceive.get();
		return consumerRecord.value();
	}
	
	/**
	 * 傳送訊息到topic
	 */
	public void sendMessage(String topic, Object message) throws Exception {
		sendMessage(topic, Utility.toJsonStr(message));
	}
	
	/**
	 * 傳送訊息到topic
	 */
	public void sendMessage(String topic, String message) throws Exception {
		kafkaTemplate.send(topic, message);
	}
	
}
