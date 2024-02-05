package clockr.api

import de.jollyday.Holiday
import de.jollyday.HolidayCalendar
import de.jollyday.HolidayManager
import java.time.LocalDate

class WorkingDayCalculator {

    static def getDays(Integer year, Integer month, String workingDaysPattern, String stateCode = "mv") {
        LocalDate date = LocalDate.of(year, month, 1)
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth())

        HolidayManager manager = HolidayManager.getInstance(HolidayCalendar.GERMANY)

        Set<Holiday> holidays = manager.getHolidays(year, stateCode)

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

    static int countWorkingDays(Integer year, Integer month, String workingDaysPattern, String stateCode = "mv") {
        LocalDate date = LocalDate.of(year, month, 1)
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth())

        HolidayManager manager = HolidayManager.getInstance(HolidayCalendar.GERMANY)

        Set<Holiday> holidays = manager.getHolidays(year, stateCode)

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
