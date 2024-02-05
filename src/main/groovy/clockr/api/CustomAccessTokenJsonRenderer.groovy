package clockr.api

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.rendering.AccessTokenJsonRenderer
import groovy.util.logging.Slf4j
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.Assert

@Slf4j
@Transactional
class CustomAccessTokenJsonRenderer implements AccessTokenJsonRenderer {
    String usernamePropertyName
    String tokenPropertyName
    String authoritiesPropertyName

    Boolean useBearerToken

    @Override
    String generateJson(AccessToken accessToken) {
        Assert.isInstanceOf(UserDetails, accessToken.principal, "A UserDetails implementation is required")
        UserDetails userDetails = accessToken.principal as UserDetails

        def result = [
                (usernamePropertyName)   : userDetails.username,
                (authoritiesPropertyName): accessToken.authorities.collect { GrantedAuthority role -> role.authority },
        ]

        if (useBearerToken) {
            result.token_type = 'Bearer'
            result.access_token = accessToken.accessToken

            if (accessToken.expiration) {
                result.expires_in = accessToken.expiration
            }

            if (accessToken.refreshToken) result.refresh_token = accessToken.refreshToken

        } else {
            result["$tokenPropertyName"] = accessToken.accessToken
        }

        User user = User.findByUsername(userDetails.username)

        result.with {
            id = user.id
        }

        def jsonResult = result as JSON

        return jsonResult.toString()
    }
}
