package clockr.api

import clockr.api.commands.WorkingTimeCommand
import grails.gorm.transactions.Transactional

@Transactional
class WorkingTimeService {

    def userAccessService
    def userService

    def saveWorkingTime(WorkingTimeCommand cmd) {
        if (userAccessService.hasUserAccess(cmd.user?.id)) {
            if (!userService.isDateInLockedMonth(cmd.user?.id, cmd.startAt) && (!cmd.endAt || !userService.isDateInLockedMonth(cmd.user?.id, cmd.endAt))) {
                WorkingTime workingTime = cmd.workingTime
                workingTime.setProperties(cmd)
                workingTime.save()
                return getWorkingTime(workingTime.id)
            }
        }
    }

    def deleteWorkingTime(Long id) {
        WorkingTime workingTime = WorkingTime.get(id)
        if (userAccessService.hasUserAccess(workingTime?.userId)) {
            if (!userService.isDateInLockedMonth(workingTime?.userId, workingTime?.startAt) && (!workingTime?.endAt || !userService.isDateInLockedMonth(workingTime?.userId, workingTime?.endAt))) {
                workingTime.delete()
                return true
            }
        }
    }

    private static getWorkingTime(Long id) {
        WorkingTime workingTime = WorkingTime.get(id)
        return [
                id       : workingTime.id,
                userId   : workingTime.userId,
                startAt  : workingTime.startAt,
                endAt    : workingTime.endAt,
                breakTime: workingTime.breakTime,
                note     : workingTime.note
        ]
    }
}
