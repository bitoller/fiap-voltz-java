package br.com.voltz.controller;

import br.com.voltz.users.User;
import br.com.voltz.service.UserService;
import com.google.gson.Gson;

import java.util.List;

import static spark.Spark.*;

public class UserController {

    private static final UserService userService = new UserService();
    private static final Gson gson = new Gson();

    public static void setupRoutes() {
        path("/api/users", () -> {

            post("", (req, res) -> {
                User user = gson.fromJson(req.body(), User.class);
                boolean created = userService.createUser(user);
                res.status(created ? 201 : 400);
                return gson.toJson(created ? "Usuário criado." : "Erro ao criar.");
            });

            get("", (req, res) -> {
                List<User> users = userService.getAllUsers();
                return gson.toJson(users);
            });

            get("/:id", (req, res) -> {
                int id = Integer.parseInt(req.params(":id"));
                User user = userService.getUserById(id);
                if (user != null) {
                    return gson.toJson(user);
                }
                res.status(404);
                return gson.toJson("Usuário não encontrado.");
            });

            put("/:id", (req, res) -> {
                int id = Integer.parseInt(req.params(":id"));
                User userUpdate = gson.fromJson(req.body(), User.class);
                boolean updated = userService.updateUser(id, userUpdate);
                res.status(updated ? 200 : 404);
                return gson.toJson(updated ? "Usuário atualizado." : "Usuário não encontrado.");
            });

            delete("/:id", (req, res) -> {
                int id = Integer.parseInt(req.params(":id"));
                boolean deleted = userService.deleteUser(id);
                res.status(deleted ? 200 : 404);
                return gson.toJson(deleted ? "Usuário deletado." : "Usuário não encontrado.");
            });

        });
    }
}
