package personal.vap78.logging.diagtool;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

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

}
