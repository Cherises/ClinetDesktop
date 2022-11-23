package com.controldesktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.util.Objects;

public class ShowDesktop{
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension ScreenSize = tk.getScreenSize();
    Socket socket;
    public ShowDesktop(){
        JFrame j = new JFrame();
        Container c = j.getContentPane();
        c.setLayout(null);
        j.setTitle("在线文章阅读小程序1.1");
        j.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        j.setResizable(false);
        int winHeight = (int) ScreenSize.getHeight();
        int winWidth = (int) ScreenSize.getWidth();
        //设置窗体长宽
        int frameWidth = 400;
        int frameHeight = 250;
        j.setBounds((winWidth - frameWidth)/2,(winHeight - frameHeight)/2, frameWidth, frameHeight);

        String[] List_Demo = new String[]{"","",""};
        JList<String> Show_Item_List = new JList<>(List_Demo);
        JScrollPane JSll = new JScrollPane(Show_Item_List);
        //JButton RefreshListButton = new JButton("获取文章列表");
        JMenuBar menuBar = new JMenuBar();
        JMenu Start_Menu = new JMenu("开始");
        JMenuItem Get_Article_Item = new JMenuItem("获取文章");
        JMenuItem Check_Update_Item = new JMenuItem("检查更新");
        JMenuItem Chat_Room_Item = new JMenuItem("聊天室");
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem Read_Article_Menu_Item = new JMenuItem("获取文章");
        popupMenu.add(Read_Article_Menu_Item);
        JTextArea Receive_Chat_TextArea = new JTextArea();
        JScrollPane Receive_Chat_Scroll_Pane = new JScrollPane(Receive_Chat_TextArea);
        JTextField Send_Chat_Text_Field = new JTextField();
        JButton Send_Chat_Button = new JButton("发送");


        JSll.setBounds(1,1,785,300);
        //RefreshListButton.setBounds(1,310,150,30);
        j.setJMenuBar(menuBar);
        Receive_Chat_TextArea.setLineWrap(true);        //激活自动换行功能
        Receive_Chat_TextArea.setWrapStyleWord(true);

        c.add(JSll);
        //c.add(RefreshListButton);
        menuBar.add(Start_Menu);
        Start_Menu.add(Get_Article_Item);
        Start_Menu.add(Check_Update_Item);
        Start_Menu.add(Chat_Room_Item);


        JFrame Chat_Room = new JFrame();
        Container Chat_c = Chat_Room.getContentPane();
        Chat_c.setLayout(null);
        Chat_Room.setTitle("聊天室");
        Chat_Room.setBounds((winWidth - frameWidth)/2,(winHeight - frameHeight)/2, frameWidth, frameHeight);
        Chat_Room.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        Chat_Room.setVisible(false);
        Chat_Room.setResizable(false);

        Receive_Chat_Scroll_Pane.setBounds(0,0,frameWidth-10,180);
        Send_Chat_Text_Field.setBounds(0,180,300,30);
        Send_Chat_Button.setBounds(300,180,80,30);

        Chat_c.add(Receive_Chat_Scroll_Pane);
        Chat_c.add(Send_Chat_Text_Field);
        Chat_c.add(Send_Chat_Button);



        j.setVisible(true);
        try {
            socket = new Socket("114.115.153.152",9574);
            //每次启动获取系统信息，并随着报头信息一同上传
            HeadMessage hm = new HeadMessage();
            hm.setType("CLIENT");
            hm.setIpInfo(new String[]{socket.getLocalAddress().getHostAddress()});//将自己的内网IP写入报头文件
            //这里设置获取的系统信息内容
            ClientFunction.sendHeadMessage(socket,hm);
            new SocketReceive(socket,Show_Item_List,Receive_Chat_TextArea,j).start();
        }catch (Exception e){
            JOptionPane.showMessageDialog(j,"网络连接失败，请检查你的网络连接！\n如有疑问请联系\nzhaozhinet@gmail.com");
            //JOptionPane.showConfirmDialog(null,"网络连接失败，请检查你的网络连接");
            j.dispose();
        }


        Get_Article_Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HeadMessage hm = new HeadMessage();
                hm.setType("GET_ARTICLE_LIST");
                ClientFunction.sendHeadMessage(socket,hm);
            }
        });

        Chat_Room_Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Chat_Room.setVisible(true);
            }
        });

        Check_Update_Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HeadMessage hm = new HeadMessage();
                hm.setType("CHECK_UPDATE");
                ClientFunction.sendHeadMessage(socket,hm);
            }
        });

        Send_Chat_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = Send_Chat_Text_Field.getText();
                if (Objects.equals(value, "") || value ==null){
                    ClientFunction.showDialog("不能发送空内容！");
                    //JOptionPane.showMessageDialog(null,"不能发送空内容！");
                }else {
                    value = ClientFunction.escapeSymbol(value);
                    if (value.length() > 120){
                        ClientFunction.showDialog("发送字数不能超过120！");
                    }else {
                        HeadMessage hm = new HeadMessage();
                        hm.setType("CHAT");
                        hm.setValue(new String[]{value});
                        ClientFunction.sendHeadMessage(socket, hm);
                        Send_Chat_Text_Field.setText("");
                    }
                }
            }
        });

        j.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    HeadMessage hm = new HeadMessage();
                    hm.setType("EXIT");
                    ClientFunction.sendHeadMessage(socket, hm);
                    socket.close();
                    System.exit(0);
                }catch (Exception es){
                    es.printStackTrace();
                }
            }
        });

        Show_Item_List.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()){
                    popupMenu.show(e.getComponent(),e.getX(),e.getY());
                }
            }
        });


        Read_Article_Menu_Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = Show_Item_List.getSelectedValue();
                if (title == null){
                    JOptionPane.showMessageDialog(null,"没有选择标题！");
                }else {
                    HeadMessage hm = new HeadMessage();
                    hm.setType("GET_ARTICLE");
                    hm.setValue(new String[]{title});
                    ClientFunction.sendHeadMessage(socket,hm);
                }
            }
        });


        Chat_Room.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Chat_Room.setVisible(false);
            }
        });

    }
}
