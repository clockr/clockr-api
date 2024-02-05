package clockr.api

import grails.converters.JSON
import grails.validation.Validateable

class RestController {

    def apiService

    protected def renderJson(model) {

        def result = [
                success: true,
                data   : model
        ]
        render result as JSON
    }

    protected def renderJson(Validateable command, Closure successHandlerAndModel) {
        command.validate()
        def result = [
                success: !command.hasErrors(),
                data   : !command.hasErrors() ? successHandlerAndModel.call() : null,
                errors : command.hasErrors() ? apiService.formatErrors(command) : []
        ]
        if (command.hasErrors()) response.status = 400
        render result as JSON
    }

    protected def renderJson(Closure model, Validateable command, Closure successHandler) {
        if (command.validate()) {
            successHandler.call()
        }
        def result = [
                success: !command.hasErrors(),
                data   : model.call(),
                errors : command.hasErrors() ? apiService.formatErrors(command) : []
        ]
        if (command.hasErrors()) response.status = 400
        render result as JSON
    }
}
