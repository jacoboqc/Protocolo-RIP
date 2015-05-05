import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
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
		ArrayList<Router> listaConf = new ArrayList<Router>();
		while(lectura.hasNext()){
			String linea=lectura.next();
			String[] separadas = linea.split("/");
			if(separadas.length==2){
				listaConf.add(new Router(separadas[0], 1, IP, Integer.parseInt(separadas[1])));
			}else{
				listaConf.add(new Router(separadas[0], 1, IP, 0));
			}
		}
		lectura.close();
		

		ServerSocket socket_servidor = null;
		Socket socket_conexion;
		boolean corriendo=true;
		
		while(corriendo){
			
			try {
				socket_servidor = new ServerSocket(50);
				socket_conexion = new Socket();
				socket_conexion = socket_servidor.accept();
				ObjectOutputStream salida = new ObjectOutputStream(socket_conexion.getOutputStream());
				salida.writeObject(listaConf);
				salida.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			Iterator<Router> iterador = listaConf.iterator();
			while(iterador.hasNext()){
				try {
					socket_conexion = new Socket(iterador.next().getDestino(), 50);
					ObjectInputStream entrada = new ObjectInputStream(socket_conexion.getInputStream());
					Object listaObject = entrada.readObject();
					ArrayList<Router> listaVecino = (ArrayList<Router>) listaObject;
					
					//Aqui habria que chequear la tabla recibida...
					
					entrada.close();
					
					
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			//Imprimir mi tabla aqui
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			
		}
		
		
		
	}
	

}
