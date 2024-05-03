package clockr.api

import clockr.api.commands.DayItemCommand
import grails.gorm.transactions.Transactional

@Transactional
class DayItemService {

    def userAccessService
    def userService

    def saveDayItem(DayItemCommand cmd) {
        if (userAccessService.hasUserAccess(cmd.user?.id) && !userService.isDateInLockedMonth(cmd.user?.id, cmd.day)) {
            DayItem dayItem = cmd.dayItem
            dayItem.setProperties(cmd)
            dayItem.save()
            return getDayItem(dayItem.id)
        }
    }

    def deleteDayItem(Long id) {
        DayItem dayItem = DayItem.get(id)
        if (userAccessService.hasUserAccess(dayItem?.userId) && !userService.isDateInLockedMonth(dayItem?.userId, dayItem?.day)) {
            dayItem.delete()
            return true
        }
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
