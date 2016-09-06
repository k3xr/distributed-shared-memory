package dsm;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;


public class AlmacenImpl extends UnicastRemoteObject implements Almacen {

	private static final long serialVersionUID = 1L;
	private Hashtable<String, ObjetoCompartido> coleccionObjetosCompartidos = new Hashtable<String, ObjetoCompartido>();

	public AlmacenImpl() throws RemoteException {
	}

	public synchronized	List<ObjetoCompartido> leerObjetos(List<CabeceraObjetoCompartido> lcab) throws RemoteException{
		
		System.out.println("Leyendo..\n");
		
		List<ObjetoCompartido> listaParaDevolver = new LinkedList<ObjetoCompartido>();		
		Iterator<CabeceraObjetoCompartido> iteradorLista = lcab.iterator();
		while(iteradorLista.hasNext()){
			CabeceraObjetoCompartido elementoCliente = iteradorLista.next();
			System.out.println("Leyendo elemento "+ elementoCliente.getNombre()+ " ...\n");
			if(coleccionObjetosCompartidos.containsKey(elementoCliente.getNombre())){
				System.out.println("Se encuentra en el servidor\n");
				ObjetoCompartido elementoServidor = coleccionObjetosCompartidos.get(elementoCliente.getNombre());
				if(elementoCliente.getVersion()< elementoServidor.getCabecera().getVersion()){
					System.out.println("Anadiendo a lista para devolver\n");
					listaParaDevolver.add(elementoServidor);
				}
			}			
		}
		if(listaParaDevolver.isEmpty()){
			return null;
		}
		else{
			return listaParaDevolver;
		}
	}
	
	public synchronized void escribirObjetos(List<ObjetoCompartido> loc) throws RemoteException{
		
		System.out.println("Escribiendo..\n");
		
		Iterator<ObjetoCompartido> iteradorLista = loc.iterator();
		while(iteradorLista.hasNext()){
			ObjetoCompartido elementoCliente = iteradorLista.next();
			System.out.println("Escribiendo elemento"+ elementoCliente.getCabecera().getNombre()+ "...\n");
			coleccionObjetosCompartidos.put(elementoCliente.getCabecera().getNombre(), elementoCliente);
		}
	}
}

