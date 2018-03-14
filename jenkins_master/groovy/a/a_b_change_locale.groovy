import groovy.json.*;
import groovy.transform.*;

import jenkins.model.*;

import net.sf.json.*;

changeConfiguredLocale();

def changeConfiguredLocale() {
  def settings = getLocaleSettings();
  def request= getLocaleRequest(settings);

  def plugin = getLocalePlugin();
  plugin.configure(request, settings);
  plugin.save();
  plugin.load();
}

def getLocalePlugin() {
  def wrapper = Jenkins.getInstance().pluginManager.getPlugin("locale");
  return wrapper.getPlugin();
}

def getLocaleSettings() {
  JSONObject settings = new JSONObject();
  settings.put("systemLocale", getConfiguredLocale());
  settings.put("ignoreAcceptLanguage", true);

  return settings;
}

def getLocaleRequest(settings) {
  def request = [
    getParameter: { name -> settings.get(name) }
  ] as org.kohsuke.stapler.StaplerRequest;
}

def getConfiguredLocale() {
  return getJsonConfiguration().jenkins.locale;
}

@Field def configuration;
def getJsonConfiguration() {
  if (configuration == null) {
    def input = new File(System.getenv()["CONFIGURATION_FILE"]);
    configuration = new JsonSlurper().parseText(input.text);
  }

  return configuration;
}
