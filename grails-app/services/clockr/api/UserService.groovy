package clockr.api

import clockr.api.commands.UserSetPasswordCommand
import grails.gorm.transactions.Transactional

import java.time.LocalDate
import java.time.ZoneId

@Transactional
class UserService {

    def userContractService
    def userWorkingTimeService
    def userDayItemService
    def userManualEntryService
    def manualEntryService
    def tokenService
    def notificationService
    def grailsApplication

    def getMonthDays(Long userId, Integer year, Integer month) {
        Contract contract = userContractService.getContractForMonth(userId, year, month)
        if (contract) {
            WorkingDayCalculator.getDays(year, month, contract.workingDays)?.collect { day ->
                day.workingTimes = userWorkingTimeService.getWorkingTimesForDay(userId, day.date as LocalDate)
                day.isHours = userWorkingTimeService.getWorkingTimeForDayInHours(userId, day.date as LocalDate) ?: 0
                day.breakfastItem = userDayItemService.getDayItemByTypeForDay(userId, DayItem.DayItemType.BREAKFAST, day.date as LocalDate)
                day.lunchItem = userDayItemService.getDayItemByTypeForDay(userId, DayItem.DayItemType.LUNCH, day.date as LocalDate)
                day.illnessItem = userDayItemService.getDayItemByTypeForDay(userId, DayItem.DayItemType.ILLNESS, day.date as LocalDate)
                day.vacationItem = userDayItemService.getDayItemByTypeForDay(userId, DayItem.DayItemType.VACATION, day.date as LocalDate)
                day.date = Date.from(day.date?.atStartOfDay(ZoneId.of("Europe/Berlin")).toInstant())
                day
            }
        }
    }

    def getMonthResult(Long userId, Integer year, Integer month) {
        Contract contract = userContractService.getContractForMonth(userId, year, month)
        if (contract) {
            Integer workingDaysAmount = WorkingDayCalculator.countWorkingDays(year, month, contract.workingDays)
            Float targetHours = workingDaysAmount * (contract.hoursPerWeek / userContractService.getDaysPerWeek(contract.id))
            Float isHours = userWorkingTimeService.getWorkingTimeForMonth(userId, year, month) ?: 0
            Integer breakfastCount = userDayItemService.countDayItemsByTypeForMonth(userId, DayItem.DayItemType.BREAKFAST, year, month)
            Integer lunchCount = userDayItemService.countDayItemsByTypeForMonth(userId, DayItem.DayItemType.LUNCH, year, month)
            Integer illnessCount = userDayItemService.countDayItemsByTypeForMonth(userId, DayItem.DayItemType.ILLNESS, year, month)
            Integer vacationCount = userDayItemService.countDayItemsByTypeForMonth(userId, DayItem.DayItemType.VACATION, year, month)
            Float difference = isHours - targetHours + (illnessCount + vacationCount) * (contract.hoursPerWeek / userContractService.getDaysPerWeek(contract.id))
            return [
                    hoursPerWeek  : contract.hoursPerWeek,
                    daysPerWeek   : userContractService.getDaysPerWeek(contract.id),
                    targetHours   : targetHours,
                    isHours       : isHours,
                    breakfastCount: breakfastCount,
                    lunchCount    : lunchCount,
                    illnessCount  : illnessCount,
                    vacationCount : vacationCount,
                    difference    : difference
            ]
        } else {
            return [
                    hoursPerWeek  : 0,
                    daysPerWeek   : 0,
                    targetHours   : 0,
                    isHours       : 0,
                    breakfastCount: 0,
                    lunchCount    : 0,
                    illnessCount  : 0,
                    vacationCount : 0,
                    difference    : 0
            ]
        }
    }

    def getYearMonths(Long userId, Integer year) {
        Float workingHoursOffset = getYearsBeforeOffsets(userId, year)?.workingHoursOffset
        (1..12)?.collect {
            def month = getMonthResult(userId, year, it)
            workingHoursOffset += month?.difference ?: 0
            month.month = it
            month.totalDifference = workingHoursOffset
            month
        }
    }

    def getYearOverview(Long userId, Integer year) {
        def offsets = getYearsBeforeOffsets(userId, year)
        Integer vacationDays = userContractService.getVacationForYear(userId, year) ?: 0
        Integer usedVacationDays = userDayItemService.countDayItemsByTypeForYear(userId, DayItem.DayItemType.VACATION, year) ?: 0
        return [
                vacationDays         : vacationDays,
                vacationOffset       : offsets.vacationOffset,
                workingHoursOffset   : offsets.workingHoursOffset,
                vacationDaysRemaining: vacationDays + offsets.vacationOffset - usedVacationDays,
                manualEntries        : userManualEntryService.getManualEntriesForYear(userId, year)?.collect { manualEntry -> manualEntryService.getManualEntry(manualEntry.id) }
        ]
    }

    private def getYearsBeforeOffsets(Long userId, Integer year) {
        Integer firstContractedYear = userContractService.getFirstContractedYear(userId)
        Integer vacationOffset = 0
        Float workingHoursOffset = 0.0

        if (firstContractedYear) {
            for (Integer i = firstContractedYear; i < year; i++) {
                def months = getYearMonths(userId, i)
                vacationOffset += userContractService.getVacationForYear(userId, i) - (months?.sum { it.vacationCount } as Integer) + (userManualEntryService.getManualEntriesByTypeForYear(userId, ManualEntry.ManualEntryType.VACATION, i)?.sum { it.amount } ?: 0) as Integer
                workingHoursOffset += months?.sum { it.difference } as Float + (userManualEntryService.getManualEntriesByTypeForYear(userId, ManualEntry.ManualEntryType.WORKING_TIME, i)?.sum { it.amount } ?: 0) as Float
            }
        }

        return [
                vacationOffset    : vacationOffset,
                workingHoursOffset: workingHoursOffset
        ]
    }

    def requestResetPasswordToken(String username) {
        username = username.replaceAll("%", "")
        User user = User.findByUsernameIlike(username)
        if (user) {
            Token token = tokenService.createToken(Token.TokenType.USER_SET_PASSWORD, user)
            def model = [tokenLink: "${grailsApplication.config.getProperty('resetPasswordByUserLink')}${token.identifier}"]
            notificationService.sendMailByView(user, "Dein Zugang", "/email/resetPasswordByUser", model)
        } else {
            log.warn "user for username $username not found"
        }
        return true
    }

    def setPassword(UserSetPasswordCommand cmd) {
        tokenService.invalidateToken(cmd.token?.id)
        User user = cmd.token?.user?.refresh()
        user.password = cmd.password
        user.save()
        if (user.hasErrors()) {
            log.warn "error while setting password for user ${user.id}"
        }
        return true
    }
}
