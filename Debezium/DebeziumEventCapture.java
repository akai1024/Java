package o2o.scheduler.debezium;

import java.util.HashMap;

/**
 * Mysql對應Debezium的事件產出格式
 * 
 * @author kai
 *
 */
public class DebeziumEventCapture {

	private static final String OP_INSERT = "c";
	private static final String OP_UPDATE = "u";
	private static final String OP_DELETE = "d";

	/**
	 * 操作類型 c:insert,u:update,d:delete
	 */
	private String op;

	/**
	 * 操作時間
	 */
	private long ts_ms;

	/**
	 * 異動之前的資料
	 */
	private HashMap<String, Object> after;

	/**
	 * 異動之後的資料
	 */
	private HashMap<String, Object> before;

	/**
	 * 資料表的相關資料
	 */
	private DebeziumSource source;
	
	/**
	 * 是否是insert
	 */
	public boolean isInsert() {
		return op != null && op.equals(OP_INSERT);
	}

	/**
	 * 是否是update
	 */
	public boolean isUpdate() {
		return op != null && op.equals(OP_UPDATE);
	}

	/**
	 * 是否是delete
	 */
	public boolean isDelete() {
		return op != null && op.equals(OP_DELETE);
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public long getTs_ms() {
		return ts_ms;
	}

	public void setTs_ms(long ts_ms) {
		this.ts_ms = ts_ms;
	}

	public HashMap<String, Object> getAfter() {
		return after;
	}

	public void setAfter(HashMap<String, Object> after) {
		this.after = after;
	}

	public HashMap<String, Object> getBefore() {
		return before;
	}

	public void setBefore(HashMap<String, Object> before) {
		this.before = before;
	}

	public DebeziumSource getSource() {
		return source;
	}

	public void setSource(DebeziumSource source) {
		this.source = source;
	}

}
