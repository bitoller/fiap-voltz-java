public class User {
    private String name;
    private String email;
    private String password;
    private boolean authentication2FA;
    private Wallet wallet;

    public User(String name, String email, String password, boolean authentication2FA) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.authentication2FA = authentication2FA;
        this.wallet = new Wallet();
    }

    public void register() {
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
}