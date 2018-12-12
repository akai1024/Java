package o2o.storm;

import java.util.HashMap;

import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;

public class ApacheStructParser {

	public static HashMap<String, Object> parseStruct(Struct struct){
		HashMap<String, Object> structMap = new HashMap<>();
		Schema schema = struct.schema();
		for(Field field : schema.fields()) {
			String fieldName = field.name();
			Object object = struct.get(field);
			if(field.schema().type().equals(Schema.Type.STRUCT)) {
				Struct subStruct = struct.getStruct(fieldName);
				if(subStruct != null) {
					object = parseStruct(subStruct);	
				}
			}
			structMap.put(fieldName, object);
		}
		return structMap;
	}
	
}
