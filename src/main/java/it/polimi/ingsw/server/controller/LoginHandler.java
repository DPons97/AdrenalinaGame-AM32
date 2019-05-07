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
	 * Default constructor
	 */

    private ServerSocket serverSocket;


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
	public void listenSocketConnection() {
        Socket clientSocket;
        while (true) {
            try {
                clientSocket= serverSocket.accept();
                System.out.println("Socket connection requested.");
                lobby.addPlayer(new PlayerSocket(new PrintWriter(clientSocket.getOutputStream(), true),
                                                 new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        exit(1);

	}

	@Override
	public void login(String name, ClientFunctionalities client){
		System.out.println("RMI connection requested");
		lobby.addPlayer(new PlayerRemote(name, client));
	}

	@Override
	public void logout() {

	}

    /**
     *
     */
    public static void main(String[] args) {
        try {
            LoginHandler loginHandler = new LoginHandler();
			Thread t1 = new Thread(loginHandler::listenSocketConnection);
			t1.start();
        } catch (RemoteException e) {
            e.printStackTrace();
            exit(1);
        }

    }


}