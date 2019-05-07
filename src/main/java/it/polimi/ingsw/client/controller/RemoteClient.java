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

	/**
	 * Remote reference to server
	 */
	ServerFunctionalities server;

	/**
	 * RMI registry
	 */
	Registry registry;

	/**
	 * Constructor
	 */
	public RemoteClient(ClientPlayer player) {
		super(player);
	}

	/**
	 * Connect client to a server at ip and port passed
	 * @param ip server ip
	 * @param port server port
	 */
	@Override
	public void connect(String ip, int port) {
		try {
			registry = LocateRegistry.getRegistry(ip, port);
			server = (ServerFunctionalities) (registry.lookup("rmiServer"));
			server.login(player.getNickname(),this.player);

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

	/**
	 * Disconnect client from server
	 */
	@Override
	public void disconnect() {

	}


}