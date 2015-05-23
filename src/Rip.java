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

		String nombItfaz = "wlan0";
		String password = null;
		try{
			password = args[0] + ";";
		} catch (ArrayIndexOutOfBoundsException e){
			System.out.println("Argumentos faltantes: indique password.");
			System.exit(1);
		}

		System.out.println("Inicializando protocolo RIP...");
		System.out.print("Obteniendo dirección IPv4 desde la interfaz "
				+ nombItfaz + "...");

		NetworkInterface interfaz = null;
		try {
			interfaz = NetworkInterface.getByName(nombItfaz);
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

		System.out.println(" Hecho.");

		String nombreFich = "ripconf-" + IP + ".txt";
		System.out.print("Leyendo fichero de configuración " + nombreFich
				+ "...");

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

		System.out.println(" Hecho.");
		System.out.println("Conectando y actualizando vector de distancias.");
		System.out.println("\nDestino - Next-hop - Distancia");
		DatagramSocket socketServidor = null;
		try {
			socketServidor = new DatagramSocket(5000);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		Iterator<Router> itImprimir = listaConf.iterator();
		while (itImprimir.hasNext()) {
			System.out.println(itImprimir.next().toString());
		}

		boolean corriendo = true;
		while (corriendo) {
			Iterator<Router> iterador = listaConf.iterator();
			while (iterador.hasNext()) {
				try {
					DatagramSocket socketUDP = new DatagramSocket();
					Router vecino = iterador.next();
					InetAddress IPvecino = InetAddress.getByName(vecino
							.getDestino());
					byte[] toByte = password.concat(listaConf.toString()).getBytes();
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
			try {
				byte[] buf = new byte[1000];
				DatagramPacket DatagramaRecibir = new DatagramPacket(buf,
						buf.length);
				Date horaInicio = new Date();
				socketServidor.setSoTimeout(10000);
				recibiendo: while (recibiendo) {
					socketServidor.receive(DatagramaRecibir);
					String IPrecibida = DatagramaRecibir.getAddress().toString().substring(1);
					String recibido = new String(DatagramaRecibir.getData());
					String[] recibidoSub = recibido.split(";");
					String passRecib = recibidoSub[0];
					if (!passRecib.equals(args[0])){
						System.out.println("La contraseña de la trama recibida es incorrecta");
						break recibiendo;
					}
					String recibidoLista = recibidoSub[1].substring(1).split("]")[0];
					String[] arrayRecibido = recibidoLista.split(", ");
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
						Router vecino = itRecibida.next();
						boolean conocido = false;
						if (vecino.getDestino().equals(IP)) {
						}else {
							int nuevaDistancia = vecino.getDistancia()+1;
							Iterator<Router> itConf = listaConf.iterator();
							while (itConf.hasNext()) {
								Router elemento = itConf.next();
								if(elemento.getDestino().equals(vecino.getDestino())){
									conocido = true;
									if(nuevaDistancia<elemento.getDistancia()){
										elemento.setDistancia(nuevaDistancia);
									}
								}
							}
							if(!conocido){
								vecino.setRuta(IPrecibida);
								vecino.setDistancia(vecino.getDistancia()+1);
								listaConf.add(vecino);
							}

						}
						
					}
					Iterator<Router> itAñadir = aAñadir.iterator();
					while (itAñadir.hasNext()) {
						listaConf.add(itAñadir.next());
					}
					socketServidor.setSoTimeout((int) (10000 - (new Date()
							.getTime() - horaInicio.getTime())));
				}

			} catch (SocketTimeoutException e) {
				recibiendo = false;
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println();
			Iterator<Router> itImprimirr = listaConf.iterator();
			while (itImprimirr.hasNext()) {
				System.out.println(itImprimirr.next().toString());
			}

		}

	}

}
