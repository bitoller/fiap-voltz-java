package main.java.voltz.users;

public class Security {
    public String encryptData(String data) {
        return data;
    }

    public boolean validateAuthentication(String user, String password) {
        return true;
    }

    public String generateToken(String user) {
        return "token";
    }
}
