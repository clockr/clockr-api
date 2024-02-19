package clockr.api

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

    static enum UserGermanState {
        BE,
        BB,
        BW,
        BY,
        HE,
        MV,
        NW,
        RP,
        SL,
        SN,
        ST,
        TH,
        SH,
        HH,
        HB,
        NI
    }

    private static final long serialVersionUID = 1

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    String firstname
    String lastname

    UserGermanState germanState

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    static hasMany = [contracts: Contract, workingTimes: WorkingTime, dayItems: DayItem, manualEntries: ManualEntry]

    static constraints = {
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true, email: true
        firstname nullable: true
        lastname nullable: true
        germanState nullable: true
    }

    static mapping = {
	    password column: '`password`'
    }
}
