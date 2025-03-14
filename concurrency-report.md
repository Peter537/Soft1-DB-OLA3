# **Performance Analysis Report: Optimistic vs. Pessimistic Concurrency Control**

## **📝 Student Names: Oskar, Peter og Yusuf**

---

## **📌 Introduction**

### **Objective:**

This report analyzes and compares the performance of **Optimistic Concurrency Control (OCC) vs. Pessimistic Concurrency Control (PCC)** when handling concurrent transactions in an Esports Tournament database.

### **Scenario Overview:**

- **OCC is tested** by simulating multiple players registering for the same tournament concurrently.
- **PCC is tested** by simulating multiple administrators updating the same match result simultaneously.

---

## **📌 Experiment Setup**

### **Database Schema Used:**

```sql
CREATE TABLE players (
    player_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    ranking INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tournaments (
    tournament_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    game VARCHAR(50) NOT NULL,
    max_players INT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tournament_registrations (
    registration_id SERIAL PRIMARY KEY,
    tournament_id INT NOT NULL,
    player_id INT NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE,
);

CREATE TABLE matches (
    match_id SERIAL PRIMARY KEY,
    tournament_id INT NOT NULL,
    player1_id INT NOT NULL,
    player2_id INT NOT NULL,
    winner_id INT NULL,
    match_date TIMESTAMP NOT NULL,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    FOREIGN KEY (player1_id) REFERENCES players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (player2_id) REFERENCES players(player_id) ON DELETE CASCADE,
    FOREIGN KEY (winner_id) REFERENCES players(player_id) ON DELETE SET NULL
);

ALTER TABLE tournaments ADD COLUMN version INT NOT NULL DEFAULT 1;
```

### **Concurrency Control Techniques Implemented:**

- **Optimistic Concurrency Control (OCC)** using a **version column** in the `Tournaments` table.
- **Pessimistic Concurrency Control (PCC)** using `SELECT ... FOR UPDATE` when updating `Matches`.

### **Test Parameters:**

| Parameter                             | Value              |
| ------------------------------------- |--------------------|
| **Number of concurrent transactions** | 10                 |
| **Database**                          | PostgreSQL         |
| **Execution Environment**             | Localhost / Docker |
| **Java Thread Pool Size**             | 10                 |

---

## **📌 Results & Observations**

### **1️⃣ Optimistic Concurrency Control (OCC) Results**

**Test Scenario:** [Describe how OCC was tested]

| **Metric**                                | **Value** |
| ----------------------------------------- |-----------|
| Execution Time (ms)                       | 478ms     |
| Number of successful transactions         | 8         |
| Number of retries due to version mismatch | 54        |

**Observations:**

- [Summarize key findings related to OCC]

---

### **2️⃣ Pessimistic Concurrency Control (PCC) Results**

**Test Scenario:** [Describe how PCC was tested]

| **Metric**                                           | **Value** |
| ---------------------------------------------------- |-----------|
| Execution Time (ms)                                  | 120ms     |
| Number of successful transactions                    | 10        |
| Number of transactions that had to wait due to locks | 9         |

**Observations:**

- [Summarize key findings related to PCC]

---

## **📌 Comparison Table**

| **Metric**               | **Optimistic CC** | **Pessimistic CC** |
| ------------------------ |------------------|--------------------|
| **Execution Time**       | 478ms            | 120ms              |
| **Transaction Failures** | 2 (retries)      | 0                  |
| **Lock Contention**      | Low              | High               |
| **Best Use Case**        | Reading data     | Inserting data     |

---

## **Performance Comparison Chart**

_You *may* want to visualize your finding by including a chart that illustrates the differences in execution time, successful transactions, and transactions with delays for OCC vs. PCC._

![Performance Comparison](./images/concurrency_report_comparison.png)

---

## **📌 Conclusion & Recommendations**

### **Key Findings:**

- [Summarize overall findings and comparison of OCC vs. PCC]

### **Final Recommendations:**

- [Provide recommendations based on the test results]
