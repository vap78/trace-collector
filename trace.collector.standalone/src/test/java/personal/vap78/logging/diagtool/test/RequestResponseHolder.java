package personal.vap78.logging.diagtool.test;

import java.io.ByteArrayOutputStream;

import org.easymock.Capture;
import org.glassfish.grizzly.http.Cookie;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

class RequestResponseHolder {
  Request mockRequest;
  Response mockResponse;
  ByteArrayOutputStream mockOutput;
  Capture<Cookie> capturedCookie;
}