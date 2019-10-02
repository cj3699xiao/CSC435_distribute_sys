/*
 CSC 435
 Yuchen Xiao
 Block Chain
 
 Please change file at line 115 to your file address before complie
 
 Run start.bat to start 3 process to together. 
 
 XML is in D://  (you can change it)
 
 2019/5/29
  
  PS: sometimes clients would suddenly stop, just restart all of them, it going to be fine. (maybe because some conflicts between them, I didnt figure it out) 
 * 
 * 
 */



import java.io.BufferedReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



public class BlockChain {
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	static int publickey_port = 4710;
	static int unverified_port = 4820;
	static int updatedBC_port = 4930;

	static int count; // count for bc verified

	static boolean calculation_flag = true;
	static boolean Toread = true;

	static Queue<String> bcqueue = new LinkedList<>();
	static String cur_data;

	static int q_len = 6; /* Number of requests for OpSys to queue */

	public static class BCLooperpk implements Runnable { // this one will listening to pkp
		static int port_pk;
//		static int port_ufdata;
//		static int port_updatebc;

//		BCLooperpk(int pkp) {
//			BCLooperpk.port_pk = pkp;
////			BCLooper.port_ufdata = ufp;
////			BCLooper.port_updatebc = ubcp;
//		}
		public void run() {

			try {
				ServerSocket servsock = new ServerSocket(port_pk, q_len);
				Socket sock;

				while (true) {
					sock = servsock.accept();
				}
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}
	}

	public static class BCLooperuf implements Runnable { // this one will listening to ufp
//		static int port_pk;
//		static int port_ufdata;
//		static int port_updatebc;

//		BCLooperuf(int ufp) {
//			BCLooperuf.port_ufdata = ufp;
//			BCLooper.port_ufdata = ufp;
//			BCLooper.port_updatebc = ubcp;
//		}
		public void run() {

			try {
				ServerSocket servsock = new ServerSocket(unverified_port, q_len);
				Socket sock;

				while (true) {
					System.out.println("uf is listening");
					sock = servsock.accept();
					new ufworker(sock).start();
				}
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}

		class ufworker extends Thread {
			Socket sock;

			ufworker(Socket c) {
				sock = c;
			}

			public void run() {
				if (Toread == false) {
					return;
				}

				try {
					File file = new File("D:\\Current Quater\\CS 435 Thur\\BlockChain\\BlockInput0.txt"); // here put
																											// your file
																											// address
					Scanner sc = new Scanner(file);

					while (sc.hasNextLine()) {
//					      System.out.println(sc.nextLine());
						bcqueue.add(sc.nextLine());
//					      
					}
					sc.close();

					Toread = false;
				} catch (Exception a) {

				}

			}

		}
	}

	public static class BCLooperbc implements Runnable { // this one will listening to ufp

		public void run() {

			try {
				ServerSocket servsock = new ServerSocket(updatedBC_port, q_len);
				Socket sock;

				while (calculation_flag) {
					sock = servsock.accept();
					new BCLooperbcWorker(sock).start();

				}
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}

		class BCLooperbcWorker extends Thread {
			Socket sock;

			BCLooperbcWorker(Socket b) {
				sock = b;
			}

			public void run() {
				PrintStream out = null;
				BufferedReader in = null;

				try {
					in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

					out = new PrintStream(sock.getOutputStream());

					String varified_data = in.readLine();
					String previous_seed = in.readLine();

					bcqueue.add(varified_data);
					// take next one
					if (count > 0) {
						cur_data = previous_seed + bcqueue.poll();

						count--;
					} else {
						calculation_flag = false;

					}

				} catch (IOException ioe) {
					System.out.println(ioe);
				}

			}

		}
	}

