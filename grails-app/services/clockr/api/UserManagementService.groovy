package clockr.api

import clockr.api.commands.ContractCommand
import clockr.api.commands.UserCommand
import grails.gorm.transactions.Transactional

@Transactional
class UserManagementService {

    def listUsers() {
        return User.all.collect { getUserModelShort(it.id) }
    }

    def getUser(Long id) {
        return getUserModel(id)
    }

    def saveUser(UserCommand cmd) {
        User user = cmd.user
        user.setProperties(cmd)
        if (!user.password) {
            user.password = UUID.randomUUID()
        }
        user.save()
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
                id       : user.id,
                username : user.username,
                firstname: user.firstname,
                lastname : user.lastname,
                enabled  : user.enabled
        ]
    }

    private static getUserModel(Long id) {
        User user = User.get(id)
        return [
                id       : user.id,
                username : user.username,
                firstname: user.firstname,
                lastname : user.lastname,
                enabled  : user.enabled,
                contracts: user.contracts?.collect { getContractModel(it.id) }
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
}
