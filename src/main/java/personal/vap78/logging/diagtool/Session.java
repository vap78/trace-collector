package personal.vap78.logging.diagtool;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Session {

  private static final int THIRTY_MINUTES = 1000*60*30;
  private static Map<String, Session> sessions = new HashMap<>();
  
  public static Session getById(String id) {
    return sessions.get(id);
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
  
  public Session(String id, Properties properties) {
    this.properties = properties;
    this.creationTime = System.currentTimeMillis();
    this.id = id;
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
}
