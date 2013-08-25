package enums;

import com.avaje.ebean.annotation.EnumValue;

public enum ACLValue {

    @EnumValue("Y")
    YES,
    @EnumValue("N")
    NOT_SET,
    @EnumValue("B")
    BLOCKED
}
