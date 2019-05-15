package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

/**
 * Handles incoming connection from both rmi and socket registering the new client accordingly
 */
public class LoginHandler extends UnicastRemoteObject implements ServerFunctionalities{

	private static String address;
	private static Registry registry;
	private ClientFunctionalities client;

	/**
	 *  max connections number
	 */
	private int maxConnections;

    /**
     * ip address
     */
    private String ip;

    /**
     * socket port
     */
    private final int socketPort=52298;

    /**
     * rmi port
     */
    private final int rmiPort=52297;

	/**
	 * controller of the lobby
	 */
	private LobbyController lobby;

	/**
	 * socket opened by the server
	 */
	private ServerSocket serverSocket;

	/**
	 * Default constructor
	 * Registers self in rmi register and opens socket
	 */
	public LoginHandler() throws RemoteException {
		super();

		this.lobby = new LobbyController();

        try {
            serverSocket= new ServerSocket(socketPort);
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }

		try
		{
			address = (InetAddress.getLocalHost()).toString();
		}
		catch (Exception e)
		{
			System.out.println("can't get inet address.");
		}
		System.out.println("this address=" + address + ",port=" + rmiPort);
		try
		{
			registry = LocateRegistry.createRegistry(rmiPort);
			registry.rebind("rmiServer", this);
		}
		catch (RemoteException e)
		{
			System.out.println("remote exception" + e);
		}
	}




	/**
	 * listen socket connection
	 */
	protected void listenSocketConnection() {
        Socket clientSocket;
        BufferedReader input;
        String name;
        while (true) {
            try {
                clientSocket= serverSocket.accept();
                System.out.println("Received socket connection request.");
                lobby.pingALl().stream().filter(Objects::nonNull).forEach(thread -> {
					try {
						thread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                name = input.readLine();
                if(lobby.getDisconnectedPlayers().contains(name))
                	lobby.reconnectPlayer(new PlayerSocket(name,
							new PrintWriter(clientSocket.getOutputStream(), true),
							input)
					);
                else
                	lobby.addPlayer(new PlayerSocket(name,
							new PrintWriter(clientSocket.getOutputStream(), true),
							input)
					);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        exit(1);

	}

	/**
	 * Allow user to connect to server.
	 * Register remote client in Player remote
	 * and adds it to Lobby Controller
	 * @param client remote reference to client connecting
	 * @param name client name
	 */
	@Override
	public void login(String name, ClientFunctionalities client){
		System.out.println("Received RMI connection request");
		lobby.pingALl().stream().filter(Objects::nonNull).forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		if(lobby.getDisconnectedPlayers().contains(name))
			lobby.reconnectPlayer(new PlayerRemote(name, client));
		else //TODO ADD CASE IN WHICH PLAYER WAS IN A MATCH
			lobby.addPlayer(new PlayerRemote(name, client));
	}

	/**
	 * Allow user to disconnect from server
	 * Removes remote client in Player remote
	 */
	@Override
	public void logout() {
		System.out.println("player disconnected");
	}

	/**
	 * Allow clients to ping server to check connection status
	 */
	@Override
	public void ping(String name) throws RemoteException {
		System.out.println("Got ping from "+ name);
		if(lobby.getPlayersNames().contains(name)){
			lobby.getPlayerByName(name).setPinged(true);
		} else if(lobby.getPlayersNameInGame().contains(name)){
			lobby.getPlayerInGameByName(name).setPinged(true);
		}
	}

	protected void checkRMIConnections(){
		while(true){
			lobby.getPlayers().forEach(this::checkPlayerPingStatus);
			lobby.getPlayersInGame().forEach(this::checkPlayerPingStatus);
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkPlayerPingStatus(PlayerConnection p) {
		if (p.getPinged())
			p.setPinged(false);
		else
		// if not received ping in last 10 seconds try to ping. if fails it automatically disconnects p
		{
			try {
				p.ping().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	/**
     * Main method to test connections
     */
    public static void main(String[] args) {
		LoginHandler loginHandler;
    	try {
            loginHandler = new LoginHandler();

        } catch (RemoteException e) {

            e.printStackTrace();
			return;
    	}
		Thread t1 = new Thread(loginHandler::listenSocketConnection);
		t1.start();
		Thread t2 = new Thread(loginHandler::checkRMIConnections);
		t2.start();
    }


}