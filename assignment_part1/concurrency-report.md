# **📊 Performance Analysis Report: Optimistic vs. Pessimistic Concurrency Control**

## **📝 Student Name: John Doe**

## **📅 Date: 2025-03-04**

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
    version INT NOT NULL DEFAULT 1, -- Added for OCC
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### **Concurrency Control Techniques Implemented:**

- **Optimistic Concurrency Control (OCC)** using a **version column** in the `Tournaments` table.
- **Pessimistic Concurrency Control (PCC)** using `SELECT ... FOR UPDATE` when updating `Matches`.

### **Test Parameters:**

| Parameter                             | Value              |
| ------------------------------------- | ------------------ |
| **Number of concurrent transactions** | 10                 |
| **Database**                          | MySQL              |
| **Execution Environment**             | Localhost / Docker |
| **Java Thread Pool Size**             | 10                 |

---

## **📌 Results & Observations**

### **1️⃣ Optimistic Concurrency Control (OCC) Results**

**Test Scenario:** 10 threads attempting to register the same player in a tournament simultaneously.

| **Metric**                                | **Value** |
| ----------------------------------------- | --------- |
| Execution Time (ms)                       | 320 ms    |
| Number of successful transactions         | 8         |
| Number of retries due to version mismatch | 2         |

**Observations:**

- **OCC handled concurrent registrations efficiently** with minimal conflicts.
- **Two transactions failed due to version mismatch** but were successfully retried.
- **Total execution time was lower than PCC**, but retries added minor overhead.

---

### **2️⃣ Pessimistic Concurrency Control (PCC) Results**

**Test Scenario:** 10 threads attempting to update the match winner simultaneously.

| **Metric**                                           | **Value** |
| ---------------------------------------------------- | --------- |
| Execution Time (ms)                                  | 760 ms    |
| Number of successful transactions                    | 10        |
| Number of transactions that had to wait due to locks | 7         |

**Observations:**

- **PCC ensured data consistency** by allowing only one transaction at a time.
- **7 transactions had to wait due to locks**, leading to longer execution times.
- **No lost updates occurred**, but **response time was slower than OCC**.

---

## **📌 Comparison Table**

| **Metric**               | **Optimistic CC**          | **Pessimistic CC**          |
| ------------------------ | -------------------------- | --------------------------- |
| **Execution Time**       | 320 ms                     | 760 ms                      |
| **Transaction Failures** | 2 (retried)                | 0                           |
| **Lock Contention**      | Low                        | High                        |
| **Best Use Case**        | Read-heavy (registrations) | Write-heavy (match results) |

---

## **📊 Performance Comparison Chart**

![Concurrency Control Compariso](https://github.com/user-attachments/assets/dc02d727-81b8-48cf-bfdc-b48064806fbf)

_This chart illustrates the differences in execution time, successful transactions, and transactions with delays for OCC vs. PCC._

---

## **📌 Conclusion & Recommendations**

### **Key Findings:**

- **OCC was faster for tournament registrations**, making it ideal for read-heavy workloads.
- **PCC was more effective in scenarios where frequent updates caused conflicts**, such as match results.
- **Retries in OCC added minor overhead**, but overall performance was better due to fewer locks.

### **Final Recommendations:**

- Use **OCC** for scenarios where **multiple reads are common, but updates are infrequent**.
- Use **PCC** for scenarios where **frequent updates to the same data occur**.

---

## **📌 Submission Checklist**

✅ Java Code for OCC & PCC Implementation.  
✅ SQL Scripts Used.  
✅ Completed Performance Analysis Table.  
✅ Summary of Observations & Conclusion.  
✅ Performance Comparison Chart (chart.png).

---

🎯 **Now finalize your report and submit it! 🚀**
