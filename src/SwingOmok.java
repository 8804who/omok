import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class SwingOmok extends JFrame{
    Container c = getContentPane();
    JLayeredPane layer = new JLayeredPane();
    JPanel stonePane = new JPanel();

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
        setResizable(false);//이미지 크기에 창을 맞추어놓았으므로 창 크기 변경 불가 설정
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

    public void makeStone(int X, int Y){//클릭한 위치에 바둑돌을 배치
        JLabel stone = new JLabel();
        if(map[X][Y]==0){
            if(turn.equals("black")) {
                map[X][Y]=1;//돌의 위치를 저장하는 배열에 돌의 종류 저장
                stone.setIcon(black);
            }
            else{
                map[X][Y]=2;//돌의 위치를 저장하는 배열에 돌의 종류 저장
                stone.setIcon(white);
            }
            stonePane.add(stone,new Integer(100));//돌을 배경 위에 배치
            stone.setSize(50,50);
            stone.setLocation(X*52, Y*52);
            layer.add(stone,new Integer(100));
        }
    }

    public void getResult(int X, int Y){//클릭한 위치를 중심으로 주변을 탐색해서 승리조건에 알맞는지 확인
        int[] straight_Num=new int[8];
        for(int i=0;i<8;i++){
            int count=0;
            for(int j=1;j<=5;j++){
                if(X+move[i][0]*j>0 & Y+move[i][1]*j>0 & X+move[i][0]*j<19 & Y+move[i][1]*j<19){//탐색범위가 맵을 벗어나는것을 방지해주기 위해 범위를 설정
                    if(map[X+(move[i][0])*j][Y+(move[i][1])*j]==map[X][Y]) {//탐색하는 위치에 있는 돌이 자신이 놓은 돌과 같은 돌이면 count++
                        count++;
                    }
                    else break;//탐색하는 위치에 있는 돌이 자신이 놓은 돌과 다르면 그 방향으로는 탐색 종료
                }
                else break;
            }
            straight_Num[i]=count;
        }

        if((straight_Num[0]+straight_Num[1]>=4) || (straight_Num[2]+straight_Num[3]>=4)|| (straight_Num[4]+straight_Num[5]>=4)|| (straight_Num[6]+straight_Num[7]>=4)) {//상+하,좌+우,좌상+우하,좌하+우상을 하여서 4이상이 되면 현재 돌과 합쳐서 5
            gameEnd=true;
            if(turn.equals("black")) JOptionPane.showMessageDialog(null, "흑의 승리입니다.","게임 종료",JOptionPane.PLAIN_MESSAGE);
            else JOptionPane.showMessageDialog(null, "백의 승리입니다.","게임 종료",JOptionPane.PLAIN_MESSAGE);
        }
    }

    class ClickListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            int X=e.getX()-30, Y=e.getY()-30;//바둑판의 안쓰는 부분제거
            if(X>=0 & Y>=0 & X<=970 & Y<=970 & map[X/50][Y/50]==0){//바둑판의 칸에만 반응하도록 범위 설정 & 돌이 놓이지 않은 곳만 지정 가능
                makeStone(X/50,Y/50);//바둑판의 칸에 돌을 놓기
                getResult(X/50, Y/50);//현재 놓은 칸 근처를 탐색
                if(turn.equals("black")) {
                    JOptionPane.showMessageDialog(null, "백의 차례입니다.","차례 변경",JOptionPane.PLAIN_MESSAGE);
                    turn="white";//한번 진행할때 마다 순서 변경
                }
                else {
                    JOptionPane.showMessageDialog(null, "흑의 차례입니다.","차례 변경",JOptionPane.PLAIN_MESSAGE);
                    turn="black";
                }
            }
        }
    }
}
