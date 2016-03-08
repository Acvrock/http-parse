package io.github.fengyouchao.httpparse;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 1:14 PM
 */
public class HttpRequestBuilder {

  private static final Pattern urlPattern =
      Pattern.compile("(http|https)://([\\w\\.]+)(:(\\d+))?(/.*)");
  private HttpMethod method;
  private Map<String, String> headers = new HashMap<>();
  private String url;
  private String host;
  private String path="/";
  private byte[] body;
  private boolean gzipCompress = false;


  public HttpRequestBuilder setPath(String path) {
    this.path = path;
    return this;
  }

  public HttpRequestBuilder setBody(byte[] body) {
    this.body = body;
    return this;
  }

  public HttpRequestBuilder setGzipCompress(boolean gzip){
    gzipCompress = true;
    return this;
  }

  public HttpRequestBuilder setHeader(String name, Object value){
    headers.put(name, value.toString());
    return this;
  }

  public HttpRequestBuilder setUserAgent(String userAgent){
    headers.put(HttpHeaders.USER_AGENT, userAgent);
    return this;
  }

  public HttpRequestBuilder setAcceptEncoding(String value){
    headers.put(HttpHeaders.ACCEPT_ENCODING, value);
    return this;
  }



  private HttpRequestBuilder addHeader(String name, String value) {
    headers.put(name, value);
    return this;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public HttpRequestBuilder setMethod(HttpMethod method) {
    this.method = method;
    return this;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public HttpRequestBuilder setHeaders(Map<String, String> headers) {
    this.headers = headers;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public HttpRequestBuilder setUrl(String url) {
    this.url = url;
    Matcher matcher = urlPattern.matcher(url);
    if (matcher.matches()) {
      host = matcher.group(2);
      path = matcher.group(5);
    } else {
      throw new IllegalArgumentException("URL format error");
    }
    return this;
  }

  public String getHost() {
    return host;
  }

  public HttpRequestBuilder setHost(String host) {
    this.host = host;
    return this;
  }

  public HttpRequest build(){
    HttpRequest request = new HttpRequest();
    request.setMethod(method);
    request.setRequestPath(path);
    request.setHeader("Host", host);
    for(String header:headers.keySet()){
      request.setHeader(header, headers.get(header));
    }
    if(gzipCompress){
      request.setHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
      request.setGzip(true);
    }
    if(body!= null){
      request.setBody(body);
    }
    return request;
  }
}
