public class TunnelConfig {
	private String name;
	private int type;
	private int listenPort;
	private int destinationPort;
	private String destinationIp;
	private int proto;
	private String fileName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getListenPort() {
		return listenPort;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}
	public int getDestinationPort() {
		return destinationPort;
	}
	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}
	public String getDestinationIp() {
		return destinationIp;
	}
	public void setDestinationIp(String destinationIp) {
		this.destinationIp = destinationIp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public int getProto() {
		return proto;
	}
	public void setProto(int proto) {
		this.proto = proto;
	}
	
}

