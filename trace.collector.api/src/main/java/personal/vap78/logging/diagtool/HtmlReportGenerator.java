package personal.vap78.logging.diagtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;

public class HtmlReportGenerator {

  private static final int MAX_LINE_LENGTH = 80;
  public static final String REPORTS_FOLDER = "reports";
  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
  private static final SimpleDateFormat SDF_WITH_TIME_ZONE = new SimpleDateFormat("yyyy MM dd HH:mm:ss z");
  private String logFile;
  private long endTime;
  private long startTime;
  private boolean isTrialAccount;
  private Session session;

  public HtmlReportGenerator(Session session, String logFile, long startTime, long endTime) {
    this.session = session;
    this.logFile = logFile;
    this.startTime = startTime;
    this.endTime = endTime;
    isTrialAccount = session.isTrialAccount();
  }

  public File generateHtmlReport() throws IOException, ParseException {
    
    String ljsReport = session.getAccount() + "_" + session.getApplication() + "_" + session.getCurrentTracesCollectionInfo().getId() + ".html";
    PrintStream ljsOutput = null;

    BufferedReader templateReader = null;
    File reportsRootDir = new File(REPORTS_FOLDER);
    if (!reportsRootDir.exists()) {
      reportsRootDir.mkdir();
    }
    BufferedReader ljsLogReader = null;
    try {
      File reportFile = new File(reportsRootDir, ljsReport);
      ljsOutput = new PrintStream(reportFile);
      templateReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("reportTemplate.html")));

