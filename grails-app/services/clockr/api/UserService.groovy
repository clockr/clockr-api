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
    def userAccessService

    def getMonthDays(Long userId, Integer year, Integer month) {
        if (userAccessService.hasUserAccess(userId)) {
            User user = User.get(userId)
            Contract[] contracts = userContractService.getContractsForMonth(userId, year, month)
            if (contracts) {
                def workingDays = contracts?.collect { contract ->
                    WorkingDayCalculator.getDays(year, month, contract.startAt, contract.endAt, contract.workingDays, user?.germanState?.name()?.toLowerCase())
                }?.flatten()?.sort{ it.date }
                workingDays?.collect { day ->
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
    }

    def getMonthResult(Long userId, Integer year, Integer month) {
        if (userAccessService.hasUserAccess(userId)) {
            User user = User.get(userId)
            Contract[] contracts = userContractService.getContractsForMonth(userId, year, month)
            if (contracts) {
                Float targetHours = contracts?.sum { contract ->
                    Integer workingDaysAmount = WorkingDayCalculator.countWorkingDays(year, month, contract.startAt, contract.endAt, contract.workingDays, user?.germanState?.name()?.toLowerCase())
                    return workingDaysAmount * (contract.hoursPerWeek / userContractService.getDaysPerWeek(contract.id))
                } ?: 0
                Float isHours = (userWorkingTimeService.getWorkingTimeForMonth(userId, year, month) ?: 0) + (contracts?.sum {contract ->
                    Integer contractIllnessCount = userDayItemService.countDayItemsByTypeForMonthAndContract(userId, contract.id, DayItem.DayItemType.ILLNESS, year, month)
                    Integer contractVacationCount = userDayItemService.countDayItemsByTypeForMonthAndContract(userId, contract.id, DayItem.DayItemType.VACATION, year, month)
                    return (contractIllnessCount + contractVacationCount) * (contract.hoursPerWeek / userContractService.getDaysPerWeek(contract.id))
                } ?: 0)
                Integer breakfastCount = userDayItemService.countDayItemsByTypeForMonth(userId, DayItem.DayItemType.BREAKFAST, year, month)
                Integer lunchCount = userDayItemService.countDayItemsByTypeForMonth(userId, DayItem.DayItemType.LUNCH, year, month)
                Integer illnessCount = userDayItemService.countDayItemsByTypeForMonth(userId, DayItem.DayItemType.ILLNESS, year, month)
                Integer vacationCount = userDayItemService.countDayItemsByTypeForMonth(userId, DayItem.DayItemType.VACATION, year, month)
                Float difference = isHours - targetHours
                return [
                        hoursPerWeek  : contracts?.sort { it.startAt }?.collect { contract -> contract.hoursPerWeek },
                        daysPerWeek   : contracts?.sort { it.startAt }?.collect { contract -> userContractService.getDaysPerWeek(contract.id) },
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
                        hoursPerWeek  : [0],
                        daysPerWeek   : [0],
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
    }

    def getYearMonths(Long userId, Integer year) {
        if (userAccessService.hasUserAccess(userId)) {
            Float workingHoursOffset = getYearsBeforeOffsets(userId, year)?.workingHoursOffset
            (1..12)?.collect {
                def month = getMonthResult(userId, year, it)
                workingHoursOffset += month?.difference ?: 0
                month.month = it
                month.totalDifference = workingHoursOffset
                month
            }
        }
    }

    def getYearOverview(Long userId, Integer year) {
        if (userAccessService.hasUserAccess(userId)) {
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
    }

    private def getYearsBeforeOffsets(Long userId, Integer year) {
        Integer firstContractedYear = userContractService.getFirstContractedYear(userId)
        Integer vacationOffset = 0
        Float workingHoursOffset = 0.0

        if (firstContractedYear) {
            for (Integer i = firstContractedYear; i < year; i++) {
                def months = getYearMonths(userId, i)
                vacationOffset += userContractService.getVacationForYear(userId, i) - (months?.sum { it.vacationCount } as Integer)
                workingHoursOffset += months?.sum { it.difference } as Float
            }
        }

        return [
                vacationOffset    : vacationOffset + (userManualEntryService.getManualEntriesByTypeBeforeYear(userId, ManualEntry.ManualEntryType.VACATION, year)?.sum { it.amount } ?: 0) as Integer,
                workingHoursOffset: workingHoursOffset + (userManualEntryService.getManualEntriesByTypeBeforeYear(userId, ManualEntry.ManualEntryType.WORKING_TIME, year)?.sum { it.amount } ?: 0) as Float
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
        User.withNewTransaction {
            User user = User.get(cmd.token?.userId)
            user.password = cmd.password
            user.save()
            if (user.hasErrors()) {
                log.warn "error while setting password for user ${user.id}"
            }
        }
        return true
    }

    Boolean isDateInLockedMonth(Long userId, Date date) {
        if (userAccessService.hasUserAccess(userId)) {
            User user = User.get(userId)
            def lockedMonth = user.lockedMonths?.find {
                LocalDate monthStart = LocalDate.of(it.year, it.month, 1)
                LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth()).plusDays(1)
                LocalDate localDate = date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
                return localDate >= monthStart && localDate < monthEnd
            }
            return lockedMonth
        }
        return true
    }

    def isMonthLocked(Long userId, Integer year, Integer month) {
        if (userAccessService.hasUserAccess(userId)) {
            User user = User.get(userId)
            return user.lockedMonths?.any { it.year == year && it.month == month }
        }
    }
}
