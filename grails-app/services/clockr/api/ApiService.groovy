package clockr.api

import grails.gorm.transactions.Transactional

@Transactional
class ApiService {

    def formatErrors(object) {
        try {
            object?.errors?.allErrors?.collectEntries {
                [it.field, it.codes?.last()]
            }
        } catch (e) {
            [serverError: e.class, mesage: e.message]
        }
    }
}
