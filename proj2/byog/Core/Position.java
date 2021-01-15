package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class Position {
    private int x;
    private int y;

    public Position(){

    }

    public Position(int i, int y) {
        this.x = i;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ArrayList<Object> randomDirection(Random r, TETile[][] world) {
        //Map<Integer,Position> res = new HashMap<>();
        ArrayList<Object> res = new ArrayList<>();
        Position p = new Position();
        int direc = RandomUtils.uniform(r, 0, 4);
        switch (direc) {
            case 0:
                if (x - 2 >= 0) {
                    p.setX(x - 2);
                    p.setY(y);
                    break;
                }
            case 1:
                if (x + 2 < Game.WIDTH) {
                    p.setX(x + 2);
                    p.setY(y);
                    break;
                }
            case 2:
                if (y - 2 >= 0) {
                    p.setX(x);
                    p.setY(y - 2);
                    break;
                }
            case 3:
                if (y + 2 < Game.HEIGHT) {
                    p.setX(x);
                    p.setY(y + 2);
                    break;
                }
        }
        if (x - 2 < 0 || y - 2 < 0 || x+ 2 >= Game.WIDTH || y + 2 >= Game.HEIGHT) {
            return null;
        }
        if (world[x - 2][y] != Tileset.NOTHING &&
                world[x + 2][y] != Tileset.NOTHING &&
                world[x][y - 2] != Tileset.NOTHING &&
                world[x][y + 2] != Tileset.NOTHING) {
            return null;
        }
        res.add(direc);
        res.add(p);
        return res;
    }

    public boolean randomConnection(Random r, TETile[][] world) {
        int direc = RandomUtils.uniform(r, 0, 4);
        switch (direc) {
            case 0:
                if (x - 2 >= 0 && world[x-2][y] == Tileset.FLOOR) {
                    world[x-1][y] = Tileset.FLOOR;
                    break;
                }
            case 1:
                if (x + 2 < Game.WIDTH && world[x+2][y] == Tileset.FLOOR) {
                    world[x+1][y] = Tileset.FLOOR;
                    break;
                }
            case 2:
                if (y - 2 >= 0 && world[x][y-2] == Tileset.FLOOR) {
                    world[x][y-1] = Tileset.FLOOR;
                    break;
                }
            case 3:
                if (y + 2 < Game.HEIGHT && world[x][y+2] == Tileset.FLOOR) {
                    world[x][y+1] = Tileset.FLOOR;
                    break;
                }
            default:
                return false;
        }
        return true;
    }

}
