package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.DbUtil;

import java.sql.*;

public class UserDao {
    private static final String CREATE_USER_QUERY = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
    private static final String READ_USER_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String READ_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id = ?";

    public User create(User user) {
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, hashPassword(user.getPassword()));
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Database constraint violation. User creation failure -  entity was not created");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }


    public User read(int userId) {
        User resultUser = new User();

        try (Connection conn = DbUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement(READ_USER_QUERY);
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                resultUser.setId(resultSet.getInt("id"));
                resultUser.setEmail(resultSet.getString("email"));
                resultUser.setUserName(resultSet.getString("username"));
                resultUser.setPassword(resultSet.getString("password"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultUser;
    }

    public void update(User newUser) {
        User currentUser = read(newUser.getId());
        String currentUserPassword = currentUser.getPassword();
        String newUserPassword = newUser.getPassword();

        try (Connection conn = DbUtil.connect()) {
            PreparedStatement stmt = conn.prepareStatement(UPDATE_USER_QUERY);
            stmt.setString(1, newUser.getUserName());
            stmt.setString(2, newUser.getEmail());
            if (!currentUserPassword.equals(newUserPassword) && (!BCrypt.checkpw(newUserPassword, currentUserPassword))) {
                stmt.setString(3, hashPassword(newUserPassword));
            } else {
                stmt.setString(3,currentUserPassword);
            }
            stmt.setInt(4, newUser.getId());

            if (stmt.executeUpdate() == 1) {
                User updatedUser = read(newUser.getId());
                newUser.setUserName(updatedUser.getUserName());
                newUser.setEmail(updatedUser.getEmail());
                newUser.setPassword(updatedUser.getPassword());
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Database constraint violation. Update failure - `users` entity remains unchanged.");
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
