// Yuchen Xiao/ 2019/4/20
//
//build 1.8.0_191
//
//Delete the package line, and then do following in cmd
//> javac JokeClientAdmin.java
//> java  JokeClientAdmin  // this connect with default localhost
//> java  JokeClientAdmin [server ip] // this connect with remote ip 
// 
//

import java.io.*; //system input and output through data steam
import java.net.*; // network applications
import java.util.*;

public class JokeClientAdmin {

//	static List<Integer> J_index = new ArrayList<>();
//	static List<Integer> P_index = new ArrayList<>();
//	static int count_J = 0;
//	static int count_P = 0;
	static String serverName;
	static String serverName2;
	static String curmode;
	static String curserver;
	static boolean multi;

	public static void main(String args[]) {
		// TODO Auto-generated method stub
		multi = false;
		if (args.length == 0) // if =2 , two port
			serverName = "localhost";
		else if (args.length == 1) {
			serverName = args[0]; // file name is not 0
		} else if (args.length == 2) {
			serverName = args[0];
			serverName2 = args[1];
		} else {
			System.out.println("Too many input; only enter at most two ip addresses!");
			return;
		}

		curserver = serverName;
		System.out.println("Yuchen Xiao's Joke Client, 1.8.\n");
		System.out.println("Using server: " + curserver + ", Port: 5050");

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		// InputStreamReader make it stream, System.in is from keyboard

		System.out.print("Please enter your username: ");

		if (serverName2 == null) {
			try {
				String command, u_name;
				u_name = in.readLine(); // from keyboard

				do {
					System.out.print("Press <Enter> to switch mode or 'quit' to end:");
					System.out.flush(); // print directly
					command = in.readLine();

					if (command.isEmpty()) {
						getfromServer(u_name, serverName);
					} else {

					}

				} while (!command.equals("quit"));
				System.out.println("Cancelled by user request.");

			} catch (IOException x) {
				x.printStackTrace();
			}

		} else {
			try {
				String command, u_name;
				u_name = in.readLine(); // from keyboard

				do {
					System.out.print("Press <Enter> to switch mode, enter 's' to toggle Server, enter 'quit' to end:");
					System.out.flush(); // print directly
					command = in.readLine();

					if (command.isEmpty()) {
						getfromServer(u_name, curserver);

					} else if (command.equals("s")) {
						if (multi == false) {
							multi = true;
							curserver = serverName2;
							System.out.println("Now toggle to secondary server " + curserver);
						} else if (multi == true) {
							multi = false;
							curserver = serverName;
							System.out.println("Now toggle to server " + curserver);
						}

					} else {

					}

				} while (!command.equals("quit"));
				System.out.println("Cancelled by user request.");

			} catch (IOException x) {
				x.printStackTrace();
			}
		}

	}

	static void getfromServer(String u_name, String serverName) {
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;// print it to Server
		String textFromServer;

		try {
			if (multi == false) {
				sock = new Socket(serverName, 5050); // create socket
				fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				toServer = new PrintStream(sock.getOutputStream());

				toServer.println("Hi,Server!");
			} else {
				sock = new Socket(serverName, 5051); // create socket
				fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				toServer = new PrintStream(sock.getOutputStream());

				toServer.println("Hi,Server!");

			}

			sock.close();
		} catch (IOException x) {
			System.out.println("Socket error.");
			x.printStackTrace();
		}

	}

}
