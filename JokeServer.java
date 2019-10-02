// Yuchen Xiao/ 2019/4/20
//
//build 1.8.0_191
//
//  Delete the package line, and then do following in cmd
//> javac Server.java
//> java  JokeServer  // Server started
//


import java.io.*;
import java.net.*;
import java.util.*;

public class JokeServer {
	static String curmode = "Joke";
	static String curmode2 = "Joke";
	public static boolean adminControlSwitch = true;
	static int sec = 0;

	public static class Handle extends Thread {
		Socket sock;

		Handle(Socket s, String curmodefromadmin) {
			sock = s;
//			curmode = curmodefromadmin;
		}

		// globel , not thread, everyone uses the same one
		String JA = " What did the spider do on the computer? \r\n" + "A: Made a website! ";
		String JB = "What did the computer do at lunchtime?\r\n" + "A: Had a byte! ";
		String JC = " What does a baby computer call his father?\r\n" + "A: Data! ";
		String JD = "Why did the computer keep sneezing?\r\n" + "A: It had a virus! ";
		String[] Jokes = { JA, JB, JC, JD };
		String[] Joke_header = { "JA", "JB", "JC", "JD" };

		String PA = "Two wrongs don't make a right.";
		String PB = "The pen is mightier than the sword.";
		String PC = "When in Rome, do as the Romans.";
		String PD = "When the going gets tough, the tough get going.";
		String[] Proverbs = { PA, PB, PC, PD };
		String[] Proverb_header = { "PA", "PB", "PC", "PD" };

		// UUID needed

		public void run() {

			PrintStream out = null; // Printstream could print the steam by bytes
			BufferedReader in = null; // Reads text from a character-input stream, buffering characters so as to
										// provide for the efficient reading of characters, arrays, and lines.

			try {
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

				out = new PrintStream(sock.getOutputStream());

				try {
					String u_name;
					int index;
					String admin;

					String hey = in.readLine();
					out.println(curmode);

					u_name = in.readLine();
					index = Integer.parseInt(in.readLine());
					admin = in.readLine();
					// if nothing read, it will wait!!!!!!

					if (curmode.equals("Joke")) {
//			    System.out.println(index);		    			    			    
						out.println(Joke_header[index] + " " + u_name + " :" + Jokes[index]);

					} else {
						out.println(Proverb_header[index] + " " + u_name + " :" + Proverbs[index]);
					}

				} catch (IOException x) {
					System.out.println("Server read error");
					x.printStackTrace(); // It tells you what happened and where in the code this happened.
				}
				sock.close(); // close this connection, but not the server
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}

	}

	public static class Handle2 extends Thread {
		Socket sock;
//		static String curmode2;// static, then it is changeable!!!!

		Handle2(Socket s, String curmodefromadmin) {
			sock = s;
			curmode2 = curmodefromadmin;
		}

		// globel , not thread, everyone uses the same one
		String JA = " What did the spider do on the computer? \r\n" + "A: Made a website! ";
		String JB = "What did the computer do at lunchtime?\r\n" + "A: Had a byte! ";
		String JC = " What does a baby computer call his father?\r\n" + "A: Data! ";
		String JD = "Why did the computer keep sneezing?\r\n" + "A: It had a virus! ";
		String[] Jokes = { JA, JB, JC, JD };
		String[] Joke_header = { "JA", "JB", "JC", "JD" };

		String PA = "Two wrongs don't make a right.";
		String PB = "The pen is mightier than the sword.";
		String PC = "When in Rome, do as the Romans.";
		String PD = "When the going gets tough, the tough get going.";
		String[] Proverbs = { PA, PB, PC, PD };
		String[] Proverb_header = { "PA", "PB", "PC", "PD" };

		// UUID needed

		public void run() {

			PrintStream out = null; // Printstream could print the steam by bytes
			BufferedReader in = null; // Reads text from a character-input stream, buffering characters so as to
										// provide for the efficient reading of characters, arrays, and lines.

			try {
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

				out = new PrintStream(sock.getOutputStream());

				try {
					String u_name;
					int index;
					String admin;

					String hey = in.readLine();
					out.println(curmode2);

					u_name = in.readLine();
					index = Integer.parseInt(in.readLine());
					admin = in.readLine();
					// if nothing read, it will wait!!!!!!

					if (curmode2.equals("Joke")) {
//			    System.out.println(index);		    			    			    
						out.println("<S2> " + Joke_header[index] + " " + u_name + " :" + Jokes[index]);

					} else {
						out.println("<S2> " + Proverb_header[index] + " " + u_name + " :" + Proverbs[index]);
					}

				} catch (IOException x) {
					System.out.println("Server read error");
					x.printStackTrace(); // It tells you what happened and where in the code this happened.
				}
				sock.close(); // close this connection, but not the server
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}

	}

