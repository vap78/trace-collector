package personal.vap78.logging.diagtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraceConfiguration {

  private static Map<String, TraceConfiguration> configurations = new HashMap<>();
  
  public static synchronized void addFromDirectory(File directory) throws IOException {
    String[] files = directory.list();
    configurations.clear();
    
    for (String name : files) {
      if (name.startsWith("locations.")) {
        int firstDot = name.indexOf(".");
        int lastDot = name.lastIndexOf(".");
        
        if (firstDot + 1 < lastDot) {
          String configName = name.substring(firstDot+1, lastDot);
          List<String> locations = readFile(new File(directory, name));
          TraceConfiguration cfg = new TraceConfiguration(configName, locations);
          configurations.put(configName, cfg);
        }
      }
    }
  }
  
  private static List<String> readFile(File file) throws IOException {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      
      List<String> locations = new ArrayList<>();
      String line = null;
      
      while ((line = reader.readLine()) != null) {
        locations.add(line);
      }
    
      return locations;
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

  private String name;
  private List<String> locations;
  
  public TraceConfiguration(String name, List<String> locations) {
    this.name = name;
    this.locations = locations;
  }

  public List<String> getLocations() {
    return locations;
  }
  
  public void setLocations(List<String> locations) {
    this.locations = locations;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getLocationsAsString() {
    StringBuilder toReturn = new StringBuilder();
    for (String s : locations) {
      toReturn.append(s);
      toReturn.append("\n");
    }
    
    return toReturn.toString();
  }
}
