import clockr.api.UserPasswordEncoderListener
import clockr.api.CustomAccessTokenJsonRenderer
// Place your Spring DSL code here
beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)
    accessTokenJsonRenderer(CustomAccessTokenJsonRenderer) {
        usernamePropertyName = "${grailsApplication.config.grails.plugin.springsecurity.rest.token.rendering.usernamePropertyName}"
        tokenPropertyName = "${grailsApplication.config.grails.plugin.springsecurity.rest.token.rendering.tokenPropertyName}"
        authoritiesPropertyName = "${grailsApplication.config.grails.plugin.springsecurity.rest.token.rendering.authoritiesPropertyName}"
        useBearerToken = "${grailsApplication.config.grails.plugin.springsecurity.rest.token.validation.useBearerToken}"
    }
}
