# ReplyingKafkaTemplate 的運用
事前當然需要將kafka整個環境建好

1. replyTopic是在送出的時候由發送方決定回傳的Topic，這個Topic必須在初始化時在KafkaConfig類中KafkaMessageListenerContainer的地方加入

2. 雖然在收聽方可以指定回傳的topic，但是在發送方便無從得知回傳的topic，因此回傳的地方直接用@SendTo而不用提供topic