package clockr.api.commands

import clockr.api.Token
import grails.databinding.BindUsing
import grails.validation.Validateable

class UserSetPasswordCommand implements Validateable {

    @BindUsing({ obj, source -> {
        if ((source['token'] as String)?.size() > 0) Token.findByIdentifier(source['token'] as String)
    }})
    Token token

    String password

    static constraints = {
        token nullable: false, validator: { val ->
            if (val) {
                Date now = new Date()
                if (val.type != Token.TokenType.USER_SET_PASSWORD) return ['actionNotAllowed']
                if (val.validUntil && val.validUntil <= now) return ['validUntil']
                if (val.validFrom > now) return ['validFrom']
            }
        }
        password nullable: false, blank: false, minSize: 8
    }
}
