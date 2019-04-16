package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapTest {
    @Test
    void checkMaps(){
        AdrenalinaMatch testMatch = new AdrenalinaMatch(3, 5,120, 1);
        checkMapTopology(testMatch.getMap().getMap());
        assertEquals(3, testMatch.getMap().getSpawnPoints().size());

        testMatch = new AdrenalinaMatch(3, 5,120, 2);
        assertEquals(3, testMatch.getMap().getSpawnPoints().size());
        checkMapTopology(testMatch.getMap().getMap());

        testMatch = new AdrenalinaMatch(3, 5,120, 3);
        assertEquals(3, testMatch.getMap().getSpawnPoints().size());
        checkMapTopology(testMatch.getMap().getMap());

        testMatch = new AdrenalinaMatch(3, 5,120, 4);
        assertEquals(3, testMatch.getMap().getSpawnPoints().size());
        checkMapTopology(testMatch.getMap().getMap());
    }

    private void checkMapTopology(Cell[][] map){
        // check topology -> edges of adjacent cells need to be the same!
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j< map[i].length; j++){
                //check right cell
                if (map[i][j] == null && j == 0) assertEquals(Side.BORDER, map[i][j+1].getWest());
                else if (map[i][j] == null && j == map[i].length-1) assertEquals(Side.BORDER, map[i][j-1].getEast());
                else if(j == 0){
                    assertEquals(Side.BORDER , map[i][j].getWest());
                    assertEquals(map[i][j].getEast(), map[i][j+1].getWest());
                }
                else if(j == map[i].length-1) assertEquals(Side.BORDER , map[i][j].getEast());
                else {
                    if( map[i][j+1] == null) assertEquals(Side.BORDER, map[i][j].getEast());
                    else assertEquals(map[i][j].getEast(), map[i][j+1].getWest());
                }

                //check bottom/top cell
                if(map[i][j] == null && i == 0) assertEquals(Side.BORDER, map[i+1][j].getNorth());
                else if(map[i][j] == null && i == map.length -1) assertEquals(Side.BORDER, map[i-1][j].getSouth());
                else if(i == 0){
                    assertEquals(Side.BORDER, map[i][j].getNorth());
                    assertEquals(map[i][j].getSouth(), map[i+1][j].getNorth());
                }
                else if(i == map.length-1)assertEquals(Side.BORDER, map[i][j].getSouth());
                else {
                    if( map[i+1][j] == null) assertEquals(Side.BORDER, map[i][j].getSouth());
                    else assertEquals(map[i][j].getSouth(), map[i+1][j].getNorth());
                }
            }
        }
    }
}