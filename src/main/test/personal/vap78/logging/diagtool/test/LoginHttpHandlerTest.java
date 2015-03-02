package personal.vap78.logging.diagtool.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;
import org.glassfish.grizzly.http.Cookie;
import org.glassfish.grizzly.http.Method;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import personal.vap78.logging.diagtool.AbstractLogCommand;
import personal.vap78.logging.diagtool.CommandExecutionException;
import personal.vap78.logging.diagtool.ListLogFilesCommand;
import personal.vap78.logging.diagtool.LogFileDescriptor;
import personal.vap78.logging.diagtool.handlers.AbstractHttpHandler;
import personal.vap78.logging.diagtool.handlers.LoginHttpHandler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({File.class, FileInputStream.class, BufferedReader.class, 
                   LoginHttpHandler.class, ListLogFilesCommand.class, 
                   OutputStreamWriter.class, FileOutputStream.class})
public class LoginHttpHandlerTest extends AbstractHandlerTest {

  @Test
  public void testLoginNoPreviousSessions() throws Exception {
    LoginHttpHandler underTest = new LoginHttpHandler();
    
    RequestResponseHolder holder = mockGetRequestResponse();
    mockFileInputs(new String[] {});
    underTest.service(holder.mockRequest, holder.mockResponse);
    
    byte[] responseBytes = holder.mockOutput.toByteArray();
    String response = new String(responseBytes, "UTF-8");
    Assert.assertTrue(response, response.contains("Login</button>"));
    Assert.assertTrue(response, response.contains("<input type=\"text\" name=\"host\" value=\"\"/>"));
    Assert.assertTrue(response, response.contains("<td class=\"right\"><input type=\"text\" name=\"account\" value=\"\"/>"));
    Assert.assertTrue(response, response.contains("<input type=\"text\" name=\"application\" value=\"\"/>"));
    Assert.assertTrue(response, response.contains("<input type=\"password\" name=\"password\"/>"));
  }
  
  @Test
  public void testLoginPageWithPreviousSessions() throws Exception {
    LoginHttpHandler underTest = new LoginHttpHandler();
    
    RequestResponseHolder holder = mockGetRequestResponse();
    mockFileInputs(new String[] {".", "..", "run.bat", "landscape1_account1_app1.session",
        "landscape2_account2_app2.session", ".session", "test.session.txt"});
    
    underTest.service(holder.mockRequest, holder.mockResponse);
    
    byte[] responseBytes = holder.mockOutput.toByteArray();
    String response = new String(responseBytes, "UTF-8");

    Assert.assertTrue(response, response.contains("Login</button>"));
    Assert.assertTrue(response, response.contains("<input type=\"text\" name=\"host\" value=\"landscape2\"/>"));
    Assert.assertTrue(response, response.contains("<td class=\"right\"><input type=\"text\" name=\"account\" value=\"account2\"/>"));
    Assert.assertTrue(response, response.contains("<input type=\"text\" name=\"application\" value=\"app2\"/>"));
    Assert.assertTrue(response, response.contains("<input type=\"password\" name=\"password\"/>"));
    Assert.assertTrue(response, response.contains("<option value=\"empty\"></option>"));
    Assert.assertTrue(response, response.contains("<option value=\"landscape1_account1_app1\">Host: landscape1 Account: account1 Application: app1</option>")); 
    Assert.assertTrue(response, response.contains("<option value=\"landscape2_account2_app2\" selected>Host: landscape2 Account: account2 Application: app2</option>"));
  }
  
  @Test
  public void testLoginFormSubmit() throws Exception {
    LoginHttpHandler underTest = new LoginHttpHandler();
    
    RequestResponseHolder holder = mockPostRequestResponse(false);
    
    underTest.service(holder.mockRequest, holder.mockResponse);
    Cookie cookie = holder.capturedCookie.getValue();
    Assert.assertNotNull(cookie);
    Assert.assertEquals(LoginHttpHandler.SESSION_ID, cookie.getName());
    
  }
  
