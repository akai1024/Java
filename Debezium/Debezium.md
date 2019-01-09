# Debezium的使用筆記
[Debezium官網](https://debezium.io/)  
這幾個範例class主要是使用springboot來執行  
若要使用java -jar直接執行進程的話，需要加入一個main函式入口  

1. 依賴，在debezium-embedded的地方exclusion是為了怕跟spring的logger包衝突

        <dependency>
        <groupId>io.debezium</groupId>
        <artifactId>debezium-embedded</artifactId>
        <version>0.8.3.Final</version>
        <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
        </exclusions>
        </dependency>

        <dependency>
        <groupId>io.debezium</groupId>
        <artifactId>debezium-connector-mysql</artifactId>
        <version>0.8.3.Final</version>
        </dependency>

        <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-clients</artifactId>
        <version>2.0.0</version><!--$NO-MVN-MAN-VER$ -->
        </dependency>

        <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        </dependency>

2. 這邊的範例是捕捉所有的db異動，將它傳進kafka的一個topic  
(而我們原本預計的流運算也將使用kafkaSpout，因此這樣可以完美銜接)

3. 不用擔心debezium啟動時無法捕捉到已存在的資料  
它的初始化流程是先執行snapshot以相同的row事件捕捉  
若要過濾已存在的資訊，可以在 `DebeziumSource` 中利用 `.isSnapshot()` 來判斷  

4. 所捕捉到的事件都會以 `org.apache.kafka.connect.data.Struct` 建立  
因此我邊利用下方函式將所有層級的成員轉成key-value的形式:

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
