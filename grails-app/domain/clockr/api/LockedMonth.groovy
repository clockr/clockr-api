package clockr.api

class LockedMonth {

    Integer year
    Integer month

    static belongsTo = [user: User]

    static constraints = {
    }
}
