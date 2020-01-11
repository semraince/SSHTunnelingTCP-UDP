import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Initializer extends Thread {
	private ArrayList<TunnelConfig> configs;
	HashMap<Diagram,ArrayList<Biagram>> clientsockets;
	HashMap<Diagram,ArrayList<Biagram>> serversockets;
	@Override
	public void run() {
		clientsockets=new HashMap<Diagram,ArrayList<Biagram>>();
		serversockets=new HashMap<Diagram, ArrayList<Biagram>>();
		FileParser fileParser=new FileParser();
		configs=fileParser.parse("config.txt");
		for(TunnelConfig config:configs) {

			if(config.getType()==Type.Client.getType()) {
				System.out.println("girdimmm");
				Diagram diagram=new Diagram(config.getDestinationIp(), config.getDestinationPort());
				if(clientsockets.containsKey(diagram)==false) {
					ArrayList<Biagram> arraylist = new ArrayList<Biagram>();
					arraylist.add(new Biagram(config.getListenPort(),config.getProto()));
					clientsockets.put(diagram, arraylist);
					
				}
				else {
					clientsockets.get(diagram).add(new Biagram(config.getListenPort(), config.getProto()));
					
				}

			}

			else if(config.getType()==Type.Server.getType()) {
				Diagram diagram=new Diagram(config.getDestinationIp(), config.getListenPort());
				if(serversockets.containsKey(diagram)==false) {
					ArrayList<Biagram> arraylist = new ArrayList<Biagram>();
					arraylist.add(new Biagram(config.getDestinationPort(),config.getProto()));
					serversockets.put(diagram, arraylist);
				}
				else {
					serversockets.get(diagram).add(new Biagram(config.getDestinationPort(), config.getProto()));
				}


			}



		}
		for (Map.Entry<Diagram,ArrayList<Biagram>> mapElement : clientsockets.entrySet()) { 
			Diagram key = (Diagram)mapElement.getKey(); 
			System.out.println("Client");
			
			ArrayList<Biagram> listenports = (ArrayList<Biagram>)mapElement.getValue() ;
			System.out.println("port is: "+key.getPort()+" ip is " +key.getIP()+" listen ports is ");
			for(Biagram b: listenports) {
				System.out.println(b.getPort()+" "+b.getProtocol());
			}

			SSLClient sslClient = new SSLClient(key.getIP(),key.getPort(),listenports);

		}


		for (Map.Entry<Diagram,ArrayList<Biagram>> mapElement : serversockets.entrySet()) { 
			Diagram key = (Diagram)mapElement.getKey(); 


			ArrayList<Biagram> connectports = (ArrayList<Biagram>)mapElement.getValue() ;
			System.out.println("port is: "+key.getPort()+" ip is " +key.getIP()+" connect ports is ");
			for(Biagram b: connectports) {
				System.out.println(b.getPort()+" "+b.getProtocol());
			}
			SSLServer sslServer = new SSLServer(key.getIP(),connectports,key.getPort());
			sslServer.start();

		}

	}
}

