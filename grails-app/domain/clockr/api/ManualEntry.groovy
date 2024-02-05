package clockr.api

class ManualEntry {

    static enum ManualEntryType {
        WORKING_TIME,
        VACATION
    }

    Date date
    ManualEntryType type
    Float amount
    String note

    static belongsTo = [user: User]

    static constraints = {
        note nullable: true
    }
}
