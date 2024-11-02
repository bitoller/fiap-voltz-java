package main.java.voltz.users;

import main.java.voltz.companies.Company;
import main.java.voltz.crypto.CryptoAsset;
import main.java.voltz.crypto.Wallet;
import main.java.voltz.entities.Entity;

public class User extends Entity {
    private String email;
    private String password;
    private boolean authentication2FA;
    private Wallet wallet;
    private Company company;

    public User(String name, String email, String password, boolean authentication2FA) {
        super(name);
        this.email = email;
        this.password = password;
        this.authentication2FA = authentication2FA;
        this.wallet = new Wallet();
        this.company = null;
    }

    @Override
    public void displayInfo() {
        System.out.println("Nome do Usuário: " + getName());
        System.out.println("Email: " + email);
    }

    public void register() {
        System.out.println("Usuário " + getName() + " registrado com sucesso.\nEmail: " + email);
    }

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public Wallet checkWallet() {
        return this.wallet;
    }

    public void addCryptoToWallet(String assetName, double amount, double currentPrice) {
        CryptoAsset cryptoAsset = new CryptoAsset(assetName, amount, currentPrice);
        wallet.addCryptoAsset(cryptoAsset);
    }

    public boolean isAuthentication2FA() {
        return authentication2FA;
    }

    public boolean sendAmountFromCompany(double amount) {
        if (company != null && company.getAvailableBalance() >= amount) {
            company.decreaseBalance(amount);
            return true;
        }
        return false;
    }

    public Company getCompany() {
        return company;
    }

    public String getCompanyName() {
        return company != null ? company.getName() : "Nenhuma empresa associada";
    }

    public double getCompanyBalance() {
        return company != null ? company.getAvailableBalance() : 0.0;
    }

    public String getEmail() {
        return email;
    }

    public double getWalletTotalBalance() {
        return wallet.getTotalBalance();
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}