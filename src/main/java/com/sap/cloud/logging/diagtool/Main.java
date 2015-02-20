package com.sap.cloud.logging.diagtool;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class Main {

  public static final String DEFAULT_PROPERTIES_FILE_NAME = "default.properties";

  private static Properties props;

  public static void main(String[] args) throws Exception {
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
    
    synchronized (Main.class) {
      Main.class.wait();
    }
//    BufferedReader reader = executeConsoleTool(executablePath, executableRoot, SET_LOG_LEVEL_COMMAND, props);
//    readFullyToSysOut(reader);
//
//    waitForInput();
//    
//    reader = executeConsoleTool(executablePath, executableRoot, LIST_LOGS_COMMAND, props);
//    Map<String, LogFileDescriptor> files = parseListLogsOutput(reader);
//    
//    if (files.isEmpty()) {
//      System.out.println("[ERROR] log list returned no files");
//      return;
//    }
//
//    for (LogFileDescriptor lfd : files.values()) {
//      props.put(FILE_PARAM, lfd.name);
//      reader = executeConsoleTool(executablePath, executableRoot, GET_LOG, props);
//      readFullyToSysOut(reader);
//    }
//    
//    props.setProperty(LEVEL_PARAM, "ERROR");
//    
//    HtmlReportGenerator generator = new HtmlReportGenerator(props, files, 0, 0);
//    generator.generateHtmlReport();
  }

  public static Properties getProps() {
    return props;
  }

//  private static void waitForInput() {
//    System.out.print("Please type 'ready' and press Enter when you have finished with your scenario: ");
//    String line = null;
//        
//    while (true) {
//      line = System.console().readLine();
//      if ("ready".equals(line)) {
//        break;
//      }
//    }
//  }


}
