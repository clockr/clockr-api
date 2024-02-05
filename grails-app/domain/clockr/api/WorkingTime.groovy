package clockr.api

class WorkingTime {

    Date startAt
    Date endAt

    Float breakTime = 0.0

    String note

    static belongsTo = [user: User]

    static constraints = {
        endAt nullable: true
        note nullable: true
    }
}
