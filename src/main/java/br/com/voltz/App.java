// Coloque este arquivo dentro do pacote: br/com/voltz/
package br.com.voltz;

// Imports Atualizados
import br.com.voltz.dao.TransactionDao;
import br.com.voltz.dao.UsersDao;
import br.com.voltz.dao.WalletDao;
import br.com.voltz.dao.WalletEntryDao;
import br.com.voltz.factory.ConnectionFactory;
import br.com.voltz.model.SupportedCrypto;
import br.com.voltz.model.Transaction;
import br.com.voltz.model.Users; // Usando Users (plural)
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

    // Scanner e DAOs como estáticos para fácil acesso
    private static final Scanner scanner = new Scanner(System.in);
    private static final UsersDao usersDao = new UsersDao(); // DAO no plural
    private static final WalletDao walletDao = new WalletDao();
    private static final WalletEntryDao walletEntryDao = new WalletEntryDao();
    private static final TransactionDao transactionDao = new TransactionDao();

    // Estado da "sessão" do usuário
    private static Users usuarioLogado = null; // Usuário logado (plural)
    private static Wallet carteiraLogada = null; // Carteira do usuário logado

    public static void main(String[] args) {
        System.out.println("Bem-vindo ao Voltz Crypto Bank!");

        // Teste inicial de conexão (opcional, mas recomendado)
        try {
            ConnectionFactory.getConnection().close();
            System.out.println("Conexão com o banco de dados estabelecida.");
        } catch (SQLException e) {
            System.err.println("ERRO FATAL: Não foi possível conectar ao banco.");
            System.err.println("Verifique URL, usuário, senha e se o banco está rodando.");
            System.err.println(e.getMessage());
            return; // Encerra se não conectar
        }

        boolean exit = false;
        while (!exit) {
            exibirMenuPrincipal();
            int choice = lerOpcao();

            try { // Bloco try-catch geral para capturar erros inesperados no fluxo
                switch (choice) {
                    // --- Menu Deslogado ---
                    case 1: if (usuarioLogado == null) handleRegistrarUsuario(); else System.out.println("Opção inválida."); break;
                    case 2: if (usuarioLogado == null) handleLogin(); else System.out.println("Opção inválida."); break;

                    // --- Menu Logado ---
                    case 3: if (checkLogin()) handleVerSaldo(); break;
                    case 4: if (checkLogin()) handleDepositar(); break;
                    case 5: if (checkLogin()) handleSacar(); break;
                    case 6: if (checkLogin()) handleTransferir(); break;
                    case 7: if (checkLogin()) handleVerExtrato(); break;
                    case 8: if (checkLogin()) handleEditarUsuario(); break; // Manteve Editar
                    case 9: if (checkLogin()) handleDeletarUsuario(); break; // Manteve Deletar
                    case 10: handleListarTodosUsuarios(); break; // Manteve Listar (Teste)
                    case 11: if (checkLogin()) handleLogout(); break;

                    // --- Sair ---
                    case 12: exit = true; break; // Opção de Sair

                    default: System.out.println("Opção inválida. Tente novamente."); break;
                }
                if (!exit) {
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine(); // Pausa para o usuário ler a saída
                }
            } catch (Exception e) {
                System.err.println("\n!!! Ocorreu um erro inesperado: " + e.getMessage() + " !!!");
                // e.printStackTrace(); // Descomente para ver o stack trace completo durante o desenvolvimento
            }
        }

        System.out.println("\nObrigado por usar o Voltz Crypto Bank!");
        scanner.close();
    }

    // --- Métodos de UI e Controle de Fluxo ---

    private static void exibirMenuPrincipal() {
        System.out.println("\n=========================");
        System.out.println("   Voltz Crypto Bank Menu");
        System.out.println("=========================");
        if (usuarioLogado == null) {
            System.out.println("1. Registrar Novo Usuário");
            System.out.println("2. Login");
            System.out.println("10. Listar Todos Usuários (Teste)"); // Mantido para teste
            System.out.println("12. Sair");
        } else {
            System.out.println("Usuário Logado: " + usuarioLogado.getNome() + " (ID: " + usuarioLogado.getId() + ")");
            System.out.println("-------------------------");
            System.out.println("3. Ver Saldo da Carteira");
            System.out.println("4. Depositar Cripto");
            System.out.println("5. Sacar Cripto");
            System.out.println("6. Transferir Cripto");
            System.out.println("7. Ver Extrato");
            System.out.println("8. Editar Meus Dados");
            System.out.println("9. Deletar Minha Conta");
            System.out.println("10. Listar Todos Usuários (Teste)"); // Mantido para teste
            System.out.println("11. Logout");
            System.out.println("12. Sair");
        }
        System.out.print("Escolha uma opção: ");
    }

    private static int lerOpcao() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consome a nova linha pendente após nextInt()
            return choice;
        } catch (InputMismatchException e) {
            System.out.println("Erro: Entrada inválida. Por favor, digite um número.");
            scanner.nextLine(); // Consome a entrada inválida que causou a exceção
            return -1; // Retorna um valor inválido para indicar erro
        }
    }

    // Verifica se o usuário está logado e carrega a carteira se necessário
    private static boolean checkLogin() {
        if (usuarioLogado == null) {
            System.out.println("\nERRO: Você precisa estar logado para realizar esta operação.");
            return false;
        }
        // Garante que a carteira associada ao usuário logado esteja carregada
        if (carteiraLogada == null) {
            try {
                Optional<Wallet> walletOpt = walletDao.findByUserId(usuarioLogado.getId());
                if (walletOpt.isPresent()) {
                    carteiraLogada = walletOpt.get();
                } else {
                    // Situação inesperada: usuário logado sem carteira no banco
                    System.err.println("ERRO CRÍTICO: Carteira não encontrada para o usuário logado ID: " + usuarioLogado.getId());
                    System.err.println("Realizando logout forçado.");
                    handleLogout(); // Desloga por segurança/consistência
                    return false;
                }
            } catch (SQLException e) {
                System.err.println("ERRO ao buscar dados da carteira: " + e.getMessage());
                return false; // Não pode continuar sem a carteira
            }
        }
        return true; // Usuário logado e carteira carregada
    }

    // --- Métodos Handler para Opções do Menu ---

    private static void handleRegistrarUsuario() {
        System.out.println("\n--- Registrar Novo Usuário ---");
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine();
        System.out.print("CPF ou CNPJ (apenas números): ");
        String cpfCnpj = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        // TODO: Adicionar validação de formato de email
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        // TODO: Adicionar validação de força da senha

        // Cria objeto Users (plural)
        Users novoUsuario = new Users(nome, cpfCnpj, email, telefone, senha, true); // Ativo por padrão

        try {
            // Verifica se o email já existe ANTES de tentar salvar
            if (usersDao.findByEmail(email).isPresent()) {
                System.out.println("\nERRO: Este email já está cadastrado.");
                return;
            }

            // Salva o usuário no banco (DAO fará o hash da senha)
            int novoUserId = usersDao.save(novoUsuario); // Pega o ID retornado
            System.out.println("\nUsuário registrado com sucesso! ID: " + novoUserId);

            // Cria a carteira para este novo usuário
            Wallet novaCarteira = new Wallet(novoUserId);
            walletDao.save(novaCarteira);
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
        if (usuarioLogado != null) {
            System.out.println("\nVocê já está logado como " + usuarioLogado.getNome() + ".");
            return;
        }
        System.out.println("\n--- Login ---");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            Optional<Users> userOpt = usersDao.findByEmail(email); // Usa usersDao

            if (userOpt.isPresent()) {
                Users user = userOpt.get(); // Usa Users
                // Verifica a senha usando o método do DAO
                if (usersDao.checkPassword(senha, user.getSenha())) { // user.getSenha() retorna o HASH do banco
                    if (user.isAtivo()) {
                        usuarioLogado = user; // Define o usuário logado
                        // Tenta carregar a carteira associada
                        Optional<Wallet> walletOpt = walletDao.findByUserId(usuarioLogado.getId());
                        if (walletOpt.isPresent()) {
                            carteiraLogada = walletOpt.get(); // Define a carteira logada
                            System.out.println("\nLogin bem-sucedido! Bem-vindo(a), " + usuarioLogado.getNome() + "!");
                        } else {
                            System.err.println("ERRO CRÍTICO: Usuário existe mas não possui carteira associada!");
                            usuarioLogado = null; // Login falha se não achar a carteira
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
        usuarioLogado = null;
        carteiraLogada = null;
        System.out.println("\nLogout realizado com sucesso.");
    }

    private static void handleVerSaldo() {
        System.out.println("\n--- Saldo da Carteira (ID: " + carteiraLogada.getId() + ") ---");
        try {
            // Busca os saldos (entradas) da carteira no banco
            List<WalletEntry> entries = walletEntryDao.findByWalletId(carteiraLogada.getId());
            // Atualiza a lista no objeto em memória (opcional, mas bom para consistência)
            carteiraLogada.setEntries(entries);

            if (entries.isEmpty()) {
                System.out.println("Sua carteira está vazia. Que tal fazer um depósito?");
            } else {
                System.out.println("---------------------");
                System.out.println(" Cripto | Quantidade");
                System.out.println("---------------------");
                for (WalletEntry entry : entries) {
                    // Usar toPlainString() para evitar notação científica em valores grandes/pequenos
                    System.out.printf(" %-6s | %s\n", entry.getCryptoSymbol(), entry.getAmount().toPlainString());
                }
                System.out.println("---------------------");
                // TODO (Opcional): Buscar preços via API e mostrar valor total em R$/USD
            }
        } catch (SQLException e) {
            System.err.println("\nERRO ao buscar saldos: " + e.getMessage());
        }
    }

    private static void handleDepositar() {
        System.out.println("\n--- Depositar Cripto ---");
        // 1. Escolher a Cripto
        System.out.println("Criptomoedas disponíveis para depósito:");
        String[] symbols = SupportedCrypto.getAllSymbols();
        for (int i = 0; i < symbols.length; i++) {
            System.out.printf("%d. %s\n", i + 1, symbols[i]);
        }
        System.out.print("Escolha o número da cripto: ");
        int choice = lerOpcao();

        if (choice < 1 || choice > symbols.length) {
            System.out.println("\nOpção de cripto inválida.");
            return;
        }
        String selectedSymbol = symbols[choice - 1];

        // 2. Ler a Quantidade
        System.out.print("Digite a quantidade a depositar (ex: 0.05): ");
        BigDecimal amount;
        try {
            amount = scanner.nextBigDecimal();
            scanner.nextLine(); // Consome newline
            // Validação simples da quantidade
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("\nERRO: A quantidade deve ser um valor positivo.");
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("\nERRO: Quantidade digitada inválida.");
            scanner.nextLine(); // Consome entrada inválida
            return;
        }

        // 3. Processar o Depósito (Simulado)
        try {
            // Verifica se já existe saldo para essa cripto
            Optional<WalletEntry> entryOpt = walletEntryDao.findByWalletIdAndSymbol(carteiraLogada.getId(), selectedSymbol);

            if (entryOpt.isPresent()) {
                // Atualiza saldo existente
                WalletEntry entry = entryOpt.get();
                BigDecimal novoSaldo = entry.getAmount().add(amount);
                // Usar o método de update que busca por walletId e Symbol é mais prático aqui
                walletEntryDao.updateAmount(carteiraLogada.getId(), selectedSymbol, novoSaldo);
            } else {
                // Cria nova entrada de saldo
                WalletEntry novaEntry = new WalletEntry(carteiraLogada.getId(), selectedSymbol, amount);
                walletEntryDao.save(novaEntry);
            }

            // 4. Registrar a Transação
            Transaction tx = new Transaction();
            tx.setType("DEPOSIT"); // Tipo da transação
            tx.setDestinationWalletId(carteiraLogada.getId()); // O destino é a carteira logada
            tx.setSourceWalletId(null); // Origem nula para depósito
            tx.setCryptoSymbol(selectedSymbol);
            tx.setAmount(amount);
            tx.setTransactionDate(java.time.LocalDateTime.now());
            tx.setStatus("COMPLETED"); // Simulação: depósito sempre completo
            transactionDao.save(tx);

            System.out.printf("\nDepósito de %s %s processado com sucesso!\n", amount.toPlainString(), selectedSymbol);

        } catch (SQLException e) {
            System.err.println("\nERRO no Banco de Dados ao processar depósito: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nERRO inesperado durante o depósito: " + e.getMessage());
        }
    }

    private static void handleSacar() {
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

    private static void handleTransferir() {
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

    private static void handleVerExtrato() {
        System.out.println("\n--- Extrato da Conta (Últimas 20 Transações) ---");
        try {
            // Busca as últimas 20 transações da carteira logada
            List<Transaction> transactions = transactionDao.findRecentByWalletId(carteiraLogada.getId(), 20);

            if (transactions.isEmpty()) {
                System.out.println("Nenhuma transação registrada para esta carteira.");
            } else {
                System.out.println("---------------------------------------------------------------------------------------------------");
                System.out.println("Data/Hora           | Tipo       | Cripto | Quantidade | De/Para     | Status");
                System.out.println("-------------------|------------|--------|------------|-------------|-----------");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                for (Transaction tx : transactions) {
                    String dePara = "-";
                    if ("DEPOSIT".equals(tx.getType())) {
                        dePara = "Depósito";
                    } else if ("WITHDRAWAL".equals(tx.getType())) {
                        dePara = "Saque";
                    } else if ("TRANSFER".equals(tx.getType())) {
                        // Verifica se a carteira logada foi a origem ou destino
                        if (carteiraLogada.getId() == tx.getSourceWalletId()) {
                            dePara = "Env->W:" + tx.getDestinationWalletId(); // Enviou para carteira X
                        } else {
                            dePara = "Rec<-W:" + tx.getSourceWalletId(); // Recebeu da carteira Y
                        }
                    }

                    System.out.printf("%-19s | %-10s | %-6s | %-10s | %-11s | %s\n",
                            tx.getTransactionDate() != null ? tx.getTransactionDate().format(formatter) : "N/A",
                            tx.getType() != null ? tx.getType() : "N/A",
                            tx.getCryptoSymbol() != null ? tx.getCryptoSymbol() : "N/A",
                            tx.getAmount() != null ? tx.getAmount().toPlainString() : "N/A",
                            dePara,
                            tx.getStatus() != null ? tx.getStatus() : "N/A");
                }
                System.out.println("---------------------------------------------------------------------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("\nERRO ao buscar extrato: " + e.getMessage());
        }
    }

    private static void handleEditarUsuario() {
        System.out.println("\n--- Editar Meus Dados ---");
        // Busca o usuário logado novamente para garantir dados atualizados
        Optional<Users> userOpt = Optional.empty();
        try {
            userOpt = usersDao.findById(usuarioLogado.getId());
        } catch (SQLException e) {
            System.err.println("ERRO ao buscar dados do usuário para edição: " + e.getMessage());
            return;
        }

        if (!userOpt.isPresent()) {
            System.err.println("ERRO: Não foi possível encontrar os dados do usuário logado.");
            handleLogout(); // Desloga se não achar mais o usuário
            return;
        }
        Users currentUser = userOpt.get();

        System.out.println("Deixe em branco para não alterar.");

        System.out.print("Novo nome [" + currentUser.getNome() + "]: ");
        String novoNome = scanner.nextLine();
        System.out.print("Novo email [" + currentUser.getEmail() + "]: ");
        String novoEmail = scanner.nextLine();
        // TODO: Validar formato do novo email, verificar se já existe (se for diferente do atual)
        System.out.print("Novo telefone [" + currentUser.getTelefone() + "]: ");
        String novoTelefone = scanner.nextLine();
        System.out.print("Nova senha (deixe em branco para não alterar): ");
        String novaSenha = scanner.nextLine();
        System.out.print("Manter usuário ativo? (S/N) [" + (currentUser.isAtivo() ? "S" : "N") + "]: ");
        String ativoInput = scanner.nextLine();

        // Atualiza o objeto currentUser com os novos dados (se fornecidos)
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            currentUser.setNome(novoNome.trim());
        }
        if (novoEmail != null && !novoEmail.trim().isEmpty()) {
            // Adicionar verificação aqui se o novo email já existe por outro usuário!
            currentUser.setEmail(novoEmail.trim());
        }
        if (novoTelefone != null && !novoTelefone.trim().isEmpty()) {
            currentUser.setTelefone(novoTelefone.trim());
        }
        if (novaSenha != null && !novaSenha.isEmpty()) {
            currentUser.setSenha(novaSenha); // DAO fará o hash se necessário
        } else {
            currentUser.setSenha(null); // Indica para o DAO não atualizar a senha
        }
        if (ativoInput != null && !ativoInput.trim().isEmpty()) {
            currentUser.setAtivo(ativoInput.trim().equalsIgnoreCase("S"));
        }

        try {
            usersDao.update(currentUser);
            System.out.println("\nDados atualizados com sucesso!");
            // Atualiza o objeto usuarioLogado com os novos dados
            usuarioLogado = currentUser;
        } catch (SQLException e) {
            System.err.println("\nERRO no Banco de Dados ao atualizar usuário: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("\nERRO de Validação: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nERRO inesperado durante a atualização: " + e.getMessage());
        }
    }

    private static void handleDeletarUsuario() {
        System.out.println("\n--- Deletar Minha Conta ---");
        System.out.println("!!! ATENÇÃO !!! Esta ação é irreversível e apagará seu usuário e sua carteira.");
        System.out.print("Digite sua senha atual para confirmar a exclusão: ");
        String senhaConfirm = scanner.nextLine();

        try {
            // Re-buscar o usuário para pegar o hash atual
            Optional<Users> userOpt = usersDao.findById(usuarioLogado.getId());
            if (!userOpt.isPresent()) {
                System.err.println("ERRO: Usuário não encontrado.");
                handleLogout();
                return;
            }
            Users userToDelete = userOpt.get();

            // Verificar a senha
            if (usersDao.checkPassword(senhaConfirm, userToDelete.getSenha())) {
                System.out.print("Confirmação final. Tem certeza que deseja deletar sua conta? (S/N): ");
                String confirm = scanner.nextLine();
                if ("S".equalsIgnoreCase(confirm)) {
                    // TODO: Implementar exclusão em cascata ou explícita
                    // 1. Deletar WalletEntries (walletEntryDao.deleteByWalletId)
                    // 2. Deletar Wallet (walletDao.delete) - CUIDADO com FK! Pode ser melhor só inativar.
                    // 3. Deletar Transações? (Opcional, pode querer manter histórico)
                    // 4. Deletar Usuário (usersDao.delete)

                    // Simplesmente deletando o usuário por enquanto:
                    usersDao.delete(userToDelete.getId());
                    System.out.println("\nUsuário deletado com sucesso.");
                    handleLogout(); // Desloga o usuário

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

    private static void handleListarTodosUsuarios() {
        System.out.println("\n--- Lista de Todos os Usuários (Apenas Teste) ---");
        try {
            List<Users> todosUsuarios = usersDao.findAll(); // Usa usersDao
            if (todosUsuarios.isEmpty()) {
                System.out.println("Nenhum usuário registrado no sistema.");
            } else {
                System.out.println("-----------------------------------------------------");
                System.out.println(" ID | Nome                 | Email                | Ativo");
                System.out.println("----|----------------------|----------------------|-------");
                for (Users u : todosUsuarios) {
                    System.out.printf(" %-2d | %-20s | %-20s | %s\n",
                            u.getId(),
                            u.getNome().length() > 20 ? u.getNome().substring(0, 17) + "..." : u.getNome(),
                            u.getEmail().length() > 20 ? u.getEmail().substring(0, 17) + "..." : u.getEmail(),
                            u.isAtivo() ? "Sim" : "Não");
                }
                System.out.println("-----------------------------------------------------");
            }
        } catch (SQLException e) {
            System.err.println("\nERRO ao listar usuários: " + e.getMessage());
        }
    }

    // Removidos métodos não mais necessários:
    // associateCompanyToUser, displayUserInfo, displayWalletInfo (agora handleVerSaldo),
    // displayCompanyInfo, sendAmountFromCompany, saveDataToFile
}