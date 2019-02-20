package o2o.app.kafka;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

/**
 * 監聽kafka訊息的控制器(center)
 */
@Service
public class KafkaListenController {

	private static final String webCode = "${kafka.topic.listen}";
	@Value(webCode)
	private String webCodeValue;

	@KafkaListener(topics = webCode + KafkaTopics.C_CENTER_CHECK)
	@SendTo
	public String centerCheck(String msgStr) throws Exception {
		return "this is center at " + webCodeValue + ", currentTime is " + new Date().toString();
	}

}
