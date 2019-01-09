package o2o.scheduler.debezium;

public class DebeziumSource {

	private long ts_sec;

	private String query;

	private int thread;

	private long server_id;

	private String version;

	private String file;

	private long pos;

	private String name;

	private String gtid;

	private int row;

	private boolean snapshot;

	private String db;

	private String table;

	public long getTs_sec() {
		return ts_sec;
	}

	public void setTs_sec(long ts_sec) {
		this.ts_sec = ts_sec;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getThread() {
		return thread;
	}

	public void setThread(int thread) {
		this.thread = thread;
	}

	public long getServer_id() {
		return server_id;
	}

	public void setServer_id(long server_id) {
		this.server_id = server_id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public long getPos() {
		return pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGtid() {
		return gtid;
	}

	public void setGtid(String gtid) {
		this.gtid = gtid;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public boolean isSnapshot() {
		return snapshot;
	}

	public void setSnapshot(boolean snapshot) {
		this.snapshot = snapshot;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

}
