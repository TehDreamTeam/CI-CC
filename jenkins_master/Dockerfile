FROM jenkins/jenkins:2.110-alpine

ENV JENKINS_OPTS="--prefix=/"
ENV JENKINS_ARGS=$JENKINS_OPTS

# Removes setup wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

# Plugin settings
ENV CONFIGURATION_FILE="/var/jenkins_home/config.json"

# Switch user to jenkins user
USER jenkins

# Install plugins
ADD plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Runs custom scripts
ADD groovy/* /usr/share/jenkins/ref/init.groovy.d/

USER root
RUN chown -R jenkins:jenkins /var/jenkins_home

VOLUME /var/jenkins_home/
