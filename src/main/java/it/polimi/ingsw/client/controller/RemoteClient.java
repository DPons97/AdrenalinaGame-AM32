package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.server.controller.ServerFunctionalities;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

/**
 * 
 */
public class RemoteClient extends ServerConnection {

	/**
	 * Remote reference to server
	 */
	private ServerFunctionalities server;

	/**
	 * RMI registry
	 */
	private Registry registry;

	private int port;

	private String ip;


	/**
	 * Constructor
	 */
	public RemoteClient(ClientPlayer player) {
		super(player);
		try {
			UnicastRemoteObject.exportObject(this.player, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Connect client to a server at ip and port passed
	 * @param ip server ip
	 * @param port server port
	 */
	@Override
	public void connect(String ip, int port) {
		this.ip = ip;
		this.port = port;
		try {
			System.out.println("Trying to connect via RMI");
			registry = LocateRegistry.getRegistry(ip, port);
			System.out.println("Connection OK. Looking up registry...");
			server = (ServerFunctionalities) (registry.lookup("rmiServer"));
			System.out.println("Registry OK. Logging in...");
			server.login(player.getNickname(), this.player);
			System.out.println("Logged");
			Thread t = new Thread(this::checkConnection);
			t.start();
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
		try {
			server.logout();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tells server that this client is ready
	 */
	@Override
	public void setReady() {
		try {
			server.ready(player.getNickname());
		} catch (RemoteException e) {
			// the disconnection is handled elsewhere
			return;
		}
	}

	/**
	 * Ask server to create a new game
	 * @param maxPlayers max players to set for this match
	 * @param maxDeaths max deaths to set forthis game
	 * @param mapID id of the map to use for this game
	 */
	@Override
	public void createGame(int maxPlayers, int maxDeaths, int turnDuration, int mapID) {
		try {
			server.createGame(player.getNickname(), maxPlayers,maxDeaths,turnDuration,mapID);
		} catch (RemoteException e) {
			// the disconnection is handled elsewhere
			return;
		}
	}

	/**
	 * Ask server to join a game
	 * @param nickname nickname of player joining the game
	 * @param id id of the match to join
	 */
	@Override
	public void joinGame(String nickname, int id) {
		try {
			server.joinGame(nickname,id);
		} catch (RemoteException e) {
			// the disconnection is handled elsewhere
			return;
		}
	}

	@Override
	public String updateLobby() {
		try {
			return server.updateLobby();
		} catch (RemoteException e) {
			// the disconnection is handled elsewhere
			return null;
		}
	}

	private void checkConnection(){
		while(true){
			try {
				server.ping(player.getNickname());
				TimeUnit.SECONDS.sleep(5);
			} catch (RemoteException e) {
				System.out.println("Connection lost");
				while (true){
					try {
						registry = LocateRegistry.getRegistry(ip, port);
						server = (ServerFunctionalities) (registry.lookup("rmiServer"));
						server.login(player.getNickname(), this.player);
						break;
					} catch (RemoteException | NotBoundException e1) {
						try {
							TimeUnit.SECONDS.sleep(5);
						} catch (InterruptedException e2) {
							e2.printStackTrace();
						}
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}