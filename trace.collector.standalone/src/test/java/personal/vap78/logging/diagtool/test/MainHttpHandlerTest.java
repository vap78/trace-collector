package personal.vap78.logging.diagtool.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.easymock.EasyMock;
import org.glassfish.grizzly.http.Cookie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import personal.vap78.logging.diagtool.TraceConfiguration;
import personal.vap78.logging.diagtool.http.handlers.AbstractHttpHandler;
import personal.vap78.logging.diagtool.http.handlers.MainHttpHandler;
import personal.vap78.logging.diagtool.impl.console.ConsoleSession;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConsoleSession.class, MainHttpHandler.class, File.class, TraceConfiguration.class})
public class MainHttpHandlerTest extends AbstractHandlerTest {

  @Test
  public void testAccessMainNoSession() throws Exception {
    MainHttpHandler underTest = new MainHttpHandler();
    
    RequestResponseHolder holder = mockNoSessionAccess();
    
    underTest.service(holder.mockRequest, holder.mockResponse);
  }

  @Test
  public void testAccessMain() throws Exception {
    MainHttpHandler underTest = new MainHttpHandler();
    
    RequestResponseHolder holder = mockAccessWithSession();
    
    underTest.service(holder.mockRequest, holder.mockResponse);
    holder.mockOutput.flush();
    String response = new String(holder.mockOutput.toByteArray(), "UTF-8");

    Assert.assertTrue(response, response.contains("Connected to: <span class=\"bold\">host1</span> Account: <span class=\"bold\">account1</span>"));
    Assert.assertTrue(response, response.contains("Application: <span class=\"bold\">application1</span> User: <span class=\"bold\">user1</span>"));
    
    Assert.assertTrue(response, response.contains("<option value=\"testConfig0\">testConfig0</option>"));
    Assert.assertTrue(response, response.contains("<option value=\"testConfig1\">testConfig1</option>"));
    Assert.assertTrue(response, response.contains("<option value=\"testConfig2\">testConfig2</option>"));
    Assert.assertTrue(response, response.contains("<option value=\"testConfig3\">testConfig3</option>"));
    Assert.assertTrue(response, response.contains("<option value=\"testConfig4\">testConfig4</option>"));
    
    Assert.assertTrue(response, response.contains("com.test.location.5"));
    Assert.assertTrue(response, response.contains("com.test.location.6"));
    Assert.assertTrue(response, response.contains("com.test.location.7"));
    Assert.assertTrue(response, response.contains("com.test.location.8"));
    Assert.assertTrue(response, response.contains("com.test.location.9"));
  }
  
  private RequestResponseHolder mockAccessWithSession() throws Exception {
    PowerMock.reset(MainHttpHandler.class, ConsoleSession.class, TraceConfiguration.class);
    RequestResponseHolder toReturn = buildCommonRequestResponse();
    
    Cookie sessionCookie = new Cookie(AbstractHttpHandler.SESSION_ID, "abc123");
    EasyMock.expect(toReturn.mockRequest.getCookies()).andReturn(new Cookie[] {sessionCookie});
    
    PowerMock.mockStaticPartialNice(ConsoleSession.class, "getById");
    ConsoleSession mockSession = EasyMock.createMock(ConsoleSession.class);
    
    EasyMock.expect(ConsoleSession.getById("abc123")).andReturn(mockSession);
    EasyMock.expect(mockSession.isValid()).andReturn(true);
    EasyMock.expect(mockSession.getHost()).andReturn("host1");
    EasyMock.expect(mockSession.getAccount()).andReturn("account1");
    EasyMock.expect(mockSession.getApplication()).andReturn("application1");
    EasyMock.expect(mockSession.getUser()).andReturn("user1");
    EasyMock.expect(mockSession.getCurrentTracesCollectionInfo()).andReturn(null);
    EasyMock.expect(mockSession.getCollectedTraceFiles()).andReturn(new ArrayList<String>());
    
    File mockCurrentFolder = EasyMock.createMock(File.class);
    PowerMock.expectNew(File.class, ".").andReturn(mockCurrentFolder);
    
    PowerMock.mockStaticPartialNice(TraceConfiguration.class, "addFromDirectory");
    TraceConfiguration.addFromDirectory(mockCurrentFolder);
    EasyMock.expectLastCall();
    
    addTraceConfigurations();
    
    EasyMock.replay(toReturn.mockRequest, toReturn.mockResponse, mockSession, mockCurrentFolder);
    PowerMock.replay(ConsoleSession.class, TraceConfiguration.class, File.class, MainHttpHandler.class);
    return toReturn;
  }

  private void addTraceConfigurations() {
    for (int i = 0; i < 5; i++) {
      List<String> locations = new ArrayList<String>();
      for (int j = (i + 1) * 5; j < (i + 1)*5 + 5; j++) {
        locations.add("com.test.location." + j);
      }
      TraceConfiguration tc = new TraceConfiguration("testConfig" + i, locations);
      TraceConfiguration.addConfiguration(tc);
    }
  }

  private RequestResponseHolder mockNoSessionAccess() throws Exception {
    RequestResponseHolder toReturn = buildCommonRequestResponse();
    
    EasyMock.expect(toReturn.mockRequest.getCookies()).andReturn(new Cookie[] {});
    
    toReturn.mockResponse.sendRedirect("/doLogin");
    
    EasyMock.replay(toReturn.mockRequest, toReturn.mockResponse);
    
    return toReturn;
  }
  
  
}
