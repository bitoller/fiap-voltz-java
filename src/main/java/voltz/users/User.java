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
        this.wallet = new Wallet();
        this.company = null;
    }

    public void register() {
        System.out.println("Usuário " + name + " registrado com sucesso.\nEmail: " + email);
    }

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public Wallet checkWallet() {
        return this.wallet;
    }

    public double getWalletTotalBalance() {
        return wallet.getTotalBalance();
    }

    public void addCryptoToWallet(String assetName, double amount, double currentPrice) {
        wallet.addCryptoAsset(assetName, amount, currentPrice);
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public double getCompanyBalance() {
        return (company != null) ? company.checkBalance() : 0.0;
    }

    public boolean sendAmountFromCompany(double amount) {
        if (company != null) {
            return company.sendAmount(amount);
        } else {
            System.out.println("Nenhuma empresa associada a este usuário.");
            return false;
        }
    }

    public String getCompanyName() {
        return (company != null) ? company.getName() : "Nenhuma empresa associada";
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
}
