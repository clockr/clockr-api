package clockr.api

import clockr.api.commands.DayItemCommand
import grails.gorm.transactions.Transactional

@Transactional
class DayItemService {

    def saveDayItem(DayItemCommand cmd) {
        DayItem dayItem = cmd.dayItem
        dayItem.setProperties(cmd)
        dayItem.save()
        return getDayItem(dayItem.id)
    }

    def deleteDayItem(Long id) {
        DayItem dayItem = DayItem.get(id)
        dayItem.delete()
        return true
    }

    private static getDayItem(Long id) {
        DayItem dayItem = DayItem.get(id)
        return [
                id    : dayItem.id,
                userId: dayItem.userId,
                type  : dayItem.type?.name(),
                day   : dayItem.day
        ]
    }
}
