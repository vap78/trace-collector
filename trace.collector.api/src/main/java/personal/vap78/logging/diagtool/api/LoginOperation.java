package personal.vap78.logging.diagtool.api;

import personal.vap78.logging.diagtool.Session;

public interface LoginOperation extends Operation {

  public Session getSession();
}
