# **🔍 Checking Query Performance with Partitioning**

This guide explains how to use **`EXPLAIN`** and **`EXPLAIN ANALYZE`** to check query performance improvements when using **partitioning**.

# Difference Between Explain And Explain Analyze

The `EXPLAIN` statement allows you to gain some insight into the performance of a query. You may hear `EXPLAIN` and `EXPLAIN ANALYZE` referred to
interchangeably. Though they can both be used to explore how a query will perform, it's important to know a key difference. `EXPLAIN ANALYZE`
executes the query, `EXPLAIN` does not.

For `SELECT` queries, the distinction may not feel that important. For `INSERT`, `UPDATE`, and `DELETE`, you'll want to be clear about which one
you are using.

With `EXPLAIN`, you get cost estimates of the `insert` statement.

With `EXPLAIN ANALYZE`, you get estimates and actual numbers. You also get a row inserted in the table.

---

## **📌 1. Running Queries on Partitioned Data**

We use the **Sales table partitioned by region**:

```sql
CREATE TABLE Sales (
    sale_id INT NOT NULL,
    region VARCHAR(10) NOT NULL,
    sale_date DATE NOT NULL,
    total DECIMAL(10,2) NOT NULL
)
PARTITION BY LIST COLUMNS (region) (
    PARTITION pUS VALUES IN ('US'),
    PARTITION pEU VALUES IN ('EU'),
    PARTITION pASIA VALUES IN ('Asia')
);
```

Now, compare query execution **with and without partitioning**.

---

## **📌 2. Running `EXPLAIN ANALYZE`**

This query searches sales data **without specifying a partition**:

```sql
EXPLAIN ANALYZE
SELECT * FROM Sales
WHERE region = 'EU'
AND sale_date BETWEEN '2023-01-01' AND '2023-12-31';
```

### **Expected Outcome**

- The query **scans the entire table**.
- The **rows examined** count will be **high**, especially for large datasets.

---

## **📌 3. Running `EXPLAIN ANALYZE` With Partition Selection**

Now, run the query by **targeting a specific partition**:

```sql
EXPLAIN ANALYZE
SELECT * FROM Sales PARTITION (pEU)
WHERE sale_date BETWEEN '2023-01-01' AND '2023-12-31';
```

### **Expected Improvement**

- The **rows examined** count should be **significantly lower**.
- The **query execution time** should be **faster**.

---

## **📌 4. Key Metrics to Compare**

After running the above queries, compare the results:

| **Metric**           | **Without Partitioning** | **With Partitioning**                  |
| -------------------- | ------------------------ | -------------------------------------- |
| **Rows Examined**    | 🔴 High (Full Scan)      | 🟢 Low (Partition Scan)                |
| **Execution Time**   | 🔴 Slower                | 🟢 Faster                              |
| **Index Usage**      | 🟠 May use index         | 🟢 Efficient indexing within partition |
| **Query Complexity** | 🟠 More costly joins     | 🟢 Simpler & optimized                 |

---

## **📌 5. Viewing Query Execution Plan in MySQL Workbench**

### **Graphical Query Plan**

**MySQL Workbench** provides a **graphical execution plan** that makes it easier to read `EXPLAIN` results.

### **✅ Steps to View the Execution Plan**

1. **Write Your Query**
   ```sql
    SELECT * FROM Sales WHERE region = 'EU' AND sale_date BETWEEN '2023-01-01' AND '2023-12-31';
   ```
2. **Choose Query --> [Explain Current Statement](https://dev.mysql.com/doc/workbench/en/wb-performance-query-statistics.html)**

   - This will show the execution plan **graphically**.

3. **Analyze the Graphical Plan**
   - Displays:
     - **Table Scans** (Full Table vs. Indexed Search).
     - **Partitions Used**.
     - **Estimated Query Cost**.
   - Helps **verify if partitions are used correctly**.

---

## **📌 6. Alternative: Using `FORMAT=JSON` for Readability**

If you prefer a **detailed textual explanation**, use:

```sql
EXPLAIN FORMAT=JSON
SELECT * FROM Sales WHERE region = 'EU' AND sale_date BETWEEN '2023-01-01' AND '2023-12-31';
```
