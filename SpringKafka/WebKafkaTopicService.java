package o2o.app.kafka;

import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import o2o.app.model.data.web.WebSite;
import o2o.app.model.data.web.WebSiteRepository;
import o2o.app.utility.Utility;

@Service
public class WebKafkaTopicService {

//	@Autowired
//	private WebKafkaTopicRepository repository;

	@Autowired
	private WebSiteRepository repository;

	/**
	 * Kafka使用的Topic對照(key: web_id, value: topic)
	 */
	private HashMap<String, String> webKafkaTopics = new HashMap<>();

	@PostConstruct
	public void loadSetting() {
		webKafkaTopics.clear();

		List<WebSite> list = repository.findAll();
		list.forEach(webSite -> {
			String webID = webSite.getWeb_id();
			String topic = webSite.getWeb_code();
			if (Utility.isEffectiveString(topic) && Utility.isEffectiveString(topic)) {
				webKafkaTopics.put(webID, topic);
			}
		});
	}

	public HashMap<String, String> getWebKafkaTopics() {
		return webKafkaTopics;
	}

	public String getKafkaTopic(String webID, String functionTopic) {
		if (Utility.isEffectiveString(webID)) {
			String topic = webKafkaTopics.get(webID);
			if (Utility.isEffectiveString(topic)) {
				return topic + functionTopic;
			}
		}
		return null;
	}

}
