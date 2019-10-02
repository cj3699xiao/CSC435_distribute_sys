

import java.io.*; //system input and output through data steam
import java.net.*; // network applications

public class InetClient {

	public static void main(String args[]) {
		// TODO Auto-generated method stub
		String serverName;
		if(args.length < 1) serverName = "localhost"; 
		else serverName = args[0];
		
		System.out.println("Yuchen Xiao's Inet Client, 1.8.\n");
		System.out.println("Using server: "+ serverName +", Port: 1565");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		// BufferedReader is a default size chararcter-input steam
		//A stream is a sequence of objects that supports various methods which can be pipelined to produce the desired result
				
		
		try {
			String name;
			do {
				System.out.print("Enter a hostname or an IP address,(quit) to end:");
				System.out.flush();  // print directly 
				name = in.readLine();
				if(name.indexOf("quit")<0)  //if no quit
					getRemoteAddress(name,serverName);
			}while(name.indexOf("quit")<0); 
			System.out.println("Cancelled by user request.");
			
		}catch(IOException x) {x.printStackTrace();}
	}

static String toText(byte ip[]) { 
	StringBuffer result = new StringBuffer(); // thread-safe, mutable sequence of characters. 
	for(int i =0;i<ip.length;++i) {
		if(i>0) result.append(".");
		result.append(0xff & ip[i]);
	}
	return result.toString();
}
	
static void getRemoteAddress(String name,String serverName) {
	Socket sock;
	BufferedReader fromServer;
	PrintStream toServer;
	String textFromServer;
	
	try {
	
		sock = new Socket(serverName,1565); // create socket
		
		fromServer= new BufferedReader(new InputStreamReader(sock.getInputStream()));
		toServer = new PrintStream(sock.getOutputStream());
		
	
		toServer.println(name);
		toServer.flush();
		
	
		for(int i =1;i<=3;i++) {
			textFromServer = fromServer.readLine();  // convert buffer stream to string
			if(textFromServer !=null) System.out.println(textFromServer);
		} // why we do it three times?
		sock.close(); 
	}catch(IOException x) {
		System.out.println("Socket error.");
		x.printStackTrace();
	}
	
}

}
