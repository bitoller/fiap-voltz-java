package main.java.voltz.users;
import main.java.voltz.assets.Wallet;
import main.java.voltz.companies.Company;

public class User {
    private String name;
    private String email;
    private String password;
    private boolean authentication2FA;
    private Wallet wallet;
    private Company company;

    public User(String name, String email, String password, boolean authentication2FA) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.authentication2FA = authentication2FA;
        this.wallet = new Wallet("defaultId", "defaultOwner", "defaultCurrency", true);
        this.company = new Company("DefaultName", 0.0, "DefaultType");
    }

    public void register() {
        System.out.println("FUNCIONOU");
    }

    public boolean login(String email, String password) {
        return true;
    }

    public Wallet checkWallet() {
        return this.wallet;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAuthentication2FA() {
        return authentication2FA;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthentication2FA(boolean authentication2FA) {
        this.authentication2FA = authentication2FA;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