  @Test
  public void testFailedLogin() throws Exception {
    LoginHttpHandler underTest = new LoginHttpHandler();
    
    RequestResponseHolder holder = mockPostRequestResponse(true);
    
    underTest.service(holder.mockRequest, holder.mockResponse);
    
    byte[] responseBytes = holder.mockOutput.toByteArray();
    String response = new String(responseBytes, "UTF-8");
    Assert.assertTrue(response, response.contains("<div id=\"errormessage\" class=\"shown\">Logon failed</div>"));
    Assert.assertTrue(response, response.contains("<input type=\"text\" name=\"sdkPath\" value=\"/home/sdkpath\"/>"));
    Assert.assertTrue(response, response.contains("<input type=\"text\" name=\"host\" value=\"host1\"/>"));
    Assert.assertTrue(response, response.contains("<input type=\"text\" name=\"account\" value=\"account1\"/>"));
    Assert.assertTrue(response, response.contains("<input type=\"text\" name=\"application\" value=\"application1\"/>"));
    Assert.assertTrue(response, response.contains("<input type=\"text\" name=\"user\" value=\"user1\"/><"));
  }
  
  private void mockFileInputs(String[] fileList) throws Exception {
    PowerMock.reset(File.class, FileInputStream.class, InputStreamReader.class, LoginHttpHandler.class);
    
    File mockedFolder = EasyMock.createMock(File.class);
    PowerMock.expectNew(File.class, ".").andReturn(mockedFolder);
    File[] files = new File[fileList.length];
    EasyMock.expect(mockedFolder.list()).andReturn(fileList).anyTimes();
    IExpectationSetters<Properties> propertiesSetter = null;
    for (int i = 0; i < fileList.length; i++) {
      files[i] = EasyMock.createMock(File.class);
      PowerMock.expectNew(File.class, mockedFolder, fileList[i]).andReturn(files[i]).anyTimes();
      EasyMock.expect(files[i].getName()).andReturn(fileList[i]);
      EasyMock.expect(files[i].isDirectory()).andReturn(".".equals(fileList[i]) || "..".equals(fileList[i]) ? true : false);
      String[] splitName = fileList[i].split("_");
      if (fileList[i].endsWith(".session") && splitName.length == 3) {
        FileInputStream mockInputStream = EasyMock.createMock(FileInputStream.class);
        PowerMock.expectNew(FileInputStream.class, files[i]).andReturn(mockInputStream).once();
        InputStreamReader mockReader = EasyMock.createMock(InputStreamReader.class);
        PowerMock.expectNew(InputStreamReader.class, mockInputStream, "UTF-8").andReturn(mockReader);
        
        mockReader.close();
        PowerMock.expectLastCall();
        mockInputStream.close();
        PowerMock.expectLastCall();
        
        
        Properties props = EasyMock.createMock(Properties.class);
        props.load(mockReader);
        EasyMock.expectLastCall();
        
        if (propertiesSetter == null) { 
          propertiesSetter = PowerMock.expectNew(Properties.class);
        } 
        propertiesSetter.andReturn(props);
        
        EasyMock.expect(props.getProperty(AbstractLogCommand.SDK_PATH_PARAM, "")).andReturn("/home/sdkpath").anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.HOST_PARAM, "")).andReturn(splitName[0]).anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.ACCOUNT_PARAM, "")).andReturn(splitName[1]).anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.APPLICATION_PARAM, "")).andReturn(splitName[2].substring(0, splitName[2].indexOf('.'))).anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.USER_PARAM, "")).andReturn("user1").anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.PROXY_PARAM, "")).andReturn("proxy").anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.PROXY_USER_PARAM, "")).andReturn("user1").anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.PROXY_PASSWORD_PARAM, "")).andReturn("xyzpass").anyTimes();

        EasyMock.expect(props.getProperty(AbstractLogCommand.HOST_PARAM)).andReturn(splitName[0]).anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.SDK_PATH_PARAM)).andReturn("/home/sdkpath").anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.ACCOUNT_PARAM)).andReturn(splitName[1]).anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.APPLICATION_PARAM)).andReturn(splitName[2].substring(0, splitName[2].indexOf('.'))).anyTimes();
        EasyMock.expect(props.getProperty(AbstractLogCommand.USER_PARAM)).andReturn("user1").anyTimes();
        
        EasyMock.expect(props.getProperty(LoginHttpHandler.STORED_TIME, "-1")).andReturn(String.valueOf(i));
        
        EasyMock.replay(mockReader, mockInputStream, props);
      }
      EasyMock.replay(files[i]);
    }
    EasyMock.replay(mockedFolder);
    
    PowerMock.replay(File.class, FileInputStream.class, Properties.class, InputStreamReader.class, LoginHttpHandler.class);
    
  }

  private RequestResponseHolder mockPostRequestResponse(boolean failLogin) throws Exception {
    RequestResponseHolder holder = buildCommonRequestResponse();
    PowerMock.reset(LoginHttpHandler.class, ListLogFilesCommand.class);
    
    Map<String, String[]> parameterMap = new HashMap<>();
    parameterMap.put(AbstractLogCommand.HOST_PARAM, new String[] {"host1"});
    parameterMap.put(AbstractLogCommand.ACCOUNT_PARAM, new String[] {"account1"});
    parameterMap.put(AbstractLogCommand.APPLICATION_PARAM, new String[] {"application1"});
    parameterMap.put(AbstractLogCommand.SDK_PATH_PARAM, new String[] {"/home/sdkpath"});
    parameterMap.put(AbstractLogCommand.USER_PARAM, new String[] {"user1"});
    EasyMock.expect(holder.mockRequest.getParameterMap()).andReturn(parameterMap);
    
    ListLogFilesCommand mockListLogs = EasyMock.createMock(ListLogFilesCommand.class);
    PowerMock.expectNew(ListLogFilesCommand.class, EasyMock.anyObject()).andReturn(mockListLogs);
    
    if (failLogin) {
      EasyMock.expect(mockListLogs.executeConsoleTool()).andThrow(new CommandExecutionException(mockListLogs, "testing failed login"));
    } else {
      BufferedReader mockReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(new byte[] {})));
      EasyMock.expect(mockListLogs.executeConsoleTool()).andReturn(mockReader);
      EasyMock.expect(mockListLogs.parseListLogsOutput(mockReader)).andReturn(new HashMap<String, LogFileDescriptor>());
      
      FileOutputStream mockFos = EasyMock.createMock(FileOutputStream.class);
      PowerMock.expectNew(FileOutputStream.class, "host1_account1_application1.session").andReturn(mockFos);
      OutputStreamWriter mockWriter = EasyMock.createMock(OutputStreamWriter.class);
      PowerMock.expectNew(OutputStreamWriter.class, mockFos, AbstractHttpHandler.UTF_8).andReturn(mockWriter);
      
      holder.capturedCookie = EasyMock.<Cookie>newCapture();
      holder.mockResponse.addCookie(EasyMock.capture(holder.capturedCookie));
      EasyMock.expectLastCall();
      
      holder.mockResponse.sendRedirect("/main");
      EasyMock.expectLastCall();
    }
    
    EasyMock.expect(holder.mockRequest.getMethod()).andReturn(Method.POST).anyTimes();

    EasyMock.replay(holder.mockRequest, holder.mockResponse, mockListLogs);
    PowerMock.replay(ListLogFilesCommand.class, OutputStreamWriter.class, FileOutputStream.class);
    return holder;
  }
  
  private RequestResponseHolder mockGetRequestResponse() throws Exception {
    RequestResponseHolder holder = buildCommonRequestResponse();
    
    EasyMock.expect(holder.mockRequest.getMethod()).andReturn(Method.GET).anyTimes();
    EasyMock.replay(holder.mockRequest, holder.mockResponse);
    return holder;
  }
  
}