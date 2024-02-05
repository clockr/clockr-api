package clockr.api

import clockr.api.commands.DayItemCommand
import grails.plugin.springsecurity.annotation.Secured

@Secured("ROLE_USER")
class DayItemController extends RestController {

    def dayItemService

    def create(DayItemCommand cmd) {
        cmd.dayItem = new DayItem()
        cmd.user = User.get(params.int('userId'))
        renderJson(cmd, { dayItemService.saveDayItem(cmd) })
    }

    def delete() {
        renderJson(dayItemService.deleteDayItem(params.int('id')))
    }
}
