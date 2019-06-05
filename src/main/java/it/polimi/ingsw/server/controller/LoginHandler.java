package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.client.controller.ClientFunctionalities;
import it.polimi.ingsw.custom_exceptions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
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
     * socket port
     */
    private int socketPort=52298;

    /**
     * rmi port
     */
    private int rmiPort=52297;

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
	public LoginHandler() throws IOException {
		super();

		this.lobby = new LobbyController();

		serverSocket= new ServerSocket(socketPort);

		Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
		String addresses="";
		for (;n.hasMoreElements();){
			NetworkInterface e = n.nextElement();
			Enumeration<InetAddress> a = e.getInetAddresses();

			if(a.hasMoreElements()) {
				InetAddress addr = a.nextElement();
				if(addr.getHostAddress().length() > 15) continue;
				addresses = addresses + "     " + addr.getHostAddress();
			}

		}
		address = addresses;
		System.out.println("Server address = " + addresses + ", Port RMI= " + rmiPort+" / Port socket= "+socketPort);

		registry = LocateRegistry.createRegistry(rmiPort);
		registry.rebind("rmiServer", this);
	}

	public LoginHandler(int rmiPort, int socketPort) throws IOException {
		super();
		this.rmiPort = rmiPort;
		this.socketPort = socketPort;
		this.lobby = new LobbyController();

		serverSocket= new ServerSocket(socketPort);

		Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
		String addresses="";
		for (;n.hasMoreElements();){
			NetworkInterface e = n.nextElement();
			Enumeration<InetAddress> a = e.getInetAddresses();

			if(a.hasMoreElements()) {
				InetAddress addr = a.nextElement();
				if(addr.getHostAddress().length() > 15) continue;
				addresses = addresses + "     " + addr.getHostAddress();
			}

		}

		System.out.println("Server address = " + addresses + ", Port RMI= " + rmiPort+"Port socket= "+socketPort);

		registry = LocateRegistry.createRegistry(rmiPort);
		registry.rebind("rmiServer", this);

	}


	/**
	 * listen socket connection
	 */
	public void listenSocketConnection() {
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
                if(lobby.getDisconnectedPlayers().contains(name) || lobby.getDisconnectedPlayersInGame().contains(name)) {
					PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);
					out.println("{\"function\": \"connection\", \"value\": \"OK\"}");
					lobby.reconnectPlayer(new PlayerSocket(name, out, input));
				}
                else if (lobby.getPlayersNameInGame().contains(name) || lobby.getPlayersNames().contains(name)){
					PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);
					out.println("{\"function\": \"connection\", \"value\": \"KO\"}");
				} else {
					PrintWriter out  = new PrintWriter(clientSocket.getOutputStream(), true);
					out.println("{\"function\": \"connection\", \"value\": \"OK\"}");
					lobby.addPlayer(new PlayerSocket(name, out, input));
				}

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
	public boolean login(String name, ClientFunctionalities client){
		System.out.println("Received RMI connection request");
		lobby.pingALl().stream().filter(Objects::nonNull).forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		if(lobby.getDisconnectedPlayers().contains(name) ||  lobby.getDisconnectedPlayersInGame().contains(name)) {
			lobby.reconnectPlayer(new PlayerRemote(name, client));
			return true;
		}
		else if(lobby.getPlayersNameInGame().contains(name) || lobby.getPlayersNames().contains(name)) {
			return false;
		} else
			lobby.addPlayer(new PlayerRemote(name, client));
			return true;
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

	@Override
	public void backToLobby(String name) throws RemoteException {
		try {
			lobby.getPlayerInGameByName(name).backToLobby();
		} catch (MatchAlreadyStartedException | PlayerNotExistsException e) {
			lobby.getPlayerInGameByName(name).alert("Error leaving waiting room");		}
	}

	/**
	 * Allow user to communicate that is ready
	 */
	@Override
	public void ready(String name) {
		lobby.getMatchByPlayerConnection(lobby.getPlayerInGameByName(name)).setPlayerReady(lobby.getPlayerInGameByName(name));
	}

	@Override
	public String updateLobby() throws RemoteException {
		return lobby.updatePlayer();
	}

	public void checkRMIConnections(){
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
				Thread t = p.ping();
				if (t!=null) t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public static LoginHandler startServer(){
		LoginHandler loginHandler;
		try {
			loginHandler = new LoginHandler();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Thread t1 = new Thread(loginHandler::listenSocketConnection);
		t1.start();
		Thread t2 = new Thread(loginHandler::checkRMIConnections);
		t2.start();
		return loginHandler;
	}

	/**
     * Main method to test connections
     */
    public static void main(String[] args) {
		startServer();
    }

	public String getAddress() {
		return address;
	}

	public int getSocketPort() {
		return socketPort;
	}

	public int getRmiPort() {
		return rmiPort;
	}
}