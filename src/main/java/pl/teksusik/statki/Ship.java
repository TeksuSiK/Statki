package pl.teksusik.statki;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    private final String shipType;
    private final int size;
    private int hits;
    private final List<int[]> coordinates;

    public Ship(String shipType, int size) {
        this.shipType = shipType;
        this.size = size;
        this.hits = 0;
        this.coordinates = new ArrayList<>();
    }

    public void hit() {
        hits++;
    }

    public boolean isSunk() {
        return hits >= size;
    }

    public int getSize() {
        return size;
    }

    public String getShipType() {
        return shipType;
    }

    public List<int[]> getCoordinates() {
        return coordinates;
    }
}
