package dsm;

import java.net.MalformedURLException;
import java.rmi.*;
import java.util.*;

public class DSMCerrojo {
	
	private List<ObjetoCompartido> objetosCompartidos = new LinkedList<ObjetoCompartido>();
	private Cerrojo cerrojoAsociado;
	private FabricaCerrojos fabricaAsociada;
	private Almacen almacenAsociado;
	private boolean esExclusivo;
	
    public DSMCerrojo (String nom) throws RemoteException {
    	
    	String servidor = System.getenv("SERVIDOR");
		String puerto = System.getenv("PUERTO");
		try {
			fabricaAsociada = (FabricaCerrojos)Naming.lookup("rmi://"+servidor+":"+puerto+"/DSM_cerrojos");
			almacenAsociado = (Almacen)Naming.lookup("rmi://"+servidor+":"+puerto+"/DSM_almacen");
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
		
		cerrojoAsociado = fabricaAsociada.iniciar(nom);
    	
    }

    public void asociar(ObjetoCompartido o) {
    	objetosCompartidos.add(o);
    }
    
    public void desasociar(ObjetoCompartido o) {
    	String nombreParam = o.getCabecera().getNombre();
    	Iterator<ObjetoCompartido> iteradorLista = objetosCompartidos.iterator();
    	int contador = 0;
    	while(iteradorLista.hasNext()){
    		ObjetoCompartido elemento = iteradorLista.next();
    		String nombreElemento = elemento.getCabecera().getNombre();
    		if (nombreParam.equals(nombreElemento)) {
    			objetosCompartidos.remove(contador);
				break;
			}
    		contador++;
    	}
    	
    }
    
    
    private ObjetoCompartido buscarLista(String nombre){
    	Iterator<ObjetoCompartido> iteradorLista = objetosCompartidos.iterator();
    	while(iteradorLista.hasNext()){
    		ObjetoCompartido  elemento=iteradorLista.next();
    		if(elemento.getCabecera().getNombre().equals(nombre)){
    			return elemento;
    		}
    	}
		return null;
    }
    
    public boolean adquirir(boolean exc) throws RemoteException {
    	
    	esExclusivo = exc;
        cerrojoAsociado.adquirir(exc);
        
        List<CabeceraObjetoCompartido> listaCabecerasCliente = new LinkedList<CabeceraObjetoCompartido>();
        
        Iterator<ObjetoCompartido> iteradorLista = objetosCompartidos.iterator();
        while(iteradorLista.hasNext()){
        	listaCabecerasCliente.add(iteradorLista.next().getCabecera());
        }
        
    	List<ObjetoCompartido> listaObjetosCompartidosActualizados = almacenAsociado.leerObjetos(listaCabecerasCliente);    	
    	if(listaObjetosCompartidosActualizados!=null){
    		Iterator<ObjetoCompartido> iteradorNuevaLista = listaObjetosCompartidosActualizados.iterator();
    		while(iteradorNuevaLista.hasNext()){
    			ObjetoCompartido elementoNuevo = iteradorNuevaLista.next();
    			ObjetoCompartido elementoViejo = buscarLista(elementoNuevo.getCabecera().getNombre());
    			if(elementoViejo!=null){
    				elementoViejo.setVersion(elementoNuevo.getCabecera().getVersion());
    				elementoViejo.setObjeto(elementoNuevo.getObjeto());
    			}
    		}
    	}
    	return true;
    }
    
    public boolean liberar() throws RemoteException {
        if(esExclusivo){
        	 Iterator<ObjetoCompartido> iteradorLista = objetosCompartidos.iterator();
             while(iteradorLista.hasNext()){
             	iteradorLista.next().incVersion();
             }             
             almacenAsociado.escribirObjetos(objetosCompartidos);
        }
        
        return cerrojoAsociado.liberar();
   }
}