	public static class AdminLooper implements Runnable {

//		static String curmode = "Joke";
//		int adsec;
//		AdminLooper(int ifsec){
//			adsec = ifsec;
//		}

//		String getmode() {
//			if (adsec == 0) {
//				return curmode;
//			} else if (adsec == 1) {
//				return curmode2;
//			}
//			return null;
//		}

		public void run() { // RUNning the Admin listen loop
			System.out.println("In the admin looper thread");
			if (sec == 0) {
				int q_len = 6; /* Number of requests for OpSys to queue */
				int port = 5050; // We are listening at a different port for Admin clients
				Socket sock;

				try {
					ServerSocket servsock = new ServerSocket(port, q_len);
					while (adminControlSwitch) {
						// wait for the next ADMIN client connection:
						sock = servsock.accept();
						new AdminWorker(sock).start();
					}
				} catch (IOException ioe) {
					System.out.println(ioe);
				}

			} else if (sec == 1) {
				int q_len = 6; /* Number of requests for OpSys to queue */
				int port = 5051; // We are listening at a different port for Admin clients
				Socket sock;

				try {
					ServerSocket servsock = new ServerSocket(port, q_len);
					while (adminControlSwitch) {
						// wait for the next ADMIN client connection:
						sock = servsock.accept();
						new AdminWorker2(sock).start();
					}
				} catch (IOException ioe) {
					System.out.println(ioe);
				}

			}
		}

		class AdminWorker extends Thread {
			Socket sock;

			AdminWorker(Socket s) {
				sock = s;
			}

			public void run() {
				PrintStream out = null; // Printstream could print the steam by bytes
				BufferedReader in = null;

				try {
					in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

					out = new PrintStream(sock.getOutputStream());

					String hey = in.readLine();

					if (curmode.equals("Joke")) {
						curmode = "Proverb";
						System.out.println("Switch to " + curmode + " mode!");
						// out.println("Switch to Proverb mode!");

					} else {
						curmode = "Joke";
						System.out.println("Switch to " + curmode + " mode!");
						// out.println("Switch to Joke mode!");

					}

				} catch (IOException ioe) {
					System.out.println(ioe);
				}
			}
		}

		class AdminWorker2 extends Thread {
			Socket sock;

			AdminWorker2(Socket s) {
				sock = s;
			}

			public void run() {
				PrintStream out = null; // Printstream could print the steam by bytes
				BufferedReader in = null;

				try {
					in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

					out = new PrintStream(sock.getOutputStream());

					String hey = in.readLine();

					if (curmode2.equals("Joke")) {
						curmode2 = "Proverb";
						System.out.println("<S2> Switch to " + curmode2 + " mode!");
						// out.println("Switch to Proverb mode!");

					} else {
						curmode2 = "Joke";
						System.out.println("<S2> Switch to " + curmode2 + " mode!");
						// out.println("Switch to Joke mode!");

					}

				} catch (IOException ioe) {
					System.out.println(ioe);
				}
			}
		}
	}

	public static void main(String a[]) throws IOException {

		if (a.length == 0) {
			int q_len = 6;
			int port = 4545; // port #
			Socket sock;// create socket
			sec = 0;

			AdminLooper AL = new AdminLooper(); // create a DIFFERENT thread

			Thread t = new Thread(AL);
			t.start(); // ...and start it, waiting for administration input

			ServerSocket servsock = new ServerSocket(port, q_len);// A server socket waits for requests to come in over
																	// the
																	// network
			System.out.println("Yuchen Xiao's Joke server 1.8 starting up, listening at port 4545 \n");
			while (true) {
				sock = servsock.accept(); // listening
				new Handle(sock, curmode).start(); // then handle it , do the things in run
				//
			}

		} else if (a.length > 0 && a[0].equals("secondary")) {
			int q_len = 6;
			int port = 4546; // port #
			Socket sock;// create socket
			sec = 1;

			AdminLooper AL2 = new AdminLooper(); // create a DIFFERENT thread
			Thread t = new Thread(AL2);
			t.start(); // ...and start it, waiting for administration input

			ServerSocket servsock = new ServerSocket(port, q_len);// A server socket waits for requests to come in over
																	// the
																	// network
			System.out.println("Yuchen Xiao's Joke server 1.8 starting up, listening at port 4546 \n");
			while (true) {
				sock = servsock.accept(); // listening
				// new Handle(sock, "Joke").start(); // then handle it , do the things in run
				new Handle2(sock, curmode2).start();
			}

		}

	}

}
