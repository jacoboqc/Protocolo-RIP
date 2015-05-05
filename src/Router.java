import java.io.Serializable;

public class Router implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int distancia;
	private String destino;
	private String ruta;
	private int prefijo;

	public Router(String destino, int distancia, String ruta, int prefijo) {
		this.distancia = distancia;
		this.destino = destino;
		this.ruta = ruta;
		this.prefijo = prefijo;
	}

	public int getDistancia() {
		return this.distancia;
	}

	public String getDestino() {
		return this.destino;
	}

	public String getRuta() {
		return this.ruta;
	}

	public int getPrefijo() {
		return prefijo;
	}

	public void setDistancia(int distancia) {
		this.distancia = distancia;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	public void setPrefijo(int prefijo) {
		this.prefijo = prefijo;
	}

	@Override
	public String toString() {
		String string = this.destino;
		if(this.prefijo!=0){
			string+="/" + this.prefijo;
		}
		string+=" - " +this.ruta + " - " + this.distancia; 
		return string;
	}
	
	
}
