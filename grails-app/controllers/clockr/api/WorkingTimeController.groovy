package clockr.api

import clockr.api.commands.WorkingTimeCommand
import grails.plugin.springsecurity.annotation.Secured

@Secured("ROLE_USER")
class WorkingTimeController extends RestController {

    def workingTimeService

    def create(WorkingTimeCommand cmd) {
        cmd.workingTime = new WorkingTime()
        cmd.user = User.get(params.int('userId'))
        renderJson(cmd, { workingTimeService.saveWorkingTime(cmd) })
    }

    def update(WorkingTimeCommand cmd) {
        cmd.workingTime = WorkingTime.get(params.int('id'))
        cmd.user = User.get(params.int('userId'))
        renderJson(cmd, { workingTimeService.saveWorkingTime(cmd) })
    }

    def delete() {
        renderJson(workingTimeService.deleteWorkingTime(params.int('id')))
    }
}
