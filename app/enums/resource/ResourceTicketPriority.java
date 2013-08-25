package enums.resource;

import com.avaje.ebean.annotation.EnumValue;

public enum ResourceTicketPriority {
	@EnumValue ("C")
	CRITICAL,
	@EnumValue ("H")
	HIGH,
	@EnumValue ("M")
	MEDIUM,
	@EnumValue ("L")
	LOW
}
