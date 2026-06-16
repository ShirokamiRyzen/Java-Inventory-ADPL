package com.inventory.database;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseCli {
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String command = args[0].toLowerCase();
        switch (command) {
            case "migrate":
                System.out.println("Running migration (creating tables)...");
                DatabaseHelper.initializeDatabase();
                System.out.println("Migration finished successfully.");
                break;
            case "fresh":
                System.out.println("Running fresh migration (resetting database)...");
                resetDatabase();
                DatabaseHelper.initializeDatabase();
                System.out.println("Fresh migration finished successfully.");
                break;
            case "seed":
                System.out.println("Running seeder...");
                DatabaseSeeder.main(new String[0]);
                break;
            case "clear":
                System.out.println("Clearing all records...");
                clearDatabase();
                break;
            default:
                System.out.println("Unknown command: " + command);
                printHelp();
                break;
        }
    }

    private static void printHelp() {
        System.out.println("====================================================");
        System.out.println("       JAVA INVENTORY - DATABASE CLI UTILITY       ");
        System.out.println("====================================================");
        System.out.println("Usage: java -cp \"bin;lib/*\" com.inventory.database.DatabaseCli [command]");
        System.out.println("\nAvailable commands:");
        System.out.println("  migrate   - Initialize database and create tables if they do not exist");
        System.out.println("  fresh     - Reset database by deleting the existing database file and recreating it");
        System.out.println("  seed      - Run seeder to populate testing data (Sembako dataset)");
        System.out.println("  clear     - Clear all transaction and product data (keeps user accounts)");
        System.out.println("====================================================");
    }

    private static void resetDatabase() {
        File dbFile = new File("inventory.db");
        if (dbFile.exists()) {
            if (dbFile.delete()) {
                System.out.println("Deleted existing inventory.db file.");
            } else {
                System.err.println("Could not delete inventory.db. Make sure no other process has locked the file.");
            }
        } else {
            System.out.println("No existing inventory.db file to delete.");
        }
    }

    private static void clearDatabase() {
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = OFF;");
            stmt.execute("DELETE FROM barang;");
            stmt.execute("DELETE FROM barang_masuk;");
            stmt.execute("DELETE FROM barang_keluar;");
            stmt.execute("DELETE FROM pengajuan_pembelian;");
            stmt.execute("DELETE FROM laporan;");
            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("All transaction and product records cleared successfully (user accounts retained).");
        } catch (SQLException e) {
            System.err.println("Failed to clear database: " + e.getMessage());
        }
    }
}
