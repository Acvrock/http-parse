package io.github.fengyouchao.httpparse;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 1:17 PM
 */
public class HttpRequest extends HttpMessage {

  private HttpMethod method;
  private String requestPath;


  public HttpMethod getMethod() {
    return method;
  }

  public void setMethod(HttpMethod method) {
    this.method = method;
  }

  public String getRequestLine() {
    return method + " " + requestPath + " " + getVersion();
  }

  @Override
  public String getProtocolLine() {
    return getRequestLine();
  }

  public String getRequestPath() {
    return requestPath;
  }

  public void setRequestPath(String requestPath) {
    this.requestPath = requestPath;
  }

}
