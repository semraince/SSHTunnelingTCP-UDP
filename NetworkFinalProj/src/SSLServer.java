import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Properties;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class SSLServer extends Thread{
	//private SSLSocket socket;
	private  SSLServerSocket serverSocket;
	private int listenport;
	ArrayList<Biagram> connectports;
	
	int protocol;
	String connectIP;
	ServerSocket ss;
	public SSLServer(String copnnectIP,ArrayList<Biagram> connectports,int listenport) {
		this.connectIP=copnnectIP;
		this.listenport=listenport;
		this.connectports=connectports;
		
		ServerSocketFactory ssf = SSLServer.getServerSocketFactory("TLS");
		try {
			ss= ssf.createServerSocket(this.listenport);
			GUI.setText("Server port for Connect IP: "+connectIP+ " for listenPort "+listenport+" is started");
		}catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	public void run() {
		while(true) {
			Socket clientSocket;
			try {
				clientSocket = ss.accept();
				Demultiplexer a=new Demultiplexer(clientSocket,connectports);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private static ServerSocketFactory getServerSocketFactory(String type) {
		if (type.equals("TLS")) {
			SSLServerSocketFactory ssf = null;
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

				ssf = ctx.getServerSocketFactory();
				return ssf;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return ServerSocketFactory.getDefault();
		}
		return null;
	}

}