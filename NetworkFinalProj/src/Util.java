import java.io.IOException;
import java.io.InputStream;

public class Util {
	public static byte[] intToByteArray(int number) {
		byte[] result=new byte[4];
		for(int i=0;i<4;i++) {
			result[3-i]=(byte)((number & (0xff << (i<<3))) >>>(i<<3));
					
		}
		return result;
	}
	public static int byteArrayToInt(byte[] byteArray) {
		int result=0;
		for(int i=0; (i<byteArray.length)&&(i<8);i++) {
			result |= (byteArray[3-i]&0xff)<<(i<<3);
		}
		return result;
	}
	public static int getInt(InputStream inputStream) throws IOException {
		
		int packetLen = inputStream.read() << 24 | inputStream.read() << 16 | inputStream.read() << 8 | inputStream.read();
		//return byteArrayToInt(result);
		return packetLen;
	}
	public static byte[] read(InputStream inputStream,int length) throws IOException {
		int bytesRead=0;
		byte[] result=new byte[length];
		int total=result.length;
		int remaining=total;
		int read=0;
		while(remaining>0) {
			read=inputStream.read(result,bytesRead,remaining);
			if(read==-1) {
				return null;
			}
			remaining-=read;
			bytesRead+=read;
		}
		return result;
		
	}

}
