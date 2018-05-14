import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Faris Fazlic on 24/05/2017.
 */
public class Game extends JFrame implements KeyListener{

    private Block head;
    private ArrayList<Block> tails;
    private Thread start;
    private boolean started = false;
    private Block fruit;
    Random rand = new Random();

    public Game(){
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(600,300);
        head = new Block(this.getWidth()/2, this.getHeight()/2-10);
        tails = new ArrayList<>();

        int x,y;
        x = y = 0;
        do{
            x = rand.nextInt((this.getWidth() - 40) / 20 )  * 20 + 20;
            y = rand.nextInt((this.getHeight() - 60)/40) * 40 + 40;
        }while(taken(x,y));
        fruit = new Block(x, y);

        tails.add(new Block(head.getX() - 20, head.getY()));
        tails.add(new Block(head.getX() - 2*20, head.getY()));
        this.addKeyListener(this);

        start = new Thread( () -> {
            try{
                while(true) {

                    checkForWall();
                    checkDeath();
                    checkColision();
                    head.setPrev();
                    head.move();
                    tails.get(0).setPrev();
                    tails.get(0).setPos(head.getPrevX(), head.getPrevY());

                    for (int i = 1; i < tails.size(); i++) {
                        tails.get(i).setPrev();
                        tails.get(i).setPos(tails.get(i-1).getPrevX(), tails.get(i-1).getPrevY());
                    }
                    repaint();
                    Thread.sleep(70);
                }
            } catch (InterruptedException e){}
        });

    }

    public synchronized void checkColision(){
        if(head.getX() == fruit.getX() && head.getY() == fruit.getY())
        {
            int x,y;
            x = y = 0;
            do{
                x = rand.nextInt((this.getWidth() - 40) / 20 )  * 20 + 20;
                y = rand.nextInt((this.getHeight() - 60)/40) * 40 + 40;
            }while(taken(x,y));
            tails.add(new Block(tails.get(tails.size()-1).getX() - 20, tails.get(tails.size()-1).getX()));
            fruit.setPos(x,y);
        }
    }

    public boolean taken(int x, int y){
        if(x == head.getX() && y == head.getY())
            return true;
        for(Block b : tails){
            if(x == b.getX() && y == b.getY())
                return true;
        }
        return false;
    }

    public synchronized void checkDeath(){
        for(Block b : tails){
            if(head.getX() == b.getX() && head.getY() == b.getY()){
                for(int i=tails.size()-1; i >= 2; i--)
                    tails.remove(i);
                return;
            }
        }
    }
    public synchronized void checkForWall(){
        if(head.getX() == 0)
            head.setPos(this.getWidth() - 40, head.getY());
        else if ( head.getX() == this.getWidth() - 20)
            head.setPos(20, head.getY());
        else if (head.getY() == 20)
            head.setPos(head.getX(), this.getHeight()-40);
        else if( head.getY() == this.getHeight() - 20)
            head.setPos(head.getX(), 40);
    }
    public synchronized void paint(Graphics g){
        g.setColor(Color.RED);
        g.fillRect(0,0, this.getWidth(), this.getHeight());
        g.setColor(Color.BLACK);
        g.fillRect(20-1, 40-1, this.getWidth() - 40+1 , this.getHeight() - 60+1);
        head.paint(g);
        for(Block b : tails){
            b.paint(g);
        }
        fruit.paint(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP){
            if(head.getMoveY() == 0)
                head.setMove(0, -20);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN){
            if (head.getMoveY() == 0)
                head.setMove(0, 20);
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            if(!started){
                started = true;
                start.start();
            }
            if (head.getMoveX() == 0)
                head.setMove(20, 0);
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT){
            if (head.getMoveX() == 0)
                head.setMove(-20, 0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {

        Game game = new Game();
        game.setVisible(true);
    }
}

class Block{

    private int x;
    private int y;

    private int moveX;
    private int moveY;

    private int prevX;
    private int prevY;

    public int getMoveX() {
        return moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public int getPrevX() {
        return prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized void move(){
        x += moveX;
        y += moveY;
    }

    public synchronized void setMove(int X, int Y){
        moveX = X;
        moveY = Y;
    }

    public synchronized void setPrev(){
        prevX = x;
        prevY = y;
    }
    public synchronized void setPos(int x, int y){
        this.x = x;
        this.y = y;
    }


    public synchronized void paint(Graphics g){
        g.setColor(Color.RED);
        g.fillRect(x, y, 19 ,19);
    }
}
