package personal.vap78.logging.diagtool.handlers;

public class TraceCollectionInfo {

  private long startTime;
  private long endTime;
  
  private String id;

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public boolean isCollecting() {
    return endTime == 0;
  }
  
}
