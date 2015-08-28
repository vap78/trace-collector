package personal.vap78.logging.diagtool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class Session {

  private static final int THIRTY_MINUTES = 1000*60*30;

  protected String id;
  protected long creationTime;
  protected Properties properties;
  protected Map<String, LogFileDescriptor> logs;
  protected TraceCollectionInfo currentTraceCollectionInfo;
  protected List<String> collectedTraceFiles; 
  
  public Session(String id, Properties properties) {
    this.properties = properties;
    this.creationTime = System.currentTimeMillis();
    this.id = id;
    this.collectedTraceFiles = new ArrayList<String>();
  }

  public Properties getProperties() {
    return properties;
  }

  public Map<String, LogFileDescriptor> getLogs() {
    return logs;
  }

  public void setLogs(Map<String, LogFileDescriptor> logs) {
    this.logs = logs;
  }
  
  public String getId() {
    return id;
  }
  
  public boolean isValid() {
    return (System.currentTimeMillis() - creationTime) < THIRTY_MINUTES;
  }
  
  public TraceCollectionInfo getCurrentTracesCollectionInfo() {
    return currentTraceCollectionInfo;
  }

  public void setCurrentTraceCollectionInfo(TraceCollectionInfo currentTraceCollectionInfo) {
    this.currentTraceCollectionInfo = currentTraceCollectionInfo;
  }
  
  public List<String> getCollectedTraceFiles() {
    return collectedTraceFiles;
  }
  
  public void addCollectedTraceFile(String name) {
    collectedTraceFiles.add(name);
  }

  public abstract String getHost();

  public abstract String getAccount();

  public abstract String getApplication();
  
  public abstract String getUser();

  public abstract String getPassword();

  public abstract String getProxy();

  public abstract String getProxyUser();

  public abstract String getProxyPassword();
  
  public abstract boolean isTrialAccount();
  
}
