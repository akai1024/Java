package o2o.storm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 流式讀取
 * @author kai
 *
 */
public class MysqlStreaming {

	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://192.168.56.11:3306/o2o_log?useSSL=false";
		String username = "o2ojava";
		String password = "o2oJAVA!^*";
		Connection conn = DriverManager.getConnection(url, username, password);
		Statement statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		statement.setFetchSize(Integer.MIN_VALUE);
		// MySQL connector 的 StatementImpl 的 createStreamingResultSet 方法中明確指出：
		// We only stream result sets when they are forward-only, read-only, and the
		// fetch size has been set to Integer.MIN_VALUE
		// 必須做到這三個屬性配置才能做到流式讀取
		int batchSize = 10;
		
		// 當前讀取到的位置，如果資料表有刪除row就完了XD
		int curIndex = 0;
		while (true) {
			System.out.println("curIndex:"+curIndex);
			curIndex = read(statement, batchSize, curIndex);
			Thread.sleep(50);// 控制間隔-----
		}
	}

	private static int read(Statement statement, int batchSize, int startIndex) throws Exception {
		String query = "select * from kafka_log limit " + startIndex + "," + batchSize;
		ResultSet rs = statement.executeQuery(query);
		for (int rowIndex = 0; rowIndex < batchSize; rowIndex++) {
			if (!rs.next()) {
				return startIndex;
			}
			startIndex++;
			
			String topic = rs.getString("kTopic");
			String key = rs.getString("kKey");
			String msg = rs.getString("kMessage");
			
			System.out.println(topic + "/" + key + "/" + msg);
			Thread.sleep(1000);// 控制間隔-----
		}
		System.out.println("one batch");
		return startIndex;
	}

}
