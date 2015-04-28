import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Main {

	public static void main(String[] args) {

		String IP=null;
		try {
			IP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String nombreFich="ripconf-" + IP;
		try {
			Scanner lectura= new Scanner(new FileInputStream(nombreFich));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

}
