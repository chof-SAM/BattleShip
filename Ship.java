import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Font;

public class Ship {
    private String name;
    private int height = 1;
    private int width;
    private int size;
    private boolean horizontal = true;
    private boolean sinked = false;
    private Point [] p = {new Point(), new Point(), new Point(), new Point(), new Point()};
    private Color color;
    private final int unitSize = 30;

    public Ship(String shipName) {
        switch (shipName) {
            case "CARRIER": width = size = 5; color = new Color(0x00AA48 ); name = "CARRIER";
            Point [] p0 = {new Point(), new Point(), new Point(), new Point(), new Point()};
            p = p0;
            for(int i = 0; i<size; i++){
                p[i].setLocation(14+i, 3);
            }
            break;
            case "BATTLESHIP": width = size = 4; color = new Color(0xFFFA89 ); name = "BATTLESHIP";
            Point [] p1 = {new Point(), new Point(), new Point(), new Point()};
            p = p1;
            for(int i = 0; i<size; i++){
                p[i].setLocation(14+i, 5);
            }
            break;
            case "CRUISER": width = size = 3; color = new Color(0xC96BDA); name = "CRUISER";
            Point [] p2 = {new Point(), new Point(), new Point()};
            p = p2;
            for(int i = 0; i<size; i++){
                p[i].setLocation(14+i, 7);
            }
            break;
            case "SUBMARINE": width = size = 3; color = new Color(0x6BDAD0); name = "SUBMARINE";
            Point [] p3 = {new Point(), new Point(), new Point()};
            p = p3;
            for(int i = 0; i<size; i++){
                p[i].setLocation(14+i, 9);
            }
            break;
            case "DESTROYER": width = size = 2; color = new Color(0xFF9E4D); name = "DESTROYER";
            Point [] p4 = {new Point(), new Point()};
            p = p4;
            for(int i = 0; i<size; i++){
                p[i].setLocation(14+i, 11);
            }
            break;
        }
    }

    public void rotate(){
        if(!horizontal){
            horizontal = true;
            width = height;
            height = 1;
            for(int i = 0; i<size; i++){
                p[i].setLocation(p[i].x+i, p[i].y-i);
            }
        } else {
            horizontal = false;
            height = width;
            width = 1;
            for(int i = 0; i<size; i++){
                p[i].setLocation(p[i].x-i, p[i].y+i);
            }
        }
    }

    public void draw(Graphics g){
        if(!sinked){
            g.setColor(Color.WHITE);                  
            g.fillRect((p[0].x)*unitSize-1,(p[0].y)*unitSize-1, unitSize*width+1, unitSize*height+1);
            g.setColor(color);                  
            g.fillRect((p[0].x)*unitSize+1,(p[0].y)*unitSize+1, unitSize*width-3, unitSize*height-3);
        }
        else {
            g.setColor(Color.RED);                  
            g.fillRect((p[0].x)*unitSize-1,(p[0].y)*unitSize-1, unitSize*width+1, unitSize*height+1);
            g.setColor(color);                  
            g.fillRect((p[0].x)*unitSize+1,(p[0].y)*unitSize+1, unitSize*width-3, unitSize*height-3);
            for(int n = 0; n<size(); n++){
                g.setColor(Color.RED);
                g.drawLine(p[n].x*unitSize + 7, p[n].y*unitSize + 7, p[n].x*unitSize + 22, p[n].y*unitSize + 22);
                g.drawLine(p[n].x*unitSize + 8, p[n].y*unitSize + 7, p[n].x*unitSize + 22, p[n].y*unitSize + 21);
                g.drawLine(p[n].x*unitSize + 7, p[n].y*unitSize + 8, p[n].x*unitSize + 21, p[n].y*unitSize + 22);
                g.drawLine(p[n].x*unitSize + 9, p[n].y*unitSize + 7, p[n].x*unitSize + 22, p[n].y*unitSize + 20);
                g.drawLine(p[n].x*unitSize + 7, p[n].y*unitSize + 9, p[n].x*unitSize + 20, p[n].y*unitSize + 22);
                //
                g.drawLine(p[n].x*unitSize + 22, p[n].y*unitSize + 7, p[n].x*unitSize + 7, p[n].y*unitSize + 22);
                g.drawLine(p[n].x*unitSize + 21, p[n].y*unitSize + 7, p[n].x*unitSize + 7, p[n].y*unitSize + 21);
                g.drawLine(p[n].x*unitSize + 22, p[n].y*unitSize + 8, p[n].x*unitSize + 8, p[n].y*unitSize + 22);
                g.drawLine(p[n].x*unitSize + 20, p[n].y*unitSize + 7, p[n].x*unitSize + 7, p[n].y*unitSize + 20);
                g.drawLine(p[n].x*unitSize + 22, p[n].y*unitSize + 9, p[n].x*unitSize + 9, p[n].y*unitSize + 22);
            }
        } 
    }

