package enums.resource;

import com.avaje.ebean.annotation.EnumValue;

public enum ResourceDownloadStatus {
	@EnumValue ("WR")
	WAITING_REVIEW,
	@EnumValue ("D")
	DENIED,
	@EnumValue ("A")
	ACTIVE
}
