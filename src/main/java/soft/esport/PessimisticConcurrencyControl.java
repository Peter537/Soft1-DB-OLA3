package soft.esport;

import soft.esport.config.DatabaseConfig;

import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PessimisticConcurrencyControl {

    public void updateMatchResult(int matchID, int winnerID, AtomicInteger successfulTransactions, AtomicInteger lockWaitCounter) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            String lockSQL = "SELECT * FROM matches WHERE match_id = ? FOR UPDATE";
            long lockStart = System.nanoTime();
            try (PreparedStatement lockStmt = conn.prepareStatement(lockSQL)) {
                lockStmt.setInt(1, matchID);
                lockStmt.executeQuery();
            }
            long lockAcquired = System.nanoTime();
            long waitTimeMs = (lockAcquired - lockStart) / 1_000_000;
            if (waitTimeMs > 5) { // If the lock wait time is greater than 5ms, we count it as a lock wait
                lockWaitCounter.incrementAndGet();
            }

            String updateSQL = "UPDATE matches SET winner_id = ? WHERE match_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setInt(1, winnerID);
                updateStmt.setInt(2, matchID);
                updateStmt.executeUpdate();
            }

            try (CallableStatement updateRankingStmt = conn.prepareCall("CALL update_ranking(?)")) {
                updateRankingStmt.setInt(1, winnerID);
                updateRankingStmt.execute();
            }

            conn.commit();
            successfulTransactions.incrementAndGet();
            System.out.println("Match result updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertRegistration(int tournamentId, int playerId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // First, we lock the tournament row and retrieve max_players
                String selectTournamentSQL = "SELECT max_players FROM tournaments WHERE tournament_id = ? FOR UPDATE";
                int maxPlayers;
                try (PreparedStatement selectTournamentStmt = conn.prepareStatement(selectTournamentSQL)) {
                    selectTournamentStmt.setInt(1, tournamentId);
                    try (ResultSet rs = selectTournamentStmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Tournament not found");
                        }

                        maxPlayers = rs.getInt("max_players");
                    }
                }

                // Then we count current registrations
                String countRegistrationsSQL = "SELECT COUNT(*) AS current_players FROM tournament_registrations WHERE tournament_id = ?";
                int currentPlayers;
                try (PreparedStatement countRegistrationsStmt = conn.prepareStatement(countRegistrationsSQL)) {
                    countRegistrationsStmt.setInt(1, tournamentId);
                    try (ResultSet rs = countRegistrationsStmt.executeQuery()) {
                        rs.next();
                        currentPlayers = rs.getInt("current_players");
                    }
                }

                // We check if the tournament is full
                if (currentPlayers >= maxPlayers) {
                    throw new SQLException("Tournament is full");
                }

                // Then we insert the new registration
                String insertRegistrationSQL = "INSERT INTO tournament_registrations (tournament_id, player_id) VALUES (?, ?)";
                try (PreparedStatement insertRegistrationStmt = conn.prepareStatement(insertRegistrationSQL)) {
                    insertRegistrationStmt.setInt(1, tournamentId);
                    insertRegistrationStmt.setInt(2, playerId);
                    insertRegistrationStmt.executeUpdate();
                }

                conn.commit();
                System.out.println("Player registered successfully!");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Player registration failed: " + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}