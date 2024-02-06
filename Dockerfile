FROM ubuntu:22.04 as build

ENV TZ=Europe/Berlin
ENV GRAILS_VERSION=5.3.0
ENV JAVA_VERSION=11.0.22-librca
WORKDIR /build

COPY ./ .

# Install dependencies
RUN apt-get update && apt-get install -y bash locales curl zip unzip && locale-gen de_DE.UTF-8

# Set Timezone
RUN \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Install SDK
SHELL ["/bin/bash", "-c"]
RUN \
   curl -s "https://get.sdkman.io" | bash && \
   source "/root/.sdkman/bin/sdkman-init.sh" && \
   sdk install java $JAVA_VERSION && \
   sdk install grails $GRAILS_VERSION

# Build grails app
RUN \
    JAVA_HOME=/root/.sdkman/candidates/java/$JAVA_VERSION  /root/.sdkman/candidates/grails/$GRAILS_VERSION/bin/grails war \
    && rm /build/build/libs/*-plain.war


FROM bellsoft/liberica-openjdk-debian:11
WORKDIR /app

ENV GRAILS_ENV=prod

COPY --from=build /build/build/libs/*.war ./ROOT.war
COPY ./docker/application.yml.j2 ./application.yml.j2
COPY ./docker/entrypoint.sh /entrypoint.sh

RUN \
   apt update && apt-get -y install j2cli

ENTRYPOINT ["/bin/bash","/entrypoint.sh"]
