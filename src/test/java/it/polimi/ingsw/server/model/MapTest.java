package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapTest {
    /**
     * Loads all maps and checks topology
     */
    @Test
    void checkMaps(){
        AdrenalinaMatch testMatch = new AdrenalinaMatch(3, 5,120, 1);
        checkMapTopology(testMatch.getBoardMap());
        assertEquals(3, testMatch.getBoardMap().getSpawnPoints().size());

        testMatch = new AdrenalinaMatch(3, 5,120, 2);
        assertEquals(3, testMatch.getBoardMap().getSpawnPoints().size());
        checkMapTopology(testMatch.getBoardMap());

        testMatch = new AdrenalinaMatch(3, 5,120, 3);
        assertEquals(3, testMatch.getBoardMap().getSpawnPoints().size());
        checkMapTopology(testMatch.getBoardMap());

        testMatch = new AdrenalinaMatch(3, 5,120, 4);
        assertEquals(3, testMatch.getBoardMap().getSpawnPoints().size());
        checkMapTopology(testMatch.getBoardMap());
    }

    private void checkMapTopology(Map map){
        // check topology -> edges of adjacent cells need to be the same!
        for(int i = 0; i < map.getXSize(); i++){
            for(int j = 0; j< map.getYSize(); j++){
                //check right cell
                if (map.getCell(i,j) == null && j == 0) assertEquals(Side.BORDER, map.getCell(i,j+1).getWest());
                else if (map.getCell(i,j) == null && j == map.getYSize()-1) assertEquals(Side.BORDER, map.getCell(i,j-1).getEast());
                else if(j == 0){
                    assertEquals(Side.BORDER , map.getCell(i,j).getWest());
                    assertEquals(map.getCell(i,j).getEast(), map.getCell(i,j+1).getWest());
                }
                else if(j == map.getYSize()-1) assertEquals(Side.BORDER , map.getCell(i,j).getEast());
                else {
                    if( map.getCell(i,j+1) == null) assertEquals(Side.BORDER, map.getCell(i,j).getEast());
                    else assertEquals(map.getCell(i,j).getEast(),map.getCell(i,j+1).getWest());
                }

                //check bottom/top cell
                if(map.getCell(i,j) == null && i == 0) assertEquals(Side.BORDER, map.getCell(i+1,j).getNorth());
                else if(map.getCell(i,j) == null && i == map.getXSize() -1) assertEquals(Side.BORDER, map.getCell(i-1,j).getSouth());
                else if(i == 0){
                    assertEquals(Side.BORDER, map.getCell(i,j).getNorth());
                    assertEquals(map.getCell(i,j).getSouth(), map.getCell(i+1,j).getNorth());
                }
                else if(i == map.getXSize()-1)assertEquals(Side.BORDER, map.getCell(i,j).getSouth());
                else {
                    if( map.getCell(i+1,j) == null) assertEquals(Side.BORDER, map.getCell(i,j).getSouth());
                    else assertEquals(map.getCell(i,j).getSouth(), map.getCell(i+1,j).getNorth());
                }
            }
        }
    }
}