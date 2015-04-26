package personal.vap78.logging.diagtool.impl.console.cmd;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import personal.vap78.logging.diagtool.http.Session;
import personal.vap78.logging.diagtool.impl.console.CommandExecutionException;

public class GetLogsCommand extends AbstractLogCommand {

  public static final String GET_LOG = "get-log";
  public static final String LOGS_DOWNLOAD_DIRECTORY = "temp_logs_download";
  public static final String FILE_PARAM = "file";
  private String fileToDownload;

  public GetLogsCommand(Session session, String fileToDownload) {
    super(session);
    this.fileToDownload = fileToDownload;
  }

  @Override
  protected String getCommandName() {
    return GET_LOG;
  }
  
  @Override
  public void executeConsoleTool() throws IOException, CommandExecutionException {
    File logStore = getLogStoreDirectory();
    if (logStore.exists()) {
      FileUtils.deleteDirectory(getLogStoreDirectory());
    }
    logStore.mkdir();
    
    super.executeConsoleTool();
  }
  
  @Override
  public boolean isExecutionSuccessful() {
    File logFile = new File(LOGS_DOWNLOAD_DIRECTORY + File.separatorChar + fileToDownload);
    if (!logFile.exists()) {
      System.out.println("File " + LOGS_DOWNLOAD_DIRECTORY + File.separatorChar + fileToDownload + " does not exist");
      return false;
    }
    return consoleOutput != null && consoleOutput.indexOf("[get-log] operation is successful.") != -1;
  }

  @Override
  protected void addCommandSpecificParameters() {
    System.out.println("Downloading file " + fileToDownload);
    
    command.add("--file");
    command.add(fileToDownload);
    command.add("--directory");
    command.add(LOGS_DOWNLOAD_DIRECTORY);
    command.add("--overwrite");    
  }
  
  public File getLogStoreDirectory() {
    return new File("./" + LOGS_DOWNLOAD_DIRECTORY);
  }
  
  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    props.setProperty(HOST_PARAM, "hana.ondemand.com");
    props.setProperty(USER_PARAM, "i030734");
    props.setProperty(APPLICATION_PARAM, "demoapp");
    props.setProperty(ACCOUNT_PARAM, "simbg");
    props.setProperty(SDK_PATH_PARAM, "/Users/vap78/develop/neo-java-web-sdk-2.16.5.1");
    Session session = new Session("test", props );
    GetLogsCommand glc = new GetLogsCommand(session, "ljs_trace_1cd8fd3_2015-04-11.log");
    
    glc.executeConsoleTool();
    glc.printConsoleToSystemOut();
    System.out.println(glc.isExecutionSuccessful());
  }

}
