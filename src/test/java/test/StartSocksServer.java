package test;

import sockslib.server.Socks5Handler;
import sockslib.server.SocksProxyServer;
import sockslib.server.SocksServerBuilder;
import sockslib.server.listener.LoggingListener;

import java.io.IOException;

/**
 * The class <code></code>
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Mar 03,2016 1:49 PM
 */
public class StartSocksServer {
  public static void main(String[] args) throws IOException {
    SocksProxyServer server = SocksServerBuilder.newBuilder(Socks5Handler.class)
        .setPipeInitializer(pipe -> {pipe.addPipeListener(new HttpPipeListener()); return pipe;})
        .addSessionListener("logging", new LoggingListener())
        .build();
    server.start();
  }
}
