package telran.logs.bugs.mongo.doc;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import telran.logs.bugs.dto.*;

@Document(collection="logs")

public class LogDoc {
	@Id
	ObjectId id;
	public ObjectId getId() {
		return id;
	}
	
	private Date dateTime;
	private LogType logType;
	private String artifact;
	private int responseTime;
	private String result;
	
	public LogDoc(LogDto logDto) {
		dateTime = logDto.dateTime;
		logType = logDto.logType;
		artifact = logDto.artifact;
		responseTime = logDto.responseTime;
		result = logDto.result;
	}
	public LogDto getLogDto () {
		LogDto res = new LogDto(dateTime, logType, artifact, responseTime, result);
		return res;
	}
	public LogDoc(Date dateTime, LogType logType, String artifact, int responseTime, String result) {
		super();
		this.dateTime = dateTime;
		this.logType = logType;
		this.artifact = artifact;
		this.responseTime = responseTime;
		this.result = result;
	}
	public LogDoc() {
	}
	
	

}
