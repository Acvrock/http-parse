package io.github.fengyouchao.httpparse;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 11:20 PM
 */
public interface HttpParser {

  /**
   * Write byte data in to HTTP
   *
   * @param bytes
   * @return length of bytes wrote
   */
  int write(byte[] bytes) throws HttpParseException;

  int write(byte[] bytes, int length) throws HttpParseException;
}
