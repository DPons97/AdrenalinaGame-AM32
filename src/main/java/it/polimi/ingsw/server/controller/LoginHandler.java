package it.polimi.ingsw.server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.System.exit;

/**
 * 
 */
public abstract class LoginHandler extends UnicastRemoteObject implements ServerFunctionalities{

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

    private ServerSocket serverSocket;

	/**
	 * Default constructor
	 */
	public LoginHandler() throws RemoteException {
		super();
        try {
            serverSocket= new ServerSocket(socketPort);
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }

    }



	/**
	 * 
	 */
	public abstract void main();

	/**
	 * listen socket connection
	 */
	public void listenSocketConnection() {
        Socket clientSocket;
        while (true) {
            try {
                clientSocket= serverSocket.accept();
                lobby.addPlayer(new PlayerSocket(new PrintWriter(clientSocket.getOutputStream(), true),
                                                 new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

        }
        exit(1);
	}

}