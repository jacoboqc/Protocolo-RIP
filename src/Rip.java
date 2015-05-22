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

public class Rip {

	public static void main(String[] args) {

		NetworkInterface interfaz = null;
		try {
			interfaz = NetworkInterface.getByName("wlan0");
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		Enumeration<InetAddress> enumDirs = interfaz.getInetAddresses();
		String IP = null, IPt = null;
		while (enumDirs.hasMoreElements()) {
			IPt = enumDirs.nextElement().getHostAddress();
			if (IPt.startsWith("192")) {
				IP = IPt;
			}
		}
		String nombreFich = "ripconf-" + IP + ".txt";
		Scanner lectura = null;
		try {
			lectura = new Scanner(new FileInputStream(nombreFich));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ArrayList<Router> listaConf = new ArrayList<Router>();
		while (lectura.hasNext()) {
			String linea = lectura.next();
			String[] separadas = linea.split("/");
			if (separadas.length == 2) {
				listaConf.add(new Router(separadas[0], 1, IP, Integer
						.parseInt(separadas[1])));
			} else {
				listaConf.add(new Router(separadas[0], 1, IP, 0));
			}
		}
		lectura.close();

		ServerSocket socket_servidor = null;
		Socket socket_conexion = null;
		boolean corriendo = true;

		while (corriendo) {

			Iterator<Router> iterador = listaConf.iterator();
			while (iterador.hasNext()) {
				try {
					socket_conexion = new Socket(iterador.next().getDestino(),
							5000);
					
					ObjectOutputStream salida = new ObjectOutputStream(
							socket_conexion.getOutputStream());
					salida.writeObject(listaConf);
					salida.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				socket_servidor = new ServerSocket(5000);
				socket_conexion = new Socket();
				socket_conexion = socket_servidor.accept();
				ObjectInputStream entrada = new ObjectInputStream(
						socket_conexion.getInputStream());
				Object listaObject = entrada.readObject();
				@SuppressWarnings("unchecked")
				ArrayList<Router> listaVecino = (ArrayList<Router>) listaObject;

				Iterator<Router> itVecinos = listaVecino.iterator();
				while (itVecinos.hasNext()) {
					boolean iguales = false;
					Router vecino = itVecinos.next();
					Iterator<Router> itConf = listaConf.iterator();
					while (itConf.hasNext()) {
						Router elemento = itConf.next();
						if (elemento.getDestino().equals(
								vecino.getDestino())) {
							iguales = true;
						} else if (iguales
								&& elemento.getDistancia() < vecino
										.getDistancia()) {
							elemento.setDistancia(vecino.getDistancia());
						}
						if (!iguales) {
							listaConf.add(vecino);
						}

					}
				}
				entrada.close();
				socket_conexion.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

			Iterator<Router> itImprimir = listaConf.iterator();
			while (itImprimir.hasNext()) {
				System.out.println(itImprimir.next().toString());
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

}
