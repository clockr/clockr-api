package clockr.api

import grails.gorm.transactions.Transactional

import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Transactional
class UserContractService {

    Contract[] getContractsForMonth(Long userId, Integer year, Integer month) {
        Contract[] contracts = User.get(userId)?.contracts
        LocalDate targetStart = LocalDate.of(year, month, 1)
        LocalDate targetEnd = targetStart.plusMonths(1).minusDays(1)

        return contracts?.findAll { contract ->
            LocalDate startAt = contract.startAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
            LocalDate endAt = (contract.endAt ?: new Date()).toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()

            return !(startAt.isAfter(targetEnd) || endAt.isBefore(targetStart))
        }
    }

    Contract[] getContractsForYear(Long userId, Integer year) {
        Contract[] contracts = User.get(userId)?.contracts
        LocalDate targetStart = LocalDate.of(year, 1, 1)
        LocalDate targetEnd = targetStart.plusYears(1)

        return contracts?.findAll { contract ->
            LocalDate startAt = contract.startAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
            LocalDate endAt = (contract.endAt ?: new Date()).toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()

            return !(startAt.isAfter(targetEnd) || endAt.isBefore(targetStart))
        }
    }

    Integer getDaysPerWeek(Long contractId) {
        Contract contract = Contract.get(contractId)
        return contract?.workingDays?.count('1')
    }

    Integer getVacationForYear(Long userId, Integer year) {
        Contract[] contracts = getContractsForYear(userId, year)
        return contracts?.sum { contract ->
            getContractYearMultiplier(contract.id, year) * contract.vacationDaysPerYear
        } as Integer
    }

    Float getContractYearMultiplier(Long contractId, Integer year) {
        Contract contract = Contract.get(contractId)
        Boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        Integer totalDaysInYear = isLeapYear ? 366 : 365

        LocalDate startOfYear = LocalDate.of(year, 1, 1)
        LocalDate endOfYear = LocalDate.of(year, 12, 31)
        LocalDate startDate = contract.startAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
        LocalDate endDate = contract.endAt ? contract.endAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate() : endOfYear
        LocalDate actualStartDate = startDate.isBefore(startOfYear) ? startOfYear : startDate
        LocalDate actualEndDate = endDate.isAfter(endOfYear) ? endOfYear : endDate

        Long duration = ChronoUnit.DAYS.between(actualStartDate, actualEndDate) + 1
        return duration / totalDaysInYear
    }

    Integer getFirstContractedYear(Long userId) {
        Contract[] contracts = User.get(userId)?.contracts
        return contracts?.collect { contract -> contract.startAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate().getYear() }?.min()
    }
}
