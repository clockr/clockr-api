package clockr.api

class UrlMappings {

    static mappings = {
        group "/api", {
            post "/oauth/access_token"(controller: 'restOauth', action: 'accessToken')
    
            group "/user-management", {
                get "/" (controller: 'userManagement', action: 'list')
                get "/$id" (controller: 'userManagement', action: 'read')
                post "/" (controller: 'userManagement', action: 'create')
                put "/$id" (controller: 'userManagement', action: 'update')
                delete "/$id" (controller: 'userManagement', action: 'delete')
    
                post "/$userId/contract" (controller: 'userManagement', action: 'createContract')
                put "/$userId/contract/$id" (controller: 'userManagement', action: 'updateContract')
                delete "/$userId/contract/$id" (controller: 'userManagement', action: 'deleteContract')
            }
    
            group "/user", {
                post "/$userId/working-time" (controller: 'workingTime', action: 'create')
                put "/$userId/working-time/$id" (controller: 'workingTime', action: 'update')
                delete "/$userId/working-time/$id" (controller: 'workingTime', action: 'delete')
    
                post "/$userId/day-item" (controller: 'dayItem', action: 'create')
                delete "/$userId/day-item/$id" (controller: 'dayItem', action: 'delete')
    
                post "/$userId/manual-entry" (controller: 'manualEntry', action: 'create')
                put "/$userId/manual-entry/$id" (controller: 'manualEntry', action: 'update')
                delete "/$userId/manual-entry/$id" (controller: 'manualEntry', action: 'delete')
    
                get "/$id/month/$year/$month" (controller: 'user', action: 'getMonth')
                get "/$id/year/$year" (controller: 'user', action: 'getYear')
    
                post "/forgot-password/$username" (controller: 'user', action: 'forgotPassword')
                post "/set-password" (controller: 'user', action: 'setPassword')
            }
        }


        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
