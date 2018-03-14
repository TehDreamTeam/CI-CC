import jenkins.model.*;

setExecutorNumber();

def setExecutorNumber() {
  def instance = Jenkins.getInstance();
  instance.setNumExecutors(0);
  instance.save();
}
