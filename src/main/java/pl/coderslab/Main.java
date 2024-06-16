package pl.coderslab;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DbUtil.connect()) {

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}