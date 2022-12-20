package com.example.drivermanager;

public class DriverSummary {

    private String did;
    private String name;
    private String address;
    private int reserved;
    private int completed;
    private int total;
    private int permitted;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPermitted() {
        return permitted;
    }

    public void setPermitted(int permitted) {
        this.permitted = permitted;
    }

    public int getStatus() {
        if (permitted == 0) {
            return 1;
        } else if (permitted == 2) {
            return 2;
        } else {
            if (address == null) {
                return 3;
            } else {
                return 4;
            }
        }
    }

    public String getStatusDisp() {
        switch (getStatus()) {
            case 1:
                return "승인대기";
            case 2:
                return "승인거부";
            case 3:
                return "대기중";
            case 4:
                return "배달중";
            default:
                return "";
        }
    }
}
