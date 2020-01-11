import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ClientServer extends Thread {
	ServerSocket serverSocket;
	int listenPort;
	Multiplexer multiplexer;
	int protocol;
	DatagramSocket clientSocket;
	public ClientServer(int port,int protocol,Multiplexer multiplexer) {
		this.listenPort=port;
		this.multiplexer=multiplexer;
		this.protocol=protocol;
		if(protocol==Protocol.TCP.getProtocol()) {
			try {
				serverSocket=new ServerSocket(listenPort);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	@Override
	public void run() {
		if(protocol==Protocol.TCP.getProtocol()) {
			while(true) {
				Socket server;
				try {
					server = serverSocket.accept();
					System.out.println("listen port is "+listenPort);
					multiplexer.addConnection(server);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		else {
			try {
				clientSocket=new DatagramSocket(this.listenPort);
				multiplexer.addConnection(clientSocket);
				
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
