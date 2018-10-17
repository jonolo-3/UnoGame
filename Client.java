import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ThreadLocalRandom;

public class Client {
	public Client() {
		//Server stuffs
		try {
			Socket aSocket = new Socket("localhost", 2060);
			DataOutputStream outToServer = new DataOutputStream(aSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
			outToServer.writeBytes("true\n"); //Tell the server that I am a bot
			while(true) {
				String text = (inFromServer.readLine());
				if(text.equals("END")) {
					System.exit(0);
				} else {
					System.out.println(text);
				}

			}
		} catch(Exception e) {
			System.out.println("ERROR! Something went wrong with client. Exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String argv[]) {
		Client client = new Client();
	}
}