    public void drawSelected(Graphics g){
        g.setColor(Color.RED);                                   
        g.fillRect((p[0].x)*unitSize-3,(p[0].y)*unitSize-3, unitSize*width+5, unitSize*height+5);
        g.setColor(color);                  
        g.fillRect((p[0].x)*unitSize,(p[0].y)*unitSize, unitSize*width-1, unitSize*height-1);
    }

    public void drawLabels(Graphics g){
        g.setColor(Color.BLACK);
        g.setFont(new Font("ARIAL", Font.BOLD, 14));
        if(horizontal && p[0].equals(new Ship(name).getPosition(0))) {
            String sName;
            if(name.equals("DESTROYER")) sName = "DEST.";
            else if (name.equals("SUBMARINE")) sName = "SUBM.";
            else sName = name;
            g.drawString(sName,p[0].x*unitSize+10,p[0].y*unitSize+20);
        }
    }

    public Point getPosition(int k){
        return p[k];
    }

    public Color getColor(){
        return color;
    }

    public String getName(){
        return name;
    }

    public int height(){
        return height;
    }

    public int width(){
        return width;
    }

    public int size(){
        return size;
    }

    public boolean isVertical(){
        return !horizontal;
    }

    public boolean isHorizontal(){
        return horizontal;
    }

    public boolean isSinked(){
        return sinked;
    }

    public void setLocationToDefault(){
        for(int k = 0; k<size; k++) p[k] = new Ship(name).getPosition(k);
    }    

    public void setLocationTo(int x, int y){
        if (horizontal )for(int k = 0; k<size; k++) p[k].setLocation(x+k, y);
        else for(int k = 0; k<size; k++) p[k].setLocation(x, y+k);
    }

    public void sinked(){
        sinked = true;
    }

    public void moveLeft(){
        for(int k = 0; k<size; k++) p[k].translate(-1, 0);
    }

    public void moveRight(){
        for(int k = 0; k<size; k++) p[k].translate(1, 0);
    }

    public void moveUp(){
        for(int k = 0; k<size; k++) p[k].translate(0, -1);
    }

    public void moveDown(){
        for(int k = 0; k<size; k++) p[k].translate(0, 1);
    }

    public boolean crashOtherShips(Ship[] ships){
        for(int k = 0; k<5; k++){
            if(!ships[k].getName().equals(name)){
                for(int m = 0; m<size; m++){
                    for(int n = 0; n<ships[k].size(); n++){
                        Point p1 = new Point(ships[k].getPosition(n).x + 1, ships[k].getPosition(n).y);
                        Point p2 = new Point(ships[k].getPosition(n).x - 1, ships[k].getPosition(n).y);
                        Point p3 = new Point(ships[k].getPosition(n).x, ships[k].getPosition(n).y + 1);
                        Point p4 = new Point(ships[k].getPosition(n).x, ships[k].getPosition(n).y - 1);
                        if(p[m].equals(ships[k].getPosition(n)) || p[m].equals(p1) || p[m].equals(p2) || p[m].equals(p3) || p[m].equals(p4)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}