package io.github.fengyouchao.httpparse;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 08, 2016 10:23 AM
 */
public class HttpParseException extends Exception{

  public HttpParseException() {
  }

  public HttpParseException(Throwable cause) {
    super(cause);
  }

  public HttpParseException(String message) {
    super(message);
  }

  public HttpParseException(String message, Throwable cause) {
    super(message, cause);
  }

  public HttpParseException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
