package test;

import io.github.fengyouchao.httpparse.HttpMessage;
import io.github.fengyouchao.httpparse.HttpMessageParser;
import io.github.fengyouchao.httpparse.HttpRequest;
import io.github.fengyouchao.httpparse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sockslib.server.io.Pipe;
import sockslib.server.io.PipeListener;
import sockslib.server.io.SocketPipe;

import java.net.Socket;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 2:02 PM
 */
public class HttpPipeListener implements PipeListener {
  private HttpMessageParser httpMessageParser = null;
  private HttpMessageParser httpMessageParser2 = null;
  protected static final Logger logger = LoggerFactory.getLogger(HttpPipeListener.class);


  public HttpPipeListener() {
    httpMessageParser = new HttpMessageParser();
  }

  @Override
  public void onStart(Pipe pipe) {
    httpMessageParser = new HttpMessageParser();
  }

  @Override
  public void onStop(Pipe pipe) {
  }

  @Override
  public void onTransfer(Pipe pipe, byte[] buffer, int bufferLength) {
//    System.out.println(pipe.getClass());
//    StreamPipe streamPipe = (StreamPipe) pipe;
    try {
      httpMessageParser.write(buffer, bufferLength);
      if (httpMessageParser.isComplete()) {
        HttpMessage message = httpMessageParser.getHttpMessage();
        httpMessageParser.reset();
        Socket sourceSocket = (Socket) pipe.getAttribute(SocketPipe.ATTR_SOURCE_SOCKET);
        Socket destinationSocket = (Socket) pipe.getAttribute(SocketPipe.ATTR_DESTINATION_SOCKET);
        if(sourceSocket!=null && destinationSocket != null){
          System.out.println(String.format("source: %s, destination: %s", sourceSocket.getRemoteSocketAddress(), destinationSocket.getRemoteSocketAddress()));
        }
        if(message instanceof HttpResponse) {
          System.out.println("=========Response===========");
          System.out.println(((HttpResponse) message).getResponseCode());
        }else if(message instanceof HttpRequest){
          System.out.println("=========Request===========");
          System.out.println(((HttpRequest) message).getMethod());
        }
//        System.out.println(message);
      }
    } catch (Exception e) {
      e.printStackTrace();
      httpMessageParser = new HttpMessageParser();
      System.out.println("response Unknown ");
    }

  }

  @Override
  public void onError(Pipe pipe, Exception exception) {

  }
}
