package csc435;

import java.io.*; // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

class Webworker extends Thread { // Class definition
	static Socket sock; 

	static String contentType;
	
	static String totalout;

	Webworker(Socket theclient) {
		sock = theclient;
	}

	private static String checkfile(String file) {
		if (file.endsWith(".html")){
			return "text/html";
		}else if (file.endsWith(".txt") || file.endsWith(".java")) {
			return "text/plain";
		}else {
			return "text/plain";
		}
	}

	public void run() {

		PrintStream out = null;
		BufferedReader in = null;

		try {
			out = new PrintStream(sock.getOutputStream());
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			String sockdata = in.readLine();
			System.out.println(sockdata);
			System.out.flush();

// get out if no request
			if (sockdata == null)
				return;

			String[] B = sockdata.split("\\s+");
			String filename = B[1];
			// zhe li you ge dian !!!!
			String thepath = "." + filename;
//			System.out.println(filename);

			if (!thepath.contains("cgi") && thepath.endsWith("/")) {

				contentType = "text/html";

				contentType = checkfile(filename);

				File f2 = new File("./" + filename + "/");
				File[] strFilesDirs = f2.listFiles();

				out.println("HTTP/1.1 200 OK"); 
				out.print("Content-Length: " + f2.length());
				out.print("Content-type: " + contentType + "\r\n\r\n");

				// I spend a long time on this part, like 4 or 5 days. But without any progress. it doesn't work
				if (strFilesDirs != null) {
				for (int i = 0; i < strFilesDirs.length; i++) {
					String curfile = strFilesDirs[i].getName();				
						totalout += "<a href=\"./" + curfile + "\">" + curfile + "</a>\n";
				}
				}
				

			} else if(thepath.contains("cgi")) { // add num
				String info = filename.substring(22);
				String[] cur_info = info.split("[=&]");
				
				String ans = "Hey " + cur_info[1] +" sum of " + cur_info[3] + "," +cur_info[5] + "is :" + (cur_info[3]+cur_info[5]);
				
				out.print("HTTP/1.1 200 OK");
				out.print("Content-Length: " + 256);
				out.print("Content-type: " + contentType + "\r\n\r\n");
				out.print("<p>" + ans + "</p>");
			
			}else { // read dog and cat 



				File f1 = new File(filename);


				out.println("HTTP/1.1 200 OK"); 
				out.print("Content-Length: " + f1.length());
				out.print("Content-type: " + contentType + "\r\n\r\n");
				if(f1.exists()) {
				String output = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);

				out.print(output);
				}
			}

		} catch (IOException x) {
			x.printStackTrace();

		}
		
		out.print(totalout);
		
	}
}

public class MywebServer {

	public static void main(String a[]) throws IOException {
		int q_len = 6; /* Number of requests for OpSys to queue */
		int port = 2540;
		Socket sock;

		ServerSocket servsock = new ServerSocket(port, q_len);

		System.out.println("Yuchen Xiao's WebServer is listening at port 2540.\n");
		while (true) {
			// wait for the next client connection:
			sock = servsock.accept();
			new Webworker(sock).start();

		
		}

	}
}