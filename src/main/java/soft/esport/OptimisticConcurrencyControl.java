package soft.esport;

import soft.esport.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class OptimisticConcurrencyControl {

    public void updateTournament(int tournamentId, int newMaxPlayers) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            String selectSQL = "SELECT version FROM tournaments WHERE tournament_id = ?";
            int currentVersion;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setInt(1, tournamentId);
                ResultSet rs = selectStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Tournament not found");
                    return;
                }

                currentVersion = rs.getInt("version");
            }

            String updateSQL = "UPDATE tournaments SET max_players = ?, version = version + 1 WHERE tournament_id = ? AND version = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setInt(1, newMaxPlayers);
                updateStmt.setInt(2, tournamentId);
                updateStmt.setInt(3, currentVersion);

                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated == 0) {
                    System.out.println("Optimistic locking failed: The tournament was modified by another transaction.");
                    conn.rollback();
                } else {
                    conn.commit();
                    System.out.println("Tournament updated successfully!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertRegistration(int tournamentId, int playerId, AtomicInteger successfulTransactions) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            // We retrieve current tournament details: version, max_players and current registration count.
            String selectSQL = """
                    SELECT t.version, t.max_players,
                           (SELECT COUNT(*) FROM tournament_registrations tr WHERE tr.tournament_id = t.tournament_id) AS current_count
                    FROM tournaments t WHERE tournament_id = ?
                    """;
            //String selectSQL = "SELECT t.version, t.max_players, " +
            //        "       (SELECT COUNT(*) FROM tournament_registrations tr WHERE tr.tournament_id = t.tournament_id) AS current_count " +
            //        "FROM tournaments t WHERE tournament_id = ?";
            int currentVersion, maxPlayers, currentCount;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setInt(1, tournamentId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Tournament not found");
                        return false;
                    }
                    currentVersion = rs.getInt("version");
                    maxPlayers = rs.getInt("max_players");
                    currentCount = rs.getInt("current_count");
                }
            }

            // We return if the tournament is already full.
            if (currentCount >= maxPlayers) {
                System.out.println("Tournament is full");
                return true;
            }

            // Update version number which is our OCC check
            String updateSQL = """
                    UPDATE tournaments
                    SET version = version + 1
                    WHERE tournament_id = ? AND version = ?
                      AND max_players > (SELECT COUNT(*) FROM tournament_registrations WHERE tournament_id = ?)
                    """;
            //String updateSQL = "UPDATE tournaments " +
            //        "SET version = version + 1 " +
            //        "WHERE tournament_id = ? AND version = ? " +
            //        "  AND max_players > (SELECT COUNT(*) FROM tournament_registrations WHERE tournament_id = ?)";
            int rowsUpdated;
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setInt(1, tournamentId);
                updateStmt.setInt(2, currentVersion);
                updateStmt.setInt(3, tournamentId);
                rowsUpdated = updateStmt.executeUpdate();
            }

            // If no row was updated, it means the tournament was concurrently modified or is now full.
            if (rowsUpdated == 0) {
                System.out.println("Optimistic locking failed: The tournament was modified by another transaction or is now full.");
                conn.rollback();
                return false;
            }

            // We insert the registration.
            String insertSQL = "INSERT INTO tournament_registrations (tournament_id, player_id) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                insertStmt.setInt(1, tournamentId);
                insertStmt.setInt(2, playerId);
                insertStmt.executeUpdate();
            }

            conn.commit();
            successfulTransactions.incrementAndGet();
            System.out.println("Player registered successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}