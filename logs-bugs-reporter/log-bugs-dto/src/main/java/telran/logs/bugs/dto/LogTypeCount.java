package telran.logs.bugs.dto;

public class LogTypeCount {
public LogTypeCount(LogType logType, long count) {
		super();
		this.logType = logType;
		this.count = count;
	}
public static final String LOG_TYPE = "logType";
public LogType logType;
public long count;
public LogTypeCount() {
	
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (int) (count ^ (count >>> 32));
	result = prime * result + ((logType == null) ? 0 : logType.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	LogTypeCount other = (LogTypeCount) obj;
	if (count != other.count)
		return false;
	if (logType != other.logType)
		return false;
	return true;
}
}
