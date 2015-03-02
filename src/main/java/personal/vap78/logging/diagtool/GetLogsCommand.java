package personal.vap78.logging.diagtool;

import java.util.Properties;

public class GetLogsCommand extends AbstractLogCommand {

  public static final String GET_LOG = "get-log";
  public static final String LOGS_DOWNLOAD_DIRECTORY = "temp_logs_download";
  public static final String FILE_PARAM = "file";

  public GetLogsCommand(Session session) {
    super(session);
  }

  @Override
  protected String getCommandName() {
    return GET_LOG;
  }

  @Override
  protected void addCommandSpecificParameters() {
    String logFileName = session.getProperties().getProperty(FILE_PARAM);
    System.out.println("Downloading file " + logFileName);
    
    command.add("--file");
    command.add(logFileName);
    command.add("--directory");
    command.add(LOGS_DOWNLOAD_DIRECTORY);
    command.add("--overwrite");    
  }

}
