package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.model.AdrenalinaMatch;
import it.polimi.ingsw.client.model.Player;
import it.polimi.ingsw.client.model.Point;
import it.polimi.ingsw.client.view.CliView;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.GuiView;
import it.polimi.ingsw.custom_exceptions.UsernameTakenException;
import it.polimi.ingsw.server.controller.TurnAction;
import it.polimi.ingsw.server.controller.WeaponSelection;
import it.polimi.ingsw.server.model.MatchState;
import it.polimi.ingsw.server.model.Powerup;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.System.exit;

/**
 *
 */
public class ClientPlayer implements ClientFunctionalities{

	private static final int RMI_PORT = 52297;
	private static final int SOCKET_PORT = 52298;

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
	 * Reference to client view
	 */
	private ClientView view;

	/**
	 * Reference to last thread that updated view
	 */
	private Thread lastUpdater;

	/**
	 * True if player has loaded game
	 */
	private boolean loaded;


	/**
	 * Constructor
	 * Initializes nickname and server connection
	 * @param nickname player nickname
	 * @param connectionType rmi/socket
	 * @param ip server ip
	 * @param port server port
	 * @param gui gui/cli true/false
	 * @throws IOException
	 * @throws NotBoundException
	 * @throws UsernameTakenException
	 */
	public ClientPlayer(String nickname, ConnectionType connectionType,String ip, int port, boolean gui) throws IOException, NotBoundException, UsernameTakenException {
		this.nickname = nickname;

		if(connectionType == ConnectionType.RMI) this.server = new RemoteClient(this);
		else this.server = new SocketClient(this);

		if(gui) view = new GuiView(this);
		else view = new CliView(this);
		this.match = null;
		loaded = false;
		server.connect(ip, port);

		updateLobby();
	}

	/**
	 * Constructor
	 * Initializes nickname and server connection
	 * @param nickname player nickname
	 */
	public ClientPlayer(String nickname) {
		this.nickname = nickname;
		this.match = null;
		loaded = false;
	}

	/**
	 * @return string with nickname
	 */
	public AdrenalinaMatch getMatch(){
		return match;
	}

	/**
	 * Match setter for testing purposes
	 * @param match
	 */
	public void setMatch(AdrenalinaMatch match) {
		this.match = match;
	}

	public Player getThisPlayer() {
		return match.getPlayers().stream().filter(player -> player.getNickname().equals(nickname))
				.collect(Collectors.toList()).get(0);
	}

	/**
	 * @return string with nickname
	 */
	public String getNickname(){
	    return nickname;
    }

	@Override
	public void ping() {
		// this method exists just for the server to see if it can call the client
	}

	/**
	 * Select a player from a given list
	 * @param selectable list of players
	 * @return player from selectable
	 */
    @Override
    public String playerSelection(List<String> selectable) {
        String selected = view.selectPlayer(selectable);
        if (selected == null) return "";
        return selected;
    }

	/**
	 * Select a Cell from a given list
	 * @param selectable list of cells
	 * @return cell from selectable
	 */
    @Override
    public Point cellSelection(List<Point> selectable) {
        return view.selectCell(selectable);
    }

	/**
	 * select a room in a given list
	 * @param selectable list of rooms
	 * @return a room from selectable
	 */
	@Override
	public List<Point> roomSelection(List<List<Point>> selectable){
		return view.selectRoom(selectable);
	}

	/**
	 * select a weapon to reload
	 * @param canLoad list of weapons to load
	 * @return WeaponSelection with weapon to reload
	 */
	@Override
	public WeaponSelection reloadSelection(List<String> canLoad){
		return view.selectReload(canLoad);
	}

	/**
	 * select a weapon and effect to shoot with
	 * @param loaded list of loaded weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	@Override
	public WeaponSelection shootSelection(List<String> loaded){ return view.selectShoot(loaded); }

	/**
	 * select a weapon from a list
	 * @param weapon list of selectable weapons
	 * @return WeaponSelection with weapon to shoot with
	 */
	@Override
	public WeaponSelection weaponSelection(List<String> weapon) { return view.selectWeapon(weapon); }

	/**
	 * select a weapon from a list
	 * @param powerup list of selectable weapons as json strings
	 * @return WeaponSelection with weapon to shoot with
	 */
	@Override
	public String powerupSelection(List<String> powerup) {
	    List<Powerup> selectables = new ArrayList<>();

	    // From json string to powerups
	    for (String jsonStr : powerup)
	        selectables.add(Powerup.parseJSON((JSONObject) JSONValue.parse(jsonStr)));

	    Powerup selected = view.selectPowerup(selectables);

	    if (selected != null){
			return selected.toJSON().toString();
		} else return "";
	}

	/**
	 * Select an action to make
	 * @return action to make
	 */
    @Override
    public TurnAction actionSelection(){
    	return view.actionSelection();
    }

	/**
	 * Updates the lobby view
	 */
	public void updateLobby() {
		String lobbyString = server.updateLobby(getNickname());

		if(lobbyString != null) {
			lastUpdater = new Thread(() -> view.showLobby(lobbyString));
			lastUpdater.start();
		}
	}

	@Override
	public void showLeaderboard(List<String> leaderboard) {
		view.showLeaderboard(leaderboard);
	}

	@Override
	public WeaponSelection weaponFreeSelection(List<String> weapons) throws RemoteException {
		return view.selectWeaponFree(weapons);
	}

	/**
	 * Updates the lobby view
	 * @param toGetUpdateFrom JSON match representation to get update from
	 */
	@Override
	public void updateMatch(JSONObject toGetUpdateFrom) {
		if(match == null){
			match = new AdrenalinaMatch();
		}

		match.update(toGetUpdateFrom);
		lastUpdater = new Thread(() -> view.showMatch());
		lastUpdater.start();
		if (!loaded && match.getState() == MatchState.LOADING &&
			!match.getPlayers().stream().filter(p->p.getNickname().equals(nickname)).
					collect(Collectors.toList()).get(0).isReadyToStart()) {
			// CLI: Reset input reader
			// VIEW: change scene
			// VIEW: change scene
			view.initMatch();

			server.setReady(true);
			loaded = true;
			System.out.println("DONE LOADING");
		}
	}

	@Override
	public void alert(String message) {
		view.showAlert(message);
	}

	public void setReady(boolean isReady){
		server.setReady(isReady);
	}

	public void createGame(int maxPlayers, int maxDeaths, int turnDuration, int mapID){
		server.createGame(maxPlayers, maxDeaths, turnDuration, mapID);
	}

	public void joinGame(int id){
		server.joinGame(this.nickname, id);
	}

	public void backToLobby(){
		this.match = null;
		server.backToLobby();
	}

	/*
	 * Main method to test connection
	 */
	public static void main(String[] args) {
		ConnectionType connectionType;
		int port;

		String ip = args[0];
		System.out.println(ip);
		Scanner in= new Scanner(System.in);

		//System.setProperty("java.rmi.server.hostname",ip);

		System.out.println("Insert nickname: ");
		String nickname= in.next();
		System.out.println("Select connection type [0: SOCKET, 1: RMI]: ");
		if (Integer.parseInt(in.next()) == 0) {
			connectionType = ConnectionType.SOCKET;
			port = SOCKET_PORT;
		} else {
			connectionType = ConnectionType.RMI;
			port = RMI_PORT;
		}

		try {
			ClientPlayer clientPlayer = new ClientPlayer(nickname, connectionType, ip, port, false);
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
			exit(1);
		} catch (UsernameTakenException e) {
			e.printStackTrace();
			exit(1);
		}
	}



	public ClientView getView() {
		return view;
	}
}