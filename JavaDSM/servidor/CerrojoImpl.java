package dsm;
import java.rmi.*;
import java.rmi.server.*;

class CerrojoImpl extends UnicastRemoteObject implements Cerrojo {

	private static final long serialVersionUID = 1L;

	private int lectores;
	private int escritores;

	CerrojoImpl() throws RemoteException {
	}

	public synchronized void adquirir (boolean exc) throws RemoteException {
		while (true) {
			if (exc) {
				if (escritores!=0 || lectores!=0) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} 
				else{
					escritores++;
					return;
				}

			}
			else if (escritores!=0) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				lectores++;
				return;
			}
		}
	}

	public synchronized boolean liberar() throws RemoteException {
		
		if (lectores==0 && escritores==0){
			return false;
		}
		else if(lectores!=0){
			lectores=lectores-1;
			if(lectores==0){
				notifyAll();
				return true;
			}
		}
		else{
			escritores=escritores-1;
			notifyAll();
			return true;
		}
		return false;
	}	
}
