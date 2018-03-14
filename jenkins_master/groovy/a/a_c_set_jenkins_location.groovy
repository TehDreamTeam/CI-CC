import groovy.json.*;
import groovy.transform.*;

import jenkins.model.*;

setJenkinsLocation();

def setJenkinsLocation() {
  def configuration = JenkinsLocationConfiguration.get();

  configuration.setUrl(getConfiguredJenkinsLocationUrl());
  configuration.save();
}

def getConfiguredJenkinsLocationUrl() {
  return getJsonConfiguration().jenkins.location;
}

@Field def configuration;
def getJsonConfiguration() {
  if (configuration == null) {
    def input = new File(System.getenv()["CONFIGURATION_FILE"]);
    configuration = new JsonSlurper().parseText(input.text);
  }

  return configuration;
}
