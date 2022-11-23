package com.controldesktop;

import java.io.Serial;
import java.io.Serializable;

public class HeadMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String device = "CLIENT";
    private String type;
    private String[] value;
    private String[] ipInfo;
    //index 0 is fromIP, 1 is toIP
    private String[] fileInfo;
    //index 0 is fileName, 1 is filePath, 2 is fileByteSize
    private byte[] fileToByte;

    public HeadMessage() {

    }
    public void setDevice(String device){
        this.device = device;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setIpInfo(String[] ipInfo){
        this.ipInfo = ipInfo;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public void setFileInfo(String[] fileInfo) {
        this.fileInfo = fileInfo;
    }


    public void setFileToByte(byte[] fileToByte){
        this.fileToByte = fileToByte;
    }


    public String getDevice() {
        return device;
    }

    public String getType() {
        return type;
    }

    public String[] getIpInfo() {
        return ipInfo;
    }

    public String[] getFileInfo() {
        return fileInfo;
    }
    public String[] getValue() {
        return value;
    }

    public byte[] getFileToByte(){
        return fileToByte;
    }
}
