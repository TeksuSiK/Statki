package pl.teksusik.statki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    void placeShipHorizontallySucceeds() {
        Board board = new Board();
        Ship ship = new Ship("Destroyer", 3);
        assertTrue(board.placeShip(ship, 0, 0, false));
        assertEquals(Cell.SHIP, board.getGrid()[0][0]);
        assertEquals(Cell.SHIP, board.getGrid()[0][1]);
        assertEquals(Cell.SHIP, board.getGrid()[0][2]);
    }

    @Test
    void placeShipVerticallySucceeds() {
        Board board = new Board();
        Ship ship = new Ship("Destroyer", 3);
        assertTrue(board.placeShip(ship, 0, 0, true));
        assertEquals(Cell.SHIP, board.getGrid()[0][0]);
        assertEquals(Cell.SHIP, board.getGrid()[1][0]);
        assertEquals(Cell.SHIP, board.getGrid()[2][0]);
    }

    @Test
    void placeShipOutOfBoundsReturnsFalse() {
        Board board = new Board();
        Ship ship = new Ship("Destroyer", 3);
        assertFalse(board.placeShip(ship, 9, 0, false));
    }

    @Test
    void placeShipsOverlappingReturnsFalse() {
        Board board = new Board();
        Ship ship1 = new Ship("Destroyer", 3);
        Ship ship2 = new Ship("Patrol", 2);
        board.placeShip(ship1, 0, 0, false);
        assertFalse(board.placeShip(ship2, 1, 0, false));
    }

    @Test
    void placeShipsAdjacentReturnsFalse() {
        Board board = new Board();
        Ship ship1 = new Ship("Destroyer", 3);
        Ship ship2 = new Ship("Patrol", 2);
        board.placeShip(ship1, 0, 0, false);
        assertFalse(board.placeShip(ship2, 0, 1, false));
    }

    @Test
    void shotMissOnEmptyCell() {
        Board board = new Board();
        assertEquals(ShotResult.MISS, board.receiveShot(5, 5));
    }

    @Test
    void shotHitOnOccupiedCell() {
        Board board = new Board();
        Ship ship = new Ship("Destroyer", 3);
        board.placeShip(ship, 2, 2, false);
        assertEquals(ShotResult.HIT, board.receiveShot(2, 2));
    }

    @Test
    void shotSunkWhenLastShipCellHit() {
        Board board = new Board();
        Ship ship = new Ship("Patrol", 1);
        board.placeShip(ship, 0, 0, false);
        assertEquals(ShotResult.SUNK, board.receiveShot(0, 0));
    }

    @Test
    void shotOnAlreadyShotCellReturnsExisting() {
        Board board = new Board();
        board.receiveShot(3, 3);
        assertEquals(ShotResult.MISS, board.receiveShot(3, 3));
    }

    @Test
    void isGameOverWhenAllShipsDestroyed() {
        Board board = new Board();
        Ship ship = new Ship("Patrol", 1);
        board.placeShip(ship, 0, 0, false);
        board.receiveShot(0, 0);
        assertTrue(board.isGameOver());
    }
}
