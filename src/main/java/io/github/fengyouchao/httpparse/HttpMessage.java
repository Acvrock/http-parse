package io.github.fengyouchao.httpparse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import static io.github.fengyouchao.httpparse.HttpHeaders.CONTENT_ENCODING;
import static io.github.fengyouchao.httpparse.HttpHeaders.CONTENT_LENGTH;
import static io.github.fengyouchao.httpparse.HttpHeaders.CONTENT_TYPE;
import static io.github.fengyouchao.httpparse.HttpHeaders.TRANSFER_ENCODING;

/**
 * The class <code>HttpMessage</code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 9:24 AM
 */
public class HttpMessage {

  private HttpHeaders headers = new HttpHeaders();
  private String protocolLine;
  private String charset = "iso-8859-1";
  private boolean gzip;
  private byte[] body;
  private String version = "HTTP/1.1";

  public String getHeader(String header) {
    HttpHeader httpHeader = headers.getHeader(header);
    if (httpHeader != null) {
      return httpHeader.getValue();
    }
    return null;
  }

  public HttpMessage setHeader(String header, Object value) {
    headers.setHeader(header, value.toString());
    return this;
  }

  public HttpMessage addHeader(String header, Object value) {
    headers.addHeader(header, value.toString());
    return this;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public void setHeaders(HttpHeaders headers) {
    this.headers = headers;
  }

  public String headerString() {
    return headers.originalString();
  }

  public String getProtocolLine() {
    return protocolLine;
  }

  public void setProtocolLine(String protocolLine) {
    this.protocolLine = protocolLine;
  }

  public byte[] getBody() {
    return body;
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

  public long getContentLength() {
    String value = getHeader(CONTENT_LENGTH);
    if (value != null) {
      try {
        return Long.parseLong(value);
      } catch (NumberFormatException e) {
        return 0;
      }
    }
    return 0;
  }

  public String getContentType() {
    return getHeader(CONTENT_TYPE);
  }

  public String getContentEncoding() {
    return getHeader(CONTENT_ENCODING);
  }

  public String getTransferEncoding() {
    return getHeader(TRANSFER_ENCODING);
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public String getText() {
    if (getBody() != null) {
      return new String(getBody(), Charset.forName(charset));
    } else {
      return "";
    }
  }

  public boolean isRequestMessage() {
    return !isResponseMessage();
  }

  public boolean isResponseMessage() {
    return protocolLine.startsWith("HTTP/1.1");
  }

  public boolean isGzip() {
    return gzip;
  }

  public void setGzip(boolean gzip) {
    this.gzip = gzip;
  }

  @Override
  public String toString() {
    return getProtocolLine() + "\r\n" + headerString() + "\r\n" + getText();
  }

  public byte[] getProtocolBytes() throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write((getProtocolLine() + "\r\n").getBytes());
    byte[] bodyBytes = getBody();
    if (bodyBytes != null && bodyBytes.length > 0) {
      if (isGzip()) {
        bodyBytes = GZipUtils.compress(bodyBytes);
        setHeader(CONTENT_ENCODING, "gzip");
        setHeader(CONTENT_LENGTH, bodyBytes.length);
      }
      setHeader(CONTENT_LENGTH, bodyBytes.length);
    }
    outputStream.write(headerString().getBytes());
    outputStream.write("\r\n".getBytes());
    if (bodyBytes != null && bodyBytes.length > 0) {
      outputStream.write(bodyBytes);
    }
    return outputStream.toByteArray();
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
