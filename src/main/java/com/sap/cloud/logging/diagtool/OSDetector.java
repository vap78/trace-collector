package com.sap.cloud.logging.diagtool;

public class OSDetector {

  private static String OS_NAME = System.getProperty("os.name").toLowerCase();

  public static boolean isWindows() {
    return (OS_NAME.indexOf("win") >= 0);
  }

  public static boolean isMac() {
    return (OS_NAME.indexOf("mac") >= 0);
  }

  public static boolean isUnix() {
    return (OS_NAME.indexOf("nux") >= 0);
  }

}
