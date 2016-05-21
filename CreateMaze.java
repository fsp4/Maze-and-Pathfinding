import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JPanel;

// @author Fabian Pisztora

public class CreateMaze {
    // size must be 3, 7, 15, 31, 63, etc
    // 2x+1 where x is one of these numbers
    private int SIZE = 63;
    
    private static final int BORDERSIZE = 2;
    private static final int WINDOWSIZE = 800;
    private int[][] board;
    private int[][] pathBoard;
    private Random rand = new Random();
    
    public void generate() {
        this.board = new int[SIZE][SIZE];
        
        // init board to 0
        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(this.board[i], 0);
        }
        
        makeMazeRecursively(0, 0, SIZE - 1, SIZE - 1);
        board = addBorder(board);
        
        // add entrance/exit
        board[SIZE / 2][0] = 0;
        board[SIZE / 2 + 2][SIZE + BORDERSIZE - 1] = 0;
        
        //printBoard(board);
        draw(board);
        
        this.pathBoard = this.board;
        
        pathfind();
        printBoard(pathBoard);
        draw(pathBoard);
    }
    
    public void makeMazeRecursively(int r1, int c1, int r2, int c2) {
        if ((c2 - c1 >= 2) && (r2 - r1 >= 2)) {
            int x, y, z, max, min, r, c;
            
            // random number 0-3
            int random = rand.nextInt(4);
            
            // middle row and column
            r = (r1 + r2) / 2;
            c = (c1 + c2) / 2;
            //int r = getRandomOddNumberInRange(r1 + r1/2, r2 - r2/2);
            //int c = getRandomOddNumberInRange(c1 + c1/2, c2 - c2/2);
            
            // set row r to 1
            // horizontal
            for (int i = c1; i <= c2; i++) {
                board[r][i] = 1;
            }
            // set row c to 1
            // vertical
            for (int i = r1;i <= r2; i++) {
                board[i][c] = 1;
            }
            
            // leave left
            if (random == 0) {
                // right
                max = c2;
                min = c + 1;
                x = getRandomEvenNumberInRange(min, max);
                
                // top
                max = r - 1;
                min = r1;
                y = getRandomEvenNumberInRange(min, max);
                
                // bottom
                max = r2;
                min = r + 1;
                z = getRandomEvenNumberInRange(min, max);
                
                board[r][x] = 0;    // right
                board[y][c] = 0;    // top
                board[z][c] = 0;    // bottom
            }
            
            // leave right
            else if (random == 1) {
                // top
                max = r - 1;
                min = r1;
                x = getRandomEvenNumberInRange(min, max);
                
                // bottom
                max = r2;
                min = r + 1;
                y = getRandomEvenNumberInRange(min, max);
                
                // left
                max = c - 1;
                min = c1;
                z = getRandomEvenNumberInRange(min, max);
                
                board[x][c] = 0;    // top
                board[y][c] = 0;    // bottom
                board[r][z] = 0;    // left
            }
            
            // leave top
            else if (random == 2) {
                // left
                max = c - 1;
                min = c1;
                x = getRandomEvenNumberInRange(min, max);
                
                // right
                max = c2;
                min = c + 1;
                y = getRandomEvenNumberInRange(min, max);
                
                // bottom
                max = r2;
                min = r + 1;
                z = getRandomEvenNumberInRange(min, max);
                
                board[r][x] = 0;    // left
                board[r][y] = 0;    // right
                board[z][c] = 0;    // bottom
            }
            
            // leave bottom
            else if (random == 3) {
                // left
                max = c - 1;
                min = c1;
                x = getRandomEvenNumberInRange(min, max);
                
                // right
                max = c2;
                min = c + 1;
                y = getRandomEvenNumberInRange(min, max);
                
                // top
                max = r - 1;
                min = r1;
                z = getRandomEvenNumberInRange(min, max);
                
                board[r][x] = 0;    // left
                board[r][y] = 0;    // right
                board[z][c] = 0;    // top
            }
            
            makeMazeRecursively(r1, c1, r - 1, c - 1);      // top left
            makeMazeRecursively(r1, c + 1, r - 1, c2);      // top right
            makeMazeRecursively(r + 1, c1, r2, c - 1);      // bottom left
            makeMazeRecursively(r + 1, c + 1, r2, c2);      // bottom right
        }
    }
    
    public int getRandomEvenNumberInRange(int min, int max) {
        if (max % 2 != 0) --max;
        if (min % 2 != 0) ++min;
        return min + 2*(int)(Math.random()*((max-min)/2+1));
    }
    
    public int getRandomOddNumberInRange(int min, int max) {
        if (max % 2 == 0) --max;
        if (min % 2 == 0) ++min;
        return min + 2*(int)(Math.random()*((max-min)/2+1));
    }
    
    public int[][] addBorder(int[][] twoDimentionalArray) {
        int origionalLength = twoDimentionalArray.length;
        int newLength = origionalLength + 2;
        int[][] newArray = new int[newLength][newLength];
        
        // copy grid over, shifting down 1 and right 1
        for (int i = 0; i < origionalLength; i++) {
            for (int j = 0; j < origionalLength; j++) {
                newArray[i + 1][j + 1] = twoDimentionalArray[i][j];
            }
        }
        
        // add border
        Arrays.fill(newArray[0], 1);                // top
        Arrays.fill(newArray[newLength - 1], 1);    // bottom
        for (int i = 0; i < newLength; i++) {
            newArray[i][0] = 1;                     // left
            newArray[i][newLength - 1] = 1;         // right
        }
        
        return newArray;
    }
    
    public void pathfind() {
        Stack<int[]> currentCheck = new Stack();
        Stack<int[]> nextCheck = new Stack();
        int incr = 2;
        pathBoard[pathBoard.length / 2 - 1][0] = 2;
        int[] start = {(pathBoard.length/2 - 1), 0};
        currentCheck.push(start);

        while (!currentCheck.isEmpty()) {   
            incr++;
            while (!currentCheck.isEmpty()) {
                int[] current = currentCheck.pop();  
                lookAround(current, nextCheck, incr);
            }
            currentCheck = (Stack<int[]>) nextCheck.clone();
            nextCheck.clear();
        }

        currentCheck.clear();
        nextCheck.clear();
        int[] backStart = {(SIZE / 2 + 2), (SIZE + BORDERSIZE - 1)};
        int incrementor = -1;
        currentCheck.push(backStart);
        for (int i = 2; i <= incr; i++) {
            while (!currentCheck.isEmpty()) {
                int[] current = currentCheck.pop();
                goBack(current, nextCheck, incrementor);
                incrementor--;
                
            }
            
            currentCheck = (Stack<int[]>) nextCheck.clone();
            nextCheck.clear();
        }
    }

    private void lookAround(int[] current, Stack nextCheck, int incr) {
        int rowCord = current[0];
        int colCord = current[1];

        int left = 1;
        int right = 1;
        int[] leftCord = new int[2];
        int[] rightCord = new int[2];

        int up = pathBoard[rowCord - 1][colCord];
        int[] upCord = {(rowCord - 1), colCord};

        int down = pathBoard[rowCord + 1][colCord];
        int[] downCord = {(rowCord + 1), colCord};

        if (colCord != 0) {
            left = pathBoard[rowCord][colCord - 1];
            leftCord[0] = rowCord;
            leftCord[1] = colCord - 1;
        }
        if (colCord != pathBoard.length - 1) {
            right = pathBoard[rowCord][colCord + 1];
            rightCord[0] = rowCord;
            rightCord[1] = colCord + 1;
        }

        if (up == 0) {
            nextCheck.push(upCord);
            pathBoard[rowCord - 1][colCord] = incr;
        }
        if (down == 0) {
            nextCheck.push(downCord);
            pathBoard[rowCord + 1][colCord] = incr;
        }
        if (left == 0) {
            nextCheck.push(leftCord);
            pathBoard[rowCord][colCord - 1] = incr;
        }
        if (right == 0) {
            nextCheck.push(rightCord);
            pathBoard[rowCord][colCord + 1] = incr;
        }
    }

    public void goBack(int[] current, Stack nextCheck, int incrementor) {
        int rowCord = current[0];
        int colCord = current[1];
        int currentValue = pathBoard[rowCord][colCord];
        pathBoard[rowCord][colCord] = incrementor;
        if (colCord == 0) {
            return;
        }
        int left = 1;
        int right = 1;
        int[] leftCord = new int[2];
        int[] rightCord = new int[2];

        int up = pathBoard[rowCord - 1][colCord];
        int[] upCord = {(rowCord - 1), colCord};

        int down = pathBoard[rowCord + 1][colCord];
        int[] downCord = {(rowCord + 1), colCord};

        if (colCord != 0) {
            left = pathBoard[rowCord][colCord - 1];
            leftCord[0] = rowCord;
            leftCord[1] = colCord - 1;
        }
        if (colCord != pathBoard.length - 1) {
            right = pathBoard[rowCord][colCord + 1];
            rightCord[0] = rowCord;
            rightCord[1] = colCord + 1;
        }

        if (up == currentValue - 1) {
            nextCheck.push(upCord);
        }
        else if (down == currentValue - 1) {
            nextCheck.push(downCord);
        }
        else if (left == currentValue - 1) {
            nextCheck.push(leftCord);
        }
        else if (right == currentValue - 1) {
            nextCheck.push(rightCord);
        }
    }
    
    public void printBoard(int[][] twoDimentionalArray) {
        for (int i = 0; i < twoDimentionalArray.length; i++) {
            for (int j = 0; j < twoDimentionalArray.length; j++) {
                if (twoDimentionalArray[i][j] < 10 && twoDimentionalArray[i][j] >= 0) System.out.print(" " + "0" + twoDimentionalArray[i][j]);
                else System.out.print(" " + twoDimentionalArray[i][j]);
            }
            System.out.println();
        }
    }
    
    public void draw(int[][] twoDimentionalArray) {
        JFrame GameBoard = new JFrame();
        GameBoard.setSize(WINDOWSIZE, WINDOWSIZE);
        GameBoard.setTitle("Maze");
        GameBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container pane = GameBoard.getContentPane();
        pane.setLayout(new GridLayout(SIZE + BORDERSIZE, SIZE + BORDERSIZE));
        Color checker;

        for (int x = 0; x < SIZE + BORDERSIZE; x++) {
            for (int y = 0; y < SIZE + BORDERSIZE; y++) {
                if (twoDimentionalArray[x][y] == 0) {
                    checker = Color.white;
                } 
                else if (twoDimentionalArray[x][y] == 1) {
                    checker = Color.blue;
                }
                else if (twoDimentionalArray[x][y] < 0) {
                    checker = Color.red;
                }
                else {
                    checker = Color.white;
                }

                JPanel panel = new JPanel();
                panel.setPreferredSize(new Dimension(WINDOWSIZE / SIZE, WINDOWSIZE / SIZE));
                panel.setBackground(checker);
                pane.add(panel);
            }
        }
        GameBoard.setVisible(true);
    }
    
    public static void main(String[] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        CreateMaze maze = new CreateMaze();
        maze.generate();
    }
}