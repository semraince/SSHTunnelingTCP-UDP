import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Properties;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClient  {
	ArrayList<Biagram> listenports;

	private int protocol;
	private SSLSocket mServerSocket;
	int connectport;
	String connectIP;
	Multiplexer multiplexer;
	ServerSocket serverSocket;
	DatagramSocket clientSocket;

	public SSLClient(String connectIP,int connectport,ArrayList<Biagram> listenports) {
		this.connectIP=connectIP;
		this.listenports=listenports;
		this.connectport=connectport;


		Properties systemProps = System.getProperties();
		systemProps.put("javax.net.ssl.trustStore", "keystore.ImportKey");

		try {
			SSLSocketFactory factory = getSSLSocketFactory("TLS");
			mServerSocket =
					(SSLSocket)factory.createSocket(connectIP, connectport);
			mServerSocket.startHandshake();
			multiplexer=new Multiplexer(mServerSocket,this.protocol);
			GUI.setText("Client port for Connect IP: "+connectIP+ " for connectPort "+connectport+" is started");
			for(int i=0;i<listenports.size();i++) {
				ClientServer clientServer=new ClientServer(listenports.get(i).getPort(), listenports.get(i).getProtocol(), multiplexer);
				System.out.println("started+ "+listenports.get(i).getPort()+" "+listenports.get(i).getProtocol());
				clientServer.start();
				
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static SSLSocketFactory getSSLSocketFactory(String type) {
		if (type.equals("TLS")) {
			SocketFactory ssf = null;
			try {
				SSLContext ctx;
				KeyManagerFactory kmf;
				KeyStore ks;
				char[] passphrase = "importkey".toCharArray();

				ctx = SSLContext.getInstance("TLS");
				kmf = KeyManagerFactory.getInstance("SunX509");
				ks = KeyStore.getInstance("JKS");

				ks.load(new FileInputStream("keystore.ImportKey"), passphrase);
				kmf.init(ks, passphrase);

				ctx.init(kmf.getKeyManagers(), null, null);

				ssf = ctx.getSocketFactory();
				return (SSLSocketFactory) ssf;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return (SSLSocketFactory) SSLSocketFactory.getDefault();
		}
		return null;
	}
}
