//JAYA SUMANTH SASAPU
//import libraries
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class hw1 {
    // Define constants for dungeon elements
    private static final char WALL = '#';
    private static final char EMPTY = ' ';
    private static final char ACT_MAN = 'A';
    private static final char OGRE = 'G';
    private static final char DEMON = 'D';
    private static final char CORPSE = '@';

    // Define constants for moves
    private static final char[] MOVES = {'1', '2', '3', '4', '6', '7', '8', '9'};
    private static final char[] DIRECTIONS = {'N', 'E', 'S', 'W'};

    // Define constants for scoring
    private static final int ACT_MAN_STARTING_SCORE = 50;
    private static final int SCORE_LOSS_PER_MOVE = 1;
    private static final int SCORE_LOSS_FOR_MAGIC_BULLET = 20;
    private static final int SCORE_GAIN_PER_MONSTER_KILL = 5;

    // Initialize Act-Man's position
    public static int actManRow = -1;
    public static int actManCol = -1;

    static ArrayList<ArrayList<Integer>> ocopyList = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> dcopyList = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> oremoveList = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> dremoveList = new ArrayList<>();

    static StringBuilder actions = new StringBuilder();
    static boolean actmanDead = false;
    private static int score;


    public static void main(String[] args) {

        String inputFile = "keyin0.txt";
        String outputFile = "outpu0.txt";

        try {
            // Read input file
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String[] dimensions = reader.readLine().split(" ");
            int rows = Integer.parseInt(dimensions[0]);
            int cols = Integer.parseInt(dimensions[1]);
            char[][] dungeon = new char[rows][cols];

            // Create a 2D ArrayList to hold ogre positions
            ArrayList<ArrayList<Integer>> ogrePositions = new ArrayList<>();
            // Create a 2D ArrayList to hold demon positions
            ArrayList<ArrayList<Integer>> demonPositions = new ArrayList<>();

            for (int i = 0; i < rows; i++) {
                String line = reader.readLine();
                for (int j = 0; j < cols; j++) {
                    dungeon[i][j] = line.charAt(j);
                    if (dungeon[i][j] == ACT_MAN) {
                        actManRow = i;
                        actManCol = j;
                    } else if (dungeon[i][j] == OGRE){
                        // Add ogre positions
                        addOgrePosition(ogrePositions, i, j);
                    } else if (dungeon[i][j] == DEMON){
                        // Add demon positions
                        addDemonPosition(demonPositions, i, j);
                    }
                }
            }
            reader.close();

            // Initialize Act-Man's score
            score = ACT_MAN_STARTING_SCORE;
            actMan(rows, cols, dungeon, ogrePositions, demonPositions, outputFile);
            gameended(dungeon, actions, outputFile, actManRow, actManCol);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void actMan(int rows, int cols, char[][] dungeon, ArrayList<ArrayList<Integer>> ogrePositions, ArrayList<ArrayList<Integer>> demonPositions, String outputFile){
        String out = "3N782";

        for (int move = 0; move < out.length(); move++) {
            char action = out.charAt(move);
            //Moving in a random direction
            if (action != 'E' && action != 'W' &&  action != 'N' &&  action != 'S') {
                // Perform a random valid move
                char moveDirection;
                int newRow = actManRow;
                int newCol = actManCol;
                int newrow = -1;
                int newcol = -1;
                char step;

                if(action == '1'){ newrow = newRow+1; newcol = newCol-1; step = '1'; }
                if(action == '2'){ newrow = newRow+1; newcol = newCol; step = '2'; }
                if(action == '3'){ newrow = newRow+1; newcol = newCol+1; step = '3'; }
                if(action == '4'){ newrow = newRow; newcol = newCol-1; step = '4'; }
                if(action == '6'){ newrow = newRow; newcol = newCol+1; step = '6'; }
                if(action == '7'){newrow = newRow-1; newcol = newCol-1; step = '7'; }
                if(action == '8'){ newrow = newRow-1; newcol = newCol; step = '8'; }
                if(action == '9'){ newrow = newRow-1; newcol = newCol+1; step = '9'; }

                dungeon[newRow][newCol] = EMPTY;

                actManRow = newrow;
                actManCol = newcol;
                score -= SCORE_LOSS_PER_MOVE;
                if(dungeon[actManRow][actManCol] == CORPSE || dungeon[actManRow][actManCol] == OGRE || dungeon[actManRow][actManCol] == DEMON){
                    score = 0;
                    actmanDead = true;
                    dungeon[newrow][newcol] = 'X';
                    return; //use return;
                }
                else{
                    dungeon[newrow][newcol] = ACT_MAN;
                }
                moveMonsters(dungeon, ogrePositions, demonPositions, newrow, newcol);
                if(score<=0){actmanDead=true;return;}
                if(ogrePositions.isEmpty() && demonPositions.isEmpty()){return;}


            } else {
                // Fire magic bullet in a random direction
                char bulletDirection = out.charAt(move);
                score -= SCORE_LOSS_FOR_MAGIC_BULLET;
                actions.append(bulletDirection);
                if(bulletDirection == 'N'){
                    for(int i=actManRow-1; i>0; i--){
                        if(dungeon[i][actManCol] == WALL){break;}
                        if(dungeon[i][actManCol] == OGRE){dungeon[i][actManCol] = CORPSE;score += SCORE_GAIN_PER_MONSTER_KILL;}
                        if(dungeon[i][actManCol] == DEMON){dungeon[i][actManCol] = CORPSE;score += SCORE_GAIN_PER_MONSTER_KILL;}
                    }
                }
                if(bulletDirection == 'E'){
                    for(int i=actManCol+1; i<cols; i++){
                        if(dungeon[actManRow][i] == WALL){break;}
                        if(dungeon[actManRow][i] == OGRE){dungeon[actManRow][i] = CORPSE;score += SCORE_GAIN_PER_MONSTER_KILL;}
                        if(dungeon[actManRow][i] == DEMON){dungeon[actManRow][i] = CORPSE;score += SCORE_GAIN_PER_MONSTER_KILL;}
                    }
                }
                if(bulletDirection == 'S'){
                    for(int i=actManRow+1; i<rows-1; i++){
                        if(dungeon[i][actManCol] == WALL){break;}
                        if(dungeon[i][actManCol] == OGRE){dungeon[i][actManCol] = CORPSE;score += SCORE_GAIN_PER_MONSTER_KILL;}
                        if(dungeon[i][actManCol] == DEMON){dungeon[i][actManCol] = CORPSE;score += SCORE_GAIN_PER_MONSTER_KILL;}
                    }
                }
                if(bulletDirection == 'W'){
                    for(int i=actManCol-1; i>0; i--){
                        if(dungeon[actManRow][i] == WALL){break;}
                        if(dungeon[actManRow][i] == OGRE){dungeon[actManRow][i] = CORPSE;score += SCORE_GAIN_PER_MONSTER_KILL;}
                        if(dungeon[actManRow][i] == DEMON){dungeon[actManRow][i] = CORPSE;score += SCORE_GAIN_PER_MONSTER_KILL;}
                    }
                }

                moveMonsters(dungeon, ogrePositions, demonPositions, actManRow, actManCol);
                if(score<=0){actmanDead = true;return;}
                if((ogrePositions.isEmpty() && demonPositions.isEmpty())){return;}
            }
        }
    }

    // Check if the move is valid
    private static boolean isValidMove(char[][] dungeon, int newRow, int newCol) {
        return newRow >= 0 && newRow < dungeon.length &&
                newCol >= 0 && newCol < dungeon[0].length &&
                dungeon[newRow][newCol] != WALL;
    }

    // Method to add ogre position to the 2D ArrayList
    public static void addOgrePosition(ArrayList<ArrayList<Integer>> ogrePositions, int x, int y) {
        ArrayList<Integer> ogre = new ArrayList<>();
        ogre.add(x);
        ogre.add(y);
        ogrePositions.add(ogre);
    }

    // Method to add Demon position to the 2D ArrayList
    public static void addDemonPosition(ArrayList<ArrayList<Integer>> demonPositions, int x, int y) {
        ArrayList<Integer> demon = new ArrayList<>();
        demon.add(x);
        demon.add(y);
        demonPositions.add(demon);
    }

    public static void moveMonsters(char[][] dungeon, ArrayList<ArrayList<Integer>> ogrePositions, ArrayList<ArrayList<Integer>> demonPositions, int newRow, int newCol){
        //Copying ogrePositions to another arraylist temporarily to avoid concurrentExceptionHandling
        ocopyList.addAll(ogrePositions);
        ogrePositions.clear();
        Iterator<ArrayList<Integer>> ogreiterator = ocopyList.iterator();
        while (ogreiterator.hasNext()) {
            ArrayList<Integer> position = ogreiterator.next();
            if(!oremoveList.contains(position)) {
                minDistOgre(dungeon, position.get(0), position.get(1), newRow, newCol, ogrePositions, demonPositions);
            }
        }
        oremoveList.clear();
        ocopyList.clear();

        //Copying demonPositions to another arraylist temporarily to avoid concurrentExceptionHandling
        dcopyList.addAll(demonPositions);
        demonPositions.clear();
        Iterator<ArrayList<Integer>> demonIterator = dcopyList.iterator();
        while (demonIterator.hasNext()) {
            ArrayList<Integer> position = demonIterator.next();
            if(!dremoveList.contains(position)) {
                minDistDemon(dungeon, position.get(0), position.get(1), newRow, newCol, ogrePositions, demonPositions);
            }
        }
        dremoveList.clear();
        dcopyList.clear();
        if(score<=0 || (ogrePositions.isEmpty() && demonPositions.isEmpty())){return;}
    }


    public static void minDistOgre(char[][] dungeon, int orow, int ocol, int actManRow, int actManCol, ArrayList<ArrayList<Integer>> ogrePositions, ArrayList<ArrayList<Integer>> demonPositions) {
        int dist = Integer.MAX_VALUE;
        int targetRow = actManRow; // Assuming actManRow and actManCol are defined globally
        int targetCol = actManCol;
        int nRow = orow;
        int nCol = ocol;

        // Define the clockwise order of neighboring cells
        int[][] clockwiseOrder = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};

        // Iterate over the neighboring cells in clockwise order
        for (int i = 0; i < clockwiseOrder.length; i++) {
            // Calculate the row and column of the neighboring cell
            int dr = clockwiseOrder[i][0];
            int dc = clockwiseOrder[i][1];
            int neRow = orow + dr;
            int neCol = ocol + dc;

            // Check if the neighboring cell is within the dungeon boundaries
            if (neRow >= 0 && neRow < dungeon.length && neCol >= 0 && neCol < dungeon[0].length) {
                // Check if the neighboring cell is not a wall
                if (dungeon[neRow][neCol] != WALL) {
                    // Calculate the distance from the neighboring cell to the target point
                    int nDist = (int) (Math.pow(targetRow - neRow, 2) + Math.pow(targetCol - neCol, 2));
                    // Update the minimum distance and position if the n distance is smaller
                    if (nDist < dist) {
                        dist = nDist;
                        nRow = neRow;
                        nCol = neCol;
                    }
                }
            }
        }

        // Position the ogre in the shortest distance neighbor
        dungeon[orow][ocol] = EMPTY; // Clear the current position

        if(dungeon[nRow][nCol] == CORPSE){
            score += SCORE_GAIN_PER_MONSTER_KILL;
        }

        else if(dungeon[nRow][nCol] == OGRE){
            score += 2*SCORE_GAIN_PER_MONSTER_KILL;
            dungeon[nRow][nCol] = CORPSE;
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(nRow);
            temp.add(nCol);
            oremoveList.add(temp);
        }

        else if(dungeon[nRow][nCol] == DEMON){
            score += 2*SCORE_GAIN_PER_MONSTER_KILL;
            dungeon[nRow][nCol] = CORPSE;
            Iterator<ArrayList<Integer>> iterator = demonPositions.iterator();
            while (iterator.hasNext()) {
                ArrayList<Integer> position = iterator.next();
                if (position.get(0) == nRow && position.get(1) == nCol) {
                    iterator.remove(); // Use iterator's remove method to remove the current element
                    break;
                }
            }
        }

        else if(dungeon[nRow][nCol] == ACT_MAN){
            score = 0;
            dungeon[nRow][nCol] = 'X';
            actmanDead = true;
        }

        else if(dungeon[nRow][nCol] == 'X'){
            dungeon[nRow][nCol] = 'X';
        }

        else{
            dungeon[nRow][nCol] = OGRE; // Position the ogre in the shortest distance neighbor
            // Replacing the values in ogrePositions ArrayList
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(nRow);
            temp.add(nCol);
            ogrePositions.add(temp);
        }

        if(dist == 0){
            score = 0;
        }
    }



    public static void minDistDemon(char[][] dungeon, int drow, int dcol, int actManRow, int actManCol, ArrayList<ArrayList<Integer>> ogrePositions, ArrayList<ArrayList<Integer>> demonPositions) {
        int dist = Integer.MAX_VALUE;
        int targetRow = actManRow; // Assuming actManRow and actManCol are defined globally
        int targetCol = actManCol;
        int nRow = drow;
        int nCol = dcol;

        // Define the clockwise order of neighboring cells
        int[][] anticlockwiseOrder = {{-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}};

        // Iterate over the neighboring cells in clockwise order
        for (int i = 0; i < anticlockwiseOrder.length; i++) {
            // Calculate the row and column of the neighboring cell
            int dr = anticlockwiseOrder[i][0];
            int dc = anticlockwiseOrder[i][1];
            int neRow = drow + dr;
            int neCol = dcol + dc;

            // Check if the neighboring cell is within the dungeon boundaries
            if (neRow >= 0 && neRow < dungeon.length && neCol >= 0 && neCol < dungeon[0].length) {
                // Check if the neighboring cell is not a wall
                if (dungeon[neRow][neCol] != WALL) {
                    // Calculate the distance from the neighboring cell to the target point
                    int nDist = (int) (Math.pow(targetRow - neRow, 2) + Math.pow(targetCol - neCol, 2));
                    // Update the minimum distance and position if the n distance is smaller
                    if (nDist < dist) {
                        dist = nDist;
                        nRow = neRow;
                        nCol = neCol;
                    }
                }
            }
        }


        dungeon[drow][dcol] = EMPTY; // Clear the current position

        // Position the ogre in the shortest distance neighbor
        if(dungeon[nRow][nCol] == CORPSE){
            score += SCORE_GAIN_PER_MONSTER_KILL;
        }

        else if(dungeon[nRow][nCol] == DEMON){
            score += 2*SCORE_GAIN_PER_MONSTER_KILL;
            dungeon[nRow][nCol] = CORPSE;
            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(nRow);
            temp.add(nCol);
            oremoveList.add(temp);
        }

        else if(dungeon[nRow][nCol] == OGRE){
            score += 2*SCORE_GAIN_PER_MONSTER_KILL;
            Iterator<ArrayList<Integer>> iterator = ogrePositions.iterator();
            while (iterator.hasNext()) {
                ArrayList<Integer> position = iterator.next();
                if (position.get(0) == drow && position.get(1) == dcol) {
                    iterator.remove(); // Use iterator's remove method to safely remove the element
                    break; // Exit the loop once the element is removed
                }
            }
            dungeon[nRow][nCol] = CORPSE;
        }

        else if(dungeon[nRow][nCol] == ACT_MAN){
            score = 0;
            dungeon[nRow][nCol] = 'X';
            actmanDead = true;
        }

        else if(dungeon[nRow][nCol] == 'X'){
            dungeon[nRow][nCol] = 'X';
        }

        else{
            dungeon[nRow][nCol] = DEMON; // Position the ogre in the shortest distance neighbor

            ArrayList<Integer> temp = new ArrayList<>();
            temp.add(nRow);
            temp.add(nCol);
            demonPositions.add(temp);
        }

        if(dist == 0){
            score = 0;
        }
    }

    public static void gameended(char[][] dungeon, StringBuilder actions, String outputFile, int actManRow, int actManCol) {
        try {
            // Write output file
            FileWriter writer = new FileWriter(outputFile);
            writer.write(actions + "\n");
            if(actmanDead){score = 0;}
            writer.write(score + "\n");
            int rows = dungeon.length;
            int cols = dungeon[0].length;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.write(dungeon[i][j]);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the output file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
