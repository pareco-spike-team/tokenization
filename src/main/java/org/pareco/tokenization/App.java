package org.pareco.tokenization;

import java.io.IOException;
import me.jabour.env.config.EnvConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

public class App {
  public static final EnvConfig config = new EnvConfig(null, "tokenization", null);
  public static final int port = new Integer(config.optional("port").orElse("5000"));
  public static final long timeout = new Long(config.optional("timeout").orElse("60000"));

  public static void main(String[] args) throws IOException, Exception {
    Server server = new Server();
    ServerConnector serverConnector = new ServerConnector(server);

    serverConnector.setPort(port);
    serverConnector.setIdleTimeout(timeout);

    server.addConnector(serverConnector);
    server.setHandler(new CoreService());

    server.start();
    server.join();
  }
}
