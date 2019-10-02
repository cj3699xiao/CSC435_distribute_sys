package csc435;
/* 2012-05-20 Version 2.0

Yuchen Xiao


  -------------------------------------------------------------------------------*/


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Second Server for change port and increasing # of conversation
 *
 */
class AgentWorker extends Thread {
	
	Socket sock; // establish connection to Client
	agentHolder parentAgentHolder; //maintains agentstate holding socket and state counter
	int localPort; //port being used by this request
	
	//basic constructor
	AgentWorker (Socket s, int prt, agentHolder ah) { //ah is 3000+ port, without any change yet(1st 3001)
		sock = s;
		localPort = prt;
		parentAgentHolder = ah;
	}
	public void run() {// for start(), whenever receive user's submit  
		
		
		PrintStream out = null;
		BufferedReader in = null;
		//server is hardcoded in, only acceptable for this basic implementation
		
		String NewHost = "localhost";
		//port the main worker will run on
		int NewHostMainPort = 1565;// Here we try to go back 1565	
		
		String buf = "";
		int newPort;
		Socket clientSock;
		BufferedReader fromHostServer;
		PrintStream toHostServer;
		
		try {
			out = new PrintStream(sock.getOutputStream());
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			
			String inLine = in.readLine();
			
			StringBuilder htmlString = new StringBuilder();
			
			//log a request
			System.out.println();
			System.out.println("Request line: " + inLine); // display the request from user
			
			if(inLine.indexOf("migrate") > -1) { // if the user submit with migrate!!!
		
				
			// create a new socket at 1565, try to have a new connect with a 3000+ port (first one is 3001 and this one could be 3002)
				clientSock = new Socket(NewHost, NewHostMainPort);// 1565
				fromHostServer = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				//send a request to port 1565 to receive the next open port
				toHostServer = new PrintStream(clientSock.getOutputStream());
				toHostServer.println("Please host me. Send my port! [State=" + parentAgentHolder.agentState + "]"); 			
				// Here is for the State at agentlistener run() !!!
				toHostServer.flush();
				
				for(;;) {
				
					buf = fromHostServer.readLine();// get from the beginning worker, there will send out a new increament port #
					if(buf.indexOf("[Port=") > -1) {// until receive a Port
						break;
					}
				}
				
				//extract the port from the response
				String tempbuf = buf.substring( buf.indexOf("[Port=")+6   ,     buf.indexOf("]", buf.indexOf("[Port=")) );
			
				newPort = Integer.parseInt(tempbuf); // new port #
				
				System.out.println("newPort is: " + newPort);
				
				//this is the html response to send the user after he did the "migrate" submit
				htmlString.append(AgentListener.sendHTMLheader(newPort, NewHost, inLine));
				//Tell user, hey you are migrating now !
				htmlString.append("<h3>We are migrating to host " + newPort + "</h3> \n");
				htmlString.append("<h3>View the source of this page to see how the client is informed of the new location.</h3> \n");
		
				htmlString.append(AgentListener.sendHTMLsubmit());

				
				System.out.println("Killing parent listening loop.");// that port now stopped
				
				ServerSocket ss = parentAgentHolder.sock;
		
				ss.close();
				
				
			} else if(inLine.indexOf("person") > -1) { // normal case:
				// increase the agent
				parentAgentHolder.agentState++;
				//send back to user about the information about changes of the ports and status 
				htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
				htmlString.append("<h3>We are having a conversation with state   " + parentAgentHolder.agentState + "</h3>\n");
				htmlString.append(AgentListener.sendHTMLsubmit());

			} else {
				//the input from user is not valid, and then we print this massage to tell user
				htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
				htmlString.append("You have not entered a valid request!\n");
				htmlString.append(AgentListener.sendHTMLsubmit());		
				
		
			}
			//send it out
			AgentListener.sendHTMLtoStream(htmlString.toString(), out);
			
			
			sock.close();
			
			
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}
	
}
/**
 * for track the agentstate.
 */
class agentHolder {
	ServerSocket sock;
	int agentState;
	//basic constructor
	agentHolder(ServerSocket s) { sock = s;}
}
/**
 *first worker deal with starting cases (1565)
 *
 */
class AgentListener extends Thread { // make it a worker thread to serve for multiple users
	Socket sock;
	int localPort;
	
	//set a default agent state of 0, it will change if there is any state happens 
	int agentState = 0;
	
