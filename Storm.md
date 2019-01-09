# Storm筆記

### 模擬schedule的作法
每個bolt自動產生時間間隔事件(tuple)
1. 部分關鍵程式碼

        public class SomeBolt extends BaseBasicBolt {

            ...

            // 複寫取得Component的配置
            @Override
            public Map<String, Object> getComponentConfiguration() {
                // 這是所有topology共用的config(可自訂)
                Config conf = TopologyCreator.stormConfig();

                // 每五秒自動觸發一個tuple(這是關鍵)
                conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 5);

                return conf;
            }

            // 計時器所產生的tuple一樣會進入execute函式中
            @Override
            public void execute(Tuple input, BasicOutputCollector collector) {
                // 計時器產生的input
                if (input.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
                        && input.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID)) {
                    String dateStr = new Date().toString();
                if(logger.isInfoEnabled()) {
                    logger.info("tick tuple, current time is " + dateStr);
                }

                return;
            }

            ...

2. 解說
    1. `conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 5);`
    這行當然就是替這個component添加一個tick tuple的關鍵
    可以想像每一個bolt都是一個component，而後面配置的變數即為間格秒數

    2. `if(input.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID) && input.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID))`
    由這兩個判斷可以精準的篩選出觸發execute的是tick tuple
    當然也可以由這個事件中去emit資訊
