import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class SwingOmok extends JFrame{
    Container c = getContentPane();
    JLayeredPane layer = new JLayeredPane();

    ImageIcon bg=new ImageIcon("img/board.jpg");//바둑판 이미지
    ImageIcon black = new ImageIcon("img/black.jpg");//흑돌 이미지
    ImageIcon white = new ImageIcon("img/white.jpg");//백돌 이미지

    static String turn = "black";//현재 순서를 나타내는 문자열
    static boolean gameEnd = false;//게임의 승패가 가려졌는지를 나타내는 변수
    static int[][] map = new int[19][19];//바둑돌의 현재 상태를 저장하는 2차원 배열
    static int[][] move={{0,-1},{0,1},{-1,0},{1,0},{-1,1},{1,-1},{-1,-1},{1,1}};//순서대로 상, 하, 좌, 우, 좌상, 우하, 우상, 좌하

    public SwingOmok() {
        setTitle("오목");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBackground();//배경설정
        setLayerPane();//계층 팬 설정

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
        layer.add(back,new Integer(0));//배경을 계층의 가장 아래에 배치
        back.setSize(1020,1050);
        back.setLocation(0,0);
    }

    public void setGame(){
        Arrays.fill(map[0],0);
        layer.removeAll();
    }

    public void makeStone(int X, int Y){
        JLabel stone = new JLabel();
        if(map[X][Y]==0){
            if(turn.equals("black")) {
                map[X][Y]=1;
                stone.setIcon(black);
            }
            else{
                map[X][Y]=2;
                stone.setIcon(white);
            }
            layer.add(stone,new Integer(100));//돌을 배경 위에 배치
            stone.setSize(50,50);
            stone.setLocation(X*52, Y*52);
        }
    }

    public void getResult(int X, int Y){
        int[] straight_Num=new int[8];
        for(int i=0;i<8;i++){
            int count=0;
            for(int j=1;j<=5;j++){
                if(X+move[i][0]*j>0 & Y+move[i][1]*j>0 & X+move[i][0]*j<19 & Y+move[i][1]*j<19){
                    if(map[X+(move[i][0])*j][Y+(move[i][1])*j]==map[X][Y]) {
                        count++;
                    }
                    else break;
                }
                else break;
            }
            straight_Num[i]=count;
        }

        if((straight_Num[0]+straight_Num[1]>=4) || (straight_Num[2]+straight_Num[3]>=4)|| (straight_Num[4]+straight_Num[5]>=4)|| (straight_Num[6]+straight_Num[7]>=4)) {
            gameEnd=true;
            if(turn.equals("black")) JOptionPane.showMessageDialog(null, "흑의 승리입니다.","게임 종료",JOptionPane.PLAIN_MESSAGE);
            else JOptionPane.showMessageDialog(null, "백의 승리입니다.","게임 종료",JOptionPane.PLAIN_MESSAGE);
        }
    }

    class ClickListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            int X=e.getX()-30, Y=e.getY()-30;
            if(X>=0 & Y>=0 & X<=970 & Y<=970){
                makeStone(X/50,Y/50);
                getResult(X/50, Y/50);
                if(turn.equals("black")) turn="white";//한번 진행할때 마다 순서 변경
                else turn="black";
            }
        }
    }
}
