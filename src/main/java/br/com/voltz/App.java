package br.com.voltz;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
import br.com.voltz.util.ValidationUtil;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UsersDao usersDao = new UsersDao();
    private static final WalletDao walletDao = new WalletDao();
    private static final WalletEntryDao walletEntryDao = new WalletEntryDao();
    private static final TransactionDao transactionDao = new TransactionDao();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static Users loggedInUser = null;
    private static Wallet loggedInWallet = null;

    public static void main(String[] args) {
        System.out.println("Welcome to Voltz Crypto Bank!");

        try {
            ConnectionFactory.getConnection().close();
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.err.println("FATAL ERROR: Could not connect to the database.");
            System.err.println("Check URL, user, password, and whether the database is running.");
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
                        if (loggedInUser == null)
                            hangleRegisterUser();
                        else
                            System.out.println("Invalid option.");
                        break;
                    case 2:
                        if (loggedInUser == null)
                            handleLogin();
                        else
                            System.out.println("Invalid option.");
                        break;
                    case 3:
                        if (checkLogin())
                            handleShowBalance();
                        break;
                    case 4:
                        if (checkLogin())
                            handleDeposit();
                        break;
                    case 5:
                        if (checkLogin())
                            handleWithdraw();
                        break;
                    case 6:
                        if (checkLogin())
                            handleTransfer();
                        break;
                    case 7:
                        if (checkLogin())
                            handleViewStatement();
                        break;
                    case 8:
                        if (checkLogin())
                            handleEditUser();
                        break;
                    case 9:
                        if (checkLogin())
                            handleDeleteUser();
                        break;
                    case 10:
                        handleListAllUsers();
                        break;
                    case 11:
                        if (checkLogin())
                            handleLogout();
                        break;
                    case 12:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid option. Tente novamente.");
                        break;
                }

                if (!exit) {
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine();
                }
            } catch (Exception e) {
                System.err.println("\n!!! An unexpected error occurred: " + e.getMessage() + " !!!");
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
            System.out.println("1. Register New User");
            System.out.println("2. Login");
            System.out.println("10. Listar Todos Usuários (Teste)");
            System.out.println("12. Exit");
        } else {
            System.out.println("Logged-in User: " + loggedInUser.getUserName() + " (ID: " + loggedInUser.getId() + ")");
            System.out.println("-------------------------");
            System.out.println("3. View Wallet Balance");
            System.out.println("4. Deposit Crypto");
            System.out.println("5. Withdraw Crypto");
            System.out.println("6. Transfer Crypto");
            System.out.println("7. View Statement");
            System.out.println("8. Edit My Profile");
            System.out.println("9. Delete My Account");
            System.out.println("10. Listar Todos Usuários (Teste)");
            System.out.println("11. Logout");
            System.out.println("12. Exit");
        }
        System.out.print("Escolha uma opção: ");
    }

    private static int readOption() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            return choice;
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Please enter a number.");
            scanner.nextLine();
            return -1;
        }
    }

    private static boolean checkLogin() {
        if (loggedInUser == null) {
            System.out.println("\nERROR: You need to be logged in to perform this operation.");
            return false;
        }

        if (loggedInWallet == null) {
            try {
                Optional<Wallet> walletOpt = walletDao.findByUserId(loggedInUser.getId());
                if (walletOpt.isPresent()) {
                    loggedInWallet = walletOpt.get();
                } else {
                    System.err.println(
                            "CRITICAL ERROR: Wallet not found for logged-in user ID: " + loggedInUser.getId());
                    System.err.println("Realizando logout forçado.");
                    handleLogout();
                    return false;
                }
            } catch (SQLException e) {
                System.err.println("ERROR fetching wallet data: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private static void hangleRegisterUser() {
        System.out.println("\n--- Register New User ---");
        System.out.print("Full name: ");
        String userName = scanner.nextLine();

        String cpfCnpj;

        do {
            System.out.print("CPF or CNPJ (digits only): ");
            cpfCnpj = scanner.nextLine();

            if (!ValidationUtil.validateCpfCnpj(cpfCnpj)) {
                System.out.println(
                        "\nERROR: Invalid CPF/CNPJ. Please enter a valid CPF (11 digits) or CNPJ (14 digits).");
            }
        } while (!ValidationUtil.validateCpfCnpj(cpfCnpj));

        String email;

        do {
            System.out.print("Email: ");
            email = scanner.nextLine();

            if (!ValidationUtil.isValidEmail(email)) {
                System.out.println("\nERROR: Invalid email format. Please enter a valid email.");
            }
        } while (!ValidationUtil.isValidEmail(email));

        System.out.print("Phone: ");
        String phoneNumber = scanner.nextLine();

        String password;

        do {
            System.out.println("\nPassword requirements:");
            System.out.println(ValidationUtil.getPasswordRequirements());
            System.out.print("Password: ");
            password = scanner.nextLine();

            if (!ValidationUtil.isStrongPassword(password)) {
                System.out.println("\nERROR: Password does not meet the minimum security requirements.");
            }
        } while (!ValidationUtil.isStrongPassword(password));

        Users newUser = new Users(userName, cpfCnpj, email, phoneNumber, password, true);

        try {
            if (usersDao.findByEmail(email).isPresent()) {
                System.out.println("\nERROR: This email is already registered.");
                return;
            }

            int newUserId = usersDao.save(newUser);
            System.out.println("\nUsuário registrado com sucesso! ID: " + newUserId);

            Wallet newWallet = new Wallet(newUserId);
            walletDao.save(newWallet);
            System.out.println("Wallet successfully created for the user.");
            System.out.println("You can now log in.");

        } catch (SQLException e) {
            System.err.println("\nERROR in the database while registering: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("\nValidation ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nUnexpected error during registration: " + e.getMessage());
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
        System.out.print("Password: ");
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
                            System.out
                                    .println("\nLogin successful! Welcome, " + loggedInUser.getUserName() + "!");
                        } else {
                            System.err.println("CRITICAL ERROR: User exists but does not have an associated wallet!");
                            loggedInUser = null;
                        }
                    } else {
                        System.out.println("\nERROR: This user is inactive. Contact support.");
                    }
                } else {
                    System.out.println("\nERROR: Incorrect password or email.");
                }
            } else {
                System.out.println("\nERROR: Incorrect password or email.");
            }
        } catch (SQLException e) {
            System.err.println("\nERROR in the database during login: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nUnexpected error during login: " + e.getMessage());
        }
    }

    private static void handleLogout() {
        loggedInUser = null;
        loggedInWallet = null;
        System.out.println("\nLogout successful.");
    }

    private static void handleShowBalance() {
        System.out.println("\n--- Wallet Balance (ID: " + loggedInWallet.getId() + ") ---");

        try {
            List<WalletEntry> entries = walletEntryDao.findByWalletId(loggedInWallet.getId());
            loggedInWallet.setEntries(entries);

            if (entries.isEmpty()) {
                System.out.println("Your wallet is empty. How about making a deposit?");
            } else {
                System.out.println("---------------------");
                System.out.println(" Crypto | Amount");
                System.out.println("---------------------");
                for (WalletEntry entry : entries) {
                    System.out.printf(" %-6s | %s\n", entry.getCryptoSymbol(), entry.getAmount().toPlainString());
                }
                System.out.println("---------------------");
            }
        } catch (SQLException e) {
            System.err.println("\nERROR fetching balances: " + e.getMessage());
        }
    }

    private static void handleDeposit() {
        System.out.println("\n--- Deposit Crypto ---");
        System.out.println("Available cryptocurrencies for deposit:");
        String[] symbols = SupportedCrypto.getAllSymbols();

        for (int i = 0; i < symbols.length; i++) {
            System.out.printf("%d. %s\n", i + 1, symbols[i]);
        }

        System.out.print("Choose the crypto number: ");
        int choice = readOption();

        if (choice < 1 || choice > symbols.length) {
            System.out.println("\nInvalid crypto option.");
            return;
        }

        String selectedSymbol = symbols[choice - 1];
        BigDecimal amount = readAmount("deposit");

        if (amount == null)
            return;

        try {
            Optional<WalletEntry> entryOpt = walletEntryDao.findByWalletIdAndSymbol(loggedInWallet.getId(),
                    selectedSymbol);

            if (entryOpt.isPresent()) {
                WalletEntry entry = entryOpt.get();
                BigDecimal newBalance = entry.getAmount().add(amount);
                walletEntryDao.updateAmount(loggedInWallet.getId(), selectedSymbol, newBalance);
            } else {
                WalletEntry newEntry = new WalletEntry(loggedInWallet.getId(), selectedSymbol, amount);
                walletEntryDao.save(newEntry);
            }

            Transaction tx = createTransaction("DEPOSIT", null, loggedInWallet.getId(), selectedSymbol, amount);
            transactionDao.save(tx);

            System.out.printf("\nDeposit of %s %s processed successfully!\n", amount.toPlainString(), selectedSymbol);
        } catch (SQLException e) {
            System.err.println("\nERROR in the database while processing deposit: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nUnexpected error during deposit: " + e.getMessage());
        }
    }

    private static void handleWithdraw() {
        System.out.println("\n--- Withdraw Crypto ---");

        List<WalletEntry> entries = showAvailableCryptos("withdraw");

        if (entries == null)
            return;

        WalletEntry selectedEntry = selectCrypto(entries);

        if (selectedEntry == null)
            return;

        BigDecimal amount = readAmount("withdraw");

        if (amount == null)
            return;

        if (selectedEntry.getAmount().compareTo(amount) < 0) {
            System.out.println("\nERROR: Insufficient balance to perform the withdrawal.");
            return;
        }

        System.out.print("Enter the destination wallet address: ");
        String destinationAddress = scanner.nextLine();

        if (destinationAddress == null || destinationAddress.trim().isEmpty()) {
            System.out.println("\nERROR: Invalid destination address.");
            return;
        }

        try {
            BigDecimal newBalance = selectedEntry.getAmount().subtract(amount);
            walletEntryDao.updateAmount(loggedInWallet.getId(), selectedEntry.getCryptoSymbol(), newBalance);

            Transaction tx = createTransaction("WITHDRAWAL", loggedInWallet.getId(), null,
                    selectedEntry.getCryptoSymbol(), amount);
            transactionDao.save(tx);

            System.out.printf("\nWithdrawal of %s %s processed successfully!\n",
                    amount.toPlainString(),
                    selectedEntry.getCryptoSymbol());
            System.out.println("Sent to: " + destinationAddress);

        } catch (SQLException e) {
            System.err.println("\nERROR in the database while processing withdrawal: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nUnexpected error during withdrawal: " + e.getMessage());
        }
    }

    private static void handleTransfer() {
        System.out.println("\n--- Transfer Crypto ---");

        List<WalletEntry> entries = showAvailableCryptos("transfer");

        if (entries == null)
            return;

        WalletEntry selectedEntry = selectCrypto(entries);

        if (selectedEntry == null)
            return;

        BigDecimal amount = readAmount("transfer");

        if (amount == null)
            return;

        if (selectedEntry.getAmount().compareTo(amount) < 0) {
            System.out.println("\nERROR: Insufficient balance to perform the transfer.");
            return;
        }

        System.out.print("Enter the recipient user email: ");
        String destinationEmail = scanner.nextLine();

        if (destinationEmail == null || destinationEmail.trim().isEmpty()) {
            System.out.println("\nERROR: Invalid recipient email.");
            return;
        }

        try {
            Optional<Users> destinationUserOpt = usersDao.findByEmail(destinationEmail);

            if (!destinationUserOpt.isPresent()) {
                System.out.println("\nERROR: Recipient user not found.");
                return;
            }

            Users destinationUser = destinationUserOpt.get();

            if (!destinationUser.isActive()) {
                System.out.println("\nERROR: Recipient user account is inactive.");
                return;
            }

            Optional<Wallet> destinationWalletOpt = walletDao.findByUserId(destinationUser.getId());

            if (!destinationWalletOpt.isPresent()) {
                System.out.println("\nERROR: Recipient wallet not found.");
                return;
            }

            Wallet destinationWallet = destinationWalletOpt.get();
            Connection connection = null;

            try {
                connection = ConnectionFactory.getConnection();
                connection.setAutoCommit(false);

                BigDecimal senderNewBalance = selectedEntry.getAmount().subtract(amount);
                walletEntryDao.updateAmount(loggedInWallet.getId(), selectedEntry.getCryptoSymbol(), senderNewBalance);

                Optional<WalletEntry> recipientEntryOpt = walletEntryDao
                        .findByWalletIdAndSymbol(destinationWallet.getId(), selectedEntry.getCryptoSymbol());

                if (recipientEntryOpt.isPresent()) {
                    WalletEntry recipientEntry = recipientEntryOpt.get();
                    BigDecimal recipientNewBalance = recipientEntry.getAmount().add(amount);
                    walletEntryDao.updateAmount(destinationWallet.getId(), selectedEntry.getCryptoSymbol(),
                            recipientNewBalance);
                } else {
                    WalletEntry newEntry = new WalletEntry(destinationWallet.getId(), selectedEntry.getCryptoSymbol(),
                            amount);
                    walletEntryDao.save(newEntry);
                }

                Transaction tx = createTransaction("TRANSFER", loggedInWallet.getId(), destinationWallet.getId(),
                        selectedEntry.getCryptoSymbol(), amount);
                transactionDao.save(tx);

                connection.commit();

                System.out.printf("\nTransfer of %s %s processed successfully!\n",
                        amount.toPlainString(),
                        selectedEntry.getCryptoSymbol());
                System.out.println("Sent to: " + destinationUser.getUserName() + " (" + destinationEmail + ")");

            } catch (Exception e) {
                if (connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException rollbackEx) {
                        System.err.println("ERROR rolling back transaction: " + rollbackEx.getMessage());
                    }
                }
                throw e;
            } finally {
                if (connection != null) {
                    try {
                        connection.setAutoCommit(true);
                        connection.close();
                    } catch (SQLException closeEx) {
                        System.err.println("ERROR closing connection: " + closeEx.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("\nERROR in the database while processing transfer: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nUnexpected error during transfer: " + e.getMessage());
        }
    }

    private static void handleViewStatement() {
        System.out.println("\n--- Account Statement (Last 20 Transactions) ---");

        try {
            List<Transaction> transactions = transactionDao.findRecentByWalletId(loggedInWallet.getId(), 20);

            if (transactions.isEmpty()) {
                System.out.println("No transactions recorded for this wallet.");
                return;
            }

            System.out.println(
                    "---------------------------------------------------------------------------------------------------");
            System.out.println("Date/Time           | Type       | Crypto | Amount | From/To     | Status");
            System.out.println("-------------------|------------|--------|------------|-------------|-----------");

            for (Transaction tx : transactions) {
                String fromTo = formatFromTo(tx);

                System.out.printf("%-19s | %-10s | %-6s | %-10s | %-11s | %s\n",
                        tx.getTransactionDate() != null ? tx.getTransactionDate().format(DATE_TIME_FORMATTER) : "N/A",
                        tx.getType() != null ? tx.getType() : "N/A",
                        tx.getCryptoSymbol() != null ? tx.getCryptoSymbol() : "N/A",
                        tx.getAmount() != null ? tx.getAmount().toPlainString() : "N/A",
                        fromTo,
                        tx.getStatus() != null ? tx.getStatus() : "N/A");
            }
            System.out.println(
                    "---------------------------------------------------------------------------------------------------");
        } catch (SQLException e) {
            System.err.println("\nERROR fetching statement: " + e.getMessage());
        }
    }

    private static String formatFromTo(Transaction tx) {
        if ("DEPOSIT".equals(tx.getType())) {
            return "Deposit";
        } else if ("WITHDRAWAL".equals(tx.getType())) {
            return "Withdrawal";
        } else if ("TRANSFER".equals(tx.getType())) {
            Integer sourceId = tx.getSourceWalletId();
            Integer destId = tx.getDestinationWalletId();
            int currentWalletId = loggedInWallet.getId();

            if (sourceId != null && sourceId == currentWalletId) {
                return "Env->W:" + destId;
            } else if (destId != null && destId == currentWalletId) {
                return "Rec<-W:" + sourceId;
            }
        }
        return "-";
    }

    private static void handleEditUser() {
        System.out.println("\n--- Edit My Profile ---");
        Optional<Users> userOpt = Optional.empty();

        try {
            userOpt = usersDao.findById(loggedInUser.getId());
        } catch (SQLException e) {
            System.err.println("ERROR fetching user data for editing: " + e.getMessage());
            return;
        }

        if (!userOpt.isPresent()) {
            System.err.println("ERROR: Could not find logged-in user data.");
            handleLogout();
            return;
        }

        Users currentUser = userOpt.get();

        System.out.println("Leave blank to keep unchanged.");

        System.out.print("New name [" + currentUser.getUserName() + "]: ");
        String newName = scanner.nextLine();

        String newEmail;

        do {
            System.out.print("New email [" + currentUser.getEmail() + "]: ");
            newEmail = scanner.nextLine();

            if (newEmail.trim().isEmpty()) {
                newEmail = currentUser.getEmail();
                break;
            }

            if (!ValidationUtil.isValidEmail(newEmail)) {
                System.out.println("\nERROR: Invalid email format. Please enter a valid email.");
                continue;
            }

            try {
                Optional<Users> existingUser = usersDao.findByEmail(newEmail);
                if (existingUser.isPresent() && existingUser.get().getId() != currentUser.getId()) {
                    System.out.println("\nERROR: This email is already in use by another user.");
                    continue;
                }
                break;
            } catch (SQLException e) {
                System.err.println("\nERROR checking email: " + e.getMessage());
                return;
            }
        } while (true);

        System.out.print("New phone [" + currentUser.getPhoneNumber() + "]: ");
        String newPhoneNumber = scanner.nextLine();

        String newPassword;

        do {
            System.out.print("New password (leave blank to keep current): ");
            newPassword = scanner.nextLine();

            if (newPassword.trim().isEmpty()) {
                newPassword = null;
                break;
            }

            if (!ValidationUtil.isStrongPassword(newPassword)) {
                System.out.println("\nERROR: Password does not meet the minimum security requirements.");
                System.out.println(ValidationUtil.getPasswordRequirements());
                continue;
            }
            break;
        } while (true);

        System.out.print("Keep user active? (Y/N) [" + (currentUser.isActive() ? "Y" : "N") + "]: ");
        String activeInput = scanner.nextLine();

        if (newName != null && !newName.trim().isEmpty()) {
            currentUser.setUserName(newName.trim());
        }

        currentUser.setEmail(newEmail);

        if (newPhoneNumber != null && !newPhoneNumber.trim().isEmpty()) {
            currentUser.setPhoneNumber(newPhoneNumber.trim());
        }

        if (newPassword != null) {
            currentUser.setPassword(newPassword);
        }

        if (activeInput != null && !activeInput.trim().isEmpty()) {
            currentUser.setActive(activeInput.trim().equalsIgnoreCase("Y") || activeInput.trim().equalsIgnoreCase("S"));
        }

        try {
            usersDao.update(currentUser);
            System.out.println("\nData updated successfully!");
            loggedInUser = currentUser;
        } catch (SQLException e) {
            System.err.println("\nERROR in the database while updating user: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("\nValidation ERROR: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nUnexpected error during update: " + e.getMessage());
        }
    }

    private static void handleDeleteUser() {
        System.out.println("\n--- Delete My Account ---");
        System.out.println("!!! WARNING !!! This action is irreversible and will delete your user and wallet.");
        System.out.print("Enter your current password to confirm deletion: ");
        String confirmPassword = scanner.nextLine();

        try {
            Optional<Users> userOpt = usersDao.findById(loggedInUser.getId());
            if (!userOpt.isPresent()) {
                System.err.println("ERROR: User not found.");
                handleLogout();
                return;
            }

            Users userToDelete = userOpt.get();

            if (!usersDao.checkPassword(confirmPassword, userToDelete.getPassword())) {
                System.out.println("\nERROR: Confirmation password is incorrect.");
                return;
            }

            System.out.print("Final confirmation. Are you sure you want to delete your account? (Y/N): ");
            String confirm = scanner.nextLine();

            if (!confirm.equalsIgnoreCase("Y") && !confirm.equalsIgnoreCase("S")) {
                System.out.println("\nAccount deletion cancelled.");
                return;
            }

            Connection connection = null;

            try {
                connection = ConnectionFactory.getConnection();
                connection.setAutoCommit(false);
                walletEntryDao.deleteByWalletId(loggedInWallet.getId());
                walletDao.delete(loggedInWallet.getId());
                usersDao.delete(userToDelete.getId());
                connection.commit();
                System.out.println("\nUser and wallet deleted successfully.");
                handleLogout();

            } catch (Exception e) {
                if (connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException rollbackEx) {
                        System.err.println("ERROR rolling back transaction: " + rollbackEx.getMessage());
                    }
                }
                throw e;
            } finally {
                if (connection != null) {
                    try {
                        connection.setAutoCommit(true);
                        connection.close();
                    } catch (SQLException closeEx) {
                        System.err.println("ERROR closing connection: " + closeEx.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("\nERROR in the database while attempting to delete user: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nUnexpected error during deletion: " + e.getMessage());
        }
    }

    private static void handleListAllUsers() {
        System.out.println("\n--- List of All Users (Test Only) ---");

        try {
            List<Users> allUsers = usersDao.findAll();

            if (allUsers.isEmpty()) {
                System.out.println("No users registered in the system.");
            } else {
                System.out.println("-----------------------------------------------------");
                System.out.println(" ID | Name                 | Email                | Active");
                System.out.println("----|----------------------|----------------------|-------");
                for (Users u : allUsers) {
                    System.out.printf(" %-2d | %-20s | %-20s | %s\n",
                            u.getId(),
                            u.getUserName().length() > 20 ? u.getUserName().substring(0, 17) + "..." : u.getUserName(),
                            u.getEmail().length() > 20 ? u.getEmail().substring(0, 17) + "..." : u.getEmail(),
                            u.isActive() ? "Yes" : "No");
                }
                System.out.println("-----------------------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("\nERROR listing users: " + e.getMessage());
        }
    }

    private static BigDecimal readAmount(String operation) {
        System.out.print("Enter the amount to " + operation + " (e.g. 0.05): ");

        try {
            BigDecimal amount = scanner.nextBigDecimal();
            scanner.nextLine();

            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("\nERROR: The amount must be a positive value.");
                return null;
            }

            return amount;
        } catch (InputMismatchException e) {
            System.out.println("\nERROR: Invalid amount entered.");
            scanner.nextLine();
            return null;
        }
    }

    private static List<WalletEntry> showAvailableCryptos(String operation) {
        try {
            List<WalletEntry> entries = walletEntryDao.findByWalletId(loggedInWallet.getId());

            if (entries.isEmpty()) {
                System.out.println("You do not have any cryptocurrency balance available for " + operation + ".");
                return null;
            }

            System.out.println("Cryptocurrencies available for " + operation + ":");
            System.out.println("---------------------");
            System.out.println(" # | Crypto | Balance");
            System.out.println("---------------------");

            for (int i = 0; i < entries.size(); i++) {
                WalletEntry entry = entries.get(i);
                System.out.printf(" %d | %-6s | %s\n",
                        i + 1,
                        entry.getCryptoSymbol(),
                        entry.getAmount().toPlainString());
            }

            System.out.println("---------------------");
            return entries;
        } catch (SQLException e) {
            System.err.println("\nERROR fetching balances: " + e.getMessage());
            return null;
        }
    }

    private static WalletEntry selectCrypto(List<WalletEntry> entries) {
        System.out.print("Choose the crypto number: ");
        int choice = readOption();

        if (choice < 1 || choice > entries.size()) {
            System.out.println("\nInvalid crypto option.");
            return null;
        }

        return entries.get(choice - 1);
    }

    private static Transaction createTransaction(String type, Integer sourceWalletId, Integer destWalletId,
            String cryptoSymbol, BigDecimal amount) {
        Transaction tx = new Transaction();
        tx.setType(type);
        tx.setSourceWalletId(sourceWalletId);
        tx.setDestinationWalletId(destWalletId);
        tx.setCryptoSymbol(cryptoSymbol);
        tx.setAmount(amount);
        tx.setTransactionDate(LocalDateTime.now());
        tx.setStatus("COMPLETED");
        return tx;
    }
}