FROM gradle:4.5.1-jdk8-alpine

ENV JENKINS_MASTER_LOCATION http://145.93.61.236:8080/jenkins
ENV JENKINS_SWARM_PLUGIN_VERSION 3.9
ENV SONAR_SCANNER_VERSION 3.0.3.778

USER root
RUN apk update && apk add git

WORKDIR /opt
RUN wget https://sonarsource.bintray.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-${SONAR_SCANNER_VERSION}.zip && \
    unzip sonar-scanner-cli-${SONAR_SCANNER_VERSION}.zip && \
    rm -f sonar-scanner-cli-${SONAR_SCANNER_VERSION}.zip && \
    ln -s sonar-scanner-${SONAR_SCANNER_VERSION} sonarscanner && \
    echo -ne "- with SonarScanner ${SONAR_SCANNER_VERSION}\n" >> /root/.built

RUN wget https://repo.jenkins-ci.org/releases/org/jenkins-ci/plugins/swarm-client/${JENKINS_SWARM_PLUGIN_VERSION}/swarm-client-${JENKINS_SWARM_PLUGIN_VERSION}.jar
CMD ["sh", "-c", "java -jar swarm-client-${JENKINS_SWARM_PLUGIN_VERSION}.jar -master ${JENKINS_MASTER_LOCATION}"]
