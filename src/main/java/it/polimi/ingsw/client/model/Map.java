package it.polimi.ingsw.client.model;

import it.polimi.ingsw.custom_exceptions.AmmoAlreadyOnCellException;
import it.polimi.ingsw.server.model.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Map {

    /**
     * Representation of the map, bidemensional array of cells.
     */
    private Cell[][] mapMatrix;

    /**
     * List to trace spawn cells
     */
    private List<SpawnCellClient> spawnPoints;

    /**
     * List of ammo cells
     */
    private List<AmmoCell> ammoPoints;


    /**
     * @param o json to parsee 1e
     * @return parsed Map object
     */
    public static Map parseJSON(JSONObject obj, List<WeaponCard> weaponList, List<Player> playersList) {
        Map map = new Map();
        map.spawnPoints = new ArrayList<>();
        map.ammoPoints = new ArrayList<>();
        JSONArray arr =(JSONArray) obj.get("map");
        int xSize = Integer.parseInt(obj.get("xSize").toString());
        int ySize = Integer.parseInt(obj.get("ySize").toString());
        map.mapMatrix = new Cell[xSize][ySize];
        int i = 0;
        for(Object el: arr){
            JSONObject cellObj = (JSONObject) el;
            if(Boolean.parseBoolean(cellObj.get("valid").toString())) {
                Color color = Color.valueOf(cellObj.get("color").toString());
                if (cellObj.get("type").toString().equals("spawn")) {
                    SpawnCellClient cellToAdd = initSpawnCell(weaponList, xSize, ySize, i, cellObj, color);
                    map.mapMatrix[(i / ySize) % xSize][i % ySize] = cellToAdd;
                    map.spawnPoints.add(cellToAdd);
                }else {
                    AmmoCell cellToAdd = initAmmoCell(xSize, ySize, i, cellObj, color);
                    map.mapMatrix[(i / ySize) % xSize][i % ySize] = cellToAdd;
                    map.ammoPoints.add(cellToAdd);
                }

                JSONArray players = (JSONArray) cellObj.get("players");
                List<Player> cellPlayers = new ArrayList<>();
                for(Object p : players)
                    cellPlayers.add(playersList.stream().filter(player -> player.getNickname().equals(p.toString()))
                            .collect(Collectors.toList()).get(0));

                int finalI = i;
                cellPlayers.forEach(p->{
                    map.mapMatrix[(finalI / ySize) % xSize][finalI % ySize].addPlayer(p);
                    p.setPosition(map.mapMatrix[(finalI / ySize) % xSize][finalI % ySize]);
                });
            } else {
                map.mapMatrix[(i / ySize) % xSize][i % ySize] = null;
            }
            i++;
        }

        return map;
    }

    private static AmmoCell initAmmoCell(int xSize, int ySize, int i, JSONObject cellObj, Color color) {
        JSONObject ammoObj =(JSONObject) cellObj.get("ammo");
        AmmoCell cellToAdd = new AmmoCell(Side.valueOf(cellObj.get("north").toString()),
                Side.valueOf(cellObj.get("south").toString()),
                Side.valueOf(cellObj.get("west").toString()),
                Side.valueOf(cellObj.get("east").toString()),
                color,
                (i / ySize) % xSize,
                i % ySize
        );
        try {
            cellToAdd.setAmmo(Ammo.parseJSON(ammoObj));
        } catch (AmmoAlreadyOnCellException e) {
            e.printStackTrace();
        }
        return cellToAdd;
    }

    private static SpawnCellClient initSpawnCell(List<WeaponCard> weaponList, int xSize, int ySize, int i, JSONObject cellObj, Color color) {
        JSONArray weapons = (JSONArray) cellObj.get("weapons");
        List<WeaponCard> cellWeapons = new ArrayList<>();
        for (Object objW : weapons) {
            cellWeapons.add(weaponList.stream().filter(w -> w.getName().equals(objW.toString()))
                    .collect(Collectors.toList()).get(0));
        }
        SpawnCellClient cellToAdd = new
                SpawnCellClient(Side.valueOf(cellObj.get("north").toString()),
                Side.valueOf(cellObj.get("south").toString()),
                Side.valueOf(cellObj.get("west").toString()),
                Side.valueOf(cellObj.get("east").toString()),
                color,
                (i / ySize) % xSize,
                i % ySize
        );
        cellWeapons.forEach(w-> {
            cellToAdd.addWeapon(w);
        });
        return cellToAdd;
    }
}
