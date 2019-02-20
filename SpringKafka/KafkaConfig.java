package o2o.app.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
@EnableKafka
public class KafkaConfig {

	@Value("${kafka.host}")
	private String host;

	@Value("${kafka.group.id}")
	private String groupId;

	@Value("${kafka.reply.timeout}")
	private int replyTimeout;

	@Value("${kafka.producer.acks}")
	private String producerAcks;

	@Value("${kafka.producer.serializer.key}")
	private String producerKeySerializer;

	@Value("${kafka.producer.serializer.value}")
	private String producerValueSerializer;

	@Value("${kafka.consumer.auto.commit}")
	private boolean consumerAutoCommit;

	@Value("${kafka.consumer.deserializer.key}")
	private String consumerKeyDeserializer;

	@Value("${kafka.consumer.deserializer.value}")
	private String consumerValueDeserializer;
	
	@Value("${kafka.topic.reply}")
	private String replyTopic;

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
//		props.put(ProducerConfig.RETRIES_CONFIG, 0);
//		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
//		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
//		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.ACKS_CONFIG, producerAcks); // all
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerKeySerializer);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerValueSerializer);
		return props;
	}

	@Bean
	public ReplyingKafkaTemplate<String, String, String> replyKafkaTemplate(ProducerFactory<String, String> pf,
			KafkaMessageListenerContainer<String, String> container) {
		ReplyingKafkaTemplate<String, String, String> replyingTemplate = new ReplyingKafkaTemplate<>(pf, container);
		replyingTemplate.setReplyTimeout(replyTimeout);
		return replyingTemplate;
	}

	@Bean
	public KafkaMessageListenerContainer<String, String> replyContainer(ConsumerFactory<String, String> cf) {
		ContainerProperties containerProperties = new ContainerProperties(replyTopic);
		return new KafkaMessageListenerContainer<>(cf, containerProperties);
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		return new KafkaTemplate<String, String>(producerFactory());
	}

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//		config.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000);
//		config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 1000);
//		config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 50);
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerAutoCommit);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerKeyDeserializer);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerValueDeserializer);
		return new DefaultKafkaConsumerFactory<>(config);
	}

	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setReplyTemplate(kafkaTemplate());
		return factory;
	}

	public String getReplyTopic() {
		return replyTopic;
	}
	
}
