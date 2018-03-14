import groovy.json.*;
import groovy.transform.*;

import jenkins.model.*;
import jenkins.branch.*
import jenkins.plugins.git.*
import jenkins.scm.api.trait.*;
import jenkins.scm.api.mixin.*;
import hudson.util.*;

import org.jenkinsci.plugins.workflow.multibranch.*
import org.jenkinsci.plugins.github_branch_source.*;
import com.cloudbees.hudson.plugins.folder.*

createConfiguredJobs();

def createConfiguredJobs() {
  getConfiguredJobs().each {
    addJob(it);
  }
}

def addJob(configuration) {
  if (projectExists(configuration)) {
    return;
  }

  def job = createMultiBranchPipelineProject(configuration);
  addGitSource(job, configuration);
}

def addGitSource(job, configuration) {
  def source = new BranchSource(getGithubSource(job, configuration));

  PersistedList sources = job.getSourcesList();
  sources.clear();
  sources.add(source);
}

def getGithubSource(job, configuration) {
  def source = new GitHubSCMSource(
    configuration.owner, // Repository owner
    configuration.repository // Repository name
  );

  source.setCredentialsId(configuration.credentials_id);
  source.setTraits([
    new BranchDiscoveryTrait(true, false),
    new OriginPullRequestDiscoveryTrait(EnumSet.of(ChangeRequestCheckoutStrategy.MERGE))
  ] as List);

  return source;
}

def createMultiBranchPipelineProject(configuration) {
  def instance = Jenkins.getInstance();
  def name = configuration.repository;

  return instance.createProject(WorkflowMultiBranchProject.class, name);
}

def projectExists(configuration) {
  def instance = Jenkins.getInstance();
  def name = configuration.repository;

  def item = instance.getItem(name);
  return item != null;
}

def getConfiguredJobs() {
  return getJsonConfiguration().jenkins.jobs;
}

@Field def configuration;
def getJsonConfiguration() {
  if (configuration == null) {
    def input = new File(System.getenv()["CONFIGURATION_FILE"]);
    configuration = new JsonSlurper().parseText(input.text);
  }

  return configuration;
}
