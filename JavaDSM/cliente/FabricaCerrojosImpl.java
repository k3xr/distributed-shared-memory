package dsm;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class FabricaCerrojosImpl extends UnicastRemoteObject implements FabricaCerrojos {

   
	private static final long serialVersionUID = 1L;
	//Asociacion de Nombre con el cerrojo
	private Hashtable<String, Cerrojo> coleccionCerrojos  = new Hashtable<String, Cerrojo>();
	
	public FabricaCerrojosImpl() throws RemoteException {
		
    }
	
    public synchronized	Cerrojo iniciar(String nombreCerrojo) throws RemoteException {
		
    	//Si no existe un cerrojo con el mismo nombre se crea y agrega
    	if(!coleccionCerrojos.containsKey(nombreCerrojo)){
    		Cerrojo nuevoCerrojo = new CerrojoImpl();
    		coleccionCerrojos.put(nombreCerrojo, nuevoCerrojo);
    		return nuevoCerrojo;
    	}
    	//En caso de que existira se devuelve
    	else{
    		return coleccionCerrojos.get(nombreCerrojo);
    	}
    }
}

