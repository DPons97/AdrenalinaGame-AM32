package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;
import it.polimi.ingsw.custom_exceptions.*;
import org.json.simple.JSONObject;

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
                if(lobby.getDisconnectedPlayers().contains(name) || lobby.getDisconnectedPlayersInGame().contains(name))
                	lobby.reconnectPlayer(new PlayerSocket(name,
							new PrintWriter(clientSocket.getOutputStream(), true),
							input)
					);
                else if (lobby.getPlayersNameInGame().contains(name) || lobby.getPlayersNames().contains(name)){
						JSONObject msg = new JSONObject();
						msg.put("function", "alert");
						msg.put("msg", "Cannot connect: username already exists");
						PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);
						out.println(msg.toString());
				} else
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
		if(lobby.getDisconnectedPlayers().contains(name) ||  lobby.getDisconnectedPlayersInGame().contains(name))
			lobby.reconnectPlayer(new PlayerRemote(name, client));
		else if(lobby.getPlayersNameInGame().contains(name) || lobby.getPlayersNames().contains(name)){
			try {
				client.allert("Cannot connect: username already exists");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else
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
	public void ping(String name) {
		System.out.println("Got ping from "+ name);
		if(lobby.getPlayersNames().contains(name)){
			lobby.getPlayerByName(name).setPinged(true);
		} else if(lobby.getPlayersNameInGame().contains(name)){
			lobby.getPlayerInGameByName(name).setPinged(true);
		}
	}

	/**
	 * Allow user to create a game while in the lobby
	 * @param name name of player creating the game
	 * @param maxPlayers max players to set for this match
	 * @param maxDeaths max deaths to set forthis game
	 * @param mapID id of the map to use for this game
	 */
	@Override
	public void createGame(String name, int maxPlayers, int maxDeaths, int turnDuration, int mapID) {
		if(lobby.getPlayersNames().contains(name)) {
			try {
				lobby.hostMatch(lobby.getPlayerByName(name), maxPlayers, maxDeaths, turnDuration, mapID);
			} catch (TooManyMatchesException e) {
				lobby.getPlayerByName(name).alert("Cannot create match: server is full");
			} catch (PlayerNotExistsException e) {
				e.printStackTrace();
			} catch (MatchAlreadyStartedException e) {
				e.printStackTrace();
			} catch (PlayerAlreadyExistsException e) {
				e.printStackTrace();
			} catch (TooManyPlayersException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Allow to join a game while in the loby
	 * @param name nickname of player joining the game
	 * @param id id of the match to join
	 */
	@Override
	public void joinGame(String name, int id) {
		try {
			lobby.joinMatch(lobby.getPlayerByName(name), id);
		} catch (TooManyPlayersException e) {
			lobby.getPlayerByName(name).alert("Cannot join match: match is full");
		} catch (MatchAlreadyStartedException e) {
			lobby.getPlayerByName(name).alert("Cannot join match: match already started");
		} catch (PlayerAlreadyExistsException e) {
			e.printStackTrace();
		} catch (PlayerNotExistsException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Allow user to communicate that is ready
	 */
	@Override
	public void ready(String name) {
		lobby.setPlayerReady(lobby.getPlayerInGameByName(name));
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