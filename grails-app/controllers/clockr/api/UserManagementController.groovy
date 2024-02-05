package clockr.api

import clockr.api.commands.ContractCommand
import clockr.api.commands.UserCommand
import grails.plugin.springsecurity.annotation.Secured

@Secured("ROLE_ADMIN")
class UserManagementController extends RestController {

    def userManagementService

    def list() {
        renderJson(userManagementService.listUsers())
    }

    def read() {
        renderJson(userManagementService.getUser(params.int('id')))
    }

    def create(UserCommand cmd) {
        cmd.user = new User()
        renderJson(cmd, { userManagementService.saveUser(cmd) })
    }

    def update(UserCommand cmd) {
        cmd.user = User.get(params.int('id'))
        renderJson(cmd, { userManagementService.saveUser(cmd) })
    }

    def delete() {
        renderJson(userManagementService.deleteUser(params.int('id')))
    }

    def createContract(ContractCommand cmd) {
        cmd.contract = new Contract()
        cmd.user = User.get(params.int('userId'))
        renderJson(cmd, { userManagementService.saveContract(cmd) })
    }

    def updateContract(ContractCommand cmd) {
        cmd.contract = Contract.get(params.int('id'))
        cmd.user = User.get(params.int('userId'))
        renderJson(cmd, { userManagementService.saveContract(cmd) })
    }

    def deleteContract() {
        renderJson(userManagementService.deleteContract(params.int('id')))
    }
}
