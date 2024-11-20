package main.java.voltz.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import main.java.voltz.companies.Company;
import main.java.voltz.users.User;

public class Main {
    private static User user;
    private static ArrayList<User> users = new ArrayList<>();
    private static HashMap<String, Company> companies = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Menu Principal ---");
            System.out.println("1. Registrar Usuário");
            System.out.println("2. Associar Empresa ao Usuário");
            System.out.println("3. Adicionar Cripto à Carteira do Usuário");
            System.out.println("4. Exibir Informações do Usuário");
            System.out.println("5. Exibir Informações da Carteira");
            System.out.println("6. Exibir Informações da Empresa");
            System.out.println("7. Enviar Quantia da Empresa");
            System.out.println("8. Salvar Dados em Arquivo");
            System.out.println("9. Sair");
            System.out.print("Selecione uma opção: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1 -> user = createUser(scanner);
                    case 2 -> associateCompanyToUser(scanner);
                    case 3 -> addCryptoToUserWallet(scanner);
                    case 4 -> displayUserInfo();
                    case 5 -> displayWalletInfo();
                    case 6 -> displayCompanyInfo();
                    case 7 -> sendAmountFromCompany(scanner);
                    case 8 -> saveDataToFile();
                    case 9 -> exit = true;
                    default -> System.out.println("Opção inválida. Por favor, tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Ocorreu um erro: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static User createUser(Scanner scanner) {
        System.out.print("Digite o nome: ");
        String name = scanner.nextLine();
        System.out.print("Digite o email: ");
        String email = scanner.nextLine();
        System.out.print("Digite a senha: ");
        String password = scanner.nextLine();
        System.out.print("Habilitar 2FA (true/false): ");
        boolean authentication2FA = scanner.nextBoolean();
        scanner.nextLine();

        User user = new User(name, email, password, authentication2FA);
        user.register();
        users.add(user);
        return user;
    }

    private static void associateCompanyToUser(Scanner scanner) {
        if (user == null) {
            System.out.println("Por favor, registre um usuário primeiro.");
            return;
        }

        System.out.print("Digite o nome da empresa: ");
        String companyName = scanner.nextLine();
        System.out.print("Digite o saldo disponível: ");
        double availableBalance = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Digite a conta bancária: ");
        String bankAccount = scanner.nextLine();

        Company company = new Company(companyName, availableBalance, bankAccount);
        user.setCompany(company);
        companies.put(companyName, company);
    }

    private static void addCryptoToUserWallet(Scanner scanner) {
        if (user == null) {
            System.out.println("Por favor, registre um usuário primeiro.");
            return;
        }

        System.out.print("Digite o nome do criptoativo: ");
        String assetName = scanner.nextLine();
        System.out.print("Digite a quantidade: ");
        double amount = scanner.nextDouble();
        System.out.print("Digite o preço atual: ");
        double currentPrice = scanner.nextDouble();
        scanner.nextLine();

        user.addCryptoToWallet(assetName, amount, currentPrice);
    }

    private static void displayUserInfo() {
        if (user == null) {
            System.out.println("Por favor, registre um usuário primeiro.");
            return;
        }

        user.displayInfo();
    }

    private static void displayWalletInfo() {
        if (user == null) {
            System.out.println("Por favor, registre um usuário primeiro.");
            return;
        }

        System.out.println("\nInformações da Carteira:");
        System.out.println("Saldo Total da Carteira: $" + user.getWalletTotalBalance());
    }

    private static void displayCompanyInfo() {
        if (user == null) {
            System.out.println("Por favor, registre um usuário primeiro.");
            return;
        }

        if (user.getCompany() != null) {
            user.getCompany().displayInfo();
        } else {
            System.out.println("Nenhuma empresa associada.");
        }
    }

    private static void sendAmountFromCompany(Scanner scanner) {
        if (user == null) {
            System.out.println("Por favor, registre um usuário primeiro.");
            return;
        }

        System.out.print("Digite a quantia a ser enviada: ");
        double amountToSend = scanner.nextDouble();
        scanner.nextLine();

        if (user.sendAmountFromCompany(amountToSend)) {
            System.out.println("\nEnvio de $" + amountToSend + " da empresa foi bem-sucedido.");
            System.out.println("Novo saldo da Empresa: $" + user.getCompanyBalance());
        } else {
            System.out.println("\nEnvio de $" + amountToSend + " da empresa falhou.");
        }
    }

    private static void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data.txt"))) {
            writer.write("Usuários:\n");
            for (User user : users) {
                writer.write("Nome: " + user.getName() + ", Email: " + user.getEmail() + "\n");
            }

            writer.write("\nEmpresas:\n");
            for (Company company : companies.values()) {
                writer.write("Nome: " + company.getName() + ", Saldo Disponível: " + company.getAvailableBalance() + "\n");
            }

            System.out.println("Dados salvos em data.txt");
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }
}