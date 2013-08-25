package enums.resource;

import com.avaje.ebean.annotation.EnumValue;

public enum ResourceTicketType {
	@EnumValue ("D")
	DEFECT,
	@EnumValue ("E")
	ENHANCEMENT,
	@EnumValue ("T")
	TASK,
	@EnumValue ("P")
	PATCH,
	@EnumValue ("O")
	OTHER
}
