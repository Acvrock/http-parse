package io.github.fengyouchao.httpparse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 9:32 AM
 */
public class HttpHeaders implements Iterable<HttpHeader> {

  private List<HttpHeader> headers = new ArrayList<>();

  public HttpHeaders addHeader(String name, String value){
    headers.add(new HttpHeader(name, value));
    return this;
  }

  public HttpHeaders setHeader(String name, String value){
    removeHeader(name);
    addHeader(name, value);
    return this;
  }

  public HttpHeader removeHeader(String name){
    for (int i = 0; i < headers.size(); i++) {
      if(headers.get(i).getName().equalsIgnoreCase(name)){
        return headers.remove(i);
      }
    }
    return null;
  }

  public HttpHeader getHeader(String name){
    for(HttpHeader header: headers){
      if(header.getName().equalsIgnoreCase(name)){
        return header;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return "HttpHeaders{" +
        "headers=" + headers +
        '}';
  }

  public String originalString(){
    StringBuilder builder = new StringBuilder();
    for(HttpHeader header: headers){
      builder.append(header.originalString());
    }
    return builder.toString();
  }

  @Override
  public Iterator<HttpHeader> iterator() {
    return headers.iterator();
  }

  public static final String CONTENT_TYPE = "Content-Type";
  public static final String CONTENT_ENCODING = "Content-Encoding";
  public static final String CONTENT_LENGTH = "Content-Length";
  public static final String TRANSFER_ENCODING = "Transfer-Encoding";
  public static final String LOCATION = "Location";
  public static final String USER_AGENT = "User-Agent";
  public static final String SERVER = "Server";
  public static final String LAST_MODIFIED = "Last-Modified";
  public static final String ETAG = "ETag";
  public static final String AGE = "AGE";
  public static final String DATE = "Date";
  public static final String CONNECTION = "Connection";
  public static final String ACCEPT_ENCODING = "Accept-Encoding";
  public static final String ACCEPT_LANGUAGE = "Accept-Language";
  public static final String HOST = "Host";
}
