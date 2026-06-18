package pl.teksusik.statki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {

    @Test
    void initialStateIsSetup() {
        Board board = new Board();
        GameEngine engine = new GameEngine(board);
        assertEquals(GameState.SETUP, engine.getGameState());
    }

    @Test
    void afterReadyStateChangesToMyTurnOrOpponentTurn() {
        GameEngine engineFirst = new GameEngine(new Board());
        engineFirst.setReady(true);
        assertEquals(GameState.MY_TURN, engineFirst.getGameState());

        GameEngine engineSecond = new GameEngine(new Board());
        engineSecond.setReady(false);
        assertEquals(GameState.OPPONENT_TURN, engineSecond.getGameState());
    }

    @Test
    void applyOpponentShotDelegatesToBoard() {
        Ship ship = new Ship("Patrol", 2);
        Board board = new Board();
        board.placeShip(ship, 2, 3, false);
        GameEngine engine = new GameEngine(board);

        ShotResult result = engine.applyOpponentShot(2, 3);

        assertEquals(ShotResult.HIT, result);
        assertFalse(ship.isSunk());
    }

    @Test
    void gameOverWhenAllShipsSunk() {
        Ship ship = new Ship("Patrol", 1);
        Board board = new Board();
        board.placeShip(ship, 0, 0, false);
        GameEngine engine = new GameEngine(board);
        engine.setReady(false);

        engine.applyOpponentShot(0, 0);

        assertEquals(GameState.GAME_OVER, engine.getGameState());
    }

    @Test
    void processShotOnlyWhenMyTurn() {
        Board board = new Board();
        GameEngine engine = new GameEngine(board);
        engine.setReady(false);

        assertFalse(engine.isMyTurn());
        assertThrows(IllegalStateException.class, () -> engine.processShot(0, 0));
    }
}
