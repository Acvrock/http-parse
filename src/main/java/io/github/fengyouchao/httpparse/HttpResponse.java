package io.github.fengyouchao.httpparse;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 11:29 PM
 */
public class HttpResponse extends HttpMessage {

  private int responseCode;
  private String responseReason;


  public String getStatusLine() {
    return getVersion() + " " + responseCode + " " + responseReason;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  public String getResponseReason() {
    return responseReason;
  }

  public void setResponseReason(String responseReason) {
    this.responseReason = responseReason;
  }

  @Override
  public String getProtocolLine() {
    return getStatusLine();
  }
}
