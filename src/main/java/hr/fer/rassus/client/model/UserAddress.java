package hr.fer.rassus.client.model;

public class UserAddress {
    private int port;
    private String ipAddress;

    public UserAddress(int port, String ipAddress) {
        this.port = port;
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "UserAddress{" +
                "port=" + port +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
