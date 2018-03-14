import groovy.json.*;
import groovy.transform.*;

import jenkins.model.*;
import hudson.plugins.sonar.*;
import hudson.plugins.sonar.model.*;
import hudson.plugins.sonar.utils.*;

setSonarServerInstallations();

def setSonarServerInstallations() {
  def configuration = getGlobalSonarConfiguration();
  def installations = getSonarInstallationsSettings();

  configuration.setInstallations((SonarInstallation[]) installations);
  configuration.save();
  configuration.load();
}

def getSonarInstallationsSettings(configuration) {
  return getSonarServers().collect {
    return new SonarInstallation(
        it.server_name, // Name
        it.server_location, // Server URL
        SQServerVersions.SQ_5_3_OR_HIGHER, // Server version
        it.server_token, // Server authentication token
        null, // Database url
        null, // Database login
        null, // Database password
        null, // mojoVersion
        null, // additionalProperties
        new TriggersConfig(), // triggers
        null, // Sonar login
        null, // Sonar password
        null // Additional Analysis Properties
    );
  }
}

def getGlobalSonarConfiguration() {
  return Jenkins.getInstance().getDescriptorByType(SonarGlobalConfiguration.class);
}

def getSonarServers() {
  return getJsonConfiguration().sonar;
}

@Field def configuration;
def getJsonConfiguration() {
  if (configuration == null) {
    def input = new File(System.getenv()["CONFIGURATION_FILE"]);
    configuration = new JsonSlurper().parseText(input.text);
  }

  return configuration;
}
