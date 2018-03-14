import jenkins.model.*;

import hudson.security.csrf.*;

setDefaultCrumbIssuer();

def setDefaultCrumbIssuer() {
  def instance = Jenkins.getInstance();

  instance.setCrumbIssuer(getDefaultCrumbIssuer());
  instance.save();
}

def getDefaultCrumbIssuer(){
  return new DefaultCrumbIssuer(true);
}
