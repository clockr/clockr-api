package clockr.api

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.security.access.AccessDeniedException

@Transactional
class UserAccessService {

    def springSecurityService

    Boolean hasUserAccess(Long userId) {
        if (SpringSecurityUtils.ifAllGranted('ROLE_ADMIN')) {
            return true
        }
        if (springSecurityService.currentUserId as Long == userId) {
            return true
        }

        throw new AccessDeniedException("user access not allowed")
    }
}
