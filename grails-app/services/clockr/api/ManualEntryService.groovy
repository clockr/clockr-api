package clockr.api

import clockr.api.commands.ManualEntryCommand
import grails.gorm.transactions.Transactional

@Transactional
class ManualEntryService {

    def saveManualEntry(ManualEntryCommand cmd) {
        ManualEntry manualEntry = cmd.manualEntry
        manualEntry.setProperties(cmd)
        manualEntry.save()
        return getManualEntry(manualEntry.id)
    }

    def deleteManualEntry(Long id) {
        ManualEntry manualEntry = ManualEntry.get(id)
        manualEntry.delete()
        return true
    }

    static def getManualEntry(Long id) {
        ManualEntry manualEntry = ManualEntry.get(id)
        return [
                id    : manualEntry.id,
                userId: manualEntry.userId,
                date  : manualEntry.date,
                type  : manualEntry.type?.name(),
                amount: manualEntry.amount,
                note  : manualEntry.note
        ]
    }
}
