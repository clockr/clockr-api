package clockr.api

import clockr.api.commands.UserSetPasswordCommand
import grails.plugin.springsecurity.annotation.Secured

@Secured("ROLE_USER")
class UserController extends RestController {

    def userService

    def getMonth() {
        def result = [
                days  : userService.getMonthDays(params.int('id'), params.int('year'), params.int('month')),
                result: userService.getMonthResult(params.int('id'), params.int('year'), params.int('month'))
        ]
        renderJson(result)
    }

    def getYear() {
        def result = [
                months: userService.getYearMonths(params.int('id'), params.int('year')),
                overview: userService.getYearOverview(params.int('id'), params.int('year'))
        ]
        renderJson(result)
    }

    @Secured('permitAll')
    def forgotPassword() {
        renderJson(userService.requestResetPasswordToken(params.username as String))
    }

    @Secured('permitAll')
    def setPassword(UserSetPasswordCommand cmd) {
        renderJson(cmd, { userService.setPassword(cmd) })
    }
}
