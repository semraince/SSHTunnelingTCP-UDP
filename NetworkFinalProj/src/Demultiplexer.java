import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;



public class Demultiplexer {
	public Socket socket;
	private ConcurrentHashMap<Integer, Socket> connections = new ConcurrentHashMap<Integer, Socket>();
	private ConcurrentHashMap<Integer, DatagramSocket> udpconnections = new ConcurrentHashMap<Integer, DatagramSocket>();
	private OutputStream osToMultiplexer;
	private InputStream isFromMultiplexer;
	ArrayList<Biagram> connectionPorts;

	private Object replySynchronizer = new Object();
	public Demultiplexer(Socket socket,ArrayList<Biagram> connectports) {
		this.socket=socket;
		try {
			this.osToMultiplexer=socket.getOutputStream();
			this.isFromMultiplexer=socket.getInputStream();
			this.connectionPorts=connectports;
			GUI.setText("Server is started for "+socket.getLocalPort()+" "+socket.getPort());
			new RequestReceiver().start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private class ReplyReceiverTCP extends Thread{
		int num;
		Socket s;
		public ReplyReceiverTCP(int id,Socket a) {
			this.num=id;
			this.s=a;

		}
		@Override
		public void run() {
			while(true) {
				byte[] buffer = new byte[0xFFFF];
				int readBytes=0;
				try {
					while((readBytes= s.getInputStream().read(buffer))>0) {


						//System.out.println("id is "+sequence);
						System.out.println("reply read bytes in server "+ readBytes+" for id"+num);
						if(readBytes<1) {
							break;
						}
						synchronized (replySynchronizer) {



							osToMultiplexer.write(Util.intToByteArray(num),0,4);//send num
							System.out.println("reply server num is"+num);
							osToMultiplexer.flush();
							//toDemultiplexer.write(Util.intToByteArray(userApplication.getPort()),0,4);//client server port
							osToMultiplexer.write(Util.intToByteArray(readBytes),0,4);//length
							System.out.println("readBytes is"+readBytes);
							osToMultiplexer.flush();
							osToMultiplexer.write(Util.intToByteArray(Protocol.TCP.getProtocol()),0,4);//send protocol
							osToMultiplexer.flush();
							osToMultiplexer.write(buffer,0,readBytes);//send data
							osToMultiplexer.flush();
							GUI.setText("TCP Packet send to "+socket.getRemoteSocketAddress());



						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	private class RequestReceiver extends Thread{
		@Override
		public void run(){
			while(true) {
				int num;
				int protocol;
				int len;
				int connectionPort;
				byte[] message;
				try {
					protocol=isFromMultiplexer.read() << 24 | isFromMultiplexer.read() << 16 | isFromMultiplexer.read() << 8 | isFromMultiplexer.read();//Util.getInt(isFromMultiplexer);
					
					if(protocol==Protocol.TCP.getProtocol()) {
						connectionPort=isFromMultiplexer.read() << 24 | isFromMultiplexer.read() << 16 | isFromMultiplexer.read() << 8 | isFromMultiplexer.read();//Util.getInt(isFromMultiplexer);
						GUI.setText("Packet is received for TCP and its "+connectionPort);
						num=isFromMultiplexer.read() << 24 | isFromMultiplexer.read() << 16 | isFromMultiplexer.read() << 8 | isFromMultiplexer.read();//Util.getInt(isFromMultiplexer);
						if(num<0) {
							break;
						}
						System.out.println("in id server "+num);
						if(connections.get(num)==null) {
							Biagram biagram=new Biagram(connectionPort, protocol);//4790,protocol);//connectionPort, protocol);
							if(connectionPorts.contains(biagram)) {
								System.out.println(connectionPort);
								Socket a=new Socket("localhost",connectionPort);
								connections.put(num, a);
								new ReplyReceiverTCP(num,a).start();
								System.out.println("girdimm");
							}
							else {
								continue;
							}
						}
						


						len=Util.getInt(isFromMultiplexer);
						System.out.println("in server len for id " +len+" "+num);
						if(len<0) {
							connections.get(num).close();
							connections.remove(num);
						}
						else {
							//synchronized (requestSynchronizer) {
							message=Util.read(isFromMultiplexer, len);
							//System.out.println("in message is"+message+"len is"+len);
							//}
							String o=new String(message);
							System.out.println("bilgi "+protocol+" "+num+" "+" "+len+ " "+o);

							connections.get(num).getOutputStream().write(message);

						}
					}

					else if(protocol==Protocol.UDP.getProtocol()){//UDP
						connectionPort=isFromMultiplexer.read() << 24 | isFromMultiplexer.read() << 16 | isFromMultiplexer.read() << 8 | isFromMultiplexer.read();//Util.getInt(isFromMultiplexer);
						num=isFromMultiplexer.read() << 24 | isFromMultiplexer.read() << 16 | isFromMultiplexer.read() << 8 | isFromMultiplexer.read();//Util.getInt(isFromMultiplexer);
						GUI.setText("Packet is received for UDP and its "+connectionPort);
						System.out.println("in id server "+num);
						if(udpconnections.get(num)==null) {
							Biagram biagram=new Biagram(connectionPort, protocol);
							if(connectionPorts.contains(biagram)) {
								DatagramSocket ds=new DatagramSocket();
								new ReplyReceiverUDP(num,ds).start();;
								udpconnections.put(num, ds);
							}
							else {
								continue;
							}
						}
						len=Util.getInt(isFromMultiplexer);
						if(len<0) {
							udpconnections.get(num).close();
							udpconnections.remove(num);
						}
						else {
							//synchronized (requestSynchronizer) {
							message=Util.read(isFromMultiplexer, len);
							InetAddress IPAddress = InetAddress.getByName("localhost");
							DatagramPacket packet=new DatagramPacket(message, message.length,IPAddress,connectionPort);
							System.out.println("length is "+message.length);
							udpconnections.get(num).send(packet);
						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
	}
	private class ReplyReceiverUDP extends Thread{
		private DatagramSocket ds;
		int num;
		public ReplyReceiverUDP(int num,DatagramSocket ds) {
			this.ds=ds;
			this.num=num;
		}
		@Override
		public void run() {
			while(true) {
				byte[] receiveData = new byte[0xFFFF];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				try {
					ds.receive(receivePacket);
					int bytes=receivePacket.getLength();

					synchronized (replySynchronizer) {

						osToMultiplexer.write(Util.intToByteArray(num),0,4);//id
						System.out.println("reply server num is"+num);
						osToMultiplexer.flush();
						//toDemultiplexer.write(Util.intToByteArray(userApplication.getPort()),0,4);//client server port
						osToMultiplexer.write(Util.intToByteArray(bytes),0,4);//length
						System.out.println("readBytes is"+bytes);
						osToMultiplexer.flush();
						osToMultiplexer.write(Util.intToByteArray(Protocol.UDP.getProtocol()),0,4);//send protocol
						osToMultiplexer.flush();
						osToMultiplexer.write(receiveData,0,bytes);//send data
						osToMultiplexer.flush();
						GUI.setText("UDP Packet send to "+socket.getRemoteSocketAddress());



					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}




