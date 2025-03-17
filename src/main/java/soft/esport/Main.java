package soft.esport;

import soft.esport.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        // Exercise 1
        OptimisticConcurrencyControl occ = new OptimisticConcurrencyControl();
        occ.updateTournament(2, 8);

        // Exercise 2 & 4 (exercise 4 added the CALL update_ranking)
        PessimisticConcurrencyControl pcc = new PessimisticConcurrencyControl();
        pcc.updateMatchResult(3, new Random().nextInt(2) + 1, new AtomicInteger(), new AtomicInteger());

        // Exercise 3
        TournamentRegistrations tr = new TournamentRegistrations();
        tr.registerPlayer(2, 1);

        // Exercise 5
        for (int i = 2; i < 10; i++) {
            pcc.insertRegistration(2, i);
        }

        // Deleting all registrations for tournament 2 to test OCC
        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);

            String deleteRegistrationsSQL = "DELETE FROM tournament_registrations WHERE tournament_id = ?";
            try (var deleteRegistrationsStmt = connection.prepareStatement(deleteRegistrationsSQL)) {
                deleteRegistrationsStmt.setInt(1, 2);
                deleteRegistrationsStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // OCC & PCC reports
        List<String> occReport = occReport(occ);
        List<String> pccReport = pccReport(pcc);
        System.out.println("----------------------------");
        System.out.println("OCC report:");
        occReport.forEach(System.out::println);
        System.out.println("----------------------------");
        System.out.println("PCC report:");
        pccReport.forEach(System.out::println);
        System.out.println("----------------------------");
    }

    public static List<String> occReport(OptimisticConcurrencyControl occ) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int tournamentId = 2;
        int playerId = 1;

        AtomicInteger occSuccessfulTransactions = new AtomicInteger(0);
        AtomicInteger occRetries = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                boolean success = false;
                int localRetries = 0;
                while (!success) {
                    localRetries++;
                    success = occ.insertRegistration(tournamentId, playerId, occSuccessfulTransactions);
                }
                occRetries.addAndGet(localRetries);
            });
        }
        executor.shutdown();
        while (!executor.isTerminated()) { }
        long endTime = System.currentTimeMillis();

        return new ArrayList<>() {
            {
                add("Time taken: " + (endTime - startTime) + "ms");
                add("Total successful transactions: " + occSuccessfulTransactions.get());
                add("Total retries: " + occRetries.get());
            }
        };
    }

    public static List<String> pccReport(PessimisticConcurrencyControl pcc) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int matchId = 3;
        int winnerId = new Random().nextInt(2) + 1;

        AtomicInteger pccSuccessfulTransactions = new AtomicInteger(0);
        AtomicInteger pccLockWaitCounter = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> pcc.updateMatchResult(matchId, winnerId, pccSuccessfulTransactions, pccLockWaitCounter));
        }
        executor.shutdown();
        while (!executor.isTerminated()) { }
        long endTime = System.currentTimeMillis();

        return new ArrayList<>() {
            {
                add("Time taken: " + (endTime - startTime) + "ms");
                add("Total successful transactions: " + pccSuccessfulTransactions.get());
                add("Total lock wait counter: " + pccLockWaitCounter.get());
            }
        };
    }
}