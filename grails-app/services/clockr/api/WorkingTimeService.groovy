package clockr.api

import clockr.api.commands.WorkingTimeCommand
import grails.gorm.transactions.Transactional

@Transactional
class WorkingTimeService {

    def saveWorkingTime(WorkingTimeCommand cmd) {
        WorkingTime workingTime = cmd.workingTime
        workingTime.setProperties(cmd)
        workingTime.save()
        return getWorkingTime(workingTime.id)
    }

    def deleteWorkingTime(Long id) {
        WorkingTime workingTime = WorkingTime.get(id)
        workingTime.delete()
        return true
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
