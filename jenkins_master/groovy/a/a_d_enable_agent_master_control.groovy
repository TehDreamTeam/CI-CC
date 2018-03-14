import jenkins.model.*;
import jenkins.security.s2m.*;

disableMasterKillSwitch();

def disableMasterKillSwitch() {
  def instance = Jenkins.getInstance();
  def injector = instance.getInjector();
  def rule = injector.getInstance(AdminWhitelistRule.class);

  rule.setMasterKillSwitch(false);
  instance.save();
}
