
public class StreamUDP {
	private int id;
	private int port;
	public StreamUDP(int id,int port) {
		this.id=id;
		this.port=port;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof StreamUDP))
            return false;
        StreamUDP streamUDP = (StreamUDP) obj;
        return streamUDP.getId() == this.getId()
                && streamUDP.getPort() == this.getPort();
    }

    // commented    
      @Override
        public int hashCode() {
            int result=17;
            result=31*result+id+port;
            
            return result;
        }
     
}
