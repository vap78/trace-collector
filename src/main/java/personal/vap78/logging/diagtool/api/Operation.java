package personal.vap78.logging.diagtool.api;

import personal.vap78.logging.diagtool.TraceConfiguration;

public interface Operation {
  public void execute(TraceConfiguration configuration);
  public boolean isSuccessful();
}
