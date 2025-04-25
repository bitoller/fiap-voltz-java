package br.com.voltz;

import br.com.voltz.dao.TransactionDao;
import br.com.voltz.dao.UsersDao;
import br.com.voltz.dao.WalletDao;
import br.com.voltz.dao.WalletEntryDao;
import br.com.voltz.factory.ConnectionFactory;
import br.com.voltz.model.SupportedCrypto;
import br.com.voltz.model.Transaction;
import br.com.voltz.model.Users;
import br.com.voltz.model.Wallet;
import br.com.voltz.model.WalletEntry;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UsersDao usersDao = new UsersDao();
    private static final WalletDao walletDao = new WalletDao();
    private static final WalletEntryDao walletEntryDao = new WalletEntryDao();
    private static final TransactionDao transactionDao = new TransactionDao();


    private static Users loggedInUser = null;
    private static Wallet loggedInWallet = null;

    public static void main(String[] args) {
        System.out.println("Bem-vindo ao Voltz Crypto Bank!");

        try {
            ConnectionFactory.getConnection().close();
            System.out.println("Conexão com o banco de dados estabelecida.");
        } catch (SQLException e) {
            System.err.println("ERRO FATAL: Não foi possível conectar ao banco.");
            System.err.println("Verifique URL, usuário, senha e se o banco está rodando.");
            System.err.println(e.getMessage());
            return;
        }

        boolean exit = false;

        while (!exit) {
            showMainMenu();
            int choice = readOption();

            try {
                switch (choice) {

                    case 1:
                        if (loggedInUser == null) hangleRegisterUser();
                        else System.out.println("Opção inválida.");
                        break;
                    case 2:
                        if (loggedInUser == null) handleLogin();
                        else System.out.println("Opção inválida.");
                        break;

                    case 3:
                        if (checkLogin()) handleShowBalance();
                        break;
                    case 4:
                        if (checkLogin()) handleDeposit();
                        break;
                    case 5:
                        if (checkLogin()) handleWithdraw();
                        break;
                    case 6:
                        if (checkLogin()) handleTransfer();
                        break;
                    case 7:
                        if (checkLogin()) handleViewStatement();
                        break;
                    case 8:
                        if (checkLogin()) handleEditUser();
                        break;
                    case 9:
                        if (checkLogin()) handleDeleteUser();
                        break;
                    case 10:
                        handleListAllUsers();
                        break;
                    case 11:
                        if (checkLogin()) handleLogout();
                        break;
                    case 12:
                        exit = true;
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                }

                if (!exit) {
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine();
                }
            } catch (Exception e) {
                System.err.println("\n!!! Ocorreu um erro inesperado: " + e.getMessage() + " !!!");
            }
        }

        System.out.println("\nObrigado por usar o Voltz Crypto Bank!");
        scanner.close();
    }

    private static void showMainMenu() {
        System.out.println("\n=========================");
        System.out.println("   Voltz Crypto Bank Menu");
        System.out.println("=========================");
        if (loggedInUser == null) {
            System.out.println("1. Registrar Novo Usuário");
            System.out.println("2. Login");
            System.out.println("10. Listar Todos Usuários (Teste)");
            System.out.println("12. Sair");
        } else {
            System.out.println("Usuário Logado: " + loggedInUser.getUserName() + " (ID: " + loggedInUser.getId() + ")");
            System.out.println("-------------------------");
            System.out.println("3. Ver Saldo da Carteira");
            System.out.println("4. Depositar Cripto");
            System.out.println("5. Sacar Cripto");
            System.out.println("6. Transferir Cripto");
            System.out.println("7. Ver Extrato");
            System.out.println("8. Editar Meus Dados");
            System.out.println("9. Deletar Minha Conta");
            System.out.println("10. Listar Todos Usuários (Teste)");
            System.out.println("11. Logout");
            System.out.println("12. Sair");
        }
        System.out.print("Escolha uma opção: ");
    }

    private static int readOption() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            return choice;
        } catch (InputMismatchException e) {
            System.out.println("Erro: Entrada inválida. Por favor, digite um número.");
            scanner.nextLine();
            return -1;
        }
    }

    private static boolean checkLogin() {
        if (loggedInUser == null) {
            System.out.println("\nERRO: Você precisa estar logado para realizar esta operação.");
            return false;
        }

        if (loggedInWallet == null) {
            try {
                Optional<Wallet> walletOpt = walletDao.findByUserId(loggedInUser.getId());
                if (walletOpt.isPresent()) {
                    loggedInWallet = walletOpt.get();
                } else {
                    System.err.println("ERRO CRÍTICO: Carteira não encontrada para o usuário logado ID: " + loggedInUser.getId());
                    System.err.println("Realizando logout forçado.");
                    handleLogout();
                    return false;
                }
            } catch (SQLException e) {
                System.err.println("ERRO ao buscar dados da carteira: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private static void hangleRegisterUser() {
        System.out.println("\n--- Registrar Novo Usuário ---");
        System.out.print("Nome completo: ");
        String userName = scanner.nextLine();
        System.out.print("CPF ou CNPJ (apenas números): ");
        String cpfCnpj = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        // TODO: Adicionar validação de formato de email
        System.out.print("Telefone: ");
        String phoneNumber = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();
        // TODO: Adicionar validação de força da senha

        Users newUser = new Users(userName, cpfCnpj, email, phoneNumber, password, true);

        try {
            if (usersDao.findByEmail(email).isPresent()) {
                System.out.println("\nERRO: Este email já está cadastrado.");
                return;
            }

            int newUserId = usersDao.save(newUser);
            System.out.println("\nUsuário registrado com sucesso! ID: " + newUserId);

            Wallet newWallet = new Wallet(newUserId);
            walletDao.save(newWallet);
            System.out.println("Carteira criada com sucesso para o usuário.");
            System.out.println("Agora você pode fazer o login.");

        } catch (SQLException e) {
            System.err.println("\nERRO no Banco de Dados ao registrar: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("\nERRO de Validação: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nERRO inesperado durante o registro: " + e.getMessage());
        }
    }

    private static void handleLogin() {
        if (loggedInUser != null) {
            System.out.println("\nVocê já está logado como " + loggedInUser.getUserName() + ".");
            return;
        }
        System.out.println("\n--- Login ---");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        try {
            Optional<Users> userOpt = usersDao.findByEmail(email);

            if (userOpt.isPresent()) {
                Users user = userOpt.get();

                if (usersDao.checkPassword(password, user.getPassword())) {
                    if (user.isActive()) {
                        loggedInUser = user;
                        Optional<Wallet> walletOpt = walletDao.findByUserId(loggedInUser.getId());

                        if (walletOpt.isPresent()) {
                            loggedInWallet = walletOpt.get();
                            System.out.println("\nLogin bem-sucedido! Bem-vindo(a), " + loggedInUser.getUserName() + "!");
                        } else {
                            System.err.println("ERRO CRÍTICO: Usuário existe mas não possui carteira associada!");
                            loggedInUser = null;
                        }
                    } else {
                        System.out.println("\nERRO: Este usuário está inativo. Contate o suporte.");
                    }
                } else {
                    System.out.println("\nERRO: Senha incorreta.");
                }
            } else {
                System.out.println("\nERRO: Email não encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("\nERRO no Banco de Dados durante o login: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nERRO inesperado durante o login: " + e.getMessage());
        }
    }

    private static void handleLogout() {
        loggedInUser = null;
        loggedInWallet = null;
        System.out.println("\nLogout realizado com sucesso.");
    }

    private static void handleShowBalance() {
        System.out.println("\n--- Saldo da Carteira (ID: " + loggedInWallet.getId() + ") ---");
        try {
            List<WalletEntry> entries = walletEntryDao.findByWalletId(loggedInWallet.getId());
            loggedInWallet.setEntries(entries);

            if (entries.isEmpty()) {
                System.out.println("Sua carteira está vazia. Que tal fazer um depósito?");
            } else {
                System.out.println("---------------------");
                System.out.println(" Cripto | Quantidade");
                System.out.println("---------------------");
                for (WalletEntry entry : entries) {
                    System.out.printf(" %-6s | %s\n", entry.getCryptoSymbol(), entry.getAmount().toPlainString());
                }
                System.out.println("---------------------");
                // TODO (Opcional): Buscar preços via API e mostrar valor total em R$/USD
            }
        } catch (SQLException e) {
            System.err.println("\nERRO ao buscar saldos: " + e.getMessage());
        }
    }

    private static void handleDeposit() {
        System.out.println("\n--- Depositar Cripto ---");
        System.out.println("Criptomoedas disponíveis para depósito:");
        String[] symbols = SupportedCrypto.getAllSymbols();

        for (int i = 0; i < symbols.length; i++) {
            System.out.printf("%d. %s\n", i + 1, symbols[i]);
        }

        System.out.print("Escolha o número da cripto: ");
        int choice = readOption();

        if (choice < 1 || choice > symbols.length) {
            System.out.println("\nOpção de cripto inválida.");
            return;
        }

        String selectedSymbol = symbols[choice - 1];

        System.out.print("Digite a quantidade a depositar (ex: 0.05): ");
        BigDecimal amount;

        try {
            amount = scanner.nextBigDecimal();
            scanner.nextLine();

            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("\nERRO: A quantidade deve ser um valor positivo.");
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("\nERRO: Quantidade digitada inválida.");
            scanner.nextLine();
            return;
        }

        try {
            Optional<WalletEntry> entryOpt = walletEntryDao.findByWalletIdAndSymbol(loggedInWallet.getId(), selectedSymbol);

            if (entryOpt.isPresent()) {
                WalletEntry entry = entryOpt.get();
                BigDecimal newBalance = entry.getAmount().add(amount);
                walletEntryDao.updateAmount(loggedInWallet.getId(), selectedSymbol, newBalance);
            } else {
                WalletEntry newEntry = new WalletEntry(loggedInWallet.getId(), selectedSymbol, amount);
                walletEntryDao.save(newEntry);
            }

            Transaction tx = new Transaction();
            tx.setType("DEPOSIT");
            tx.setDestinationWalletId(loggedInWallet.getId());
            tx.setSourceWalletId(null);
            tx.setCryptoSymbol(selectedSymbol);
            tx.setAmount(amount);
            tx.setTransactionDate(java.time.LocalDateTime.now());
            tx.setStatus("COMPLETED");
            transactionDao.save(tx);

            System.out.printf("\nDepósito de %s %s processado com sucesso!\n", amount.toPlainString(), selectedSymbol);

        } catch (SQLException e) {
            System.err.println("\nERRO no Banco de Dados ao processar depósito: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nERRO inesperado durante o depósito: " + e.getMessage());
        }
    }

    private static void handleWithdraw() {
        System.out.println("\n--- Sacar Cripto ---");
        System.out.println("!!! FUNCIONALIDADE AINDA NÃO IMPLEMENTADA !!!");

        // Lógica similar ao depósito, mas com verificação de saldo e subtração
        // 1. Listar apenas as criptos que o usuário TEM SALDO (findByWalletId)
        // 2. Pedir qual sacar e a quantidade
        // 3. Pedir endereço de destino (apenas para simular, não faremos envio real)
        // 4. VERIFICAR se amount do WalletEntry >= quantidade a sacar
        // 5. Se sim: Calcular novo saldo = entry.getAmount().subtract(quantidade)
        // 6. Chamar walletEntryDao.updateAmount(...) com o novo saldo
        // 7. Chamar transactionDao.save(...) com type="WITHDRAWAL", sourceWalletId=carteiraLogada.getId()
        // 8. Se não tiver saldo: Mensagem de erro.
    }

    private static void handleTransfer() {
        System.out.println("\n--- Transferir Cripto ---");
        System.out.println("!!! FUNCIONALIDADE AINDA NÃO IMPLEMENTADA !!!");

        // Lógica mais complexa, envolve duas carteiras e transação de banco
        // 1. Listar criptos que o usuário TEM SALDO
        // 2. Pedir qual transferir e a quantidade
        // 3. Pedir o EMAIL do usuário de destino
        // 4. Verificar saldo do remetente (findByWalletIdAndSymbol)
        // 5. Buscar usuário de destino pelo email (usersDao.findByEmail)
        // 6. Se usuário destino existir, buscar carteira destino (walletDao.findByUserId)
        // 7. Se tudo OK: INICIAR TRANSAÇÃO DE BANCO DE DADOS (Connection.setAutoCommit(false))
        // 8. Tentar:
        //    a. Subtrair do WalletEntry do remetente (updateAmount)
        //    b. Adicionar/Atualizar WalletEntry do destinatário (findByWalletIdAndSymbol + save/updateAmount)
        //    c. Salvar a transação (transactionDao.save com type="TRANSFER", source e dest IDs)
        // 9. Se TUDO deu certo: Connection.commit()
        // 10. Se QUALQUER passo falhar: Connection.rollback()
        // 11. SEMPRE (bloco finally): Connection.setAutoCommit(true); Connection.close();
        // 12. Informar sucesso ou falha.
    }

    private static void handleViewStatement() {
        System.out.println("\n--- Extrato da Conta (Últimas 20 Transações) ---");
        try {
            List<Transaction> transactions = transactionDao.findRecentByWalletId(loggedInWallet.getId(), 20);

            if (transactions.isEmpty()) {
                System.out.println("Nenhuma transação registrada para esta carteira.");
            } else {
                System.out.println("---------------------------------------------------------------------------------------------------");
                System.out.println("Data/Hora           | Tipo       | Cripto | Quantidade | De/Para     | Status");
                System.out.println("-------------------|------------|--------|------------|-------------|-----------");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                for (Transaction tx : transactions) {
                    String fromTo = "-";
                    if ("DEPOSIT".equals(tx.getType())) {
                        fromTo = "Depósito";
                    } else if ("WITHDRAWAL".equals(tx.getType())) {
                        fromTo = "Saque";
                    } else if ("TRANSFER".equals(tx.getType())) {
                        if (loggedInWallet.getId() == tx.getSourceWalletId()) {
                            fromTo = "Env->W:" + tx.getDestinationWalletId();
                        } else {
                            fromTo = "Rec<-W:" + tx.getSourceWalletId();
                        }
                    }

                    System.out.printf("%-19s | %-10s | %-6s | %-10s | %-11s | %s\n",
                            tx.getTransactionDate() != null ? tx.getTransactionDate().format(formatter) : "N/A",
                            tx.getType() != null ? tx.getType() : "N/A",
                            tx.getCryptoSymbol() != null ? tx.getCryptoSymbol() : "N/A",
                            tx.getAmount() != null ? tx.getAmount().toPlainString() : "N/A",
                            fromTo,
                            tx.getStatus() != null ? tx.getStatus() : "N/A");
                }
                System.out.println("---------------------------------------------------------------------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("\nERRO ao buscar extrato: " + e.getMessage());
        }
    }

    private static void handleEditUser() {
        System.out.println("\n--- Editar Meus Dados ---");
        Optional<Users> userOpt = Optional.empty();

        try {
            userOpt = usersDao.findById(loggedInUser.getId());
        } catch (SQLException e) {
            System.err.println("ERRO ao buscar dados do usuário para edição: " + e.getMessage());
            return;
        }

        if (!userOpt.isPresent()) {
            System.err.println("ERRO: Não foi possível encontrar os dados do usuário logado.");
            handleLogout();
            return;
        }

        Users currentUser = userOpt.get();

        System.out.println("Deixe em branco para não alterar.");

        System.out.print("Novo nome [" + currentUser.getUserName() + "]: ");
        String newName = scanner.nextLine();
        System.out.print("Novo email [" + currentUser.getEmail() + "]: ");
        String newEmail = scanner.nextLine();
        // TODO: Validar formato do novo email, verificar se já existe (se for diferente do atual)
        System.out.print("Novo telefone [" + currentUser.getPhoneNumber() + "]: ");
        String newPhoneNumber = scanner.nextLine();
        System.out.print("Nova senha (deixe em branco para não alterar): ");
        String newPassword = scanner.nextLine();
        System.out.print("Manter usuário ativo? (S/N) [" + (currentUser.isActive() ? "S" : "N") + "]: ");
        String activeInput = scanner.nextLine();

        if (newName != null && !newName.trim().isEmpty()) {
            currentUser.setUserName(newName.trim());
        }
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            currentUser.setEmail(newEmail.trim());
        }
        if (newPhoneNumber != null && !newPhoneNumber.trim().isEmpty()) {
            currentUser.setPhoneNumber(newPhoneNumber.trim());
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        } else {
            currentUser.setPassword(null);
        }
        if (activeInput != null && !activeInput.trim().isEmpty()) {
            currentUser.setActive(activeInput.trim().equalsIgnoreCase("S"));
        }

        try {
            usersDao.update(currentUser);
            System.out.println("\nDados atualizados com sucesso!");
            loggedInUser = currentUser;
        } catch (SQLException e) {
            System.err.println("\nERRO no Banco de Dados ao atualizar usuário: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("\nERRO de Validação: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nERRO inesperado durante a atualização: " + e.getMessage());
        }
    }

    private static void handleDeleteUser() {
        System.out.println("\n--- Deletar Minha Conta ---");
        System.out.println("!!! ATENÇÃO !!! Esta ação é irreversível e apagará seu usuário e sua carteira.");
        System.out.print("Digite sua senha atual para confirmar a exclusão: ");
        String confirmPassword = scanner.nextLine();

        try {
            Optional<Users> userOpt = usersDao.findById(loggedInUser.getId());
            if (!userOpt.isPresent()) {
                System.err.println("ERRO: Usuário não encontrado.");
                handleLogout();
                return;
            }

            Users userToDelete = userOpt.get();

            if (usersDao.checkPassword(confirmPassword, userToDelete.getPassword())) {
                System.out.print("Confirmação final. Tem certeza que deseja deletar sua conta? (S/N): ");
                String confirm = scanner.nextLine();
                if ("S".equalsIgnoreCase(confirm)) {
                    // TODO: Implementar exclusão em cascata ou explícita
                    // 1. Deletar WalletEntries (walletEntryDao.deleteByWalletId)
                    // 2. Deletar Wallet (walletDao.delete) - CUIDADO com FK! Pode ser melhor só inativar.
                    // 3. Deletar Transações? (Opcional, pode querer manter histórico)
                    // 4. Deletar Usuário (usersDao.delete)

                    usersDao.delete(userToDelete.getId());
                    System.out.println("\nUsuário deletado com sucesso.");
                    handleLogout();

                } else {
                    System.out.println("\nExclusão cancelada.");
                }
            } else {
                System.out.println("\nERRO: Senha de confirmação incorreta.");
            }
        } catch (SQLException e) {
            System.err.println("\nERRO no Banco de Dados ao tentar deletar usuário: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nERRO inesperado durante a exclusão: " + e.getMessage());
        }
    }

    private static void handleListAllUsers() {
        System.out.println("\n--- Lista de Todos os Usuários (Apenas Teste) ---");
        try {
            List<Users> allUsers = usersDao.findAll();
            if (allUsers.isEmpty()) {
                System.out.println("Nenhum usuário registrado no sistema.");
            } else {
                System.out.println("-----------------------------------------------------");
                System.out.println(" ID | Nome                 | Email                | Ativo");
                System.out.println("----|----------------------|----------------------|-------");
                for (Users u : allUsers) {
                    System.out.printf(" %-2d | %-20s | %-20s | %s\n",
                            u.getId(),
                            u.getUserName().length() > 20 ? u.getUserName().substring(0, 17) + "..." : u.getUserName(),
                            u.getEmail().length() > 20 ? u.getEmail().substring(0, 17) + "..." : u.getEmail(),
                            u.isActive() ? "Sim" : "Não");
                }
                System.out.println("-----------------------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("\nERRO ao listar usuários: " + e.getMessage());
        }
    }
}