package o2o.storm.spout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

/**
 * 跟蹤檔案噴嘴，噴出檔案結尾新增的行
 * 
 * @author kai
 *
 */
@SuppressWarnings("serial")
public abstract class TailFileSpout extends BaseRichSpout {

	private static Logger logger = Logger.getLogger(TailFileSpout.class);

	private String tailFile;

	private String fieldName;

	private SpoutOutputCollector collector;

	private FileInputStream fileInput;

	private InputStreamReader inputReader;

	private BufferedReader bufferReader;

	public TailFileSpout(String tailFile, String fieldName) {
		Validate.notNull(tailFile);
		this.tailFile = tailFile;

		Validate.notNull(fieldName);
		this.fieldName = fieldName;
	}

	@Override
	public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;

		try {

			fileInput = new FileInputStream(tailFile);
			inputReader = new InputStreamReader(fileInput, "UTF-8");
			bufferReader = new BufferedReader(inputReader);

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("open fail", e);
			}
		}

	}

	@Override
	public void nextTuple() {
		String line = "";

		if (bufferReader != null) {
			try {
				while ((line = bufferReader.readLine()) != null) {
					if (!line.isEmpty()) {
						collector.emit(new Values(line));
					}
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("nextTuple fail", e);
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("open file buffer reader fail");
			}
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(fieldName));
	}

}
