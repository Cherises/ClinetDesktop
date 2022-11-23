package com.controldesktop;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Arrays;


class ReceiveFileTCP extends Thread{
    HeadMessage hm;

    ServerSocket SSocket;

    Socket socket;
    JProgressBar progressBar;
    long FileSize;
    InputStream is;

    FileOutputStream fos;

    JFrame j;
    public ReceiveFileTCP(HeadMessage hm, JProgressBar progressBar,JFrame j) throws IOException {
        SSocket = new ServerSocket(9575);
        this.hm = hm;
        this.progressBar = progressBar;
        this.j=j;
        File file = new File("D:\\360MoveData\\Users\\zhaoz\\Desktop\\"+hm.getFileInfo()[0]);
        fos = new FileOutputStream(file);
        FileSize = Long.parseLong(hm.getFileInfo()[2]);
    }

    @Override
    public void run() {
        try {
            socket = SSocket.accept();
            is = socket.getInputStream();
            byte[] data = new byte[1024];
            int length = 0;
            long allSize = 0;
            while ((length = is.read(data)) != -1){
                try {
                    allSize += length;
                    fos.write(data, 0, length);
                    double per = 100.0 * allSize / FileSize;
                    progressBar.setString("正在接收文件" + (int) per + "%");
                    progressBar.setValue((int) (per));
                }catch (Exception e){
                    JOptionPane.showMessageDialog(null,e.toString());
                    break;
                }
            }
            progressBar.setString("接收完成！");
            fos.flush();
            socket.close();
            fos.close();
            is.close();
            //j.dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,e.toString());
        }
    }
}


public class DownLoadFile{
    HeadMessage hm;
    final JProgressBar progressBar;
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension ScreenSize = tk.getScreenSize();
    public DownLoadFile(HeadMessage hm) throws IOException {
        JFrame j = new JFrame();
        //Image image = ImageIO.read(new File("D:\\Desktop\\桌面\\珊瑚宫心海_副本.png"));
        //ImageIcon imageIcon = new ImageIcon("xinhai.ico");
        //j.setIconImage(image);
        this.hm=hm;
        progressBar = new JProgressBar();
        int winHeight = (int) ScreenSize.getHeight();
        int winWidth = (int) ScreenSize.getWidth();
        //设置窗体长宽
        int frameWidth = 400;
        int frameHeight = 200;
        j.setBounds((winWidth - frameWidth)/2,(winHeight - frameHeight)/2, frameWidth, frameHeight);


        j.setResizable(false);
        j.setAlwaysOnTop(true);
        j.setTitle("接收文件:"+hm.getFileInfo()[0]);

        Container c = j.getContentPane();
        progressBar.setStringPainted(true);
        progressBar.setString("准备接收文件...");
        progressBar.setMaximum(100);
        //progressBar.setIndeterminate(true);
        //progressBar.setBounds(1,1,350,50);
        //JPanel jp1 = new JPanel();
        c.setLayout(null);

        JLabel fileName = new JLabel("文件名称: "+hm.getFileInfo()[0]);
        JLabel filePath = new JLabel("原始路径: "+hm.getFileInfo()[1]);
        float size = Long.parseLong(hm.getFileInfo()[2]);
        size = size/1024/1024;
        JLabel fileSize = new JLabel("文件大小: "+hm.getFileInfo()[2]+"  "+size+"MB");
        JButton OpenFile = new JButton("打开");


        fileName.setBounds(1,0,384,20);
        filePath.setBounds(1,20,384,20);
        fileSize.setBounds(1,40,384,20);
        progressBar.setBounds(1,60,384,25);
        OpenFile.setBounds(1,90,80,30);


        c.add(fileName);
        c.add(filePath);
        c.add(fileSize);
        c.add(progressBar);
        c.add(OpenFile);

        OpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("D:\\360MoveData\\Users\\zhaoz\\Desktop\\"+hm.getFileInfo()[0]);
                System.out.println("已点击");
                if (Desktop.isDesktopSupported()){
                    System.out.println("支持展示");
                    Desktop desktop = Desktop.getDesktop();
                    if (file.exists()){
                        try {
                            System.out.println("尝试打开");
                            desktop.open(file);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        j.setVisible(true);


        new ReceiveFileTCP(hm, progressBar,j).start();
    }
}
