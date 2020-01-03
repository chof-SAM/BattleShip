import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.imageio.ImageIO;
import java.io.*;

public class BattleShip extends JPanel {
    private Board userBoard;
    private Board oppBoard;
    private Ship[] ships;
    private Ship[] oppShips;
    private static Ship curShip;
    private final int time = 600;
    private final  int unitSize = 30;
    private static int initialCounter;
    private static Point pointer;
    private static Point opp;
    private boolean turn;
    private boolean gameOver;
    private boolean gameWin;
    private boolean playAgain;
    private static Point [] oppThrows;
    private int oppCounter;
    private static boolean oppHit;
    private static boolean keepShooting;
    private static boolean changeDirection;
    private static int choice;
    private static int streak;
    private int movesToSink;
    private JFrame mainWindow;

    public BattleShip() {
        init();
    }

    private void init() {
        userBoard = new Board(0, 0);
        oppBoard = new Board(13, 0);
        ships = new Ship[]{new Ship("CARRIER"), new Ship("BATTLESHIP"), new Ship("CRUISER"), new Ship("SUBMARINE"), new Ship("DESTROYER")};
        oppShips = new Ship[]{new Ship("CARRIER"), new Ship("BATTLESHIP"), new Ship("CRUISER"), new Ship("SUBMARINE"), new Ship("DESTROYER")};
        pointer = new Point(15, 3);
        opp = new Point();
        oppShips = oppBoard.addRandomShips(oppShips);
        initialCounter = -1;
        oppCounter = -1;
        oppThrows = new Point[12*12];
        for(int i = 0; i<12*12; i++){
            oppThrows[i] = new Point();
        }
        oppHit = false;
        keepShooting = false;
        changeDirection = false;
        choice = 0;
        streak = 0;
        movesToSink = 0;
        turn = true;
        gameOver = false;
        gameWin = false;
        playAgain = false;

        mainWindow = new JFrame("BattleShip");
        mainWindow.setDefaultCloseOperation(3);
        mainWindow.setSize(40*unitSize, 480);
        setBackground(Color.DARK_GRAY);
        mainWindow.setVisible(true);
        mainWindow.add(this);
        mainWindow.addKeyListener(new KeyListener() {
                public void keyTyped(KeyEvent e) {
                }

                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();
                    switch(code) {
                        case KeyEvent.VK_UP:
                        KeyMoveUpPressed();
                        break;
                        case KeyEvent.VK_DOWN:
                        KeyMoveDownPressed();
                        break;
                        case KeyEvent.VK_LEFT:
                        KeyMoveLeftPressed();
                        break;
                        case KeyEvent.VK_RIGHT:
                        KeyMoveRightPressed();
                        break;
                        case KeyEvent.VK_SPACE:
                        KeySelectPressed();
                        break;
                        case KeyEvent.VK_ENTER:
                        KeySelectPressed();
                        break;
                        case KeyEvent.VK_SHIFT:
                        KeyRotatePressed();
                        break;
                        case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                        case KeyEvent.VK_R:
                        new BattleShip();
                        break;
                        case KeyEvent.VK_T:
                        if(initialCounter>=5)gameOver = true;
                        break;
                    }
                }

                public void keyReleased(KeyEvent e) {
                }
            });

