
public class Router {

	private String distancia;
	private String destino;
	private String ruta;
	
	public Router(String destino, String distancia, String ruta){ 
		this.distancia = distancia;
		this.destino=destino;
		this.ruta=ruta;
	}
	
	
	
	public String getDistancia(){
		return this.distancia;
	}
	public String getDestino(){
		return this.destino;
	}
	public String getRuta(){
		return this.ruta;
	}
	
	public void setDistancia(String distancia){
		 this.distancia= distancia;
	}
	
	public void setDistancia(int distancia){
		 this.distancia= Integer.toString(distancia);
	}
	
	
	public void setDestino(String destino){
		 this.destino= destino;
	}
	public void setRuta(String ruta){
			this.ruta=ruta;
	}
}
