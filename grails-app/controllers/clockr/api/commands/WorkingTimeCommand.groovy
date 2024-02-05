package clockr.api.commands

import clockr.api.WorkingTime
import clockr.api.User
import grails.validation.Validateable

class WorkingTimeCommand implements Validateable {
    WorkingTime workingTime
    User user

    Date startAt
    Date endAt

    Float breakTime

    String note

    static constraints = {
        workingTime nullable: false
        user nullable: false
        importFrom WorkingTime
    }
}