        new Thread() {
            @Override public void run() {
                while (true) {
                    try {
                        while(!turn && !gameOver && !gameWin) {
                            if(!oppHit)Thread.sleep(4*time/3);
                            else Thread.sleep(time);
                            oppTurn();
                            repaint();
                        }
                        repaint();
                    } catch ( InterruptedException e ) {}
                }
            }
        }.start();      
    }

    private synchronized void KeyMoveUpPressed() {
        if (initialCounter < 5 && initialCounter > -1) {
            curShip.moveUp();
            if (userBoard.outsideTheBoard(curShip)) {
                curShip.moveDown();
            }
        } else {
            pointer.translate(0, -1);
            if (oppBoard.outsideTheBoard(pointer)) {
                pointer.translate(0, 1);
            }
        }
        repaint();
    }

    private synchronized void KeyMoveDownPressed() {
        if (initialCounter < 5 && initialCounter > -1) {
            curShip.moveDown();
            if (userBoard.outsideTheBoard(curShip)) {
                curShip.moveUp();
            }
        } else {
            pointer.translate(0, 1);
            if (oppBoard.outsideTheBoard(pointer)) {
                pointer.translate(0, -1);
            }
        }

        repaint();
    }

    private synchronized void KeyMoveLeftPressed() {
        if (initialCounter < 5 && initialCounter > -1) {
            curShip.moveLeft();
            if (userBoard.outsideTheBoard(curShip)) {
                curShip.moveRight();
            }
        } 
        else if (gameOver || gameWin) {
            playAgain = true;
        } else {
            pointer.translate(-1, 0);
            if (oppBoard.outsideTheBoard(pointer)) {
                pointer.translate(1, 0);
            }
        }

        repaint();
    }

    private synchronized void KeyMoveRightPressed() {
        if (initialCounter < 5 && initialCounter > -1) {
            curShip.moveRight();
            if (userBoard.outsideTheBoard(curShip)) {
                curShip.moveLeft();
            }
        } else if (gameOver || gameWin) {
            playAgain = false;
        } else {
            pointer.translate(1, 0);
            if (oppBoard.outsideTheBoard(pointer)) {
                pointer.translate(-1, 0);
            }
        }

        repaint();
    }

    private synchronized void KeyRotatePressed() {
        if(initialCounter < 5 && initialCounter > -1){
            curShip.rotate();
            if (userBoard.outsideTheBoard(curShip)) {
                curShip.rotate();
            }
        }
        repaint();
    }

    private synchronized void KeySelectPressed() {
        if (initialCounter == -1) {
            initialCounter++;
            curShip = ships[initialCounter];
            curShip.setLocationTo(2, 3);
        } else if (initialCounter < 4) {
            initialCounter++;
            if(curShip.crashOtherShips(ships)) initialCounter--;
            curShip = ships[initialCounter];
            curShip.setLocationTo(2, 3);
        } else if (initialCounter == 4){
            initialCounter++;
            if(curShip.crashOtherShips(ships)) {
                initialCounter--;
                curShip = ships[initialCounter];
                curShip.setLocationTo(2, 3);
            }
        }
        else if (gameWin || gameOver) {
            if(playAgain) new BattleShip();
            else System.exit(0);
        }
        else if (turn){
            if(oppBoard.throwMissil(pointer , oppShips).equals("hit") || oppBoard.throwMissil(pointer , oppShips).equals("sinked")) turn = true;
            else turn = false;
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(userBoard != null) userBoard.draw(g, "PLAYER'S BOARD");        
        if (initialCounter < 5 && userBoard != null) {
            userBoard.drawShipsBoard(g);
            for(int i = 0; i < ships.length; i++) {
                if (ships[i] == curShip) {
                    curShip.drawSelected(g);
                } else {
                    ships[i].draw(g);   
                }
                ships[i].drawLabels(g);
            }
            drawShipsLegend(g);
        } else if (oppBoard != null) {
            oppBoard.draw(g, "OPPONENT'S BOARD");
            drawShipsLegend(g);
            userBoard.draw(g, "PLAYER'S BOARD");

            if(oppCounter>=0) drawPointer(g, opp);
            for(int i = 0; i < ships.length; i++) {
                ships[i].draw(g);   
            }
            oppBoard.drawHit(g, oppShips);
            userBoard.drawHit(g, ships);
            for(int i = 0; i < oppShips.length; i++) {
                if (oppShips[i].isSinked()) oppShips[i].draw(g);   
            }
            for(int i = 0; i < ships.length; i++) {
                if (ships[i].isSinked()) ships[i].draw(g);   
            }
            if(areShipsSinked(ships)) gameOver = true;
            if(areShipsSinked(oppShips)) gameWin = true;

            drawPointer(g, pointer);
        }
        if(initialCounter<0){
            g.setColor(Color.WHITE);
            g.fillRect(4*unitSize, 7*unitSize + 10 , 6*unitSize, 1*unitSize+10);
            g.setColor(Color.BLACK);
            g.fillRect(4*unitSize+5, 7*unitSize + 15 , 6*unitSize-10, 1*unitSize);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", 0, 11));
            g.drawString("PRESS 'ENTER' TO START", 4*unitSize+25, 8*unitSize+5);
        }
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.fillRect(28*unitSize+10, 2*unitSize, 7*unitSize, 3*unitSize);
            g.setColor(Color.BLACK);
            g.fillRect(28*unitSize+15, 2*unitSize+5, 7*unitSize-10, 3*unitSize-10);
            g.setColor(Color.RED);
            g.setFont(new Font("Consolas", 0, 30));
            g.drawString("GAME OVER,", 29*unitSize, 3*unitSize+5);
            g.drawString("  LOSER  ", 29*unitSize, 4*unitSize + 15);
        } else if (gameWin){
            g.setColor(Color.WHITE);
            g.fillRect(27*unitSize+10, 2*unitSize, 10*unitSize + 5, 3*unitSize);
            g.setColor(Color.BLACK);
            g.fillRect(27*unitSize+15, 2*unitSize+5, 10*unitSize-5, 3*unitSize-10);
            g.setColor(Color.RED);
            g.setFont(new Font("Consolas", 0, 30));
            g.drawString("CONGRATULATIONS,", 28*unitSize, 3*unitSize+5);
            g.drawString("  YOU'VE WON  ", 28*unitSize, 4*unitSize + 15);
        }
        if(gameOver || gameWin){
            g.setColor(Color.RED);
            g.fillRect(29*unitSize+15, 5*unitSize + 15 , 4*unitSize+20, 3*unitSize-10);
            g.setColor(Color.BLACK);
            g.fillRect(29*unitSize+20, 5*unitSize + 20 , 4*unitSize+10, 3*unitSize-20);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", 0, 15));
            g.drawString("DO YOU WANT TO", 29*unitSize+30, 6*unitSize+5);
            g.drawString("PLAY AGAIN?", 29*unitSize+30, 6*unitSize+22);
            if(playAgain){
                g.setColor(Color.GRAY);
                g.drawRect(31*unitSize-14, 7*unitSize-1, 33, 21);
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(31*unitSize-13, 7*unitSize, 32, 20);
                g.setFont(new Font("Consolas", Font.BOLD, 15));
                g.setColor(Color.RED);
                g.drawString("YES", 31*unitSize-9, 7*unitSize + 15);
                g.setColor(Color.GRAY);
                g.drawRect(32*unitSize+4, 7*unitSize-1, 31, 21);
                g.setColor(Color.WHITE);
                g.fillRect(32*unitSize+5, 7*unitSize, 30, 20);
                g.setFont(new Font("Consolas", 0, 13));
                g.setColor(Color.BLACK);
                g.drawString("NO", 32*unitSize+12, 7*unitSize + 15);
            } else {
                g.setColor(Color.GRAY);
                g.drawRect(31*unitSize-14, 7*unitSize-1, 33, 21);
                g.setColor(Color.WHITE);
                g.fillRect(31*unitSize-13, 7*unitSize, 32, 20);
                g.setFont(new Font("Consolas", 0, 13));
                g.setColor(Color.BLACK);
                g.drawString("YES", 31*unitSize-9, 7*unitSize + 15);
                g.setColor(Color.GRAY);
                g.drawRect(32*unitSize+4, 7*unitSize-1, 31, 21);
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(32*unitSize+5, 7*unitSize, 30, 20);
                g.setFont(new Font("Consolas", Font.BOLD, 15));
                g.setColor(Color.RED);
                g.drawString("NO", 32*unitSize+12, 7*unitSize + 15);
            }
        }

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Times New Roman", 0, 12));
        g.drawString("(c) Sofía Andújar Muñoz", unitSize , 410);
        g.drawString("chof,  2019", 6*unitSize - 10, 410);
        g.drawString("last version: 02/01/2020", 8*unitSize, 410);
    }

    public static void main(String[] args) {
        new BattleShip();
    }

    public void oppTurn() {
        if(!gameOver){
            if(changeDirection){
                changeDirection = false;
                if(choice==0 ) choice = 1;   
                else if(choice==1 ) choice = 0;
                else if(choice==2 ) choice = 3;
                else if(choice==3 )  choice = 2;
            }
            if (oppHit && !keepShooting) {
                choice = (int)(Math.random()*4);
                streak = 0;
                movesToSink = 1;
                switch(choice) {
                    case 0:
                    if(oppMoveLeft()) {
                        if(userBoard.throwMissil(opp , ships).equals("hit")) {
                            streak++;
                            movesToSink++;
                            keepShooting = true;
                        } else if (userBoard.throwMissil(opp , ships).equals("sinked")) {
                            keepShooting = false;
                            changeDirection = false;
                            oppHit = false;
                            surroundingSinked(movesToSink, 0);
                        }
                        else {
                            keepShooting = false;
                            turn = true;
                            opp = new Point(opp.x + 1, opp.y);
                        }
                    } else oppTurn();
                    break;
                    case 1:
                    if(oppMoveRight()) {
                        if(userBoard.throwMissil(opp , ships).equals("hit")) {
                            streak++;
                            movesToSink++;
                            keepShooting = true;
                        } else if (userBoard.throwMissil(opp , ships).equals("sinked")) {
                            keepShooting = false;
                            changeDirection = false;
                            oppHit = false;
                            surroundingSinked(movesToSink, 1);
                        } else {
                            keepShooting = false;
                            turn = true;
                            opp = new Point(opp.x - 1, opp.y);
                        }
                    } else oppTurn();
                    break;
                    case 2:
                    if(oppMoveUp()) {
                        if(userBoard.throwMissil(opp , ships).equals("hit")) {
                            streak++;
                            movesToSink++;
                            keepShooting = true;
                        } else if (userBoard.throwMissil(opp , ships).equals("sinked")) {
                            keepShooting = false;
                            changeDirection = false;
                            oppHit = false;
                            surroundingSinked(movesToSink, 2);
                        } else {
                            keepShooting = false;
                            turn = true;
                            opp = new Point(opp.x, opp.y + 1);
                        }
                    } else oppTurn();
                    break;
                    case 3:
                    if(oppMoveDown()) {
                        if(userBoard.throwMissil(opp , ships).equals("hit")) {
                            streak++;
                            movesToSink++;
                            keepShooting = true;
                        } else if (userBoard.throwMissil(opp , ships).equals("sinked")) {
                            keepShooting = false;
                            changeDirection = false;
                            oppHit = false;
                            surroundingSinked(movesToSink, 3);
                        } else {
                            keepShooting = false;
                            turn = true;
                            opp = new Point(opp.x, opp.y - 1);
                        }
                    } else oppTurn();
                    break;
                }
            } else
            if (keepShooting==true) {
                switch (choice) {
                    case 0: 
                    if(oppMoveLeft()) {
                        shootAgain();
                    } else {
                        changeDirection = true;
                        opp = new Point(opp.x + streak, opp.y);
                    }
                    break;
                    case 1:
                    if(oppMoveRight()){
                        shootAgain();
                    }
                    else {
                        changeDirection = true;
                        opp = new Point(opp.x - streak, opp.y);
                    }
                    break;
                    case 2:
                    if(oppMoveUp()){
                        shootAgain();
                    } else {
                        changeDirection = true;
                        opp = new Point(opp.x, opp.y + streak);
                    }
                    break;
                    case 3:
                    if(oppMoveDown()){
                        shootAgain();
                    } else {
                        changeDirection = true;
                        opp = new Point(opp.x, opp.y - streak);
                    }
                    break;
                }
            }
            else {
                oppMoveRandom();
                if(userBoard.throwMissil(opp , ships).equals("hit")) {
                    repaint();
                    oppHit = true;
                } else {
                    turn = true;
                }
            }
        }
    }

    private void shootAgain(){
        int x = 0;
        int y = 0;
        streak++;
        switch(choice){
            case 0: //From the left to the right
            x = streak;
            break;
            case 1: //From the right to the left
            x = -streak;
            break;
            case 2: //From up to down
            y = streak;
            break;
            case 3: //From down to up
            y = -streak;
            break;
        }
        int direction = choice;

        if (userBoard.throwMissil(opp , ships).equals("hit")){
            movesToSink++;
        }
        else if (userBoard.throwMissil(opp , ships).equals("sinked")) {
            keepShooting = false;
            changeDirection = false;
            oppHit = false;
            surroundingSinked(movesToSink, direction);
        } else {
            changeDirection = true;
            turn = true;
            opp = new Point(opp.x + x, opp.y + y);
        }
    }

    private void surroundingSinked(int times, int direc){
        int x = 0;
        int y = 0;
        switch(direc){
            case 0: //From the left to the right
            x = 1;
            break;
            case 1: //From the right to the left
            x = -1;
            break;
            case 2: //From up to down
            y = 1;
            break;
            case 3: //From down to up
            y = -1;
            break;
        }
        for(int k = 0; k<=times; k++){
            Point oppAux = new Point (opp.x + x*k, opp.y + y*k);
            for(int n = 0; n<4; n++){
                Point p = new Point(oppAux);
                switch(n){
                    case 0:
                    p = new Point(p.x + 1, p.y);
                    already(p);
                    break;
                    case 1:
                    p = new Point(p.x - 1, p.y);
                    already(p);
                    break;
                    case 2:
                    p = new Point(p.x, p.y + 1);
                    already(p);
                    break;
                    case 3:
                    p = new Point(p.x, p.y - 1);
                    already(p);
                    break;
                }
            }
        }
    }

    private synchronized boolean oppMoveLeft(){
        opp = new Point(opp.x - 1, opp.y);
        if (userBoard.outsideTheBoard(opp) || already(opp)) {
            opp = new Point(opp.x + 1, opp.y);
            return false;
        }
        repaint();
        return true;
    }

    private synchronized boolean oppMoveRight(){
        opp = new Point(opp.x + 1, opp.y);
        if (userBoard.outsideTheBoard(opp) || already(opp)) {
            opp = new Point(opp.x - 1, opp.y);
            return false;
        }
        repaint();
        return true;
    }

    private synchronized boolean oppMoveUp(){
        opp = new Point(opp.x, opp.y - 1);
        if (userBoard.outsideTheBoard(opp) || already(opp)) {
            opp = new Point(opp.x, opp.y + 1);
            return false;
        }
        repaint();
        return true;
    }

    private synchronized boolean oppMoveDown(){
        opp = new Point(opp.x, opp.y + 1);
        if (userBoard.outsideTheBoard(opp) || already(opp)) {
            opp = new Point(opp.x, opp.y - 1);
            return false;
        }
        repaint();
        return true;
    }

    private synchronized void oppMoveRandom(){
        do{
            int x = (int)(Math.random()*10)+2;
            int y = (int)(Math.random()*10)+3;
            opp = new Point(x, y);
        } while (already(opp));
        repaint();
    }

    private synchronized boolean already(Point p) {
        oppCounter++;
        oppThrows[oppCounter] = p;
        for(int k = 0; k<oppCounter; k++){
            if(oppThrows[k].equals(oppThrows[oppCounter])) {
                oppCounter--;
                return true;
            }
        }
        return false;
    }

    public void drawPointer(Graphics g, Point cell) {
        g.drawImage(loadImage("posInd.png"), cell.x * unitSize +1 , cell.y * unitSize + 1,null);
    }

    public static BufferedImage loadImage(String name){
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File(name));
        } catch (Exception e){}
        return img;
    }

    public static boolean areShipsSinked(Ship [] ships){
        for(int n = 0; n<5; n++){
            if (!ships[n].isSinked()) return false;
        }
        return true;
    }

    private void drawShipsLegend(Graphics g) {
        int unitSize = this.unitSize*2/3;
        g.setColor(Color.BLACK);
        g.fillRect(29*this.unitSize, 14*this.unitSize - 8*unitSize, 9*unitSize, 8*unitSize);
        g.setColor(Color.WHITE);
        g.fillRect(29*this.unitSize + unitSize-1, 14*this.unitSize - 7*unitSize, 7*unitSize+2, unitSize);
        g.setColor(Color.GRAY);
        g.fillRect(29*this.unitSize + unitSize, 2+ 14*this.unitSize - 6*unitSize, 7*unitSize, 5*unitSize-1);

        for(int k = 0; k < 5; k++) {
            g.setColor(Color.WHITE);
            g.drawRect(29*this.unitSize + 2*unitSize - 10, 14*this.unitSize - (6-k)*unitSize + 6, unitSize, unitSize/2);
            g.setColor(ships[k].getColor());
            g.fillRect(29*this.unitSize + 2*unitSize - 10, 14*this.unitSize - (6-k)*unitSize + 6, unitSize, unitSize/2);
            g.setColor(Color.BLACK);
            g.setFont(new Font("ARIAL", Font.BOLD, 12));
            g.drawString(ships[k].getName(), 29*this.unitSize + 3*unitSize , 14*this.unitSize - (5-k)*unitSize - 5);
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("ARIAL", Font.BOLD, 12));
        g.drawString("SHIPS LEGEND", 29*this.unitSize + 2*unitSize +5 , 14*this.unitSize - 6*unitSize - 5);
    }

}
