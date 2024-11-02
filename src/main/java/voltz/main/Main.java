package main.java.voltz.main;

import java.util.Scanner;
import main.java.voltz.companies.Company;
import main.java.voltz.users.User;

public class Main {
    private static User user;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Menu Principal ---");
            System.out.println("1. Registrar Usuário");
            System.out.println("2. Associar Empresa ao Usuário");
            System.out.println("3. Adicionar Cripto à Wallet do Usuário");
            System.out.println("4. Mostrar Informações do Usuário");
            System.out.println("5. Mostrar Informações da Wallet");
            System.out.println("6. Mostrar Informações da Empresa");
            System.out.println("7. Mandar Quantia da Empresa");
            System.out.println("8. Sair");
            System.out.print("Selecione Uma Opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> user = createUser(scanner);
                case 2 -> associateCompanyToUser(scanner);
                case 3 -> addCryptoToUserWallet(scanner);
                case 4 -> displayUserInfo();
                case 5 -> displayWalletInfo();
                case 6 -> displayCompanyInfo();
                case 7 -> sendAmountFromCompany(scanner);
                case 8 -> exit = true;
                default -> System.out.println("Opção Inválida. Tente Novamente.");
            }
        }

        scanner.close();
    }

    private static User createUser(Scanner scanner) {
        System.out.print("Digite o Seu Nome: ");
        String name = scanner.nextLine();
        System.out.print("Digite o Seu Email: ");
        String email = scanner.nextLine();
        System.out.print("Digite a Sua Senha: ");
        String password = scanner.nextLine();
        System.out.print("Habilitar 2FA? (true/false): ");
        boolean authentication2FA = scanner.nextBoolean();
        scanner.nextLine();

        User user = new User(name, email, password, authentication2FA);
        user.register();
        return user;
    }

    private static void associateCompanyToUser(Scanner scanner) {
        if (user == null) {
            System.out.println("Por Favor, Registre Um Usuário Primeiro.");
            return;
        }

        System.out.print("Digite o Nome da Empresa: ");
        String companyName = scanner.nextLine();
        System.out.print("Digite o Saldo Disponível: ");
        double availableBalance = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Digite a Conta Bancária: ");
        String bankAccount = scanner.nextLine();

        Company company = new Company(companyName, availableBalance, bankAccount);
        user.setCompany(company);
    }

    private static void addCryptoToUserWallet(Scanner scanner) {
        if (user == null) {
            System.out.println("Por Favor, Registre Um Usuário Primeiro.");
            return;
        }

        System.out.print("Digite o Nome do Ativo Cripto: ");
        String assetName = scanner.nextLine();
        System.out.print("Digite a Quantidade: ");
        double amount = scanner.nextDouble();
        System.out.print("Digite o Preço Atual: ");
        double currentPrice = scanner.nextDouble();
        scanner.nextLine();

        user.addCryptoToWallet(assetName, amount, currentPrice);
    }

    private static void displayUserInfo() {
        if (user == null) {
            System.out.println("Por Favor, Registre Um Usuário Primeiro.");
            return;
        }

        System.out.println("Informações do Usuário:");
        System.out.println("Nome: " + user.getName());
        System.out.println("Email: " + user.getEmail());
    }

    private static void displayWalletInfo() {
        if (user == null) {
            System.out.println("Por Favor, Registre Um Usuário Primeiro.");
            return;
        }

        System.out.println("\nInformações da Wallet:");
        System.out.println("Saldo Total da Wallet: $" + user.getWalletTotalBalance());
    }

    private static void displayCompanyInfo() {
        if (user == null) {
            System.out.println("Por Favor, Registre Um Usuário Primeiro.");
            return;
        }

        System.out.println("\nInformações da Empresa:");
        System.out.println("Nome da Empresa: " + user.getCompanyName());
        System.out.println("Saldo da Empresa: $" + user.getCompanyBalance());
    }

    private static void sendAmountFromCompany(Scanner scanner) {
        if (user == null) {
            System.out.println("Por Favor, Registre Um Usuário Primeiro.");
            return;
        }

        System.out.print("Digite a Quantia a Enviar: ");
        double amountToSend = scanner.nextDouble();
        scanner.nextLine();

        if (user.sendAmountFromCompany(amountToSend)) {
            System.out.println("\nEnvio de $" + amountToSend + " da empresa foi bem-sucedido.");
            System.out.println("Novo saldo da Empresa: $" + user.getCompanyBalance());
        } else {
            System.out.println("\nEnvio de $" + amountToSend + " da empresa falhou.");
        }
    }
}