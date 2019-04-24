package louis.app.pointofsale.dto;

public class SettingsDTO {
    private String hostUrl;
    private int port;
    private String userName;
    private String password;
    private boolean stayLogedIn;

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStayLogedIn() {
        return stayLogedIn;
    }

    public void setStayLogedIn(boolean stayLogedIn) {
        this.stayLogedIn = stayLogedIn;
    }
}
