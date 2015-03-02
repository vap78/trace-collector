package personal.vap78.logging.diagtool.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.easymock.EasyMock;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.handlers.AbstractHttpHandler;

public class AbstractHandlerTest {
  
  protected RequestResponseHolder buildCommonRequestResponse() throws Exception {
    RequestResponseHolder holder = new RequestResponseHolder();
    
    holder.mockRequest = EasyMock.createMock(Request.class);
    holder.mockRequest.setCharacterEncoding(AbstractHttpHandler.UTF_8);
    EasyMock.expectLastCall();
    
    holder.mockResponse = EasyMock.createMock(Response.class);
    holder.mockOutput = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(holder.mockOutput);
    EasyMock.expect(holder.mockResponse.getWriter()).andReturn(writer).anyTimes();
    
    holder.mockResponse.setCharacterEncoding(AbstractHttpHandler.UTF_8);
    EasyMock.expectLastCall();
    
    holder.mockResponse.setContentType(AbstractHttpHandler.TEXT_HTML);
    EasyMock.expectLastCall();
    
    return holder;
  }
  
  

}
