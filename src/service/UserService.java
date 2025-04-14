package br.com.voltz.services;

import br.com.voltz.dao.UserDao;
import br.com.voltz.users.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private final UserDao userDao = new UserDao();

    public boolean createUser(User user) throws SQLException {
        return userDao.insert(user);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDao.findAll();
    }

    public User getUserById(int id) throws SQLException {
        return userDao.findById(id);
    }

    public boolean updateUser(User user) throws SQLException {
        return userDao.update(user);
    }

    public boolean deleteUser(int id) throws SQLException {
        return userDao.delete(id);
    }
}
