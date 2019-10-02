
import java.io.*; 
import java.net.*;

class Worker extends Thread {   
	Socket sock;				
	Worker(Socket s){sock = s;} 
	
	// socket is the connection between server and client, socket will
	// have binded ip address and port #
	// socket is like a address create by server. Then based on its unique address
	// client will create its own socket and try to connect to server's socket
	// client and server will communicate by writing to or reading from their sockets
	
	
	public void run() {
		
			PrintStream out = null;   // Printstream could print the steam by bytes 
			BufferedReader in =null;  // Reads text from a character-input stream, buffering characters so as to provide for the efficient reading of characters, arrays, and lines. 
			
		try {
		in = new BufferedReader(new InputStreamReader(sock.getInputStream())); 
		//getInputStream will return an input steam for this socket
		//InputStreamReader: take input steam and create a reader for it
		// BufferedReader is a default size chararcter-input steam
		//A stream is a sequence of objects that supports various methods which can be pipelined to produce the desired result
		
		out = new PrintStream(sock.getOutputStream());

			try {
				String name;
				name = in.readLine();  // reads a line of text, if meet anything like \n or \r or like enter, will treate like another line
				System.out.println("Looking up "+ name);
				printRemoteAddress(name,out); 
			}catch(IOException x){
				System.out.println("Server read error");
				x.printStackTrace(); //It tells you what happened and where in the code this happened.
			}
				sock.close(); //close this connection, but not the server
			}catch(IOException ioe) {System.out.println(ioe);}
		}
	// try and catch. try to do something, if wrong, do catch code. 
	// https://www.w3schools.com/java/java_try_catch.asp
	
	
	static void printRemoteAddress(String name,PrintStream out) {
		try {
			out.println("Looking up "+ name +"...");
			InetAddress machine = InetAddress.getByName(name); // getbyname will return an ip address, name is a string from readline
			//This class represents an Internet Protocol (IP) address. 
			//An IP address is either a 32-bit or 128-bit unsigned number used by IP,
			// a lower-level protocol on which protocols like UDP and TCP are built.  TCP realiable, UDP not way to check if complete or not

			out.println("Host name :" + machine.getHostName()); // gethostname returns string for the host name
			out.println("Host IP :" + toText(machine.getAddress()));//getAddress Returns the raw IP(ip[]) address of this InetAddressobject.
		}catch(UnknownHostException ex) {
			out.println("Failed in attempt to look up "+name);
		}	
	}
	
	static String toText(byte ip[]) { 
		StringBuffer result = new StringBuffer(); //A thread-safe, mutable sequence of characters
		
		// thread-safe: A piece of code is thread-safe if it only manipulates shared data structures 
		// in a manner that guarantees safe execution by multiple threads at the same time
		
		for(int i =0;i<ip.length;++i) {
			if(i>0) result.append(".");
			result.append(0xff & ip[i]);  // kind of like every time changes ip[i] to different number
		}
		return result.toString();
	}
}



public class InetServer {

	public static void main(String a[] ) throws IOException{
		
		int q_len = 6;
		int port = 1565; // port #
		Socket sock;// create socket
		ServerSocket servsock = new ServerSocket(port,q_len);// A server socket waits for requests to come in over the network
		
		System.out.println("Yuchen Xiao's Inet server 1.8 starting up, listening at port 1565. \n");
		while(true) {
			sock = servsock.accept();   // listening 
			new Worker(sock).start();  // then handle it 
		}
		
	}

}
