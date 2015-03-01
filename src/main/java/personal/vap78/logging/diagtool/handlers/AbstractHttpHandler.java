package personal.vap78.logging.diagtool.handlers;

import java.io.IOException;
import java.io.InputStream;

import org.glassfish.grizzly.http.Cookie;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;

import personal.vap78.logging.diagtool.Session;

public class AbstractHttpHandler extends HttpHandler {

  public static final String TEXT_HTML = "text/html";
  public static final String UTF_8 = "UTF-8";
  public static final String SESSION_ID = "sessionId";

  protected static String readTemplate(String resource) {
    InputStream input = null;
    try {
      input = MainHttpHandler.class.getResourceAsStream(resource);
      byte[] buffer = new byte[1024];
      int counter = 0; 
      StringBuilder builder = new StringBuilder();
      
      while ((counter = input.read(buffer, 0, 1024)) != -1) {
        builder.append(new String(buffer, 0, counter));
      }
      return builder.toString();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to load template");
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
        }
      }
    }
  }

  @Override
  public void service(Request req, Response resp) throws Exception {
    req.setCharacterEncoding(UTF_8);
    resp.setCharacterEncoding(UTF_8);
    resp.setContentType(TEXT_HTML);
  }
  
  protected Session getSession(Request req, Response resp) throws IOException {
    Cookie cookie = getSessionCookie(req);
    if (cookie == null) {
      resp.sendRedirect("login.html");
      return null;
    }
    Session session = Session.getById(cookie.getValue());
    if (!session.isValid()) {
      cookie.setMaxAge(0);
      Session.deleteSession(session.getId());
      resp.addCookie(cookie);
      resp.sendRedirect("login.html");
      return null;
    }
    
    return session;
  }

  private Cookie getSessionCookie(Request req) {
    Cookie[] allCookies = req.getCookies();
    
    for (Cookie cookie : allCookies) {
      if (cookie.getName().equals(SESSION_ID)) {
        return cookie;
      }
    }
    return null;
  }
  
}
