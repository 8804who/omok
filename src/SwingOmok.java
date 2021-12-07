import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Objects;

public class SwingOmok extends JFrame{
    Container c = getContentPane();//메인 패널
    JLayeredPane layer = new JLayeredPane();//배경과 돌을 배치하기 위한 계층 패널
    JMenuBar menubar = new JMenuBar();//메뉴를 표시하기 위한 메뉴바

    ImageIcon bg=new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("board.jpg")));//바둑판 이미지
    ImageIcon black = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("black.jpg")));//흑돌 이미지
    ImageIcon white = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("white.jpg")));//백돌 이미지

    static String turn = "black";//현재 순서를 나타내는 문자열
    static boolean turnAlarm = true;// 순서 알림 창의 표시 여부
    static int[][] map = new int[19][19];//바둑돌의 현재 상태를 저장하는 2차원 배열
    static int[][] move={{0,-1},{0,1},{-1,0},{1,0},{-1,1},{1,-1},{-1,-1},{1,1}};//순서대로 상, 하, 좌, 우, 좌상, 우하, 우상, 좌하

    public SwingOmok() {
        setTitle("오목");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMenu();//메뉴창 부착

        setSize(1020, 1050);
        setVisible(true);
        setResizable(false);//이미지 크기에 창을 맞추어놓았으므로 창 크기 변경 불가 설정

        setLayerPane();//c에 레이어 부착
        setGame();//게임 초기화
    }

    public void setMenu(){//메뉴바를 설정하는 메서드
        JMenu menu=new JMenu("메뉴");

        JMenuItem restart = new JMenuItem("다시하기");
        JMenuItem changeAlarmPrint = new JMenuItem("알림 설정 변경");
        JMenuItem close = new JMenuItem("종료하기");
        restart.addActionListener(new MenuListener());
        changeAlarmPrint.addActionListener(new MenuListener());
        close.addActionListener(new MenuListener());

        menu.add(restart);
        menu.add(changeAlarmPrint);
        menu.add(close);

        menubar.add(menu);
        setJMenuBar(menubar);
    }

    public void setBackground(){//배경을 layer에 부착하는 메서드
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

    public void setLayerPane(){//layer를 c에 부착하는 메서드
        c.add(layer);
        layer.setSize(1020,1050);
        layer.setLocation(0,0);
    }

    public void setGame(){//게임을 초기화하는 메서드
        for(int i=0;i<19;i++) Arrays.fill(map[i],0);//바둑판을 초기화
        layer.removeAll();//레이어 초기화
        setBackground();//레이어에 배경 부착
        JOptionPane.showMessageDialog(null, "게임을 시작합니다.","게임 시작",JOptionPane.PLAIN_MESSAGE);
        JOptionPane.showMessageDialog(null, "흑의 차례입니다.","게임 시작",JOptionPane.PLAIN_MESSAGE);
    }

    public void makeStone(int X, int Y){//클릭한 위치에 바둑돌을 배치
        JLabel stone = new JLabel();
        if(map[X][Y]==0){
            if(turn.equals("black")) {
                map[X][Y]=1;//돌의 위치를 저장하는 배열에 돌의 종류 저장
                stone.setIcon(black);//배치할 돌의 아이콘을 흑돌로 지정
            }
            else{
                map[X][Y]=2;//돌의 위치를 저장하는 배열에 돌의 종류 저장
                stone.setIcon(white);//배치할 돌의 아이콘을 흰돌로 지정
            }
            stone.setSize(50,50);
            stone.setLocation(X*52, Y*52);
            layer.add(stone,new Integer(100));//돌을 배경 위에 배치
        }
    }

    public void getResult(int X, int Y){//클릭한 위치를 중심으로 주변을 탐색해서 승리조건에 알맞는지 확인
        int[] straight_Num=new int[8];//각각 상,하,좌,우,좌상,우하,좌하,우상 탐색값을 저장하는 배열
        for(int i=0;i<8;i++){//탐색 위치를 바꿔주는 변수 및 반복문
            int count=0;//탐색 결과를 저장하는 변수
            for(int j=1;j<=5;j++){//5개까지만 탐색하면 되므로 최대를 5로 지정
                if(X+move[i][0]*j>0 & Y+move[i][1]*j>0 & X+move[i][0]*j<19 & Y+move[i][1]*j<19){//탐색범위가 맵을 벗어나는것을 방지해주기 위해 범위를 설정
                    if(map[X+(move[i][0])*j][Y+(move[i][1])*j]==map[X][Y]) {//탐색하는 위치에 있는 돌이 자신이 놓은 돌과 같은 돌이면 count++
                        count++;
                    }
                    else break;//탐색하는 위치에 있는 돌이 자신이 놓은 돌과 다르면 그 방향으로는 탐색 종료
                }
                else break;//탐색 범위가 맵을 벗어나면 break
            }
            straight_Num[i]=count;//탐색결과를 배열에 저장
        }

        if((straight_Num[0]+straight_Num[1]>=4) || (straight_Num[2]+straight_Num[3]>=4)|| (straight_Num[4]+straight_Num[5]>=4)|| (straight_Num[6]+straight_Num[7]>=4)) {//상+하,좌+우,좌상+우하,좌하+우상을 하여서 4이상이 되면 현재 돌과 합쳐서 5개
            if(turn.equals("black")) JOptionPane.showMessageDialog(null, "흑의 승리입니다.","게임 종료",JOptionPane.PLAIN_MESSAGE);
            else JOptionPane.showMessageDialog(null, "백의 승리입니다.","게임 종료",JOptionPane.PLAIN_MESSAGE);
            int result=0;
            result=JOptionPane.showConfirmDialog(null,"게임을 다시 시작하시겠습니까?", "게임 종료", JOptionPane.YES_NO_OPTION);//게임이 종료되면 새로 게임을 할지 종료할지 여부 확인
            if(result==JOptionPane.YES_OPTION) setGame();//새로 게임을 할 경우 게임을 초기화하는 메서드
            else System.exit(0);//새로 게임을 하지않으면 게임 종료
        }
        else{
            if(turn.equals("black")) {
                if(turnAlarm) JOptionPane.showMessageDialog(null, "백의 차례입니다.","차례 변경",JOptionPane.PLAIN_MESSAGE);//알람 표시가 켜져있으면 차례 표시
                turn="white";//한번 진행할때 마다 순서 변경
            }
            else {
                if(turnAlarm) JOptionPane.showMessageDialog(null, "흑의 차례입니다.","차례 변경",JOptionPane.PLAIN_MESSAGE);//알람 표시가 켜져있으면 차례 표시
                turn="black";//한번 진행할때 마다 순서 변경
            }
        }
    }

    class ClickListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {//바둑판을 클릭할 때 발동
            int X=(e.getX()-30)/50, Y=(e.getY()-30)/50;//바둑판의 안 쓰는 부분을 제거하고 입력받은 위치를 가져와서 좌표화함
            if(X>=0 & Y>=0 & X<19 & Y<19 & map[X][Y]==0){//바둑판의 칸에만 반응하도록 범위 설정 & 돌이 놓이지 않은 곳만 지정 가능
                makeStone(X,Y);//바둑판의 칸에 돌을 놓기
                getResult(X,Y);//현재 놓은 칸 근처를 탐색
            }
        }
    }

    class MenuListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();//선택된 버튼의 문자열을 받아옴
            if(cmd.equals("다시하기")) setGame();//다시하기를 누르면 게임을 초기화
            else if(cmd.equals("종료하기")) System.exit(0);//종료하기를 누르면 게임 종료
            else{
                turnAlarm = !turnAlarm;//표시 알람 여부 상태 변경
            }
        }
    }
}