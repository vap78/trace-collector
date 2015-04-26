package personal.vap78.logging.diagtool;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import personal.vap78.logging.diagtool.automatic.Action;
import personal.vap78.logging.diagtool.http.LocalServer;

public class Main {

  public static final String DEFAULT_PROPERTIES_FILE_NAME = "default.properties";

  private static Properties props;

  public static void main(String[] args) throws Exception {
    AutomaticSessionProperties autoSession = parseArguments(args);

    if (autoSession != null) {
      
    } else {
      LocalServer server = new LocalServer();
      
      server.start();
      Desktop.getDesktop().browse(new URI("http://localhost:4242/main"));
      
      synchronized (Main.class) {
        Main.class.wait();
      }
    }
  }

  private static AutomaticSessionProperties parseArguments(String[] args) throws FileNotFoundException, IOException {
    if (args.length == 0) {
      return null; // no arguments - display the UI
    }
    if (args.length % 2 != 0) {
      throw new RuntimeException("Incorrect number of command line arguments");
    }
    AutomaticSessionProperties autoSessionProps = new AutomaticSessionProperties();
    if (args != null) {
      for (int i = 0; i < args.length; i += 2) {
        if ("--properties".equals(args[i])) {
          String propsFilePath = args[i + 1];
          autoSessionProps.hcpProperties = new Properties();
          autoSessionProps.hcpProperties.load(new FileInputStream(propsFilePath));
        } else if ("--action".equals(args[i])) {
          autoSessionProps.action = Action.getByName(args[i+1]);
        } else if ("--scenario".equals(args[i])) {
          TraceConfiguration.addFromDirectory(new File("."));
        }
      }
    }
    return null;
  }

  private static  class AutomaticSessionProperties {
    Properties hcpProperties;
    Action action;
    TraceConfiguration traceConfig;
  }
}
