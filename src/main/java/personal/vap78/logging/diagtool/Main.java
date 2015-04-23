package personal.vap78.logging.diagtool;

import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.Properties;

public class Main {

  public static final String DEFAULT_PROPERTIES_FILE_NAME = "default.properties";

  private static Properties props;

  public static void main(String[] args) throws Exception {
    System.out.println(new File(".").getAbsolutePath());
    String propsFilePath;
    if (args != null) {
      for (int i = 0; i < args.length; i += 2) {
        if ("--properties".equals(args[i])) {
          propsFilePath = args[i + 1];
        }
      }
    }

    propsFilePath = DEFAULT_PROPERTIES_FILE_NAME;
    props = new Properties();
    File propsFile = new File(propsFilePath);
    if (propsFile.exists() && propsFile.isFile()) {
      props.load(new FileReader(propsFile));
    }
    
    LocalServer server = new LocalServer();
    
    server.start();
    Desktop.getDesktop().browse(new URI("http://localhost:4242/main"));
    
    synchronized (Main.class) {
      Main.class.wait();
    }
  }

}
