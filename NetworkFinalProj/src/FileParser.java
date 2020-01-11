import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileParser {
	
	public ArrayList<TunnelConfig> parse(String name) {
		ArrayList<TunnelConfig> list=new ArrayList<>();
		File file = new File(name);
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			while(scanner.hasNext()) {
				String line=scanner.nextLine();
				if ( line.trim().length() == 0 ) {
					continue;  // Skip blank lines
				}
				System.out.println(line);
				String parts[];
				TunnelConfig a=new TunnelConfig();
				for(int i=0;i<6;i++) {
					line=scanner.nextLine();
					if ( line.trim().length() == 0 ) {
						i--;
						continue;  // Skip blank lines
					}
					parts=line.split("=");
					assign(parts[0].trim(),parts[1].trim(),a);

				}
				list.add(a);

			}
			

			scanner.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}
	private void assign(String key,String value,TunnelConfig t) {

		if(key.equalsIgnoreCase("client")){
			System.out.println("client: "+value);
			if(value.equalsIgnoreCase("Yes")) {
				t.setType(Type.Client.getType());
			}
			else if(value.equalsIgnoreCase("No")){
				t.setType(Type.Server.getType());
			}
			//t.setType(Type.valueOf(value).getType());	
			
		}		
		else if(key.equalsIgnoreCase("ListenPort")){
			System.out.println("ListenPort "+value);
			t.setListenPort(Integer.parseInt(value));
		}	
		else if(key.equalsIgnoreCase("DestionationIP")){
			System.out.println("DestionationIP "+value);
			t.setDestinationIp(value);
		}
		else if(key.equalsIgnoreCase("DestinationPort")){
			System.out.println("DestionationPort "+value);
			t.setDestinationPort(Integer.parseInt(value));
			
		}
		else if(key.equalsIgnoreCase("Proto")){
			System.out.println("Proto "+value);
			if(value.equalsIgnoreCase("TCP")) {
				t.setProto(Protocol.TCP.getProtocol());
			}
			else if(value.equalsIgnoreCase("UDP")) {
				t.setProto(Protocol.UDP.getProtocol());
			}
			
		
		}
		else if(key.equalsIgnoreCase("Key")){
			System.out.println("Key "+value);
			t.setFileName(value);
			
		}

	}

	public static void main(String[] args) {
		FileParser parser=new FileParser();
		parser.parse("config.txt");
	}

}
