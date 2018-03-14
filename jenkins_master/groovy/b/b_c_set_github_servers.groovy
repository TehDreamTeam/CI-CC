import groovy.json.*;
import groovy.transform.*;

import jenkins.model.*;

import org.jenkinsci.plugins.github.*;
import org.jenkinsci.plugins.github.config.*;

setConfiguredGithubServers();

def setConfiguredGithubServers() {
  def servers = getConfiguredServers().collect {
    def server = new GitHubServerConfig(it.credential_id);
    server.setName(it.name);
    server.setManageHooks(it.manage_hooks);

    if (it.api_url != null) {
      server.setCustomApiUrl(true);
      server.setApiUrl(it.api_url);
    }

    return server;
  } as List<GitHubServerConfig>;

  GitHubPlugin.configuration().setConfigs(servers);
}

def getConfiguredServers() {
  return getJsonConfiguration().github.servers;
}

@Field def configuration;
def getJsonConfiguration() {
  if (configuration == null) {
    def input = new File(System.getenv()["CONFIGURATION_FILE"]);
    configuration = new JsonSlurper().parseText(input.text);
  }

  return configuration;
}
