package enums.forum;

import com.avaje.ebean.annotation.EnumValue;

public enum ForumType {

    @EnumValue("C")
    CATEGORY,

    @EnumValue("F")
    FORUM,

    @EnumValue("L")
    LINK
}
