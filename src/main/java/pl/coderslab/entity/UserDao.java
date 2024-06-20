package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private static final String CREATE_USER_QUERY = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
    private static final String READ_USER_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String READ_ALL_USERS_QUERY = "SELECT * FROM users";
//    is this proper way to split line?
    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";

    public User create(User user) {
        try (Connection conn = DbUtil.connect()) {
//            wastes id numbers in case of not unique e-mail addresses
            PreparedStatement stmt = conn.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashPassword(user.getPassword()));
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public User read(int userId) {
        User resultUser = new User();

        try (Connection conn = DbUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement(READ_USER_QUERY);
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.next();
            resultUser.setId(resultSet.getInt("id"));
            resultUser.setEmail(resultSet.getString("email"));
            resultUser.setUserName(resultSet.getString("username"));
            resultUser.setPassword(resultSet.getString("password"));
        } catch (SQLException e) {
            return null;
        }
        return resultUser;
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
