package clockr.api

import clockr.api.ManualEntry.ManualEntryType
import grails.gorm.transactions.Transactional

import java.time.LocalDate
import java.time.ZoneId

@Transactional
class UserManualEntryService {

    def getManualEntriesForYear(Long userId, Integer year) {
        ManualEntry[] manualEntries = ManualEntry.findAllByUser(User.get(userId))
        LocalDate targetStart = LocalDate.of(year, 1, 1)
        LocalDate targetEnd = LocalDate.of(year + 1, 1, 1)

        return manualEntries?.findAll { manualEntry ->
            LocalDate date = manualEntry.date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()

            return date >= targetStart && date < targetEnd
        }
    }

    def getManualEntriesByTypeForYear(Long userId, ManualEntryType type, Integer year) {
        ManualEntry[] manualEntries = ManualEntry.findAllByUserAndType(User.get(userId), type)
        LocalDate targetStart = LocalDate.of(year, 1, 1)
        LocalDate targetEnd = LocalDate.of(year + 1, 1, 1)

        return manualEntries?.findAll { manualEntry ->
            LocalDate date = manualEntry.date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()

            return date >= targetStart && date < targetEnd
        }
    }

    ManualEntry[] getManualEntriesByTypeBeforeYear(Long userId, ManualEntryType type, Integer year) {
        ManualEntry[] manualEntries = ManualEntry.findAllByUserAndType(User.get(userId), type)
        LocalDate targetStart = LocalDate.of(year, 1, 1)

        return manualEntries?.findAll { manualEntry ->
            LocalDate date = manualEntry.date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDate()

            return date < targetStart
        }
    }
}
