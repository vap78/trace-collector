package personal.vap78.logging.diagtool.http.handlers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import personal.vap78.logging.diagtool.HtmlReportGenerator;

public class DownloadLogsHandler extends AbstractHttpHandler {

  
  private static final String LOG_FILE_NAME = "name";

  @Override
  public void service(Request request, Response response) throws Exception {
    super.service(request, response);
    
    String fileName = request.getParameter(LOG_FILE_NAME);

    File root = new File (HtmlReportGenerator.REPORTS_FOLDER);
    File logFile = new File(root, fileName);
    if (!logFile.getAbsolutePath().startsWith(root.getAbsolutePath())) {
      System.out.println("The request log file path is not under the reports folder");
      response.sendError(400);
      return;
    }

    writeFileToOutput(response, logFile);
    response.getOutputStream().flush();
  }

  private void writeFileToOutput(Response response, File logFile) throws IOException, Exception {
    if (logFile.exists() && logFile.isFile()) {
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
    } else {
      response.setStatus(HttpStatus.BAD_REQUEST_400);
      response.getWriter().write("Log file with the given name does not exist");
      response.flush();
    }
  }

}
