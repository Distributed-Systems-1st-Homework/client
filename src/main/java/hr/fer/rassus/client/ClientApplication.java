package hr.fer.rassus.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientApplication {
	public static long START_TIME;

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
		START_TIME = System.currentTimeMillis();

		SocketClient socketClient = new SocketClient();
		socketClient.startup();
		socketClient.loop();
		socketClient.shutdown();
	}

}
