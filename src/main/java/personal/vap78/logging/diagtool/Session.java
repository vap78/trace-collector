package personal.vap78.logging.diagtool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import personal.vap78.logging.diagtool.handlers.TraceCollectionInfo;

public class Session {

  private static final int THIRTY_MINUTES = 1000*60*30;
  private static Map<String, Session> sessions = new HashMap<>();
  
  public static Session getById(String id) {
    return sessions.get(id);
  }
  
  public static void deleteSession(String id) {
    sessions.remove(id);
  }
  
  public static Session createSession(String id, Properties props) {
    Session session = new Session(id, props);
    sessions.put(id, session);
    return session;
  }
  
  private String id;
  private long creationTime;
  private Properties properties;
  private Map<String, LogFileDescriptor> logs;
  private TraceCollectionInfo currentTraceCollectionInfo;
  private List<String> collectedTraceFiles; 
  
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
  
  public String getHost() {
    return properties.getProperty(AbstractLogCommand.HOST_PARAM);
  }
  
  public String getAccount() {
    return properties.getProperty(AbstractLogCommand.ACCOUNT_PARAM);
  }
  
  public String getApplication() {
    return properties.getProperty(AbstractLogCommand.APPLICATION_PARAM);
  }
  
  public String getUser() {
    return properties.getProperty(AbstractLogCommand.USER_PARAM);
  }
  
  public String getPassword() {
    return properties.getProperty(AbstractLogCommand.PASSWORD_PARAM);
  }
  
  public String getSDKPath() {
    return properties.getProperty(AbstractLogCommand.SDK_PATH_PARAM, "");
  }
  
  public String getProxy() {
    return properties.getProperty(AbstractLogCommand.PROXY_PARAM, "");
  }
  
  public String getProxyUser() {
    return properties.getProperty(AbstractLogCommand.PROXY_USER_PARAM, "");
  }
  
  public String getProxyPassword() {
    return properties.getProperty(AbstractLogCommand.PROXY_PASSWORD_PARAM, "");
    
  }

  public String getLongName() {
    return getHost() + "_" + getAccount() + "_" + getApplication();
  }

  public TraceCollectionInfo getCurrentTracesCollectionInfo() {
    return currentTraceCollectionInfo;
  }

  public void setCurrentTracesId(TraceCollectionInfo info) {
    this.currentTraceCollectionInfo = info;
  }
  
  public List<String> getCollectedTraceFiles() {
    return collectedTraceFiles;
  }
  
  public void addCollectedTraceFile(String name) {
    collectedTraceFiles.add(name);
  }

}
