package telran.logs.bugs.dto;

import java.util.Objects;

public class SeriousnessBugCount {
	Seriousness seriousness;
	long count;
	
public Seriousness getSeriousness() {
	return seriousness;
}
public long getCount() {
	return count;
}
public SeriousnessBugCount(Seriousness seriousness, long count) {
	this.seriousness = seriousness;
	this.count = count;
}
@Override
public int hashCode() {
	return Objects.hash(count, seriousness);
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	SeriousnessBugCount other = (SeriousnessBugCount) obj;
	boolean res =  count == other.count && seriousness == other.seriousness;
	return res;
}

}