	//basic constructor
	AgentListener(Socket As, int prt) {
		sock = As;  // this sock for 1565
		localPort = prt;  // 3000+ port 
	}
	
	
	// for this start at main, go here directly 
	public void run() {
		BufferedReader in = null; 
		PrintStream out = null; // initialize
		String NewHost = "localhost";  //
		System.out.println("In AgentListener Thread");	
		
		try {
			String buf;
			out = new PrintStream(sock.getOutputStream());
			in =  new BufferedReader(new InputStreamReader(sock.getInputStream())); /// these two send and receive from socket
			
			//read first line
			buf = in.readLine(); // get the first line from the browser
			
			//if we have a state, parse the request and store it, from Agentwork run();
			if(buf != null && buf.indexOf("[State=") > -1) {// go here when input the migrate!!
			
				String tempbuf = buf.substring(     buf.indexOf("[State=")+7,     buf.indexOf("]", buf.indexOf("[State="))    ); // first and second var
				agentState = Integer.parseInt(tempbuf);
				System.out.println("agentState is: " + agentState);
					
			}
			
			System.out.println(buf);  // what we get when correctly from Browser
			
			//stringbuilder like Stringbuffer,  we use this to construct our send out statement
			StringBuilder htmlResponse = new StringBuilder();
			// like String but mutable, kind of like String buffer
			
			//output first request html to user
			//show the port and display the form. we know agentstate is 0 since game hasnt been started
			htmlResponse.append(sendHTMLheader(localPort, NewHost, buf)); // Localport is 3000+ port, newhost here is localhost
			
			
			htmlResponse.append("Now in Agent Looper starting Agent Listening Loop\n<br />\n");
			htmlResponse.append("[Port="+localPort+"]<br/>\n");
			htmlResponse.append(sendHTMLsubmit());
			
			sendHTMLtoStream(htmlResponse.toString(), out);
			
			//now open a connection at the port
			ServerSocket servsock = new ServerSocket(localPort,2);  // most of time 6, but this time 2. I don't know why
			
			//create a new agentholder and store the socket and agentState
			agentHolder agenthold = new agentHolder(servsock); // this agenthold for port 3000+
			agenthold.agentState = agentState;
			
			//wait for connections.
			while(true) {  
				// here we are listening to users' next command, after the connect with 1565
				// just wait when they click submit
				sock = servsock.accept();
				
				System.out.println("Got a connection to agent at port " + localPort);
				//connection received. create new agentworker object and start it up!
				new AgentWorker(sock, localPort, agenthold).start();
			}
		
		} catch(IOException ioe) {
			//this happens when an error occurs OR when we switch port
			System.out.println("Either connection failed, or just killed listener loop for agent at port " + localPort);
			System.out.println(ioe);
		}
	}
	//send the html header but NOT the response header
	//otherwise same as original implementation. Load html, load form,
	//add port to action attribute so the next request goes back to the port
	//or goes to the new one we are listening on
	static String sendHTMLheader(int localPort, String NewHost, String inLine) {
		
		StringBuilder htmlString = new StringBuilder();

		htmlString.append("<html><head> </head><body>\n");  // here is what will show on after user enter "localhost:1565"
		htmlString.append("<h2>This is for submission to PORT " + localPort + " on " + NewHost + "</h2>\n");
		htmlString.append("<h3>You sent: "+ inLine + "</h3>");
		htmlString.append("\n<form method=\"GET\" action=\"http://" + NewHost +":" + localPort + "\">\n");
		htmlString.append("Enter text or <i>migrate</i>:");
		htmlString.append("\n<input type=\"text\" name=\"person\" size=\"20\" value=\"YourTextInput\" /> <p>\n");
		
		return htmlString.toString();
	}
	
	static String sendHTMLsubmit() {
		return "<input type=\"submit\" value=\"Submit\"" + "</p>\n</form></body></html>\n"; // I guess this is html standard 
	}
	//send the response headers and calculate the content length so we play nicer with all browsers,
	//and can actually work with non ie browser
	
	static void sendHTMLtoStream(String html, PrintStream out) {  // this is standard sent back to firefox browser.
		
		out.println("HTTP/1.1 200 OK");
		out.println("Content-Length: " + html.length());
		out.println("Content-Type: text/html");
		out.println("");		
		out.println(html);
	}
	
}
/* 
 * Main is here!
 * */
public class HostServer {
	//we start listening on port 3001
	public static int NextPort = 3000;
	
	public static void main(String[] a) throws IOException {
		int q_len = 6;   // default value as we did before
		int port = 1565;	// Start at this port 
		Socket sock;	// Create connection 
		
		ServerSocket servsock = new ServerSocket(port, q_len);
		System.out.println("Yuchen Xiao's DIA Master receiver started at port 1565."); // NOW IS MINE ,LMAO
		System.out.println("Connect from 1 to 3 browsers using \"http:\\\\localhost:1565\"\n");
		
		//listen on port 1565 for new requests OR migrate requests
		
		while(true) {
			
			NextPort = NextPort + 1; // So we actually start at 3001, since it is increment each time, there won't be any conflict
			//open the socket and listening for oncoming requests
			sock = servsock.accept();
			
			System.out.println("Starting AgentListener at port " + NextPort);
			
			//new AgentListener is now at NextPort to wait for requests
			new AgentListener(sock, NextPort).start();  // give it sock of 1565 and port # for 3000+
		}
		
	}
}
