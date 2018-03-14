import groovy.json.*;
import groovy.transform.*;

import jenkins.model.*;
import hudson.util.*;

import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.impl.*

addConfiguredCredentials();

def addConfiguredCredentials() {
  removeExistingCredentials();

  def credentials = getConfiguredCredentials();
  credentials.each {
    def type = it.type;
    def scope = getScope(it.scope);

    if ("secretText".equalsIgnoreCase(type)) {
      addSecretTextCredentials(it, scope);
    } else if ("usernamePassword".equalsIgnoreCase(type)) {
      addUsernamePasswordCredentials(it, scope);
    }
  }
}

def addUsernamePasswordCredentials(credential, scope) {
  def store = getCredentialStore();
  def newCredential = new UsernamePasswordCredentialsImpl(
    scope,
    credential.id,
    credential.description,
    credential.username,
    credential.password
  );

  store.addCredentials(getGlobalDomain(), newCredential);
}

def addSecretTextCredentials(credential, scope) {
  def store = getCredentialStore();
  def newCredential = new StringCredentialsImpl(
    scope,
    credential.id,
    credential.description,
    Secret.fromString(credential.text)
  );

  store.addCredentials(getGlobalDomain(), newCredential);
}

def removeExistingCredentials() {
  def store = getCredentialStore();
  def domains = store.getDomains();

  domains.each { domain ->
    def credentials = store.getCredentials(domain);
    credentials.each { credential ->
      store.removeCredentials(domain, credential)
    }

    store.removeDomain(domain)
  }
}

def getScope(scopeString) {
  if ("global".equalsIgnoreCase(scopeString)) {
    return CredentialsScope.GLOBAL;
  } else {
    return CredentialsScope.SYSTEM;
  }
}

def getGlobalDomain() {
  return Domain.global();
}

def getCredentialDomain(domain ){
  return getCredentialStore().getCredentials(domain);
}

def getCredentialStore() {
  def instance = Jenkins.instance;

  return instance.getExtensionList("com.cloudbees.plugins.credentials.SystemCredentialsProvider")[0].getStore();
}

def getConfiguredCredentials() {
  return getJsonConfiguration().jenkins.credentials;
}

@Field def configuration;
def getJsonConfiguration() {
  if (configuration == null) {
    def input = new File(System.getenv()["CONFIGURATION_FILE"]);
    configuration = new JsonSlurper().parseText(input.text);
  }

  return configuration;
}
