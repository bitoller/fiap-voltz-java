package main.java.voltz.main;

import main.java.voltz.users.User;
import main.java.voltz.assets.Wallet;
import main.java.voltz.companies.Company;

public class Main {
    public static void main(String[] args) {
        try {
            // Criação de um usuário
            User user = new User("Bruno", "email@example.com", "123456", true);
            user.register();

            // Associa uma empresa ao usuário
            Company company = new Company("Empresa Exemplo", 10000.0, "12345-6");
            user.setCompany(company);

            // Adiciona um criptoativo na carteira do usuário
            user.addCryptoToWallet("Bitcoin", 1.5, 50000.0);

            // Exibe informações do usuário, carteira e empresa associada
            System.out.println("Informações do Usuário:");
            System.out.println("Nome: " + user.getName());
            System.out.println("Email: " + user.getEmail());

            System.out.println("\nInformações da Wallet:");
            System.out.println("Saldo Total da Wallet: $" + user.getWalletTotalBalance());

            System.out.println("\nInformações da Empresa:");
            System.out.println("Nome da Empresa: " + user.getCompanyName());
            System.out.println("Saldo da Empresa: $" + user.getCompanyBalance());

            // Envia um valor da empresa associada
            double amountToSend = 5000.0;
            if (user.sendAmountFromCompany(amountToSend)) {
                System.out.println("\nEnvio de $" + amountToSend + " da empresa foi bem-sucedido.");
                System.out.println("Novo saldo da Empresa: $" + user.getCompanyBalance());
            } else {
                System.out.println("\nEnvio de $" + amountToSend + " da empresa falhou.");
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
