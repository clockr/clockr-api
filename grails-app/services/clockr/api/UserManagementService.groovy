package clockr.api

import clockr.api.commands.ContractCommand
import clockr.api.commands.UserCommand
import grails.gorm.transactions.Transactional

@Transactional
class UserManagementService {

    def tokenService
    def notificationService
    def grailsApplication

    def listUsers() {
        return User.all.collect { getUserModelShort(it.id) }
    }

    def getUser(Long id) {
        return getUserModel(id)
    }

    def saveUser(UserCommand cmd) {
        User user = cmd.user
        Boolean isNewUser = !user.id
        user.setProperties(cmd)
        if (!user.password) {
            user.password = UUID.randomUUID()
        }
        user.save()
        if (isNewUser) {
            Token token = tokenService.createToken(Token.TokenType.USER_SET_PASSWORD, user)
            def model = [tokenLink: "${grailsApplication.config.getProperty('setPasswordNewUserLink')}${token.identifier}"]
            notificationService.sendMailByView(user, "Für dich wurde ein Account erstellt", "/email/setPasswordNewUser", model)
            UserRole.create(user, Role.findByAuthority('ROLE_USER'), true)
        }
        if (!cmd.isAdmin && user.authorities?.any { it.authority == 'ROLE_ADMIN'}) {
            UserRole.remove(user, Role.findByAuthority('ROLE_ADMIN'))
        }
        if (cmd.isAdmin && !user.authorities?.any { it.authority == 'ROLE_ADMIN'}) {
            UserRole.create(user, Role.findByAuthority('ROLE_ADMIN'), true)
        }
        return getUser(user.id)
    }

    def deleteUser(Long id) {
        User user = User.get(id)
        user.delete()
        return true
    }

    def saveContract(ContractCommand cmd) {
        Contract contract = cmd.contract
        contract.setProperties(cmd)
        contract.save()
        return getUser(contract.user.id)
    }

    def deleteContract(Long id) {
        Contract contract = Contract.get(id)
        Long userId = contract.userId
        User.get(userId)?.removeFromContracts(contract)
        contract.delete()
        return getUser(userId)
    }

    private static getUserModelShort(Long id) {
        User user = User.get(id)
        return [
                id         : user.id,
                username   : user.username,
                firstname  : user.firstname,
                lastname   : user.lastname,
                germanState: user.germanState?.name(),
                enabled    : user.enabled,
                isAdmin    : user.authorities?.any { it.authority == 'ROLE_ADMIN' },
                isArchived : user.isArchived
        ]
    }

    private static getUserModel(Long id) {
        User user = User.get(id)
        return [
                id         : user.id,
                username   : user.username,
                firstname  : user.firstname,
                lastname   : user.lastname,
                germanState: user.germanState?.name(),
                enabled    : user.enabled,
                contracts  : user.contracts?.collect { getContractModel(it.id) },
                isAdmin    : user.authorities?.any { it.authority == 'ROLE_ADMIN' },
                isArchived : user.isArchived
        ]
    }

    private static getContractModel(Long id) {
        Contract contract = Contract.get(id)
        return [
                id                 : contract.id,
                userId             : contract.userId,
                startAt            : contract.startAt,
                endAt              : contract.endAt,
                hoursPerWeek       : contract.hoursPerWeek,
                workingDays        : contract.workingDays,
                vacationDaysPerYear: contract.vacationDaysPerYear
        ]
    }

    def lockMonth(Long userId, Integer year, Integer month) {
        User user = User.get(userId)
        LockedMonth lockedMonth = LockedMonth.findByUserAndYearAndMonth(user, year, month)
        if (!lockedMonth) {
            lockedMonth = new LockedMonth(user: user, year: year, month: month)
            lockedMonth.save()
        }
        return true
    }

    def unlockMonth(Long userId, Integer year, Integer month) {
        User user = User.get(userId)
        LockedMonth lockedMonth = LockedMonth.findByUserAndYearAndMonth(user, year, month)
        if (lockedMonth) {
            lockedMonth.delete()
        }
        return true
    }
}
