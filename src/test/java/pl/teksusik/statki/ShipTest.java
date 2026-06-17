package pl.teksusik.statki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShipTest {

    @Test
    void newShipNotSunk() {
        Ship ship = new Ship("Destroyer", 3);
        assertFalse(ship.isSunk());
    }

    @Test
    void hitIncrementsCounter() {
        Ship ship = new Ship("Destroyer", 3);
        ship.hit();
        ship.hit();
        assertFalse(ship.isSunk());
    }

    @Test
    void allHitsSunkShip() {
        Ship ship = new Ship("Submarine", 2);
        ship.hit();
        ship.hit();
        assertTrue(ship.isSunk());
    }

    @Test
    void getSizeReturnsCorrectValue() {
        Ship ship = new Ship("Carrier", 5);
        assertEquals(5, ship.getSize());
    }

    @Test
    void getShipTypeReturnsCorrectValue() {
        Ship ship = new Ship("Battleship", 4);
        assertEquals("Battleship", ship.getShipType());
    }
}
