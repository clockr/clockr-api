package clockr.api

class Contract {

    Date startAt
    Date endAt

    Float hoursPerWeek = 40
    String workingDays = "1111100"
    Integer vacationDaysPerYear = 30

    static belongsTo = [user: User]

    static constraints = {
        endAt nullable: true
    }
}
