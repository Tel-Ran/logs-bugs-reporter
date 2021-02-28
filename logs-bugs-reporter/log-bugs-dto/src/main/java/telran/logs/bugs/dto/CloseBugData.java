package telran.logs.bugs.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;

public class CloseBugData {
	public CloseBugData(@Min(1) long bugId, LocalDate dateClose, String description) {
		super();
		this.bugId = bugId;
		this.dateClose = dateClose;
		this.description = description;
	}
	@Min(1)
public long bugId;
public LocalDate dateClose;
public String description;
}
