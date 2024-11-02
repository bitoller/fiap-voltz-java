package main.java.voltz.main;

import main.java.voltz.users.User;

public class Main {
    public static void main(String[] args) throws Exception {
        User user = new User("bruno", "email@email.com", "123" , true);

        user.register();
    }
}
