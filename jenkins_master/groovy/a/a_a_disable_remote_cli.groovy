import groovy.json.*;
import groovy.transform.*;

import jenkins.*;
import jenkins.model.*;
import hudson.model.*;

disableCli();

def disableCli() {
  if (isCliEnabled()) {
    return;
  }

  def instance = Jenkins.getInstance();

  instance.getDescriptor("jenkins.CLI").get().setEnabled(false);
  instance.save();

  disableCliOverTcp();
  disableCliOverWeb();
}

def disableCliOverTcp() {
  def protocols = AgentProtocol.all();
  protocols.each {
      if (it.name && it.name.contains("CLI")) {
          protocols.remove(it);
      }
  };
}

def disableCliOverWeb() {
  def instance = Jenkins.getInstance();

  removal(instance.getExtensionList(RootAction.class));
  removal(instance.actions);
}

def removal(actions) {
  actions.each {
      if (it.getClass().name.contains("CLIAction")) {
          actions.remove(it);
      }
  };
}

def isCliEnabled() {
  return getJsonConfiguration().remote_cli_enabled;
}

@Field def configuration;
def getJsonConfiguration() {
  if (configuration == null) {
    def input = new File(System.getenv()["CONFIGURATION_FILE"]);
    configuration = new JsonSlurper().parseText(input.text);
  }

  return configuration;
}
