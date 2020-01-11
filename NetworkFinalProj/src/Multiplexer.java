import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLSocket;



public class Multiplexer {
	private Object requestSynchronizer = new Object();
	private Object receiversynch=new Object();
	private Object idsynch=new Object();
	private OutputStream toDemultiplexer;
	private InputStream fromDemultiplexer;
	SSLSocket sslSocket;
	DatagramSocket udpSocket;
	private ConcurrentHashMap<Integer, Socket> connections = new ConcurrentHashMap<Integer, Socket>();
	private ArrayList<Integer> udpPorts=new ArrayList<Integer>();
	int id;
	int protocol;
	public Multiplexer(SSLSocket mServerSocket,int protocol) {

		id=0;
		this.sslSocket=mServerSocket;
		this.protocol=protocol;
		initialize();
	}
	//private ConcurrentHashMap<Integer, Socket> connections = new ConcurrentHashMap<Integer, Socket>(); // multiplexed connections
	/** Add new connection that shall be multiplexed. */
	public void addConnection(Socket socketToMultiplex) {
		GUI.setText("New connection added for "+socketToMultiplex.getLocalPort()+sslSocket.getRemoteSocketAddress());
		new RequestForwarder(socketToMultiplex).start();
		//new ReplyForwarder().start();
	}
	public void addConnection(DatagramSocket socketToMultiplex) {
		this.udpSocket=socketToMultiplex;
		new RequestForwarderUDP(socketToMultiplex).start();
		//new ReplyForwarder().start();
	}

	private void initialize() {
		try {
			this.fromDemultiplexer=sslSocket.getInputStream();
			this.toDemultiplexer=sslSocket.getOutputStream();
			new ReplyForwarder().start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class RequestForwarder extends Thread {


		Socket userApplication;
		InputStream fromUserApplication;
		int sequence;

		public RequestForwarder(Socket userApplication) {
			this.userApplication = userApplication;
			System.out.println("server id is "+id);
			synchronized (idsynch) {
				sequence=id+1;
				id++;
			}
			connections.put(sequence,userApplication);

			//connections.put(id, userApplication);
		}
		@Override
		public void run() {
			try {
				this.fromUserApplication = userApplication.getInputStream();
				while(true) {
					byte[] buffer = new byte[0xFFFF];
					int readBytes=0;
					while((readBytes= fromUserApplication.read(buffer))>0) {


						if(readBytes<1) {
							break;
						}
						synchronized (requestSynchronizer) {
							//new
							toDemultiplexer.write(Util.intToByteArray(Protocol.TCP.getProtocol()),0,4);//send protocol
							toDemultiplexer.flush();
							toDemultiplexer.write(Util.intToByteArray(userApplication.getLocalPort()),0,4);//send connection port
							toDemultiplexer.flush();
							//and new
							toDemultiplexer.write(Util.intToByteArray(sequence),0,4);//id number
							toDemultiplexer.flush();
							//toDemultiplexer.write(Util.intToByteArray(userApplication.getPort()),0,4);//client server port
							toDemultiplexer.write(Util.intToByteArray(readBytes),0,4);//length
							toDemultiplexer.flush();
							toDemultiplexer.write(buffer,0,readBytes);//send data
							toDemultiplexer.flush();
							GUI.setText("TCP Packet send to "+userApplication.getLocalPort()+" "+sslSocket.getRemoteSocketAddress());

						}
					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private class ReplyForwarder extends Thread{
		@Override
		public void run(){
			while(true) {
				int num;
				int len;
				byte[] message;
				int protocol;
				try {
					synchronized (receiversynch) {


						num=fromDemultiplexer.read() << 24 | fromDemultiplexer.read() << 16 | fromDemultiplexer.read() << 8 | fromDemultiplexer.read();//Util.getInt(isFromMultiplexer);
						System.out.println("in id client  reply "+num);

						len=fromDemultiplexer.read() << 24 | fromDemultiplexer.read() << 16 | fromDemultiplexer.read() << 8 | fromDemultiplexer.read();//Util.getInt(fromDemultiplexer);
						protocol=fromDemultiplexer.read() << 24 | fromDemultiplexer.read() << 16 | fromDemultiplexer.read() << 8 | fromDemultiplexer.read();//Util.getInt(fromDemultiplexer);
						if(len<0&&protocol==Protocol.TCP.getProtocol()) {
							connections.get(num).close();
							connections.remove(num);
						}
						else {
							//synchronized (requestSynchronizer) {
							System.out.println("id len"+num+" "+len);
							message=Util.read(fromDemultiplexer, len);
							String str=new String(message);
							//System.out.println("in message is"+message+"len is"+len);
							//}
							if(protocol==Protocol.TCP.getProtocol()) {
								Socket s=connections.get(num);
								if(s==null|| message==null) {
									continue;
								}
								OutputStream os=s.getOutputStream();
								os.write(message);
								os.flush();
								GUI.setText("TCP Packet received and send  to "+s.getLocalPort()+" "+s.getPort());

							}
							else {

								udpSocket.send(new DatagramPacket(message, message.length,InetAddress.getByName("localhost"),num));
								GUI.setText("UDP Packet received and send  to "+num);

							}
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	private class RequestForwarderUDP extends Thread{
		DatagramSocket ds;
		public RequestForwarderUDP(DatagramSocket ds) {
			this.ds=ds;
		}
		@Override
		public void run() {
			while(true) {
				byte[] receiveData=new byte[0xFFFF];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				try {
					ds.receive(receivePacket);
					int port = receivePacket.getPort();
					int bytes=receivePacket.getLength();
					if(udpPorts.contains(port)) {
						udpPorts.add(port);
					}
					synchronized (requestSynchronizer) {
						//added new
						toDemultiplexer.write(Util.intToByteArray(Protocol.UDP.getProtocol()),0,4);//send protocol
						toDemultiplexer.flush();
						toDemultiplexer.write(Util.intToByteArray(ds.getLocalPort()),0,4);//send connection port
						toDemultiplexer.flush();
						//end 
						toDemultiplexer.write(Util.intToByteArray(port),0,4);//id number
						toDemultiplexer.flush();
						//toDemultiplexer.write(Util.intToByteArray(userApplication.getPort()),0,4);//client server port
						toDemultiplexer.write(Util.intToByteArray(bytes),0,4);//length
						toDemultiplexer.flush();
						toDemultiplexer.write(receiveData,0,bytes);//send data
						toDemultiplexer.flush();
						GUI.setText("UDP Packet send to "+ds.getLocalPort()+" from "+sslSocket.getRemoteSocketAddress());

					}


				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

}



