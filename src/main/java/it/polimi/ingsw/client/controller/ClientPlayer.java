package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.AdrenalinaMatch;
import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.model.Cell;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * 
 */
public class ClientPlayer extends UnicastRemoteObject implements ClientFunctionalities{

	private static final int rmiPort = 52297;
	private static final int socketPort = 52298;

	/**
	 * Client nickname
	 */
	private String nickname;

	/**
	 * Reference to local adrenalina match
	 */
	private AdrenalinaMatch match;

	/**
	 * Reference to server connection
	 */
	private ServerConnection server;

	/**
	 * Constructor
	 * Initializes nickname and server connection
	 */
	public ClientPlayer(String nickname, ConnectionType connectionType,String ip, int port) throws RemoteException {
		super();
		this.nickname = nickname;

		if(connectionType == ConnectionType.RMI) this.server = new RemoteClient(this);
		else this.server = new SocketClient(this);

		server.connect(ip, port);

	}
	/**
	 * @return string with nickname
	 */
	public String getNickname(){
	    return nickname;
    }

	/**
	 * Select a player from a given list
	 * @param selectable list of players
	 * @return player from selectable
	 */
    @Override
    public Player playerSelection(List<Player> selectable) throws RemoteException {
        return null;
    }

	/**
	 * Select a Cell from a given list
	 * @param selectable list of cells
	 * @return cell from selectable
	 */
    @Override
    public Cell cellSelection(List<Cell> selectable) throws RemoteException {
        return null;
    }

	/**
	 * Select an action to make
	 * @return action to make
	 */
    @Override
    public TurnAction actionSelection() throws RemoteException {
        return null;
    }

	/**
	 * Main method to test connection
	 */
	public static void main(String[] args) {
		ConnectionType connectionType;
		int port;
		String ip = args[0];
		System.out.println(ip);
		Scanner in= new Scanner(System.in);
		System.out.println("Insert nickname: ");
		String nickname= in.next();
		System.out.println("Select connection type [0: SOCKET, 1: RMI]: ");
		if (Integer.parseInt(in.next()) == 0) {
			connectionType = ConnectionType.SOCKET;
			port = socketPort;
		} else {
			connectionType = ConnectionType.RMI;
			port = rmiPort;
		}

		try {
			ClientPlayer clientPlayer = new ClientPlayer(nickname, connectionType, ip, port);
		} catch (RemoteException e) {
			e.printStackTrace();
			exit(1);
		}
	}

}