package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class Room {
    private Position pos;   //bottom-left
    private int width;
    private int height;

    public Room(Position pos, int width, int height) {
        this.height = height;
        this.pos = pos;
        this.width = width;
    }

    public boolean isOverLap(Room r){
        return (Math.min(pos.getX() + width, r.pos.getX() + r.width)+1) > Math.max(pos.getX(), r.pos.getX()) && // width > 0
                (Math.min(pos.getY() + height, r.pos.getY() + r.height)+1) > Math.max(pos.getY(), r.pos.getY());
    }
    public boolean isValid(){
        return pos.getX()+width < Game.WIDTH && pos.getY()+height < Game.HEIGHT;
    }

    public void drawRoom(TETile[][] world) {
        for (int x = pos.getX(); x < pos.getX()+width; x += 1) {
            for (int y = pos.getY(); y < pos.getY()+height; y += 1) {
                world[x][y] = Tileset.FLOOR;
            }
        }
    }

    public boolean addConnection(TETile[][] world, Random r){
        int direc = RandomUtils.uniform(r, 0, 4);  //corresponding to left,up,right,down
        switch (direc) {
            case 0:
                int point = RandomUtils.uniform(r,pos.getY()+1,pos.getY()+height);//point cannot be on corner
                //world[pos.getX()-1][point] = Tileset.FLOWER;   used for debugging, pinpoint the right positioin
                if (pos.getX()-1 >= 0 && world[pos.getX()-2][point] == Tileset.FLOOR){
                    world[pos.getX()-1][point] = Tileset.FLOOR;
                    return true;
                }else{
                    return false;
                }

            case 1:
                point = RandomUtils.uniform(r, pos.getX()+1,  pos.getX() + width);
                //world[point][pos.getY()+height] = Tileset.FLOWER;
                if (pos.getY()+height+1 < Game.HEIGHT && world[point][pos.getY()+height+1] == Tileset.FLOOR){
                    world[point][pos.getY()+height] = Tileset.FLOOR;
                    return true;
                }else{
                    return false;
                }
            case 2:
                point = RandomUtils.uniform(r,pos.getY()+1,pos.getY()+height);
                //world[pos.getX()+width][point] = Tileset.FLOWER;
                if (pos.getX()+width+1 < Game.WIDTH && world[pos.getX()+width+1][point] == Tileset.FLOOR){
                    world[pos.getX()+width][point] = Tileset.FLOOR;
                    return true;
                }else{
                    return false;
                }
            case 3:
                point = RandomUtils.uniform(r, pos.getX()+1,  pos.getX() + width);
                //world[point][pos.getY()-1] = Tileset.FLOWER;
                if (pos.getY()-2 >= 0 && world[point][pos.getY()-1] == Tileset.FLOOR){
                    world[point][pos.getY()-1] = Tileset.FLOOR;
                    return true;
                }else {
                    return false;
                }
        }
        return false;
    }
}