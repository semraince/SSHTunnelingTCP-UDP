public enum Protocol {

	TCP(0),
	UDP(1);
	
	private int proto;

    Protocol(int proto) {
        this.proto = proto;
    }

    public int getProtocol() { 
        return proto;
    }
}