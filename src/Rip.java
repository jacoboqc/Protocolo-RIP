import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
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

		boolean corriendo = true;
		while (corriendo) {
			Iterator<Router> iterador = listaConf.iterator();
			while (iterador.hasNext()) {
				try {
					DatagramSocket socketUDP = new DatagramSocket();
					Router vecino = iterador.next();
					InetAddress IPvecino = InetAddress.getByName(vecino
							.getDestino());
					byte[] toByte = listaConf.toString().getBytes();
					DatagramPacket DatagramaEnviar = new DatagramPacket(toByte,
							toByte.length, IPvecino, 5000);
					socketUDP.send(DatagramaEnviar);
					socketUDP.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			boolean recibiendo = true;
			ArrayList<Router> listaRecib = new ArrayList<Router>();
			DatagramSocket socketUDP = null;
			try {
				socketUDP = new DatagramSocket(5000);
				byte[] buf = new byte[1000];
				DatagramPacket DatagramaRecibir = new DatagramPacket(buf,
						buf.length);
				Date horaInicio = new Date();
				socketUDP.setSoTimeout(10000);
				while (recibiendo) {
					socketUDP.receive(DatagramaRecibir);
					String recibido = new String(DatagramaRecibir.getData());
					String recibidoSub = recibido.substring(1).split("]")[0];
					String[] arrayRecibido = recibidoSub.split(", ");
					for (int i = 0; i < arrayRecibido.length; i++) {
						String[] vecinoString = arrayRecibido[i].split(" - ");
						listaRecib
								.add(new Router(vecinoString[0], Integer
										.parseInt(vecinoString[2]),
										vecinoString[1], 0)); // Cambiar esto
																// para subredes
					}
					Iterator<Router> itRecibida = listaRecib.iterator();
					ArrayList<Router> aAñadir = new ArrayList<Router>();
					while (itRecibida.hasNext()) {
						boolean iguales = false;
						Router vecino = itRecibida.next();
						Iterator<Router> itConf = listaConf.iterator();
						while (itConf.hasNext()) {
							Router elemento = itConf.next();
							if (elemento.getDestino().equals(
									vecino.getDestino())) {
								iguales = true;
							}
							if (iguales
									&& elemento.getDistancia() > vecino
											.getDistancia()) {
								elemento.setDistancia(vecino.getDistancia());
							}
							if (!iguales) {
								aAñadir.add(vecino);
							}

						}

					}
					Iterator<Router> itAñadir = aAñadir.iterator();
					while (itAñadir.hasNext()) {
						listaConf.add(itAñadir.next());
					}
					socketUDP
							.setSoTimeout((int) (10000 - (new Date().getTime() - horaInicio
									.getTime())));
				}

			} catch (SocketTimeoutException e) {
				recibiendo = false;
				socketUDP.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Iterator<Router> itImprimir = listaConf.iterator();
			while (itImprimir.hasNext()) {
				System.out.println(itImprimir.next().toString());
			}
			System.out.println();

		}

	}

}