      ljsLogReader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), Charset.forName("UTF-8")));
      parseHeader(ljsLogReader);

      writeHeader(ljsOutput, templateReader);

      LjsLogEntry entry = null;
      boolean tableHeaderWritten = false;
      while ((entry = getNextLJSLogEntry(ljsLogReader)) != null) {
        if (entry.time < startTime || entry.time > endTime) {
          continue;
        }
        if (!tableHeaderWritten) {
          writeTableHeader(ljsOutput, entry);
          tableHeaderWritten = true;
        }
        ljsOutput.println(entry.toHtmlRow());
      }

      writeFooter(ljsOutput, templateReader);
      return reportFile;
    } finally {
      IOUtils.closeQuietly(templateReader);
      IOUtils.closeQuietly(ljsLogReader);
      IOUtils.closeQuietly(ljsOutput);
    }
  }

  private void writeTableHeader(PrintStream ljsOutput, LjsLogEntry firstEntry) {
    ljsOutput.print("<tr>");
    ljsOutput.print("<th>");
    ljsOutput.print("Time");
    ljsOutput.print("</th>");

    ljsOutput.print("<th class=\"severity\">");
    ljsOutput.print("Severity");
    ljsOutput.print("</th>");

    ljsOutput.print("<th class=\"traceLocation\">");
    ljsOutput.print("Trace Location");
    ljsOutput.print("</th>");

    ljsOutput.print("<th class=\"ACH\">");
    ljsOutput.print("ACH");
    ljsOutput.print("</th>");

    ljsOutput.print("<th class=\"user\">");
    ljsOutput.print("User");
    ljsOutput.print("</th>");

    ljsOutput.print("<th class=\"thread\">");
    ljsOutput.print("Thread");
    ljsOutput.print("</th>");

    ljsOutput.print("<th class=\"bundleName\">");
    ljsOutput.print("Bundle Name");
    ljsOutput.print("</th>");

    ljsOutput.print("<th class=\"account\">");
    ljsOutput.print("Account");
    ljsOutput.print("</th>");

    ljsOutput.print("<th class=\"application\">");
    ljsOutput.print("Application");
    ljsOutput.print("</th>");

    ljsOutput.print("<th class=\"component\">");
    ljsOutput.print("Component");
    ljsOutput.print("</th>");

    if (!isTrialAccount) {
      ljsOutput.print("<th class=\"tenantAlias\">");
      ljsOutput.print("Tenant Alias");
      ljsOutput.print("</th>");
    }

    ljsOutput.print("<th class=\"text\">");
    ljsOutput.print("Text");
    ljsOutput.print("</th>");

    ljsOutput.println("</tr>");
  }

  private void writeFooter(PrintStream ljsOutput, BufferedReader templateReader) throws IOException {
    String line = null;
    while ((line = templateReader.readLine()) != null) {
      ljsOutput.println(line);
    }
  }

  private void writeHeader(PrintStream ljsOutput, BufferedReader templateReader) throws IOException {
    boolean headerWritten = false;
    while (!headerWritten) {
      String line = templateReader.readLine();
      if ("${header}".equals(line)) {
        ljsOutput.println("Collected ljs traces for:<br/>");
        ljsOutput.println("Host: <b>" + session.getHost() + "</b><br/>");
        ljsOutput.println("Account: <b>" + session.getAccount() + "</b><br/>");
        ljsOutput.println("Application: <b>" + session.getApplication() + "</b><br/>");
        ljsOutput.println("File: <b>" + logFile + "</b><br/>");
        ljsOutput.println("Start time: <b>" + SDF_WITH_TIME_ZONE.format(startTime) + "</b><br/>");
        ljsOutput.println("End time: <b>" + SDF_WITH_TIME_ZONE.format(endTime) + "</b><br/>");
        ljsOutput.println("<hr>");
      } else if ("${table}".equals(line)) {
        headerWritten = true;
      } else if (line == null) {
        throw new IOException("reached end of the template file before reaching the end of header");
      } else {
        ljsOutput.println(line);
      }
    }
  }

  private HeaderDescriptor parseHeader(BufferedReader ljsLogReader) throws IOException {
    ljsLogReader.mark(2048);
    HeaderDescriptor hd = new HeaderDescriptor();

    while (true) {
      String line = ljsLogReader.readLine();
      if (line.startsWith("HEADER_END")) {
        break;
      }
      // else if (line.startsWith("RECORD_SEPARATOR")) {
      // String recSeparatorStr = line.substring(line.indexOf(":") + 1).trim();
      // char recSeparatorChar = (char) Integer.parseInt(recSeparatorStr);
      // hd.recordSeparator = Character.toString(recSeparatorChar);
      // } else if (line.startsWith("COLUMN_SEPARATOR")) {
      // String colSeparatorStr = line.substring(line.indexOf(":") + 1).trim();
      // char colSeparatorChar = (char) Integer.parseInt(colSeparatorStr);
      // hd.columnSeparator = Character.toString(colSeparatorChar);
      // }
      else if (isLogLine(line)) {
        ljsLogReader.reset();
        return hd;
      }

      ljsLogReader.mark(2048);
    }

    return hd;
  }

  private boolean isLogLine(String line) {
    String[] records = line.split("#");
    if (records.length > 1) {
      if (records[records.length - 1].endsWith("|")) {
        try {
          SDF.parse(records[0]);
          return true;
        } catch (ParseException e) {
          return false;
        }
      }
    }
    return false;
  }

  private LjsLogEntry getNextLJSLogEntry(BufferedReader ljsLogReader) throws IOException, ParseException {
    String line = ljsLogReader.readLine();

    if (line == null) {
      return null;
    }
    while (!line.endsWith("|") || line.endsWith("\\|")) {
      String tmpLine = ljsLogReader.readLine();
      if (tmpLine == null) {
        System.out.println("[Error] wrong line: " + tmpLine);
        return null; // incomplete line at the end of the file - most probably
                     // corrupted log file
      }
      line += "\n" + tmpLine;
//      System.out.println("Read line: " + line);
    }

    line = line.trim();
    String[] parsedLine = line.split("(?<!\\\\)#");
    LjsLogEntry entry = new LjsLogEntry();
    entry.time = SDF_WITH_TIME_ZONE.parse(parsedLine[0] + " " + parsedLine[1] + "00").getTime();
    entry.timeZone = TimeZone.getTimeZone(parsedLine[1] + "00");
    entry.severity = parsedLine[2];
    entry.logger = parsedLine[3];
    entry.ACH = parsedLine[4];
    entry.user = parsedLine[5];
    entry.thread = parsedLine[6];
    entry.bundleName = parsedLine[7];
    entry.account = parsedLine[8];
    entry.application = parsedLine[9];
    entry.component = parsedLine[10];
    if (isTrialAccount) {
      entry.text = parsedLine[11];
      appendNext(parsedLine, entry, 11);
    } else {
      entry.tenantAlias = parsedLine[11];
      entry.text = parsedLine[12];
      appendNext(parsedLine, entry, 12);
    }
    entry.text = htmlEncode(entry.text);
    String[] entryLines = entry.text.split("\n");
    for (int i = 0; i < entryLines.length; i++) {
      entryLines[i] = addLineBreaks(entryLines[i]);
    }
    StringBuilder builder = new StringBuilder();
    for (String entryLine : entryLines) {
      builder.append(entryLine);
      builder.append("\n");
    }
    entry.text = builder.toString();
    return entry;
  }

  private static String addLineBreaks(String text) {
    int start = 0;
    int end = MAX_LINE_LENGTH;
    StringBuilder builder = new StringBuilder();
    while (start < text.length()) {
      if (end >= text.length()) {
        end = text.length();
      }
      builder.append(text.substring(start, end));
      builder.append("\n");
      start += MAX_LINE_LENGTH;
      end += MAX_LINE_LENGTH;
    }
    return builder.toString();
  }
  
  private String htmlEncode(String text) {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
        out.append("&#");
        out.append((int) c);
        out.append(';');
      } else {
        out.append(c);
      }
    }

    return out.toString();
  }

  private void appendNext(String[] parsedLine, LjsLogEntry entry, int startIndex) {
    for (int i = startIndex + 1; i < parsedLine.length; i++) {
      entry.text += parsedLine[i];
    }
  }

