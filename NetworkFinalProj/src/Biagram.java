
public class Biagram {


	private String port;
	private String protocol;
	
	public Biagram(int port,int protocol) {
		this.port=Integer.toString(port);
		this.protocol=Integer.toString(protocol);
		
	}
	public int getPort() {
		return Integer.parseInt(port);
	}
	public int getProtocol() {
		return Integer.parseInt(protocol);
	}
	
	@Override
	public int hashCode() {
		return port.hashCode() ^ protocol.hashCode() ;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Biagram) && ((Biagram) obj).port.equals(port)
				&& ((Biagram) obj).protocol.equals(protocol);
				
	}
}
