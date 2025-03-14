package soft;

import soft.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TournamentRegistrations {

    public void registerPlayer(int tournamentId, int playerId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String insertRegistrationSQL = "INSERT INTO tournament_registrations (tournament_id, player_id) VALUES (?, ?)";
                try (PreparedStatement insertRegistrationStmt = conn.prepareStatement(insertRegistrationSQL)) {
                    insertRegistrationStmt.setInt(1, tournamentId);
                    insertRegistrationStmt.setInt(2, playerId);
                    insertRegistrationStmt.executeUpdate();
                }

                String updatePlayerRankingSQL = "UPDATE players SET ranking = ranking + 1 WHERE player_id = ?";
                try (PreparedStatement updatePlayerRankingStmt = conn.prepareStatement(updatePlayerRankingSQL)) {
                    updatePlayerRankingStmt.setInt(1, playerId);
                    updatePlayerRankingStmt.executeUpdate();
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