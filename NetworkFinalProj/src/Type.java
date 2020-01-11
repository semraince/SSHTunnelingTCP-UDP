public enum Type {

	Client(0),
	Server(1);
	
	private int type;

    Type(int proto) {
        this.type = proto;
    }

    public int getType() { 
        return type;
    }
}