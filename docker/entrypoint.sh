#!/bin/bash
cd /app && j2 application.yml.j2 > application.yml && java -Xmx16384m -Dgrails.env=$GRAILS_ENV -jar ROOT.war