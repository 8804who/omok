import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwingOmok extends JFrame{
    static String turn = "black";
    Container c = getContentPane();
    ImageIcon bg=new ImageIcon("img/board.jpg");
    JLayeredPane layer = new JLayeredPane();
    ImageIcon black = new ImageIcon("img/black.jpg");
    ImageIcon white = new ImageIcon("img/white.jpg");

    public SwingOmok() {
        setTitle("오목");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBackground();
        setLayerPane();


        setSize(1020, 1050);
        setVisible(true);
        setResizable(false);
    }
    public void setLayerPane(){
        c.add(layer);
        layer.setSize(1020,1050);
        layer.setLocation(0,0);
    }
    public void setBackground(){
        JPanel back=new JPanel(){
            public void paintComponent(Graphics g){
                g.drawImage(bg.getImage(),0,0,null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };

        back.addMouseListener(new ClickListener());
        layer.add(back,new Integer(0));
        back.setSize(1020,1050);
        back.setLocation(0,0);
    }
    class ClickListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            System.out.printf("%d, %d\n",e.getX(), e.getY());
        }
    }
}
