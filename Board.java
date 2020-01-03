import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;
import java.io.*;
public class Board {
    private Color[][] grid;
    private final int unitSize = 30;
    public final Color NAVY = new Color(128);
    private Font font = new Font("Dialog", 1, 15);
    private static String[] letter = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private static String[] number = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private int x;
    private int y;
    private Random random = new Random();

    public Board(int x, int y) {
        this.x = x;
        this.y = y;
        this.grid = new Color[14][13];

        for(int i = 0; i < this.grid.length; ++i) {
            for(int j = 0; j < this.grid[i].length; ++j) {
                if (i == 0 || j == 0 || i == this.grid.length - 1 || j == this.grid[i].length - 1 || i == 2 && j == 1) {
                    this.grid[i][j] = Color.BLACK;
                } else if (i == 1) {
                    this.grid[i][j] = Color.WHITE;
                } else if ((i != 2 || 1 >= j) && j != 1) {
                    this.grid[i][j] = new Color(128);
                } else {
                    this.grid[i][j] = Color.GRAY;
                }
            }
        }

    }

    public void draw(Graphics g, String name) {
        g.setColor(Color.BLACK);
        g.fillRect(this.x, this.y, unitSize * (this.grid[0].length + this.x), unitSize * (this.grid.length + this.y));
        g.setColor(Color.WHITE);
        g.fillRect(unitSize * (1 + this.x) - 1, unitSize * (1 + this.y) - 1, 11*unitSize +1, unitSize);
        g.fillRect(unitSize * (1 + this.x), unitSize * (2 + this.y), 11*unitSize -1, 11*unitSize -1);

        for(int i = 0; i < this.grid.length; ++i) {
            for(int j = 0; j < this.grid[i].length; ++j) {
                g.setColor(this.grid[i][j]);
                g.fillRect(unitSize * (j + this.x), unitSize * (i + this.y), unitSize - 1, unitSize - 1);
                if (i == 2 && 1 < j && j < 12) {
                    g.setColor(Color.BLACK);
                    g.setFont(this.font);
                    g.drawString(number[j - 2], (j + this.x) * unitSize + 10, (i + this.y) * unitSize + 20);
                } else if (2 < i && i < 13 && j == 1) {
                    g.setColor(Color.BLACK);
                    g.setFont(this.font);
                    g.drawString(letter[i - 3], (j + this.x) * unitSize + 10, (i + this.y) * unitSize + 20);
                }
                if(grid[i][j].equals(Color.BLUE.brighter())) {
                    g.drawImage(loadImage("miss.png"), (j + this.x)*unitSize + 1, (i + this.y)*unitSize + 2, null);
                }
            }
        }

        g.setColor(Color.BLACK);
        g.setFont(this.font);
        g.drawString(name, (4 + this.x) * unitSize + 10, (2 + this.y) * unitSize - 10);
    }

    public void drawShipsBoard(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect((13 + this.x) * unitSize, this.y * unitSize, 8*unitSize, 14*unitSize);
        g.setColor(Color.WHITE);
        g.fillRect((13 + this.x) * unitSize - 1, (1 + this.y) * unitSize - 1, 7*unitSize+1, unitSize);

        for(int i = 0; i < 7; ++i) {
            for(int j = 0; j < 11; ++j) {
                g.setColor(Color.GRAY);
                g.fillRect(unitSize * (i + 13 + this.x), unitSize * (j + 2 + this.y), unitSize - 1, unitSize - 1);
            }
        }

        g.setColor(Color.BLACK);
        g.setFont(this.font);
        g.drawString("SHIPS", (15 + this.x) * unitSize + 20, (2 + this.y) * unitSize - 10);
    }

    public Color getColor(Point cell) {
        return grid[cell.y - this.y][cell.x - this.x];
    }

    public Color[][] toMatrix() {
        return this.grid;
    }

    public boolean outsideTheBoard(Ship s1) {
        int h = s1.height();
        int w = s1.width();
        Point position = s1.getPosition(0);

        for(int y = position.y; y < position.y + h; ++y) {
            for(int x = position.x; x < position.x + w; ++x) {
                if (2 + this.y >= y || y >= 13 + this.y || 1 + this.x >= x || x >= 12 + this.x) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean outsideTheBoard(Point p) {
        return 2 + this.y >= p.y || p.y >= 13 + this.y || 1 + this.x >= p.x || p.x >= 12 + this.x;
    }

    public String throwMissil(Point cell, Ship [] ships) {
        for(int k = 0; k < 5; ++k) {
            for(int n = 0; n < ships[k].size(); ++n) {
                if (ships[k].getPosition(n).equals(cell)) {
                    hit(cell, ships);
                    if(ships[k].isSinked()) return "sinked";
                    return "hit";
                }
            }
        }
        miss(cell);
        return "miss";
    }

    public void miss(Point cell) {
        int i = cell.y - this.y;
        int j = cell.x - this.x;
        this.grid[i][j] = Color.BLUE.brighter();
    }

    public void hit(Point cell, Ship [] ships) {
        int i = cell.y - this.y;
        int j = cell.x - this.x;
        this.grid[i][j] = Color.RED;
        sinking(cell, ships);
    }

    public void drawHit(Graphics g, Ship [] ships){
        for(int k = 0; k<5; k++){
            for(int n = 0; n<ships[k].size(); n++){
                Point p = ships[k].getPosition(n);
                if(getColor(p).equals(Color.RED)){
                    g.drawImage(loadImage("tnt.jpg"), p.x*unitSize + 4, p.y*unitSize+4, null);
                }
            }
        } 
    }

    public void sinking(Point cell, Ship [] ships){
        boolean stop = false;
        for(int k = 0; k<5; k++){
            stop = false;
            for(int n = 0; n<ships[k].size() && stop == false; n++){
                Point p = ships[k].getPosition(n);
                if(!getColor(p).equals(Color.RED)){
                    stop = true;
                }
            }
            if(!stop){
                ships[k].sinked();
                for(int n = 0; n<ships[k].size(); n++){
                    Point p = ships[k].getPosition(n);
                    int i = p.y - this.y;
                    int j = p.x - this.x;
                    this.grid[i][j] = Color.GRAY;
                }
            }
        }
    }

    public Point getPosition() {
        return new Point(this.x, this.y);
    }

    public Ship[] addRandomShips(Ship [] ships) {
        for(int k = 0; k < 5; ++k) {
            ships[k].setLocationTo(this.x, this.y);

            do {
                int orient = this.random.nextInt(2);
                if (orient == 1) {
                    ships[k].rotate();
                }

                int x = this.random.nextInt(10 - ships[k].width()) + 2 + this.x;
                int y = this.random.nextInt(10 - ships[k].height()) + 3 + this.y;
                ships[k].setLocationTo(x, y);
            } while(ships[k].crashOtherShips(ships));
        }

        return ships;
    }

    public static BufferedImage loadImage(String name){
        BufferedImage img = null;
        try{
            img = ImageIO.read(new File(name));
        } catch (Exception e){}
        return img;
    }

}