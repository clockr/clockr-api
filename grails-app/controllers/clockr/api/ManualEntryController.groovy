package clockr.api

import clockr.api.commands.ManualEntryCommand
import grails.plugin.springsecurity.annotation.Secured

@Secured("ROLE_ADMIN")
class ManualEntryController extends RestController {

    def manualEntryService

    def create(ManualEntryCommand cmd) {
        cmd.manualEntry = new ManualEntry()
        cmd.user = User.get(params.int('userId'))
        renderJson(cmd, { manualEntryService.saveManualEntry(cmd) })
    }

    def update(ManualEntryCommand cmd) {
        cmd.manualEntry = ManualEntry.get(params.int('id'))
        cmd.user = User.get(params.int('userId'))
        renderJson(cmd, { manualEntryService.saveManualEntry(cmd) })
    }

    def delete() {
        renderJson(manualEntryService.deleteManualEntry(params.int('id')))
    }
}
