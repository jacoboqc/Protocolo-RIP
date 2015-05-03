import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;


public class Main {

	public static void main(String[] args) {

		NetworkInterface interfaz = null;
		try {
			interfaz = NetworkInterface.getByName("wlan0");
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		Enumeration<InetAddress> enumDirs = interfaz.getInetAddresses();
		String IP = null, IPt = null;
		while(enumDirs.hasMoreElements()){
			IPt = enumDirs.nextElement().getHostAddress();
			if(IPt.startsWith("192")){
				IP=IPt;
			}
		}
		String nombreFich="ripconf-" + IP + ".txt";
		Scanner lectura = null;
		try {
			lectura= new Scanner(new FileInputStream(nombreFich));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ArrayList<String> listaConf = new ArrayList<String>();
		while(lectura.hasNext()){
			listaConf.add(lectura.next());
		}
		
	}
	

}
