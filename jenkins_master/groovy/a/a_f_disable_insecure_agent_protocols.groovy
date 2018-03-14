import jenkins.model.*;

setAgentProtocols();

def setAgentProtocols() {
  def instance = Jenkins.getInstance();

  instance.setAgentProtocols(getSecureAgentProtocols());
  instance.save();
}

def getSecureAgentProtocols() {
  return ['JNLP4-connect', 'Ping'] as Set<String>;
}
