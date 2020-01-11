
public class Diagram {
	private String ip;
	private String port;
	
	public Diagram(String ip,int port) {
		this.ip=ip;
		this.port=Integer.toString(port);
		
		
	}
	public int getPort() {
		return Integer.parseInt(port);
	}
	
	public String getIP() {
		return ip;
	}
	
	@Override
	public int hashCode() {
		return port.hashCode() ^  ip.hashCode() ;
	}
	

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Diagram) && ((Diagram) obj).ip.equals(ip)
				&& ((Diagram) obj).port.equals(port);
				
	}
}
