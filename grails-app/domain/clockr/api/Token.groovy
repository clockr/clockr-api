package clockr.api

class Token {

    static enum TokenType {
        USER_SET_PASSWORD
    }

    TokenType type
    String identifier

    Date validFrom = new Date()
    Date validUntil

    User user

    static constraints = {
        type nullable: false
        identifier nullable: false, blank: false, minSize: 16
        validFrom nullable: false
        validUntil nullable: true
        user nullable: true, validator: { val, obj ->
            if (!val && obj.type === TokenType.USER_SET_PASSWORD) return ['nullable']
        }
    }
}
