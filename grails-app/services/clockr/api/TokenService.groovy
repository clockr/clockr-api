package clockr.api

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class TokenService {

    Token createToken(Token.TokenType type, User user = null, Date validUntil = null, Boolean invalidateSameType = true) {
        if (!validUntil) {
            use(TimeCategory) {
                validUntil = new Date() + 1.week
            }
        }
        Token token = new Token(type: type, identifier: generateTokenIdentifier(), validUntil: validUntil, user: user)
        token.save()

        if (invalidateSameType) {
            switch (type) {
                case Token.TokenType.USER_SET_PASSWORD:
                    Token.findAllByTypeAndUserAndIdNotEqual(Token.TokenType.USER_SET_PASSWORD, user, token?.id)?.each {
                        it.validUntil = new Date()
                        it.save()
                    }
                    break
            }
        }

        return !token.hasErrors() ? token : null
    }

    Token invalidateToken(Long tokenId) {
        Token token = Token.get(tokenId)
        token.validUntil = new Date()
        token.save()
        return token
    }

    String generateTokenIdentifier() {
        return UUID.randomUUID()
    }
}
