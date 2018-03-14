import groovy.json.*;
import groovy.transform.*;

import jenkins.model.*;
import hudson.model.*;
import hudson.security.*;

import org.jenkinsci.plugins.*;

enableGithubAuthentication();

def enableGithubAuthentication() {
  def realm = getGithubSecurityRealm();
  def strategy = getAuthorizationStrategy();

  def instance = Jenkins.getInstance();
  instance.setAuthorizationStrategy(strategy);
  instance.setSecurityRealm(realm);
  instance.save();
}

def getGithubSecurityRealm() {
  return new GithubSecurityRealm(
    getConfiguredGithubWebUri(), // Github web URI
    getConfiguredGithubApiUri(), // Github API URI
    getConfiguredGithubClientId(), // Client ID
    getConfiguredGithubClientSecret(), // Client secret
    getConfiguredGithubOAuthScopes() // OAuth scopes
  );
}

def getAuthorizationStrategy() {
  def strategy = new hudson.security.ProjectMatrixAuthorizationStrategy();

  addAnonymousPermissions(strategy);

  getConfiguredGithubNormalUsers().each {
    addDefaultPermissions(strategy, it);
  }

  getConfiguredGithubAdminUsers().each {
    addAllPermissions(strategy, it);
  }

  return strategy;
}

def addAnonymousPermissions(strategy) {
  def anonymous = "anonymous";

  // Overall permissions
  strategy.add(Jenkins.READ, anonymous);

  // Node permissions
  strategy.add(Computer.CONNECT, anonymous);
  strategy.add(Computer.CREATE, anonymous);
}

def addDefaultPermissions(strategy, sid) {
  // Overall permissions
  strategy.add(Jenkins.READ, sid);

  // Job permissions
  strategy.add(Item.BUILD, sid);
  strategy.add(Item.CANCEL, sid);
  strategy.add(Item.DISCOVER, sid);
  strategy.add(Item.READ, sid);
  strategy.add(Item.WORKSPACE, sid);

  // View PERMISSIONS
  strategy.add(View.READ, sid);
}

def addAllPermissions(strategy, sid) {
  Jenkins.PERMISSIONS.getAll().each {
    it.getPermissions().each {
      strategy.add(it, sid);
    };
  };
}

def getConfiguredGithubAdminUsers() {
    return getJsonConfiguration().github.authentication.users.admin;
}

def getConfiguredGithubNormalUsers() {
  return getJsonConfiguration().github.authentication.users.normal;
}

def getConfiguredGithubWebUri() {
  return getJsonConfiguration().github.authentication.web_uri;
}

def getConfiguredGithubApiUri() {
  return getJsonConfiguration().github.authentication.api_uri;
}

def getConfiguredGithubClientId() {
  return getJsonConfiguration().github.authentication.client_id;
}

def getConfiguredGithubClientSecret() {
  return getJsonConfiguration().github.authentication.client_secret;
}

def getConfiguredGithubOAuthScopes() {
  def scopes = getJsonConfiguration().github.authentication.oauth_scopes;
  if (scopes == null) {
    scopes = "read:org,user:email";
  }

  return scopes;
}

@Field def configuration;
def getJsonConfiguration() {
  if (configuration == null) {
    def input = new File(System.getenv()["CONFIGURATION_FILE"]);
    configuration = new JsonSlurper().parseText(input.text);
  }

  return configuration;
}
