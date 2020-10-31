package hr.fer.rassus.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
	}

}
