// ApiApp.java
import br.com.voltz.factory.ConnectionFactory;
import br.com.voltz.users.Users;
import br.com.voltz.usuarios.UsersDao; // Importe o UsersDao correto
import com.google.gson.Gson;

import java.sql.Connection;
import java.util.List;

import static spark.Spark.*;

public class ApiApp {

    public static void main(String[] args) {
        port(4567); // <- TEM QUE VIR PRIMEIRO
        get("/", (req, res) -> "API está rodando");
        Gson gson = new Gson();

        try {
            Connection connection = ConnectionFactory.getConnection();
            if (connection != null) {
                System.out.println("Conexão com o banco de dados estabelecida.");
            } else {
                System.out.println("Falha ao estabelecer conexão com o banco de dados.");
            }
            UsersDao usuarioDao = new UsersDao(connection); // Use o UsersDao atualizado

            // GET - Listar todos os usuários
            get("/users", (req, res) -> {
                try {
                    List<Users> usuarios = usuarioDao.findAll();
                    res.type("application/json");
                    return gson.toJson(usuarios);
                } catch (Exception e) {
                    res.status(500);  // Código HTTP de erro
                    return generateErrorResponse("Erro ao listar usuários", e);
                }
            });

            // GET - Buscar usuário por ID
            get("/users/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Users usuario = usuarioDao.findById(id);
                    if (usuario != null) {
                        res.type("application/json");
                        return gson.toJson(usuario);
                    } else {
                        res.status(404);
                        return generateErrorResponse("Usuário não encontrado", null);
                    }
                } catch (Exception e) {
                    res.status(500);
                    return generateErrorResponse("Erro ao buscar usuário", e);
                }
            });

            // POST - Criar novo usuário
            post("/users", (req, res) -> {
                try {
                    Users usuario = gson.fromJson(req.body(), Users.class);
                    usuarioDao.insert(usuario);  // Criação do usuário, criptografia ocorre aqui
                    res.status(201);  // Código HTTP 201: Created
                    return "Usuário criado com sucesso!";
                } catch (Exception e) {
                    res.status(500);
                    return generateErrorResponse("Erro ao criar usuário", e);
                }
            });

            // PUT - Atualizar usuário por ID
            put("/users/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Users existing = usuarioDao.findById(id);

                    if (existing == null) {
                        res.status(404);
                        return generateErrorResponse("Usuário não encontrado", null);
                    }

                    Users updatedUsuario = gson.fromJson(req.body(), Users.class);
                    updatedUsuario.setId(id);
                    usuarioDao.update(updatedUsuario);

                    return "Usuário atualizado com sucesso!";

                } catch (Exception e) {
                    res.status(500);
                    return generateErrorResponse("Erro ao atualizar usuário", e);
                }
            });

            // DELETE - Remover usuário por ID
            delete("/users/:id", (req, res) -> {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    Users existing = usuarioDao.findById(id);
                    if (existing == null) {
                        res.status(404);
                        return generateErrorResponse("Usuário não encontrado", null);
                    }
                    usuarioDao.delete(id);
                    return "Usuário deletado com sucesso!";
                } catch (Exception e) {
                    res.status(500);
                    return generateErrorResponse("Erro ao deletar usuário", e);
                }
            });

        } catch (Exception e) {
            System.out.println("Erro ao iniciar API: " + e.getMessage());
        }
    }

    // Método para gerar uma resposta de erro no formato JSON
    private static String generateErrorResponse(String message, Exception e) {
        // Se a exceção não for nula, adiciona o stack trace à resposta
        String errorDetail = (e != null) ? e.getMessage() : "Detalhes não disponíveis.";
        return new Gson().toJson(new ErrorResponse(message, errorDetail));
    }

    // Classe para formatar a resposta de erro
    public static class ErrorResponse {
        private String message;
        private String details;

        public ErrorResponse(String message, String details) {
            this.message = message;
            this.details = details;
        }

        public String getMessage() {
            return message;
        }

        public String getDetails() {
            return details;
        }
    }
}