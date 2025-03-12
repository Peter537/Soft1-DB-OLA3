## Part 1: Optimistic & Pessimistic Concurrency Control

## **Scenario: Esports Tournament System**

The esports platform manages players, tournaments, match results, and registrations. Concurrency issues arise when multiple users try to register for tournaments or update match results simultaneously.

You will implement **concurrency control mechanisms** to prevent concurrency problems.

### **📌 Database Schema**

```sql
CREATE TABLE Players (
    player_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    ranking INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Tournaments (
    tournament_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    game VARCHAR(50) NOT NULL,
    max_players INT NOT NULL,
    start_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Tournament_Registrations (
    registration_id INT PRIMARY KEY AUTO_INCREMENT,
    tournament_id INT NOT NULL,
    player_id INT NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES Tournaments(tournament_id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES Players(player_id) ON DELETE CASCADE
);

CREATE TABLE Matches (
    match_id INT PRIMARY KEY AUTO_INCREMENT,
    tournament_id INT NOT NULL,
    player1_id INT NOT NULL,
    player2_id INT NOT NULL,
    winner_id INT NULL,
    match_date DATETIME NOT NULL,
    FOREIGN KEY (tournament_id) REFERENCES Tournaments(tournament_id) ON DELETE CASCADE,
    FOREIGN KEY (player1_id) REFERENCES Players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (player2_id) REFERENCES Players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (winner_id) REFERENCES Players(player_id) ON DELETE SET NULL
);
```

---

## **📖 Read this First**

Read [this text](application-concurrency-note.md) first that describes and illustrates how application code can handle concurrency issues by means of optimistic and pessimistic concurrency strategies.

## **📌 Exercises**

### **1️⃣ Implement Optimistic Concurrency Control for Tournament Registration**

📌 **Problem:** Two players attempt to register for the same tournament at the same time. If the **max_players** limit is reached, one should be rejected.

✅ **Task:**

- Add a **version column** to `Tournaments`.
- Implement **version-based optimistic concurrency control** in Java using JDBC.
- Ensure that only one registration is successful when two concurrent users try to register.

#### **Example: Version Column for Optimistic Concurrency Control**

```sql
ALTER TABLE Tournaments ADD COLUMN version INT NOT NULL DEFAULT 1;
```

#### **Java Hint: Optimistic Locking**

```java
String query = "UPDATE Tournaments SET version = version + 1 WHERE tournament_id = ? AND version = ?";
```

---

### **2️⃣ Implement Pessimistic Concurrency Control for Match Updates**

📌 **Problem:** Two admins attempt to update the **same match result** at the same time. Ensure only one update happens at a time.

✅ **Task:**

- Implement **pessimistic locking** using `SELECT ... FOR UPDATE`.
- Ensure only one admin can update match results at a time.

#### **Example: Pessimistic Locking Query**

```sql
SELECT * FROM Matches WHERE match_id = 1 FOR UPDATE;
```

---

### **3️⃣ Handle Transactions for Tournament Registrations**

📌 **Problem:** Ensure **atomicity** when registering a player in a tournament. If any part of the transaction fails, rollback all changes.

✅ **Task:**

- If registration is successful, insert a record into `Tournament_Registrations` and update player ranking.
- If the tournament is full, **rollback the transaction**.

#### **Java Hint: Managing Transactions**

```java
conn.setAutoCommit(false);
try {
    // Insert registration
    // Update player stats
    conn.commit();
} catch (SQLException e) {
    conn.rollback();
}
```

---

### **4️⃣ Implement a Stored Procedure for Safe Ranking Updates**

📌 **Problem:** A player’s **ranking** should increase after winning a match. Ensure **concurrent updates do not cause inconsistencies**.

✅ **Task:**

- Create a **stored procedure** that updates player ranking.
- Use **pessimistic locking** to prevent simultaneous updates.

#### **Example: Stored Procedure for Ranking Updates**

```sql
DELIMITER $$
CREATE PROCEDURE UpdateRanking(IN playerID INT)
BEGIN
    START TRANSACTION;
    UPDATE Players SET ranking = ranking + 10 WHERE player_id = playerID;
    COMMIT;
END $$
DELIMITER ;
```

✅ **Call Procedure in Java:**

```java
CallableStatement stmt = conn.prepareCall("CALL UpdateRanking(?)");
stmt.setInt(1, playerID);
stmt.execute();
```

---

### **5️⃣ Compare Optimistic vs. Pessimistic Concurrency Control**

📌 **Problem:** Run a performance test comparing Optimistic and Pessimistic Concurrency Control under heavy load.

✅ **Task:**

- Simulate concurrent updates.
- Measure and compare **transaction latency**.
- Analyze when **Optimistic and Pessimistic Concurrency Control is better**.

To run a performance test comparing the two concurrency strategies under heavy load, you can find inspiration in these [guidelines](performance-test.md).

You must document your finding in a Performance comparison report. Use this [concurrency report template](concurrency-report-template.md)

---

## **🚀 Submission Requirements**

1️⃣ Application implementations for **Optimistic and Pessimistic Concurrency Control**.  
2️⃣ SQL scripts for table creation and stored procedures.  
3️⃣ **Performance comparison report** between Optimistic and Pessimistic Concurrency Control.
