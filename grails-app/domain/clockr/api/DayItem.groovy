package clockr.api

class DayItem {

    static enum DayItemType {
        VACATION,
        ILLNESS,
        BREAKFAST,
        LUNCH
    }

    DayItemType type
    Date day

    static belongsTo = [user: User]

    static constraints = {
    }
}
