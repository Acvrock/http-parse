package io.github.fengyouchao.httpparse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 6:16 PM
 */
public class HttpMessageParser extends AbstractHttpParser {

  private static final Pattern statusLinePattern =
      Pattern.compile("(HTTP/1\\.[01]) (\\d\\d\\d) (.*)");
  private static final Pattern requestLinePattern =
      Pattern.compile("(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS) (.*) (HTTP/1\\.[01])");

  private ByteCache cache = new ByteCache();
  private Status status = Status.WRITE_STATUS_LINE;
  private HttpMessage httpMessage;
  private long bodyLength = 0;
  private long chunked_max_length = 0;
  private long chunked_length = 0;
  private ByteArrayOutputStream bodyCache = new ByteArrayOutputStream();
  private Consumer<HttpMessage> consumer;


  public void reset() {
    cache.clear();
    status = Status.WRITE_STATUS_LINE;
    httpMessage = new HttpResponse();
    bodyLength = 0;
    chunked_max_length = 0;
    chunked_length = 0;
    bodyCache.reset();
  }


  private void initAfterSolveAllHeaders() {
    String value = httpMessage.getTransferEncoding();
    if (value != null && value.equalsIgnoreCase("chunked")) {
      status = Status.WRITE_CHUNKED_LENGTH;
    } else {
      status = Status.WRITE_LENGTH_BODY;
      if (httpMessage.getContentLength() == 0) {
        status = Status.WRITE_COMPLETED;
      }
    }
    if (httpMessage.getContentEncoding() != null && httpMessage.getContentEncoding()
        .equalsIgnoreCase("gzip")) {
      httpMessage.setGzip(true);
    }
    httpMessage.setCharset(HttpUtils.findCharset(httpMessage, httpMessage.getCharset()));
  }

  private void writeStatusLine(byte b) throws HttpParseException {
    cache.put(b);
    if (cache.hasCRLF()) {
      String protocolLine = cache.getLineWithoutCRLF();
      Matcher matcher = statusLinePattern.matcher(protocolLine);
      Matcher matcher1 = requestLinePattern.matcher(protocolLine);
      if (matcher.matches()) {
        httpMessage = new HttpResponse();
        HttpResponse httpResponse = (HttpResponse) httpMessage;
        httpResponse.setProtocolLine(protocolLine);
        httpResponse.setVersion(matcher.group(1));
        httpResponse.setResponseCode(Integer.parseInt(matcher.group(2)));
        httpResponse.setResponseReason(matcher.group(3));
      } else if (matcher1.matches()) {
        httpMessage = new HttpRequest();
        HttpRequest httpRequest = (HttpRequest) httpMessage;
        httpRequest.setProtocolLine(protocolLine);
        httpRequest.setMethod(HttpMethod.valueOf(matcher1.group(1)));
        httpRequest.setRequestPath(matcher1.group(2));
        httpRequest.setVersion(matcher1.group(3));
      } else {
        throw new HttpParseException("Bad HTTP protocol line: " + protocolLine);
      }
      httpMessage.setProtocolLine(cache.getLineWithoutCRLF());
      cache.clear();
      status = Status.WRITE_HEADER;
    }
  }

  private void writeHeader(byte b) throws HttpParseException {
    cache.put(b);
    if (cache.hasCRLF()) {
      String line = cache.getLineWithoutCRLF();
      cache.clear();
      if (line.equals("")) {
        initAfterSolveAllHeaders();
        return;
      }
      String[] values = line.split(":\\s*", 2);
      if (values.length != 2) {
        throw new HttpParseException("Wrong header format:" + line);
      }
      httpMessage.setHeader(values[0], values[1]);
    }
  }

  private void setHttpMessageBody() throws HttpParseException {
    if (httpMessage.isGzip()) {
      try {
        httpMessage.setBody(GZipUtils.uncompress(bodyCache.toByteArray()));
      } catch (IOException e) {
        throw new HttpParseException("Uncompress data error");
      }
    } else {
      httpMessage.setBody(bodyCache.toByteArray());
    }
    if (httpMessage.getContentType() != null && httpMessage.getContentType()
        .contains("text/html")) {
      String charset = HttpUtils.findHtmlCharset(httpMessage.getText());
      if (charset != null) {
        httpMessage.setCharset(charset);
      }
    }
  }

  private void writeLengthBody(byte b) throws HttpParseException {
    if (bodyLength < httpMessage.getContentLength()) {
      bodyCache.write(b);
      bodyLength++;
      if (bodyLength == httpMessage.getContentLength()) {
        status = Status.WRITE_COMPLETED;
        setHttpMessageBody();
      }
    }
  }

  private void writeChunkedLength(byte b) throws HttpParseException {
    cache.put(b);
    if (cache.hasCRLF()) {
      String line = "0x" + cache.getLineWithoutCRLF();
      cache.clear();
      if (line.equalsIgnoreCase("0x")) {
        return;
      }
      try {
        chunked_max_length = Long.decode(line);
      } catch (NumberFormatException e) {
        throw new HttpParseException("Bad chunked length:" + line);
      }
      System.out.println(chunked_max_length);
      if (chunked_max_length == 0) {
        status = Status.WRITE_COMPLETED;
        setHttpMessageBody();
      } else {
        status = Status.WRITE_CHUNKED_BODY;
      }
    }
  }

  private void writeChunkedBody(byte b) throws HttpParseException {
    if (chunked_max_length == 0) {
      throw new HttpParseException("End of HTTP message");
    }
    if (chunked_length < chunked_max_length) {
      bodyCache.write(b);
      chunked_length++;
      if (chunked_length == chunked_max_length) {
        status = Status.WRITE_CHUNKED_LENGTH;
      }
    }
  }


  public int write(byte[] bytes, int length) throws HttpParseException {
    int count = 0;
    for (int i = 0; i < length; i++) {
      if (status == Status.WRITE_COMPLETED) {
        return 0;
      }
      byte b = bytes[i];
      count++;
      switch (status) {

        case WRITE_STATUS_LINE:
          writeStatusLine(b);
          break;
        case WRITE_HEADER:
          writeHeader(b);
          break;
        case WRITE_LENGTH_BODY:
          writeLengthBody(b);
          break;
        case WRITE_CHUNKED_LENGTH:
          writeChunkedLength(b);
          break;
        case WRITE_CHUNKED_BODY:
          writeChunkedBody(b);
          break;

      }
    }
    return count;
  }

  public HttpMessage getHttpMessage() {
    return httpMessage;
  }

  public void setHttpMessage(HttpMessage httpMessage) {
    this.httpMessage = httpMessage;
  }

  public long getBodyLength() {
    return bodyLength;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public boolean isComplete() {
    return status == Status.WRITE_COMPLETED;
  }


  enum Status {
    WRITE_STATUS_LINE, WRITE_HEADER, WRITE_LENGTH_BODY, WRITE_CHUNKED_LENGTH, WRITE_CHUNKED_BODY,
    WRITE_COMPLETED
  }

  public long getChunked_length() {
    return chunked_length;
  }

  public long getChunked_max_length() {
    return chunked_max_length;
  }
}
