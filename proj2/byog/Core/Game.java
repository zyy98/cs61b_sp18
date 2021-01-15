package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 81;
    public static final int HEIGHT = 31;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        drawUI();
        char first = keyboardGetFirstChar();
        if (first == 'n' || first == 'N') {  //can only apply ' not ", the latter is java.lang.String
            newGame();
        }else if(first == 'l' || first == 'L'){
            loadGame();
        } else if (first == 'q' || first == 'Q') {
            System.exit(0);
        }
    }

    private char keyboardGetFirstChar() {
        char first;
        while(true){
            if(!StdDraw.hasNextKeyTyped()){   //how to use keyboard
                continue;
            }
            first = StdDraw.nextKeyTyped();
            if (first == 'n' || first == 'N' ||first == 'l' || first == 'L'||first == 'q' || first == 'Q'){
                break;
            }
        }
        return first;
    }


    private void drawUI(){
        StdDraw.setCanvasSize(WIDTH * 16, (HEIGHT + 1) * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT + 1);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);

        Font font = new Font("Monaco", Font.PLAIN, 60);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, 3 * HEIGHT / 4, "BYoW");

        Font smallFont = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(smallFont);
        StdDraw.text(WIDTH / 2, HEIGHT / 4 + 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 4 - 2, "Quit (Q)");

        StdDraw.show();
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // TODO: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().

        TETile[][] finalWorldFrame = null;
        char first = input.charAt(0);
        if (first == 'n' || first == 'N') {  //can only apply ' not ", the latter is java.lang.String
            finalWorldFrame = newGame(input);
        }else if(first == 'l' || first == 'L'){
            finalWorldFrame = loadGame(input);
        } else if (first == 'q' || first == 'Q') {
            System.exit(0);
        }
        return finalWorldFrame;
    }


    private long keyboardGetSeed() {
        String seedString = "";
        while (true) {
            StdDraw.clear(Color.BLACK);
            StdDraw.setFont(new Font("Monaco", Font.PLAIN, 50));
            StdDraw.text(WIDTH / 2, 3 * HEIGHT / 4, "Please enter a random seed:");
            StdDraw.show();
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char digit = Character.toLowerCase(StdDraw.nextKeyTyped());
            if (digit != 's') {
                if (!Character.isDigit(digit)) {
                    continue;
                }
                seedString += digit;
                StdDraw.setFont(new Font("Monaco", Font.PLAIN, 30));
                StdDraw.text(WIDTH / 2, HEIGHT / 2, seedString);
                StdDraw.show();
            } else {
                break;
            }
        }
        return Long.valueOf(seedString);
    }

    private void newGame() {
        long seed = keyboardGetSeed();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = generateWorld(seed);
        ter.renderFrame(world);
        playGame(world);
    }

    private TETile[][] newGame(String input) {
        int index = input.indexOf('S');
        long seed = Long.parseLong(input.substring(1, index));

        ter.initialize(WIDTH, HEIGHT);
        TETile[][] finalWorldFrame = generateWorld(seed);

        playGame(finalWorldFrame,input.substring(index+1));
        ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }

    private TETile[][] loadGame(String input){
        TETile[][] finalWorldFrame = loading();
        playGame(finalWorldFrame,input.substring(1));
        return finalWorldFrame;
    }

    private void loadGame(){

    }

    private TETile[][] loading(){
        TETile[][] finalWorldFrame = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("savefile.txt"));
            finalWorldFrame = (TETile[][]) in.readObject();
            Player.setPos((Position) in.readObject());
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return finalWorldFrame;
    }

    private void saveGame(TETile[][] finalWorldFrame){
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("savefile.txt"));
            out.writeObject(finalWorldFrame);
            out.writeObject(Player.getPos());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playGame(TETile[][] world, String play){
        for(int i = 0;i<play.length();i++){
            switch (play.charAt(i)){
                case 'w':
                    Player.goUp(world);
                    break;
                case 'a':
                    Player.goLeft(world);
                    break;
                case 's':
                    Player.goDown(world);
                    break;
                case 'd':
                    Player.goRight(world);
                    break;
                case ':':
                    if(i+1<play.length() && play.charAt(i+1) == 'q'){
                        saveGame(world);
                        return;
                    }
                    break;
            }
        }
    }

    private void playGame(TETile[][] world){
        while (true){
            if(!StdDraw.hasNextKeyTyped()){
                continue;
            }
            char c = Character.toLowerCase(StdDraw.nextKeyTyped());
            switch (c){
                case 'w':
                    Player.goUp(world);
                    ter.renderFrame(world);
                    break;
                case 'a':
                    Player.goLeft(world);
                    ter.renderFrame(world);
                    break;
                case 's':
                    Player.goDown(world);
                    ter.renderFrame(world);
                    break;
                case 'd':
                    Player.goRight(world);
                    ter.renderFrame(world);
                    break;
                case ':':
                    while (true){
                        if(!StdDraw.hasNextKeyTyped()){
                            continue;
                        }
                        if(Character.toLowerCase(StdDraw.nextKeyTyped())=='q'){
                            saveGame(world);
                            System.exit(0);
                        }else {
                            break;
                        }
                    }
                    break;
            }
        }
    }

    private TETile[][] generateWorld(long seed) {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        initializeWorld(world);

        Random r = new Random(seed);

        List<Room> roomList = generateRoom(world, r);
        generateHalls(world, r);

        HallsMopping(world);

        generateConnection(world, r, roomList);

        addPlayer(world, r);
        return world;
    }

    private void addPlayer(TETile[][] world, Random r) {
        int start = RandomUtils.uniform(r, 2, WIDTH - 4);
        if(world[start][3]!=Tileset.WALL){
            world[start][2] = Tileset.PLAYER;
            Player.setPos(new Position(start,2));
        }else{
            world[start+1][2] = Tileset.PLAYER;
            Player.setPos(new Position(start+1,2));
        }
    }

    private void generateConnection(TETile[][] world, Random r, List<Room> roomList) {
        for (Room room : roomList) {
            boolean flag1 = room.addConnection(world, r);
            boolean flag2 = room.addConnection(world, r);
            while (!flag1) {
                flag1 = room.addConnection(world, r);
            }
            while (!flag2){
                flag2 = room.addConnection(world, r);
            }
        }
    }

    private void HallsMopping(TETile[][] world) {
        //left
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        for (int y = 2; y < HEIGHT; y += 1) {
            world[2][y] = Tileset.WALL;
        }
        //down
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < 2; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        for (int x = 2; x < WIDTH; x++) {
            world[x][2] = Tileset.WALL;
        }
        //right
        for (int x = WIDTH - 3; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        for (int y = 2; y < HEIGHT; y += 1) {
            world[WIDTH - 3][y] = Tileset.WALL;
        }
        //up
        for (int x = 0; x < WIDTH; x++) {
            for (int y = HEIGHT - 3; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        for (int x = 2; x < WIDTH - 2; x++) {    //because here creates a loop, cut both the beginning and the end
            world[x][HEIGHT - 3] = Tileset.WALL;
        }
    }


    private void initializeWorld(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.WALL;
            }
        }
        for (int x = 1; x < WIDTH; x += 2) {
            for (int y = 1; y < HEIGHT; y += 2) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private int checkSingularity(int num) {  //return an odd number
        if (num % 2 == 0) {
            num += 1;
        }
        return num;
    }

    private List<Room> generateRoom(TETile[][] world, Random r) {
        int roomNum = RandomUtils.uniform(r, 15, 25); // maxinum of 10 rooms
        List<Room> roomList = new ArrayList<Room>(roomNum);

        for (int i = 0; i < roomNum; i++) {
            boolean flag = true;
            Position pos = new Position();
            pos.setX(checkSingularity(RandomUtils.uniform(r, 2, WIDTH)));  //have to be odd number because index starts at 0
            pos.setY(checkSingularity(RandomUtils.uniform(r, 2, HEIGHT))); //why 2, for hallway generation
            int width = checkSingularity(RandomUtils.uniform(r, 2, 10));  //have to be odd
            int height = checkSingularity(RandomUtils.uniform(r, 2, 8));
            Room curRoom = new Room(pos, width, height);
            if (!curRoom.isValid()) {
                continue;
            }
            for (Room room : roomList) {
                if (curRoom.isOverLap(room)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                curRoom.drawRoom(world);
                roomList.add(curRoom);
            }
        }
        return roomList;
    }

    private void generateHalls(TETile[][] world, Random r) {
        Position curPos = new Position();
        Stack<Position> stack = new Stack<>();

        while (world[curPos.getX()][curPos.getY()] != Tileset.NOTHING) {
            curPos.setX(checkSingularity(RandomUtils.uniform(r, 0, WIDTH)));  //odd, to be nothing
            curPos.setY(checkSingularity(RandomUtils.uniform(r, 0, HEIGHT)));
            System.out.println("trying to get a start point");
        }

        stack.push(curPos);
        while (!stack.empty()) {
            Position cur = stack.peek();
            ArrayList<Object> nextRes = cur.randomDirection(r, world);
            // ArrayList<Object> nextRes = randomDirection(r, cur, world);  //really ugly, just need to return two items
            if (nextRes == null) {
                stack.pop();
                continue;
            }
            Integer nextKey = (Integer) nextRes.get(0);
            Position nextPos = (Position) nextRes.get(1);
//            if (nextPos == null) {
//                stack.pop();
//                continue;
//            }
            while (nextPos != null) {   //if not null, at least one direction has Tileset Nothing
                if (world[nextPos.getX()][nextPos.getY()] == Tileset.NOTHING) {
                    world[nextPos.getX()][nextPos.getY()] = Tileset.FLOOR;
                    stack.push(nextPos);
                    if (nextKey == 0) {
                        world[nextPos.getX() + 1][nextPos.getY()] = Tileset.FLOOR;   //really ugly,need to seperate different conditions
                    } else if (nextKey == 1) {
                        world[nextPos.getX() - 1][nextPos.getY()] = Tileset.FLOOR;
                    } else if (nextKey == 2) {
                        world[nextPos.getX()][nextPos.getY() + 1] = Tileset.FLOOR;
                    } else if (nextKey == 3) {
                        world[nextPos.getX()][nextPos.getY() - 1] = Tileset.FLOOR;
                    }
                    break;
                } else {
                    nextRes = randomDirection(r, cur, world);
                    nextKey = (Integer) nextRes.get(0);
                    nextPos = (Position) nextRes.get(1);
                }
            }

        }

    }

    private ArrayList<Object> randomDirection(Random r, Position curPos, TETile[][] world) {
        //Map<Integer,Position> res = new HashMap<>();
        ArrayList<Object> res = new ArrayList<>();
        Position p = new Position();
        int direc = RandomUtils.uniform(r, 0, 4);
        switch (direc) {
            case 0:
                if (curPos.getX() - 2 >= 0) {
                    p.setX(curPos.getX() - 2);
                    p.setY(curPos.getY());
                    break;
                }
            case 1:
                if (curPos.getX() + 2 < WIDTH) {
                    p.setX(curPos.getX() + 2);
                    p.setY(curPos.getY());
                    break;
                }
            case 2:
                if (curPos.getY() - 2 >= 0) {
                    p.setX(curPos.getX());
                    p.setY(curPos.getY() - 2);
                    break;
                }
            case 3:
                if (curPos.getY() + 2 < HEIGHT) {
                    p.setX(curPos.getX());
                    p.setY(curPos.getY() + 2);
                    break;
                }
        }
        if (curPos.getX() - 2 < 0 || curPos.getY() - 2 < 0 || curPos.getX() + 2 >= WIDTH || curPos.getY() + 2 >= HEIGHT) {
            return null;
        }
        if (world[curPos.getX() - 2][curPos.getY()] != Tileset.NOTHING &&
                world[curPos.getX() + 2][curPos.getY()] != Tileset.NOTHING &&
                world[curPos.getX()][curPos.getY() - 2] != Tileset.NOTHING &&
                world[curPos.getX()][curPos.getY() + 2] != Tileset.NOTHING) {
            return null;
        }
        res.add(direc);
        res.add(p);
        return res;
    }


//    private TETile[][] loadGame(String input){
//
//    }
}
