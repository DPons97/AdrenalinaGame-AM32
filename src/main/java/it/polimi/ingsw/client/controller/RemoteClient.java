package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.server.controller.ServerFunctionalities;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.lang.System.exit;

/**
 * 
 */
public class RemoteClient extends ServerConnection {
	ServerFunctionalities server;
	Registry registry;
	String serverAddress = "192.168.56.1";
	String serverPort = "3232";

	/**
	 * Default constructor
	 */
	public RemoteClient(ClientPlayer player) {
		super(player);
	}

	@Override
	public void connect(String ip, int port, String nickname) {
		try {
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(serverPort)).intValue());
			server = (ServerFunctionalities) (registry.lookup("rmiServer"));
			server.login(nickname,this.player);

		} catch (RemoteException e)
		{
			e.printStackTrace();
			exit(1);
		}
		catch (NotBoundException e)
		{
			System.err.println(e);
			exit(1);
		}
	}

	@Override
	public void disconnect() {

	}


}