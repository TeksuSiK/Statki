package pl.teksusik.statki;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int BOARD_SIZE = 10;
    private final Cell[][] grid;
    private final List<Ship> ships;

    public Board() {
        this.grid = new Cell[BOARD_SIZE][BOARD_SIZE];
        this.ships = new ArrayList<>();
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                grid[y][x] = Cell.EMPTY;
            }
        }
    }

    public boolean placeShip(Ship ship, int x, int y, boolean vertical) {
        List<int[]> coords = new ArrayList<>();
        for (int i = 0; i < ship.getSize(); i++) {
            int cx;
            int cy;
            if (vertical) {
                cx = x;
                cy = y + i;
            } else {
                cx = x + i;
                cy = y;
            }
            coords.add(new int[]{cx, cy});
        }

        for (int[] c : coords) {
            if (c[0] < 0 || c[0] >= BOARD_SIZE || c[1] < 0 || c[1] >= BOARD_SIZE) {
                return false;
            }
        }

        for (int[] c : coords) {
            if (grid[c[1]][c[0]] == Cell.SHIP) {
                return false;
            }
        }

        for (int[] c : coords) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int nx = c[0] + dx;
                    int ny = c[1] + dy;
                    if (nx >= 0 && nx < BOARD_SIZE && ny >= 0 && ny < BOARD_SIZE) {
                        if (grid[ny][nx] == Cell.SHIP) {
                            return false;
                        }
                    }
                }
            }
        }

        for (int[] c : coords) {
            grid[c[1]][c[0]] = Cell.SHIP;
            ship.getCoordinates().add(c);
        }
        ships.add(ship);
        return true;
    }

    public ShotResult receiveShot(int x, int y) {
        Cell cell = grid[y][x];
        if (cell == Cell.MISS) return ShotResult.MISS;
        if (cell == Cell.HIT)  return ShotResult.HIT;
        if (cell == Cell.EMPTY) {
            grid[y][x] = Cell.MISS;
            return ShotResult.MISS;
        }
        for (Ship ship : ships) {
            for (int[] c : ship.getCoordinates()) {
                if (c[0] == x && c[1] == y) {
                    ship.hit();
                    if (ship.isSunk()) {
                        for (int[] sc : ship.getCoordinates()) {
                            grid[sc[1]][sc[0]] = Cell.HIT;
                        }
                        return ShotResult.SUNK;
                    }
                    grid[y][x] = Cell.HIT;
                    return ShotResult.HIT;
                }
            }
        }
        return ShotResult.MISS;
    }

    public boolean isGameOver() {
        if (ships.isEmpty()) {
            return false;
        }
        return ships.stream().allMatch(Ship::isSunk);
    }

    public Cell[][] getGrid() {
        return grid;
    }
}
