package test;

import io.github.fengyouchao.httpparse.HttpMessage;
import io.github.fengyouchao.httpparse.HttpMessageParser;
import io.github.fengyouchao.httpparse.HttpMethod;
import io.github.fengyouchao.httpparse.HttpParseException;
import io.github.fengyouchao.httpparse.HttpRequest;
import io.github.fengyouchao.httpparse.HttpRequestBuilder;
import io.github.fengyouchao.httpparse.HttpResponse;
import sockslib.common.net.MonitorOutputStreamWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 1:06 PM
 */
public class StartClient {

  public static HttpMessage request(String host, int port, HttpMessageParser httpMessageParser,
      HttpRequest message) {
    try {
      Socket socket = new Socket();
      socket.connect(new InetSocketAddress(host, 80));
      MonitorOutputStreamWrapper outputStream =
          new MonitorOutputStreamWrapper(socket.getOutputStream());
      HttpMessageParser httpMessageParser1 = new HttpMessageParser();
      outputStream.addMonitor(bytes -> {
        try {
          httpMessageParser1.write(bytes);
          if (httpMessageParser1.isComplete()) {
            HttpRequest message1 = (HttpRequest) httpMessageParser1.getHttpMessage();
            System.out.println(message1);
          }
        } catch (HttpParseException e) {
          e.printStackTrace();
        }
      });
      InputStream inputStream = socket.getInputStream();
      outputStream.write(message.getProtocolBytes());
      byte[] buffer = new byte[1024];
      int length = 0;
      while ((length = inputStream.read(buffer)) > 0) {
        httpMessageParser.write(buffer, length);
        if (httpMessageParser.isComplete()) {
          socket.close();
          break;
        }
      }
      return httpMessageParser.getHttpMessage();
    } catch (IOException | HttpParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) throws IOException {
    String host = "httpbin.org";
    HttpRequestBuilder builder = new HttpRequestBuilder();
    builder.setMethod(HttpMethod.POST).setUserAgent(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko)"
            + " Chrome/48.0.2564.116 Safari/537.36").setHost(host)
        .setBody("{\"hello\":\"world\"}".getBytes()).setPath("/post").setGzipCompress(true);
    HttpRequest message = builder.build();
    HttpMessageParser httpMessageParser = new HttpMessageParser();
    HttpResponse response = (HttpResponse) request(host, 80, httpMessageParser, message);
    System.out.println(response);
  }
}
