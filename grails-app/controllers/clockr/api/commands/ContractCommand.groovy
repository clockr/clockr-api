package clockr.api.commands

import clockr.api.Contract
import clockr.api.User
import grails.validation.Validateable

class ContractCommand implements Validateable {

    Contract contract
    User user

    Date startAt
    Date endAt
    Float hoursPerWeek
    String workingDays
    Integer vacationDaysPerYear

    static constraints = {
        contract nullable: false
        user nullable: false
        importFrom Contract
    }
}
