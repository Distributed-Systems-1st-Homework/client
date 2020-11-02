package hr.fer.rassus.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

@SpringBootApplication
public class ClientApplication {
	public static long START_TIME;
	public static Logger logger = LoggerFactory.getLogger(ClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
		START_TIME = System.currentTimeMillis();

		int randomPort = (int)(Math.random() * (65000 - 64000) + 64000);
		SocketClient socketClient = new SocketClient(randomPort);

		socketClient.startup();
		socketClient.loop();

		Runtime.getRuntime().addShutdownHook(new Thread()  {
			public void run() {
				ClientApplication.logger.info("Shutting down sensor...");
				try {
					ResponseEntity response = socketClient.getClientToServer().shutdown(socketClient.getClientUsername());

					if(response.getStatusCode().is2xxSuccessful()) {
						ClientApplication.logger.info("Sensor \"" + socketClient.getClientUsername() + "\"" +
								" is successfully shut down on the server");
					} else {
						ClientApplication.logger.info("Sensor could not be shut down on the server");
					}
				} catch (ResourceAccessException rae) {
					ClientApplication.logger.info("Sensor could not be shut down on the server because the server is shutdown");
				}
			}
		});
	}
}
