# **Performance Test: Optimistic vs. Pessimistic Concurrency Control (OCC vs. PCC)**

You can use **[Java's `ExecutorService`](https://www.baeldung.com/java-executor-service-tutorial)** to create multiple threads that execute transactions concurrently.

### **🔹 Step 1: Simulating Heavy Load for OCC**

Optimistic Concurrency Control allows multiple transactions to proceed simultaneously but checks for conflicts at commit time.

1️⃣ Create multiple threads that attempt to register the same player in a tournament.

2️⃣ Each thread:

- Reads the current tournament version.
- Tries to **register a player** using OCC logic (`UPDATE ... WHERE version = ?`).
- If the update fails, it retries the operation. 3️⃣ Measure time taken and log failed attempts.

**Java Code: OCC Simulation**

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 10; i++) {
    executor.submit(() -> {
        boolean success = false;
        while (!success) {
            success = registerPlayerOptimisticConcurrency(tournamentId, playerId);
        }
    });
}
executor.shutdown();
```

### **🔹 Step 2: Simulating Heavy Load for PCC**

Pessimistic Concurrency Control locks the row when a transaction starts, preventing other transactions from proceeding.

1️⃣ Create multiple threads attempting to update the same match result.
2️⃣ Each thread:

- Executes a `SELECT ... FOR UPDATE` to lock the row.
- Updates the match result.
- Commits the transaction. 3️⃣ Measure how long transactions wait due to locks.

**Java Code: PCC Simulation**

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 10; i++) {
    executor.submit(() -> updateMatchResultPessimistically(matchId, winnerId));
}
executor.shutdown();
```

### **🔹 Step 3: Measure and Compare Performance**

After running both simulations:

- **Log the execution time** for each approach.
- **Count how many transactions failed and had to retry** (OCC).
- **Measure how many transactions had to wait** (PCC).

## **📌 Performance Comparison Table**

| **Metric**                   | **Optimistic CC (OCC)**             | **Pessimistic CC (PCC)**            |
| ---------------------------- | ----------------------------------- | ----------------------------------- |
| **Execution Time**           | Fast/Slower? (Why)                  | Fast/Slower? (Why)                  |
| **Transaction Success Rate** | Lower/Higher? (Why)                 | Lower/Higher? (Why)                 |
| **Lock Contention**          | None/Low/High?                      | None/Low/High?                      |
| **Best Use Case**            | Read-heavy/Write-heavy? (use cases) | Read-heavy/Write-heavy? (use cases) |
