package com.example.mrsa;

public class StatusModel {

    String deviceName;
    String operationPerformed;
    String operationTime;
    int operationImage;

    public StatusModel(String deviceName, String operationPerformed, String operationTime, int operationImage) {
        this.deviceName = deviceName;
        this.operationPerformed = operationPerformed;
        this.operationTime = operationTime;
        this.operationImage = operationImage;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getOperationPerformed() {
        return operationPerformed;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public int getOperationImage() {
        return operationImage;
    }
}