//  public static void main(String[] args) throws Exception {
//    Properties props = new Properties();
//    props.load(new FileInputStream("C:/cloud/1.74.20/tools/deploy_oauth.properties"));
//    Session session = new Session("testid", props);
//    TraceCollectionInfo traceCollectionInfo = new TraceCollectionInfo();
//    traceCollectionInfo.setId("test");
//    session.setCurrentTraceCollectionInfo(traceCollectionInfo);
//    HtmlReportGenerator gen = new HtmlReportGenerator(session, "ljs_trace_1b440fa_2015-04-30.log", 0, Long.MAX_VALUE);
//
//    gen.generateHtmlReport();
//  }
//
  static class HeaderDescriptor {
    String columnSeparator;
    String recordSeparator;
    String escapeChar;

    HeaderDescriptor() {
      columnSeparator = "#";
      recordSeparator = "|";
      escapeChar = "\\";
    }
  }

  static class LjsLogEntry {
    long time;
    TimeZone timeZone;
    String severity;
    String logger;
    String ACH;
    String user;
    String thread;
    String bundleName;
    String account;
    String application;
    String component;
    String tenantAlias;
    String text;

    String toHtmlRow() {
      StringBuffer row = new StringBuffer();
      row.append("<tr");
      row.append(" class=\"");
      row.append(severity);
      row.append("\">");

      row.append("<td class=\"time\">");
      row.append(SDF_WITH_TIME_ZONE.format(new Date(time)));
      row.append("</td>");

      row.append("<td class=\"severity\">");
      row.append(severity);
      row.append("</td>");

      row.append("<td class=\"logger\">");
      row.append(convertTraceLocation(logger));
      row.append("</td>");

      row.append("<td class=\"ACH\">");
      row.append(ACH);
      row.append("</td>");

      row.append("<td class=\"user\">");
      row.append(user);
      row.append("</td>");

      row.append("<td class=\"thread\">");
      row.append(thread);
      row.append("</td>");

      row.append("<td class=\"bundleName\"");
      row.append(bundleName);
      row.append("</td>");

      row.append("<td class=\"account\">");
      row.append(account);
      row.append("</td>");

      row.append("<td class=\"application\">");
      row.append(application);
      row.append("</td>");

      row.append("<td class=\"component\">");
      row.append(component);
      row.append("</td>");

      if (tenantAlias != null) {
        row.append("<td class=\"tenantAlias\">");
        row.append(tenantAlias);
        row.append("</td>");
      }

      row.append("<td class=\"text\">");
      row.append("<pre>");
      row.append(text);
      row.append("</pre>");
      row.append("</td>");

      return row.toString();
    }

    private String convertTraceLocation(String tr) {
      StringBuilder builder = new StringBuilder();
      builder.append("<span style=\"cursor: pointer\" class=\"loggerLine\" onclick=\"switchLoggerName(this)\" ondblclick=\"switchAllLoggers()\" alt=\"");
      builder.append(tr);
      builder.append("\">");
      builder.append(shortenLoggerName(tr));
      builder.append("</span>");
      return builder.toString();
    }

    private String shortenLoggerName(String tr) {
      String toReturn = null;
      if (tr.length() > 30) {
        toReturn = tr.substring(0, 15) + "..." + tr.substring(tr.length() - 15);
        return toReturn;
      } else {
        return tr;
      }
      
    }
  }

}
