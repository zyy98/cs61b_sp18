package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Player {
    private static Position pos;

    public static Position getPos() {
        return pos;
    }

    public static void setPos(Position pos) {
        Player.pos = pos;
    }

    public static void goLeft(TETile[][] world){
        if (world[pos.getX()-1][pos.getY()] == Tileset.FLOOR){
            world[pos.getX()-1][pos.getY()] = Tileset.PLAYER;
            world[pos.getX()][pos.getY()] = Tileset.FLOOR;
            pos = new Position(pos.getX()-1,pos.getY());
        }
    }

    public static void goRight(TETile[][] world){
        if (world[pos.getX()+1][pos.getY()] == Tileset.FLOOR){
            world[pos.getX()+1][pos.getY()] = Tileset.PLAYER;
            world[pos.getX()][pos.getY()] = Tileset.FLOOR;
            pos = new Position(pos.getX()+1,pos.getY());
        }
    }

    public static void goUp(TETile[][] world){
        if (world[pos.getX()][pos.getY()+1] == Tileset.FLOOR){
            world[pos.getX()][pos.getY()+1] = Tileset.PLAYER;
            world[pos.getX()][pos.getY()] = Tileset.FLOOR;
            pos = new Position(pos.getX(),pos.getY()+1);
        }
    }

    public static void goDown(TETile[][] world){
        if (world[pos.getX()][pos.getY()-1] == Tileset.FLOOR){
            world[pos.getX()][pos.getY()-1] = Tileset.PLAYER;
            world[pos.getX()][pos.getY()] = Tileset.FLOOR;
            pos = new Position(pos.getX(),pos.getY()-1);
        }
    }
}
