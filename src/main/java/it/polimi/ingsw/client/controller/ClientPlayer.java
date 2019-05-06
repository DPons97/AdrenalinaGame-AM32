package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.AdrenalinaMatch;
import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.model.Cell;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * 
 */
public class ClientPlayer extends UnicastRemoteObject implements ClientFunctionalities{

	/**
	 *
	 */
	private String nickname;

	/**
	 *
	 */
	private AdrenalinaMatch match;

	/**
	 *
	 */
	private ServerConnection server;

	/**
	 * Default constructor
	 */
	public ClientPlayer(String nickname, ConnectionType connectionType,String ip, int port) throws RemoteException {
		super();
		this.nickname = nickname;

		if(connectionType == ConnectionType.RMI) this.server = new RemoteClient(this);
		else this.server = new SocketClient(this);

		server.connect(ip, port);

	}

	public String getNickname(){
	    return nickname;
    }

    @Override
    public Cell playerSelection(List<Player> selectable) throws RemoteException {
        return null;
    }

    @Override
    public Cell cellSelection(List<Cell> selectable) throws RemoteException {
        return null;
    }

    @Override
    public TurnAction actionSelection() throws RemoteException {
        return null;
    }

	/**
	 * 
	 */
	public static void main(String[] args) {
		String ip = args[0];
		int port= Integer.parseInt(args[1]);
		Scanner in= new Scanner(System.in);
		System.out.println("Insert nickname: ");
		String nickname= in.next();
		System.out.println("Select connection type [0: RMI, 1: SOCKET]: ");
		ConnectionType connectionType = (Integer.parseInt(in.next()) == 0) ? ConnectionType.SOCKET : ConnectionType.RMI;

		try {
			ClientPlayer clientPlayer = new ClientPlayer(nickname, connectionType, ip, port);
		} catch (RemoteException e) {
			e.printStackTrace();
			exit(1);
		}
	}

}