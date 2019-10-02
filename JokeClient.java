// Yuchen Xiao/ 2019/4/20
//
//build 1.8.0_191
//
//Delete the package line, and then do following in cmd
//> javac JokeClient.java
//> java  JokeClient  // this connect with default localhost
//> java  JokeClient [server ip] // this connect with remote ip 
// 
//


import java.io.*; //system input and output through data steam
import java.net.*; // network applications
import java.util.*;

public class JokeClient {

	static List<Integer> J_index = new ArrayList<>();
	static List<Integer> P_index = new ArrayList<>();
	static List<Integer> J_index2 = new ArrayList<>();
	static List<Integer> P_index2 = new ArrayList<>();
	static int count_J = 0;
	static int count_P = 0;
	static int count_J2 = 0;
	static int count_P2 = 0;
	static String serverName;
	static String serverName2;
	static String curmode;
	static String curserver;
	static boolean multi = false;

	Random rd = new Random();
	int uuid = rd.nextInt(1000000); // uuids

	public static void main(String args[]) {
		// TODO Auto-generated method stub

		if (args.length < 1) { // self to self
			serverName = "localhost";
			System.out.println("Yuchen Xiao's Joke Client, 1.8.\n");
			System.out.println("Using server: " + serverName + ", Port: 4545");
		} else if (args.length == 1) { // remote connect with primary
			serverName = args[0]; // file name is not 0
			System.out.println("Yuchen Xiao's Joke Client, 1.8.\n");
			System.out.println("Using server: " + serverName + ", Port: 4545");
		} else if (args.length == 2) {
			// multi = true;
			serverName = args[0];
			serverName2 = args[1];
			System.out.println("Yuchen Xiao's Joke Client, 1.8.\n");
			System.out.println("Using server: " + serverName + ", Port: 4545");
			System.out.println("Using server: " + serverName2 + ", Port: 4546");

		} else {
			System.out.println("Error input, please re-open !");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		// InputStreamReader make it stream, System.in is from keyboard

		System.out.print("Please enter your username: ");

		try {
			String command, u_name;
			u_name = in.readLine(); // from keyboard

			for (int i = 0; i <= 3; i++) {
				J_index.add(i);
				J_index2.add(i);
				P_index.add(i);
				P_index2.add(i);
			}
			Collections.shuffle(J_index); // for J random array
			Collections.shuffle(J_index2);
			Collections.shuffle(P_index); // for p random array
			Collections.shuffle(P_index2);

			do {

				if (serverName2 == null) {
					System.out.println("Press <Enter> to get a Joke or Proverb or 'quit' to end:");
					System.out.flush(); // print directly

					command = in.readLine();
					if (command.isEmpty()) {

						getfromServer(u_name, curserver);

						if (curmode.equals("Joke") && multi == false) {
							count_J++;
							if (count_J == 4) {
								System.out.println(curmode + " CYCLE COMPLETED");
								count_J = 0;
								J_index.clear();
								for (int i = 0; i <= 3; i++) {
									J_index.add(i);
								}
								Collections.shuffle(J_index);
							}
						} else if (curmode.equals("Proverb") && multi == false) {
							count_P++;
							if (count_P == 4) {
								System.out.println(curmode + " CYCLE COMPLETED");
								count_P = 0;
								P_index.clear();
								for (int i = 0; i <= 3; i++) {
									P_index.add(i);
								}
								Collections.shuffle(P_index);
							}
						}
					}

				} else {
					System.out.println(
							"Press <Enter> to get a Joke or Proverb, enter 's' to toggle to different server, enter 'quit' to end:");
					System.out.flush();
					curserver = serverName;

					command = in.readLine();

					if (command.isEmpty()) {
						if (curserver.equals(serverName)) {

							getfromServer(u_name, curserver);

						} else {

							getfromServer(u_name, curserver);
						}

						if (curmode.equals("Joke") && multi == false) {
							count_J++;
							if (count_J == 4) {
								System.out.println(curmode + " CYCLE COMPLETED");
								count_J = 0;
								J_index.clear();
								for (int i = 0; i <= 3; i++) {
									J_index.add(i);
								}
								Collections.shuffle(J_index);
							}
						} else if (curmode.equals("Proverb") && multi == false) {
							count_P++;
							if (count_P == 4) {
								System.out.println(curmode + " CYCLE COMPLETED");
								count_P = 0;
								P_index.clear();
								for (int i = 0; i <= 3; i++) {
									P_index.add(i);
								}
								Collections.shuffle(P_index);

							}
						} else if (curmode.equals("Joke") && multi == true) {
							count_J2++;
							if (count_J2 == 4) {
								System.out.println(curmode + " CYCLE COMPLETED");
								count_J2 = 0;
								J_index2.clear();
								for (int i = 0; i <= 3; i++) {
									J_index2.add(i);
								}
								Collections.shuffle(J_index2);
							}
						} else if (curmode.equals("Proverb") && multi == true) {
							count_P2++;
							if (count_P2 == 4) {
								System.out.println(curmode + "<S2> CYCLE COMPLETED");
								count_P2 = 0;
								P_index2.clear();
								for (int i = 0; i <= 3; i++) {
									P_index2.add(i);
								}
								Collections.shuffle(P_index2);
							}

						}
					} else if (command.equals("s")) {
						if (multi == false) {
							multi = true;
							curserver = serverName2;
							System.out.println("Now toggle to: " + serverName2 + " (secondary server)");
						} else if (multi == true) {
							curserver = serverName;
							multi = false;
							System.out.println("Now toggle to: " + serverName);
						}

					}
				}

			} while (!command.equals("quit"));
			System.out.println("Cancelled by user request.");

		} catch (

		IOException x) {
			x.printStackTrace();
		}
	}

	static void getfromServer(String u_name, String serverName) {
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;// print it to Server
		String textFromServer;

		try {
//			if(serverName2.equals(null) && !serverName.equals(null)) 
			if (multi == false) {
				sock = new Socket(serverName, 4545); // create socket
				System.out.println("You are now connected with port: " + 4545);

				fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				toServer = new PrintStream(sock.getOutputStream());

				toServer.println("Hi,Server!");
				curmode = fromServer.readLine();

				if (curmode.equals("Joke")) {
					toServer.println(u_name + "\n" + J_index.get(count_J) + "\nFalse");
//			    toServer.println(J_index.get(count_J));
					toServer.flush();
				} else {
					toServer.println(u_name + "\n" + P_index.get(count_P) + "\nFalse");
				}

				for (int i = 1; i <= 3; i++) {
					textFromServer = fromServer.readLine();
					// wait, until something from server
					if (textFromServer != null)
						System.out.println(textFromServer);
				}
				sock.close();

			} else {
				sock = new Socket(serverName, 4546); // create socket
				System.out.println("You are now connected with port: " + 4546);

				fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				toServer = new PrintStream(sock.getOutputStream());

				toServer.println("Hi,Server!");
				curmode = fromServer.readLine();

				if (curmode.equals("Joke")) {
					toServer.println(u_name + "\n" + J_index2.get(count_J2) + "\nFalse");
					toServer.flush();
				} else {
					toServer.println(u_name + "\n" + P_index2.get(count_P2) + "\nFalse");
					toServer.flush();
				}

				for (int i = 1; i <= 3; i++) {
					textFromServer = fromServer.readLine();
					// wait, until something from server
					if (textFromServer != null)
						System.out.println(textFromServer);
				}
				sock.close();

			}

		} catch (IOException x) {
			System.out.println("Socket error.");
			x.printStackTrace();
		}

	}

}
