package clockr.api

import grails.core.GrailsApplication

class BootStrap {

    GrailsApplication grailsApplication

    def init = { servletContext ->
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        Role userRole = Role.findOrCreateByAuthority("ROLE_USER").save()
        Role adminRole = Role.findOrCreateByAuthority("ROLE_ADMIN").save()

        if (grailsApplication.config.initialAdminUser.email && grailsApplication.config.initialAdminUser.password) {
            User adminUser = User.findOrCreateByUsername(grailsApplication.config.initialAdminUser.email as String)
            if (!adminUser.id) {
                adminUser.password = grailsApplication.config.initialAdminUser.password as String
                adminUser.save()

                UserRole.withTransaction {
                    UserRole.create adminUser, adminRole, true
                }
            }
        }
    }
    def destroy = {
    }
}
