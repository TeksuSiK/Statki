package pl.teksusik.statki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnumsTest {

    @Test
    void cellDefaultIsEmpty() {
        Cell cell = Cell.EMPTY;
        assertEquals(Cell.EMPTY, cell);
    }

    @Test
    void shotResultValuesExist() {
        assertNotNull(ShotResult.HIT);
        assertNotNull(ShotResult.MISS);
        assertNotNull(ShotResult.SUNK);
    }

    @Test
    void gameStateValuesExist() {
        assertNotNull(GameState.SETUP);
        assertNotNull(GameState.MY_TURN);
        assertNotNull(GameState.OPPONENT_TURN);
        assertNotNull(GameState.GAME_OVER);
    }
}
