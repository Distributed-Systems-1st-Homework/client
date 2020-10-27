package hr.fer.rassus.client.api;

import java.util.concurrent.atomic.AtomicBoolean;

public interface SocketApi {
    // Server startup. Starts all services offered by the server.
    public void startup();
    // Server loops when in running mode. The server must be active
    // to accept client requests.
    public void loop();
    // Server shutdown. Shuts down all services started during
    //startup.
    public void shutdown();
}
