package clockr.api.commands

import clockr.api.ManualEntry
import clockr.api.User
import grails.validation.Validateable

class ManualEntryCommand implements Validateable {
    ManualEntry manualEntry
    User user

    Date date
    ManualEntry.ManualEntryType type
    Float amount
    String note

    static constraints = {
        manualEntry nullable: false
        user nullable: false
        importFrom ManualEntry
    }
}
