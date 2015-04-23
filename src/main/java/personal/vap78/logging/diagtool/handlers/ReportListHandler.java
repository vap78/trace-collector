package personal.vap78.logging.diagtool.handlers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.HtmlReportGenerator;

public class ReportListHandler extends AbstractHttpHandler {

  private static final String FILE_EXTENSION = ".html";
  private static final String TEMPLATE = readTemplate("reports.html"); 
      
  class ReportFileDescriptor {
    private String fullName;
    private String account;
    private String application;
    private Date date;
  }

  @Override
  public void service(Request request, Response response) throws Exception {
    super.service(request, response);
    
    Map<Long, ReportFileDescriptor> reports = new TreeMap<>();
    
    File[] reportFiles = new File(HtmlReportGenerator.REPORTS_FOLDER).listFiles();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
    for (File report : reportFiles) {
      String fileName = report.getName();
      if (report.isFile() && fileName.endsWith(FILE_EXTENSION)) {
        fileName = fileName.substring(0, fileName.length() - FILE_EXTENSION.length());
        String[] parts = report.getName().split("_");
        ReportFileDescriptor descriptor = new ReportFileDescriptor();
        descriptor.fullName = fileName;
        descriptor.account = parts[0];
        descriptor.application = parts[1];
        descriptor.date = format.parse(parts[2]);
        reports.put(descriptor.date.getTime(), descriptor);
      }
    }
    
    StringBuilder text = new StringBuilder();
    
    Date lastDate = null;
    for (Long time : reports.keySet()) {
      if (lastDate == null || isOnDifferentDay(lastDate, new Date(time))) {
        if (lastDate != null) {
          text.append("<hr/>");
        }
        text.append("Trace recodrings taken on <bold>");
        text.append(outputFormat.format(time));
        text.append("</bold>\n<br/><hr/>");
      }
      text.append("<a href=\"/getLog?name=");
      text.append(reports.get(time).fullName);
      text.append(".html\">");
      text.append(reports.get(time).fullName);
      text.append("</a><br/>\n");
      lastDate = new Date(time);
    }
    
    String responseStr = TEMPLATE.replace("${reports}", text.toString());
    response.getWriter().write(responseStr);
    response.getWriter().flush();
    
  }

  private boolean isOnDifferentDay(Date date1, Date date2) {
    if (date1 == null || date2 == null) {
      return true;
    }
    
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date1);

    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date2);

    return cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR) || 
        cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH) ||
        cal1.get(Calendar.DAY_OF_MONTH) != cal2.get(Calendar.DAY_OF_MONTH);
  }
}
