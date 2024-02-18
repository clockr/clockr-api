package clockr.api

import grails.gorm.transactions.Transactional
import static grails.async.Promises.task

@Transactional
class NotificationService {

    def grailsApplication
    def groovyPageRenderer

    def sendMailByView(User receiver, String subject, String view, Map model = [:]) {
        model.user = receiver
        model.appName = grailsApplication.config.getProperty('appName')
        model.emailFooter = grailsApplication.config.getProperty('emailFooter')
        String text = groovyPageRenderer.render(view: view, model: model)
        sendNow(receiver, subject, text)
        return true
    }

    def sendNow(User mailReceiver, String mailSubject, String mailText) {
        try {
            task {
                sendMail {
                    to mailReceiver?.username
                    subject mailSubject
                    body mailText
                    from grailsApplication.config.getProperty('grails.mail.from')
                }
            }
        } catch (e) {
            log.error e.message
        }
    }
}