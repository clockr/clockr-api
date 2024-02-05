package clockr.api

import grails.gorm.transactions.Transactional

import java.time.LocalDate
import java.time.ZoneId

@Transactional
class UserWorkingTimeService {

    Float getWorkingTimeForMonth(Long userId, Integer year, Integer month) {
        WorkingTime[] workingTimes = User.get(userId)?.workingTimes
        LocalDate targetStart = LocalDate.of(year, month, 1)
        LocalDate targetEnd = targetStart.plusMonths(1).minusDays(1)

        return workingTimes?.findAll { workingTime ->
            LocalDate startAt = workingTime.startAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
            LocalDate endAt = workingTime.endAt ? workingTime.endAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate() : startAt

            return !(startAt.isAfter(targetEnd) || endAt.isBefore(targetStart))
        }?.sum { workingTime ->
            return getGrossWorkingTimeInHours(workingTime.id)
        }
    }

    Float getGrossWorkingTimeInHours(Long workingTimeId) {
        WorkingTime workingTime = WorkingTime.get(workingTimeId)
        if (!workingTime.endAt) return 0.0
        return ((workingTime.endAt).time - workingTime.startAt?.time) / (1000 * 60 * 60) - (workingTime.breakTime ?: 0)
    }

    WorkingTime[] getWorkingTimesForDay(Long userId, LocalDate day) {
        WorkingTime[] workingTimes = User.get(userId)?.workingTimes
        LocalDate dayStart = day
        LocalDate dayEnd = day.plusDays(1)

        return workingTimes?.findAll { workingTime ->
            LocalDate startAt = workingTime.startAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
            return startAt >= dayStart && startAt < dayEnd
        }
    }

    Float getWorkingTimeForDayInHours(Long userId, LocalDate day) {
        WorkingTime[] workingTimes = getWorkingTimesForDay(userId, day)
        return workingTimes?.collect {workingTime -> getGrossWorkingTimeInHours(workingTime.id) }?.sum()
    }
}
