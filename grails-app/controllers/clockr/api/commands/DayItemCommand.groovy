package clockr.api.commands

import clockr.api.DayItem
import clockr.api.User
import grails.validation.Validateable

class DayItemCommand implements Validateable {
    DayItem dayItem
    User user

    DayItem.DayItemType type
    Date day

    static constraints = {
        dayItem nullable: false
        user nullable: false
        importFrom DayItem
    }
}
