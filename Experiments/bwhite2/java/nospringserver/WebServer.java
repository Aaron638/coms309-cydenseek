import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class WebServer {

	private static final int PORT = (int)(Math.random() * 65534) + 1;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println(serverSocket);
		} catch(IOException e) {
			System.out.println("Could not listen on port: " + PORT);
			System.exit(-1);
		}
		while(true) {
			try {
				Socket clientSocket = serverSocket.accept();
				new Thread(new WebClient(clientSocket)).start();
			} catch(IOException e) {
				System.out.println("Accept failed: " + PORT);
				System.exit(-1);
			}
		}
	}
}

class WebClient implements Runnable {

	private Socket s;

	WebClient(Socket s) {
		this.s = s;
	}

	public void run() {
		try(Scanner in = new Scanner(s.getInputStream())) {
			System.out.println("Server - handling client!");
			String request = in.nextLine();
			System.out.println(request);
			if(request.startsWith("GET")) {
				PrintWriter client = new PrintWriter(s.getOutputStream());

				client.println("\nHTTP/1.1 200 OK");
				client.println("Connection: close");
				client.println("Content-Type: text/html");
				client.println();
				client.println("<h1>Hi there! \n\n</h1>" );

				client.flush();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
