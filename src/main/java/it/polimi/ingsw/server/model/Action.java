package it.polimi.ingsw.server.model;

import it.polimi.ingsw.custom_exceptions.InvalidJSONException;
import it.polimi.ingsw.custom_exceptions.InvalidStringException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 
 */
public class Action {

	/**
	 * Functional interface to compose base actions
	 */
	public interface BaseAction {
		/**
		 *
		 */
		void applyOn(Player caller);
	}

	/**
	 * Action name
	 */
	private String name;

	/**
	 * Action description
	 */
	private String description;

	/**
	 * Action description
	 */
	private List<Resource> cost;

	/**
	 * List of base actions to run in order
	 */
	private List<BaseAction> actions;

	/**
	 * List of target players
	 */
	private List<Player> targetPlayers;

	/**
	 * List of target cells
	 */
	private List<Cell> targetCells;

	/**
	 * True if action requires base effect to be executed
	 */
	private String requires;

	/**
	 * True if action was executed in this turn.
	 */
	private boolean executed;

	/**
	 * @param name action name
	 * @param effect action JSONObject effect to parse
	 */
	public Action(String name,JSONObject effect) {
		this.name = name;
		this.actions = new ArrayList<>();
		this.targetCells = new ArrayList<>();
		this.targetPlayers = new ArrayList<>();
		this.cost = new ArrayList<>();
		executed = false;
		parseEffect(effect);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * @return copy of actions list
	 */
	public List<BaseAction> getActions() {
		return new ArrayList<>(actions);
	}

	/**
	 * @return copy of actions list
	 */
	public List<Resource> getCost() {
		return cost;
	}

	/**
	 * @param effect json effect to parse
	 */
	public void  parseEffect(JSONObject effect) {
		try {
			this.description = effect.get("description").toString();

			if(name.equals("base effect") || name.contains("mode")) requires = null;
			else {
				Object jsonGet = effect.get("requires");
				if(jsonGet == null) this.requires = "base effect";
				else if(jsonGet.toString().equals("null")) requires = null;
				else this.requires = jsonGet.toString();
			}


			JSONArray costJSON = (JSONArray) effect.get("cost");
			for(Object res: costJSON){
				this.cost.add(AdrenalinaMatch.stringToResource(res.toString()));
			}

			JSONArray baseActionsJSON = (JSONArray) effect.get("actions");
			for(Object baseActionObj: baseActionsJSON){
				JSONObject baseActionJSON = (JSONObject) baseActionObj;
				switch(baseActionJSON.get("type").toString()){
					case "SELECT":
						parseSelectAction(baseActionJSON);
						break;
					case "DAMAGE":
						actions.add(damageLambda(baseActionJSON));
						break;
					case "MOVE":
						actions.add(moveLambda());
						break;
					case "MARK":
						actions.add(markLambda(baseActionJSON));
						break;
					default:
						throw new InvalidJSONException();
				}
			}
		} catch (InvalidJSONException | InvalidStringException  e) {
			e.printStackTrace();
		}
	}

	public void parseSelectAction(JSONObject baseActionJSON) throws InvalidJSONException {
		Object notID = baseActionJSON.get("notID");
		JSONArray distance = (JSONArray) baseActionJSON.get("distance");
		JSONArray qty = (JSONArray) baseActionJSON.get("quantity");
		Object diffCellsObj =  baseActionJSON.get("differentCells");
		boolean diffCells = diffCellsObj != null && Boolean.parseBoolean(diffCellsObj.toString());
		int minQty = Integer.parseInt(qty.get(0).toString());
		int maxQty = Integer.parseInt(qty.get(1).toString());
		// check what needs to be selected (cell/player/room/self)
		switch(baseActionJSON.get("target").toString()){
			case "CELL":
				actions.add(selectCellLambda(baseActionJSON, notID, distance, minQty, maxQty));
				break;
			case "PLAYER":
				actions.add(selectPlayerLambda(baseActionJSON, notID, distance, diffCells, minQty, maxQty));
				break;
			case "SELF":
				selectSeflLambda();
				break;
			case "ROOM":
				actions.add(selectRoomLambda(minQty, maxQty));
				break;
			default:
				throw new InvalidJSONException();

		}
	}

	public BaseAction markLambda(JSONObject baseActionJSON) {
		return caller -> {
			List<Player> toApply = selectTargets();
			toApply.forEach(p-> {
				int dmg = Integer.parseInt((baseActionJSON.get("value")).toString());
				IntStream.range(0,dmg).forEach(a-> p.takeMark(caller));
			});
		};
	}

	public BaseAction moveLambda() {
		return caller -> {
			List<Player> toApply = selectTargets();
			if(!targetCells.isEmpty())
				toApply.forEach(p->p.move(targetCells.get(0)));
		};
	}

	public BaseAction damageLambda(JSONObject baseActionJSON) {
		return caller -> {
			List<Player> toApply = selectTargets();
			toApply.forEach(p-> {
				int dmg = Integer.parseInt((baseActionJSON.get("value")).toString());
				IntStream.range(0,dmg).forEach(a-> {
					p.takeDamage(caller);
				});
			});
		};
	}

	public BaseAction selectRoomLambda(int minQty, int maxQty) {
		return caller -> {
			targetCells.clear();
			List<List<Cell>> couldBeAdded = caller.getMatch().getSelectableRooms(caller);
			if( maxQty == -1 || (minQty == maxQty && minQty >= couldBeAdded.size()))
				// if there is no choice -> add all you could add
				targetCells.addAll(couldBeAdded.stream().flatMap(List::stream).collect(Collectors.toList()));
			else{
				if (caller.getConnection() != null) {
					int nSelected = 0;
					while (nSelected < maxQty && !couldBeAdded.isEmpty()) {
						List<Cell> selected = caller.getConnection().selectRoom(couldBeAdded);
						targetCells.addAll(selected);

						if (selected.isEmpty()) {
							nSelected++;

							// Set ids to selected players
							couldBeAdded.remove(selected);
						} else if (nSelected >= minQty) {
							break;
						}
					}
				}

				if (caller.getConnection() != null) caller.getConnection().selectRoom(couldBeAdded);
			}
		};
	}

	public boolean selectSeflLambda() {
		return actions.add(caller -> {
			targetPlayers.clear();
			targetPlayers.add(caller);
		});
	}

	public BaseAction selectPlayerLambda(JSONObject baseActionJSON, Object notID, JSONArray distance, boolean diffCells, int minQty, int maxQty) {
		return caller -> {
			targetPlayers.clear();
			List<Player> couldBeAdded = caller.getMatch().
					getSelectablePlayers(caller,
							baseActionJSON.get("from").toString(),
							notID != null ? Integer.parseInt(notID.toString()) : -10,
							Integer.parseInt(distance.get(0).toString()),
							Integer.parseInt(distance.get(1).toString()),
							targetPlayers
					);

			couldBeAdded.remove(caller);

			if((!diffCells && maxQty == -1) || (minQty == maxQty && minQty >= couldBeAdded.size()))
				// if there is no choice -> add all you could add
				targetPlayers.addAll(couldBeAdded);
			else {
				if (caller.getConnection() != null) {
					int nSelected = 0;
					while (nSelected < maxQty && !couldBeAdded.isEmpty()) {
						Player selected = caller.getConnection().selectPlayer(couldBeAdded);
						targetPlayers.add(selected);

						if (selected != null) {
							nSelected++;

							// Set ids to selected players
							int id = Integer.parseInt(baseActionJSON.get("ID").toString());
							if (id != -2) {
								selected.setID(id);
								selected.getPosition().setID(id);
							}

							couldBeAdded.remove(selected);
						} else if (nSelected >= minQty) {
							break;
						}
					}
				}
			}
		};
	}

	public BaseAction selectCellLambda(JSONObject baseActionJSON, Object notID, JSONArray distance, int minQty, int maxQty) {
		return caller -> {
			targetCells.clear();
			List<Cell> couldBeAdded = caller.getMatch().
					getSelectableCells(caller,
							baseActionJSON.get("from").toString(),
							notID != null ? Integer.parseInt(notID.toString()) : -10,
							Integer.parseInt(distance.get(0).toString()),
							Integer.parseInt(distance.get(1).toString()),
							targetPlayers
					);

			if( maxQty == -1 || (minQty == maxQty && minQty >= couldBeAdded.size()))
				// if there is no choice -> add all you could add
				targetCells.addAll(couldBeAdded);
			else{
				if (caller.getConnection() != null) {
					int nSelected = 0;
					while (nSelected < maxQty && !couldBeAdded.isEmpty()) {
						Cell selected = caller.getConnection().selectCell(couldBeAdded);
						targetCells.add(selected);

						if (selected != null) {
							nSelected++;

							// Set ids to selected players
							int id = Integer.parseInt(baseActionJSON.get("ID").toString());
							if (id != -2) selected.setID(id);

							couldBeAdded.remove(selected);
						} else if (nSelected >= minQty) {
							break;
						}
					}
				}
			}
		};
	}

	private List<Player> selectTargets() {
		List<Player> toApply = new ArrayList<>();
		if (targetPlayers.isEmpty()) {
			toApply.addAll(targetCells.stream().map(Cell::getPlayers).flatMap(List::stream).collect(Collectors.toList()));
		} else {
			toApply.addAll(targetPlayers);
		}
		return toApply;
	}

	/**
	 * @return copy of target players
	 */
	public List<Player> getTargetPlayers() {
		return new ArrayList<>(targetPlayers);
	}

	/**
	 * @return copy of target cells
	 */
	public List<Cell> getTargetCells() {
		return new ArrayList<>(targetCells);
	}

	/**
	 * @return required action to execute this
	 */
	public String getRequires() {
		return requires;
	}

	/**
	 * @return true if this action is executed during current turn
	 */
	public boolean isExecuted() {
		return executed;
	}

	/**
	 * @param executed new executed value
	 */
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	/**
	 * @param newP new players to add as targets
	 */
	public void setTargetPlayers(List<Player> newP) {
		this.targetPlayers.addAll(newP);
	}

	/**
	 * @param newC new cell to add as targets
	 */
	public void setTargetCells(List<Cell> newC) {
		this.targetCells.addAll(newC);
	}



}