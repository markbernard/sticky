package sticky.plugin.passwordsaver;

import java.io.Serializable;

public class PasswordBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String locationForPassword;
    private String password;

    public PasswordBean() {
        locationForPassword = "";
        password = "";
    }
    public PasswordBean(String locationForPassword, String password) {
        this.locationForPassword = locationForPassword;
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getLocationForPassword() {
        return locationForPassword;
    }
    public void setLocationForPassword(String passwordForLocation) {
        this.locationForPassword = passwordForLocation;
    }
}
