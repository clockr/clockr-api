package clockr.api

import de.jollyday.Holiday
import de.jollyday.HolidayCalendar
import de.jollyday.HolidayManager
import de.jollyday.HolidayType

import java.time.LocalDate
import java.time.ZoneId

class WorkingDayCalculator {

    static def getDays(Integer year, Integer month, Date contractStartAt, Date contractEndAt, String workingDaysPattern, String stateCode = "mv") {
        LocalDate localContractStartAt = contractStartAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
        LocalDate localContractEndAt = contractEndAt ? contractEndAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate() : null

        LocalDate date = LocalDate.of(year, month, 1)
        if (localContractStartAt.isAfter(date)) {
            date = LocalDate.of(year, month, localContractStartAt.getDayOfMonth())
        }

        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth())
        if (localContractEndAt && localContractEndAt.isBefore(endOfMonth)) {
            endOfMonth = LocalDate.of(year, month, localContractEndAt.getDayOfMonth())
        }

        HolidayManager manager = HolidayManager.getInstance(HolidayCalendar.GERMANY)

        Set<Holiday> holidays = manager.getHolidays(year, stateCode)

        if (year >= 2023 && stateCode?.toLowerCase() == 'mv') {
            holidays.add(new Holiday(LocalDate.of(year, 3, 8), 'INTERNATIONAL_WOMAN', HolidayType.OFFICIAL_HOLIDAY))
        }

        def days = []
        while (date.isBefore(endOfMonth) || date.isEqual(endOfMonth)) {
            def day = [date: date, isWorkingDay: false]
            if (isWorkingDay(date, workingDaysPattern) && !isHoliday(date, holidays)) {
                day.isWorkingDay = true
            }
            days += day
            date = date.plusDays(1)
        }
        return days
    }

    static int countWorkingDays(Integer year, Integer month, Date contractStartAt, Date contractEndAt, String workingDaysPattern, String stateCode = "mv") {
        LocalDate localContractStartAt = contractStartAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()
        LocalDate localContractEndAt = contractEndAt ? contractEndAt.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate() : null

        LocalDate date = LocalDate.of(year, month, 1)
        if (localContractStartAt.isAfter(date)) {
            date = LocalDate.of(year, month, localContractStartAt.getDayOfMonth())
        }

        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth())
        if (localContractEndAt && localContractEndAt.isBefore(endOfMonth)) {
            endOfMonth = LocalDate.of(year, month, localContractEndAt.getDayOfMonth())
        }

        HolidayManager manager = HolidayManager.getInstance(HolidayCalendar.GERMANY)

        Set<Holiday> holidays = manager.getHolidays(year, stateCode)

        if (year >= 2023 && stateCode?.toLowerCase() == 'mv') {
            holidays.add(new Holiday(LocalDate.of(year, 3, 8), 'INTERNATIONAL_WOMAN', HolidayType.OFFICIAL_HOLIDAY))
        }

        int workingDays = 0
        while (date.isBefore(endOfMonth) || date.isEqual(endOfMonth)) {
            if (isWorkingDay(date, workingDaysPattern) && !isHoliday(date, holidays)) {
                workingDays++
            }
            date = date.plusDays(1)
        }
        return workingDays
    }

    private static boolean isWorkingDay(LocalDate date, String pattern) {
        int dayOfWeek = date.getDayOfWeek().getValue()
        return pattern.charAt(dayOfWeek - 1)?.toString() == '1'
    }

    private static boolean isHoliday(LocalDate date, Set<Holiday> holidays) {
        return holidays.stream().anyMatch(holiday -> holiday.getDate() == date)
    }

}
