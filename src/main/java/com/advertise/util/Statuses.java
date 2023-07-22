package com.advertise.util;
public enum Statuses {
    IN_PROGRESS("In Progress"),
    PAUSE("Pause"),
    DONE("Done"),
    FINISHED("Finished"),
    COMPLETED("Completed"),
    REMOVED("Removed"),
    ;
    private final String value;

    Statuses(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Statuses findByValue(String value) {
        Statuses result = null;
        for (Statuses day : values()) {
            if (day.getValue().equalsIgnoreCase(value)) {
                result = day;
                break;
            }
        }
        return result;
    }
}
