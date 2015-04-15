package personal.vap78.logging.diagtool.handlers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import personal.vap78.logging.diagtool.HtmlReportGenerator;
import personal.vap78.logging.diagtool.Session;

public class DownloadLogsHandler extends AbstractHttpHandler {

  
  private static final String LOG_FILE_NAME = "name";

  @Override
  public void service(Request request, Response response) throws Exception {
    super.service(request, response);
    
    Session session = getSession(request, response);
    if (session == null) {
      response.sendRedirect(DO_LOGIN_ALIAS);
      return;
    }
    
    String fileName = request.getParameter(LOG_FILE_NAME);
    if (!session.getCollectedTraceFiles().contains(fileName)) {
      System.out.println("The requested log is not in the list of downloaded log files");
      response.sendError(400);
      return;
    }
    File root = new File (HtmlReportGenerator.REPORTS_FOLDER);
    File logFile = new File(root, fileName);
    if (!logFile.getAbsolutePath().startsWith(root.getAbsolutePath())) {
      System.out.println("The request log file path is not under the reports folder");
      response.sendError(400);
      return;
    }
    if (logFile.exists() && logFile.isFile()) {
      writeToServletOutput(logFile, response);
    } else {
      response.setStatus(HttpStatus.BAD_REQUEST_400);
      response.getWriter().write("Log file with the given name does not exist");
      response.flush();
    }
    response.getOutputStream().flush();
  }

  private void writeToServletOutput(File logFile, Response response) throws Exception {
    BufferedInputStream input = null;
    byte[] buffer = new byte[1024];
    int count;
    OutputStream output = response.getOutputStream();
    try {
      input = new BufferedInputStream(new FileInputStream(logFile));
      while ((count = input.read(buffer, 0, buffer.length)) != -1) {
        output.write(buffer, 0, count);
      }
    } finally {
      IOUtils.closeQuietly(input);
    }
  }
}
