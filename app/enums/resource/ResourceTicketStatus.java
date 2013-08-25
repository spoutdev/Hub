package enums.resource;

import com.avaje.ebean.annotation.EnumValue;

public enum ResourceTicketStatus {
	@EnumValue ("N")
	NEW,
	@EnumValue ("A")
	ACCEPTED,
	@EnumValue ("W")
	WAITING,
	@EnumValue ("R")
	REPLIED,
	@EnumValue ("S")
	STARTED,
	@EnumValue ("F")
	FIXED,
	@EnumValue ("V")
	VERIFIED,
	@EnumValue ("I")
	INVALID,
	@EnumValue ("D")
	DUPLICATE,
	@EnumValue ("DE")
	DECLINED
}
