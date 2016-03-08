package io.github.fengyouchao.httpparse;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 3:27 PM
 */
public abstract class AbstractHttpParser implements HttpParser {
  @Override
  public int write(byte[] bytes) throws HttpParseException {
    return write(bytes, bytes.length);
  }

}
