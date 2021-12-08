import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Objects;

public class SwingOmok extends JFrame{
    private final Container c = getContentPane();//메인 패널
    private final JLayeredPane layer = new JLayeredPane();//배경과 돌을 배치하기 위한 계층 패널

    private final ImageIcon bg=new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("board.jpg")));//바둑판 이미지
    private final ImageIcon black = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("black.png")));//흑돌 이미지
    private final ImageIcon white = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("white.png")));//백돌 이미지

    private static String turn = "black";//현재 순서를 나타내는 문자열
    private static boolean turnAlarm = true;// 순서 알림 창의 표시 여부

    private static final int MIN_WIDTH=0;//바둑판의 크기
    private static final int MIN_HEIGHT=0;//바둑판의 크기
    private static final int MAX_WIDTH=18;//바둑판의 크기
    private static final int MAX_HEIGHT=18;//바둑판의 크기

    private static final int[][] map = new int[19][19];//바둑돌의 현재 상태를 저장하는 2차원 배열
    private static final int[][] move={{0,-1},{0,1},{-1,0},{1,0},{-1,1},{1,-1},{-1,-1},{1,1}};//순서대로 상, 하, 좌, 우, 좌상, 우하, 우상, 좌하
    private static final int NO_STONE=0;//배열에 입력된 값이 0인 경우 그자리에는 돌이 없음
    private static final int BLACK_STONE=1;//배열에 입력된 값이 1인 경우 그자리에는 흑돌이 있음
    private static final int WHITE_STONE=2;//배열에 입력된 값이 2인 경우 그자리에는 백돌이 있음

    private static final int STRAIGHT_TO_WIN=5;//승리에 필요한 직선상의 돌 개수

    public SwingOmok() {
        setTitle("오목");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMenu();//메뉴창 부착
        setLayerPane();//c에 레이어 부착

        setSize(1020, 1060);
        setVisible(true);
        setResizable(false);//이미지 크기에 창을 맞추어놓았으므로 창 크기 변경 불가 설정

        setGame();//게임 초기화
    }

    private void setMenu(){//메뉴바를 설정하는 메서드
        JMenuBar menubar = new JMenuBar();//메뉴를 표시하기 위한 메뉴바
        JMenu menu=new JMenu("메뉴");
        JMenuItem[] menuItems = new JMenuItem[3];
        String[] ItemName ={"다시하기", "알림 설정 변경", "종료하기"};
        for(int i=0;i<3;i++){
            menuItems[i]=new JMenuItem(ItemName[i]);
            menuItems[i].addActionListener(new MenuListener());
            menu.add(menuItems[i]);
        }
        menubar.add(menu);
        setJMenuBar(menubar);
    }

    private void setLayerPane(){//layer를 c에 부착하는 메서드
        c.add(layer);
        layer.setSize(1020,1060);
        layer.setLocation(0,0);
    }

    private void addComponentToLayer(JComponent j, int widthSize, int heightSize, int locationX, int locationY, int priority){//컴포넌트를 layer에 부착하는 메서드
        layer.add(j, new Integer(priority));
        j.setSize(widthSize,heightSize);
        j.setLocation(locationX,locationY);
    }

    private void setBackground(){//배경을 layer에 부착하는 메서드
        JPanel back=new JPanel(){
            public void paintComponent(Graphics g){
                g.drawImage(bg.getImage(),0,0,null);
                setOpaque(false);
            }
        };
        back.addMouseListener(new ClickListener());
        addComponentToLayer(back,1020,1060,0,0,0);//배경을 계층의 가장 아래에 배치
    }

    private void setGame(){//게임을 초기화하는 메서드
        for (int[] ints : map) Arrays.fill(ints, NO_STONE);//바둑판을 초기화
        layer.removeAll();//레이어 초기화
        setBackground();//레이어에 배경 부착
        turn="black";//차례를 흑부터 시작
        JOptionPane.showMessageDialog(null, "게임을 시작합니다.","게임 시작",JOptionPane.PLAIN_MESSAGE);
        JOptionPane.showMessageDialog(null, "흑의 차례입니다.","게임 시작",JOptionPane.PLAIN_MESSAGE);
    }

    private void makeStone(int X, int Y){//클릭한 위치에 바둑돌을 배치
        JLabel stone = new JLabel();
        if(turn.equals("black")) {
            map[X][Y]=BLACK_STONE;//돌의 위치를 저장하는 배열에 돌의 종류 저장
            stone.setIcon(black);//배치할 돌의 아이콘을 흑돌로 지정
        }
        else{
            map[X][Y]=WHITE_STONE;//돌의 위치를 저장하는 배열에 돌의 종류 저장
            stone.setIcon(white);//배치할 돌의 아이콘을 흰돌로 지정
        }
        addComponentToLayer(stone,60,60,X*52+5,Y*52+5,100);//돌을 배경 위에 배치
    }

    private void getResult(int X, int Y){//클릭한 위치를 중심으로 주변을 탐색해서 승리조건에 알맞는지 확인
        int[] straightCount = searchStone(X,Y);//맵을 탐색해서 결과를 받아옴
        for(int i=0;i<7;i+=2){//2칸씩 탐색하므로 반복문이 2씩증가
            if(straightCount[i]+straightCount[i+1]>=STRAIGHT_TO_WIN-1) {//상+하,좌+우,좌상+우하,좌하+우상을 하여서 STRAIGHT_TO_WIN-1 이상이 되면 방금 놓은 돌과 합쳐서 승리조건을 채우므로 게임이 종료
                engGame();//승리 조건을 채운 경우 게임을 종료
                return;//승리하면 changeTurn까지 가지않고 함수 종료
            }
        }
        changeTurn();//승리 조건을 채우지 못 한 경우 차례를 변경
    }

    private int[] searchStone(int X, int Y){//맵을 탐색하고 탐색 결과를 저장하는 메서드
        int count;//탐색 결과를 저장하는 변수
        int[] straightCount=new int[8];//각각 상,하,좌,우,좌상,우하,좌하,우상 탐색값을 저장하는 배열
        for(int i=0;i<8;i++){//탐색 위치를 바꿔주는 변수 및 반복문
            count = 0;//탐색 결과를 저장하는 변수 count를 초기화
            for(int j=1;j<STRAIGHT_TO_WIN;j++){//승리에 필요한 개수까지만 탐색하면 되므로 STRAIGHT_TO_WIN까지만 수행
                if(X+move[i][0]*j>=MIN_HEIGHT & Y+move[i][1]*j>=MIN_WIDTH & X+move[i][0]*j<=MAX_HEIGHT & Y+move[i][1]*j<=MAX_WIDTH){//탐색범위가 맵을 벗어나는것을 방지해주기 위해 범위를 설정
                    if(map[X+(move[i][0])*j][Y+(move[i][1])*j]==map[X][Y]) count++;//탐색하는 위치에 있는 돌이 자신이 놓은 돌과 같은 돌이면 count++
                    else break;//탐색하는 위치에 있는 돌이 자신이 놓은 돌과 다르면 그 방향으로는 탐색 종료
                }
                else break;//탐색 범위가 맵을 벗어나면 break
            }
            straightCount[i]= count;//탐색결과를 배열에 저장
        }
        return straightCount;//탐색결과를 반환
    }

    private void engGame(){//게임이 끝난 경우 승자를 알려주고 게임을 다시 시작할지 물어보는 메서드
        if(turn.equals("black")) JOptionPane.showMessageDialog(null, "흑의 승리입니다.","게임 종료",JOptionPane.PLAIN_MESSAGE);
        else JOptionPane.showMessageDialog(null, "백의 승리입니다.","게임 종료",JOptionPane.PLAIN_MESSAGE);
        int result=JOptionPane.showConfirmDialog(null,"게임을 다시 시작하시겠습니까?", "게임 종료", JOptionPane.YES_NO_OPTION);//게임이 종료되면 새로 게임을 할지 종료할지 여부 확인
        if(result==JOptionPane.YES_OPTION) setGame();//새로 게임을 할 경우 게임을 초기화하는 메서드
        else System.exit(0);//새로 게임을 하지않으면 게임 종료
    }

    private void changeTurn(){//게임이 계속 진행되는 경우 순서를 바꾸는 메서드
        if(turn.equals("black")) {
            if(turnAlarm) JOptionPane.showMessageDialog(null, "백의 차례입니다.","차례 변경",JOptionPane.PLAIN_MESSAGE);//알람 표시가 켜져있으면 차례 표시
            turn="white";//한번 진행할때 마다 순서 변경
        }
        else {
            if(turnAlarm) JOptionPane.showMessageDialog(null, "흑의 차례입니다.","차례 변경",JOptionPane.PLAIN_MESSAGE);//알람 표시가 켜져있으면 차례 표시
            turn="black";//한번 진행할때 마다 순서 변경
        }
    }

    class ClickListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {//바둑판을 클릭할 때 발동
            int X=(e.getX()-30)/50, Y=(e.getY()-30)/50;//바둑판의 안 쓰는 부분을 제거하고 좌표화
                if (X <= MAX_HEIGHT & Y <= MAX_WIDTH) {//바둑판의 칸에만 반응하도록 범위 설정
                    if (map[X][Y] == NO_STONE) {//돌이 놓이지 않은 곳만 지정 가능
                        makeStone(X, Y);//바둑판의 칸에 돌을 놓기
                        getResult(X, Y);//현재 놓은 칸 근처를 탐색
                    }
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