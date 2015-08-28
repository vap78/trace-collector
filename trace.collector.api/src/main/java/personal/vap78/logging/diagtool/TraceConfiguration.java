package personal.vap78.logging.diagtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraceConfiguration {

  private static Map<String, TraceConfiguration> configurations = new HashMap<>();
  private String name;
  private List<String> loggers;
  
  public static synchronized void addFromDirectory(File directory) throws IOException {
    File[] files = directory.listFiles();
    configurations.clear();
    
    for (File file : files) {
      addFromFile(file);
    }
  }
  
  public synchronized static void addFromFile(File file) throws IOException {
    String name = file.getName();
    if (name.startsWith("locations.")) {
      
      int firstDot = name.indexOf(".");
      int lastDot = name.lastIndexOf(".");
      
      if (firstDot + 1 < lastDot) {
        String configName = name.substring(firstDot+1, lastDot);
        
        addFromInputStream(configName, new FileInputStream(file));
      }
    }
  }

  public synchronized static void addFromInputStream(String configName, InputStream input) throws IOException {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
      
      List<String> locations = new ArrayList<>();
      String line = null;
      
      while ((line = reader.readLine()) != null) {
        locations.add(line);
      }
      
      TraceConfiguration cfg = new TraceConfiguration(configName, locations);
      configurations.put(configName, cfg);
    } finally {
      if (reader != null) {
        reader.close();
      }
    }

  }

  public static synchronized void addConfiguration(TraceConfiguration cfg) {
    configurations.put(cfg.getName(), cfg);
  }
  
  public static synchronized Collection<String> getAllConfigurationNames() {
    return configurations.keySet();
  }
  
  public static synchronized TraceConfiguration getByName(String name) {
    return configurations.get(name);
  }

  
  public TraceConfiguration(String name, List<String> locations) {
    this.name = name;
    this.loggers = locations;
  }

  public List<String> getLoggers() {
    return loggers;
  }
  
  public void setLoggers(List<String> locations) {
    this.loggers = locations;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getLoggersAsString() {
    StringBuilder toReturn = new StringBuilder();
    for (String s : loggers) {
      toReturn.append(s);
      toReturn.append("\n");
    }
    
    return toReturn.toString();
  }
}
