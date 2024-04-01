package clockr.api.commands

import clockr.api.User
import grails.validation.Validateable

class UserCommand implements Validateable {

    User user

    String username
    String firstname
    String lastname
    Boolean enabled
    Boolean isAdmin

    User.UserGermanState germanState

    static constraints = {
        user nullable: false
        username nullable: false, email: true, validator: { val, obj ->
            if (val) {
                User otherUser = User.findByUsername(val)
                if (otherUser && otherUser.id != obj.user?.id) return ['unique']
            }
        }
        isAdmin nullable: true
        importFrom User
    }
}
