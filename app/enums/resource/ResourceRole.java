package enums.resource;

import com.avaje.ebean.annotation.EnumValue;

public enum ResourceRole {
	@EnumValue ("A")
	AUTHOR,
	@EnumValue ("C")
	CONTRIBUTOR,
	@EnumValue ("D")
	DOCUMENTER,
	@EnumValue ("M")
	MAINTAINER,
	@EnumValue ("FA")
	FORMER_AUTHOR,
	@EnumValue ("T")
	TESTER,
	@EnumValue ("TM")
	TICKET_MANAGER,
	@EnumValue ("TR")
	TRANSLATOR
}