	public static void main(String a[]) throws IOException {
		if (a.length == 1) {
			try {

				publickey_port = Integer.parseInt(a[0]) + publickey_port;// first one 0 +4710
				unverified_port = Integer.parseInt(a[0]) + unverified_port;
				updatedBC_port = Integer.parseInt(a[0]) + updatedBC_port;

				System.out.println("publickey_port" + publickey_port);
				System.out.println("unverified_port" + unverified_port);
				System.out.println("updatedBC_port" + updatedBC_port);

				BCLooperpk BCLpk = new BCLooperpk();
				Thread b = new Thread(BCLpk);
				b.start();

				BCLooperuf BCLuf = new BCLooperuf();
				Thread c = new Thread(BCLuf);
				c.start();

				BCLooperbc BCLbc = new BCLooperbc();
				Thread d = new Thread(BCLbc);
				d.start();

				TimeUnit.SECONDS.sleep(3); // sleep before run the main, to make sure other 2 process have time to run

				for (int i = 0; i < 3; i++) {

					try {
						int aa = 4820 + i;
						Socket sock = new Socket("localhost", aa);
//				ServerSocket servsock = new ServerSocket(unverified_port, q_len);

//				BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
						PrintStream out = new PrintStream(sock.getOutputStream());
						out.println("hey ,data plz");
						sock.close();
					} catch (IOException e) {
						System.out.println(e);
					}
				}

//				System.out.println("here");

				TimeUnit.SECONDS.sleep(2); // wait for thread to put data

				count = bcqueue.size();


				cur_data = bcqueue.poll(); // get the first data

				while (calculation_flag) {
					TimeUnit.SECONDS.sleep(1);
					String now_data = cur_data;
					String randString = randomAlphaNumeric(8);// this seed
					String concatString = cur_data + randString;
					MessageDigest MD = MessageDigest.getInstance("SHA-256");
					byte[] bytesHash = MD.digest(concatString.getBytes("UTF-8"));
					String OutString = DatatypeConverter.printHexBinary(bytesHash);
					System.out.println("Hash is: " + OutString);
					int workNumber = Integer.parseInt(OutString.substring(0, 4), 16);
					System.out.println(
							"First 16 bits in Hex and Decimal: " + OutString.substring(0, 4) + " and " + workNumber);
					if (!(workNumber < 20000)) { // lower number = more work.
						System.out.format("%d is not less than 20,000 so we did not solve the puzzle\n\n", workNumber);

					} else {
						if (now_data != cur_data) {
							continue; // DO NOT SEND IF SOMEONE ELSE GOT IT
						}
						System.out.format("%d IS less than 20,000 so puzzle solved!\n", workNumber);
						System.out.println("The seed (puzzle answer) was: " + randString);
//						System.out.println(count);
//						System.out.println(cur_data);
						for (int i = 0; i < 3; i++) {

							try {
								int aa = 4930 + i;
								Socket sock = new Socket("localhost", aa);
//						ServerSocket servsock = new ServerSocket(unverified_port, q_len);

//						BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
								PrintStream out = new PrintStream(sock.getOutputStream());
								out.println(OutString);
								out.println(randString);
								sock.close();
							} catch (IOException e) {
								System.out.println(e);
							}

						}

					}

			
				}

				System.out.println("BC finished!!!!!");

			} catch (Exception e) {
				System.out.println(e);
			}
		} // if
		
		  Customer customer = new Customer();
	      customer.setb1(bcqueue.poll());
	      customer.setb2(bcqueue.poll());
	      customer.setb3(bcqueue.poll());
	      customer.setb4(bcqueue.poll());
		 try {

		        File file = new File("D:\\BlockchainLedgerSample.xml");  // address for xml, change it if you need.
		        JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
		        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		        // output pretty printed
		        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		        jaxbMarshaller.marshal(customer, file);
		        jaxbMarshaller.marshal(customer, System.out);

		          } catch (JAXBException e) {
		        e.printStackTrace();
		          }

	}

	public static String randomAlphaNumeric(int a) {
		StringBuilder builder = new StringBuilder();
		while (a-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
	
	@XmlRootElement
	public static class Customer {
		
	    String b1;
	    String b2;
	    String b3;
	    String b4;

	    public String getb1() {
	        return b1;
	    }

	    @XmlElement
	    public void setb1(String block1) {
	        this.b1 = block1;
	    }

	    public String getb2() {
	        return b2;
	    }

	    @XmlElement
	    public void setb2(String block2) {
	        this.b2 = block2;
	    }

	    public String getb3() {
	        return b3;
	    }

	    @XmlElement
	    public void setb3(String block3) {
	        this.b3 = block3;
	    }
	    
	    public String getb4() {
	        return b4;
	    }

	    @XmlElement
	    public void setb4(String block4) {
	        this.b4 = block4;
	    }



	} 

}


