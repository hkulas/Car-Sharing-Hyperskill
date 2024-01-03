package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        String databaseFileName = "default";

        for (int i = 0; i < args.length; i++) {
            if ("-databaseFileName".equals(args[i]) && i + 1 < args.length) {
                databaseFileName = args[i + 1];
                break;
            }
        }

        String url = "jdbc:h2:./src/carsharing/db/" + databaseFileName;

        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(true);

            try (Statement statement = connection.createStatement()) {
                String sql = "ALTER TABLE COMPANY \n" +
                        "    ALTER COLUMN ID INT AUTO_INCREMENT,\n" +
                        "    ADD CONSTRAINT UNIQUE_NAME UNIQUE (NAME);";

                statement.execute(sql);

                System.out.println("Table COMPANY created successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}