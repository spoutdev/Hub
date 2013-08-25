package enums.resource;

import com.avaje.ebean.annotation.EnumValue;

public enum ResourceStatus {
	@EnumValue ("WR")
	WAITING_REVIEW,
	@EnumValue ("R")
	REVIEWING,
	@EnumValue ("D")
	DENIED,
	@EnumValue ("WM")
	WAITING_FOR_MODIFICATION,
	@EnumValue ("A")
	ACTIVE
}
