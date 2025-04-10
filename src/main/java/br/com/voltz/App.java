package br.com.voltz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import br.com.voltz.companies.Company;
import br.com.voltz.dao.CompanyDao;
import br.com.voltz.dao.UserDao;
import br.com.voltz.users.User;

public class App {

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
            System.out.println("9. Listar Todos os Usuários ** (Apenas para Teste) **");
            System.out.println("10. Editar Usuário");
            System.out.println("11. Deletar Usuário");
            System.out.println("12. Sair");
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
                    case 9 -> listAllUsers();
                    case 10 -> editUser(scanner);
                    case 11 -> deleteUser(scanner);
                    case 12 -> exit = true;
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
        users.add(user);

        try {
            UserDao userDao = new UserDao();
            if (userDao.insert(user)) {
                System.out.println("Usuário salvo no banco de dados com sucesso!");
            } else {
                System.out.println("Falha ao salvar usuário no banco de dados.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao salvar usuário no banco de dados: " + e.getMessage());
        }

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

        try {
            CompanyDao companyDao = new CompanyDao();
            if (companyDao.insert(company)) {
                System.out.println("Empresa associada ao usuário com sucesso!");
            } else {
                System.out.println("Falha ao associar empresa ao usuário.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao associar empresa ao usuário no banco de dados: " + e.getMessage());
        }
    }

    private static void listAllUsers() {
        System.out.println("** Alerta: Esta exibição é apenas para teste do professor. **");

        try {
            UserDao userDao = new UserDao();
            List<User> userList = userDao.findAll();

            if (userList.isEmpty()) {
                System.out.println("Nenhum usuário registrado.");
                return;
            }

            System.out.println("\n--- Lista de Usuários ---");
            for (User user : userList) {
                System.out.println("ID: " + user.getId() + ", Nome: " + user.getName() + ", Email: " + user.getEmail());
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuários no banco de dados: " + e.getMessage());
        }
    }

    private static void editUser(Scanner scanner) {
        System.out.print("Digite o ID do usuário que deseja editar: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        try {
            UserDao userDao = new UserDao();
            User selectedUser = userDao.findById(userId);

            if (selectedUser == null) {
                System.out.println("Usuário não encontrado.");
                return;
            }

            System.out.print("Digite o novo nome: ");
            String newName = scanner.nextLine();
            System.out.print("Digite o novo email: ");
            String newEmail = scanner.nextLine();
            System.out.print("Digite a nova senha: ");
            String newPassword = scanner.nextLine();
            System.out.print("Habilitar 2FA (true/false): ");
            boolean newAuthentication2FA = scanner.nextBoolean();
            scanner.nextLine();

            selectedUser.setName(newName);
            selectedUser.setEmail(newEmail);
            selectedUser.setPassword(newPassword);
            selectedUser.setAuthentication2FA(newAuthentication2FA);

            if (userDao.update(selectedUser)) {
                System.out.println("Usuário atualizado no banco de dados com sucesso!");
            } else {
                System.out.println("Falha ao atualizar usuário no banco de dados.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar usuário no banco de dados: " + e.getMessage());
        }
    }

    private static void deleteUser(Scanner scanner) {
        System.out.print("Digite o ID do usuário que deseja deletar: ");
        int userId = scanner.nextInt();
        scanner.nextLine();

        try {
            UserDao userDao = new UserDao();
            if (userDao.delete(userId)) {
                System.out.println("Usuário deletado com sucesso!");
            } else {
                System.out.println("Falha ao deletar usuário.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao deletar usuário no banco de dados: " + e.getMessage());
        }
    }
}