## **📌 Query Optimization Exercises**

### **Exercise 1: Optimizing a Subquery into a Join**

#### **Task:**

The following query is slow because it uses a subquery. Rewrite it as a **JOIN** to improve performance.

```sql
SELECT order_id, total_amount,
       (SELECT name FROM Customers WHERE customer_id = Orders.customer_id) AS customer_name
FROM Orders
WHERE total_amount > 100;
```

✅ **Goal:**

- Convert the query to **use a JOIN**.
- Use `EXPLAIN` to verify **index usage and row scans**.

---

### **Exercise 2: Optimizing a JOIN by Using an Index**

#### **Task:**

The following query is slow because the **join condition is missing an index**.

1. **Check its execution plan (`EXPLAIN`)**.
2. **Create an appropriate index**.
3. **Rerun the query and compare performance**.

```sql
SELECT o.order_id, o.total_amount, c.name
FROM Orders o
JOIN Customers c ON o.customer_id = c.customer_id
WHERE o.order_date > '2023-01-01';
```

✅ **Goal:**

- Create an index on `Orders.customer_id`.
- Compare **query performance before and after indexing**.

---

### **Exercise 3: Identifying and Fixing the N+1 Query Problem**

#### **Task:**

Below is an inefficient way of retrieving data that causes an **N+1 Query Problem**.

```sql
-- Fetch all orders first
SELECT order_id, customer_id FROM Orders WHERE order_date > '2023-01-01';

-- Then, for each order, fetch customer name separately
SELECT name FROM Customers WHERE customer_id = 1;
SELECT name FROM Customers WHERE customer_id = 2;
SELECT name FROM Customers WHERE customer_id = 3;
...
```

✅ **Goal:**

- Identify why the **N+1 Query Problem happens**.
- Rewrite it **using a single JOIN query**.

---

### **Exercise 4: Optimizing Aggregate Queries with Indexes**

#### **Scenario:**

A database stores **product sales** with the following tables:

**Products Table**

```sql
CREATE TABLE Products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);
```

**Sales Table**

```sql
CREATE TABLE Sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    sale_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);
```

**❌ Problem: Slow Aggregate Query**
The following query **aggregates sales by category**, but it runs slowly on large datasets.

```sql
SELECT p.category, SUM(s.total_amount) AS total_sales
FROM Sales s
JOIN Products p ON s.product_id = p.product_id
GROUP BY p.category;
```

**✅ Tasks**

**1️⃣ Analyze Query Performance**

- Use `EXPLAIN` to **check execution plan**:
  ```sql
  EXPLAIN SELECT p.category, SUM(s.total_amount) AS total_sales
  FROM Sales s
  JOIN Products p ON s.product_id = p.product_id
  GROUP BY p.category;
  ```
- Identify **how many rows are scanned** and **whether indexes are used**.

**2️⃣ Identify Missing Indexes**

- Check if an **index on `product_id`** exists in the `Sales` table.
- Consider **creating an index** on `category` for faster `GROUP BY` operations.

**3️⃣ Optimize the Query Using Indexes**

- Add an **index to improve performance**:
  ```sql
  CREATE INDEX idx_product_id ON Sales(product_id);
  CREATE INDEX idx_category ON Products(category);
  ```
- Rerun the query and **compare execution times**.

**4️⃣ Bonus: Use a Covering Index**

- Create a **covering index** for the query:
  ```sql
  CREATE INDEX idx_sales_optimized ON Sales(product_id, total_amount);
  ```
- Test if it **further improves performance**.

---

## **🚀 Expected Outcome**

After optimization, the query should:

✅ **Run faster with fewer scanned rows**  
✅ **Use indexes for improved performance**  
✅ **Reduce full table scans**

## **📌 Summary: Best Practices**

| Query Type         | Performance                 | Best Use Case                                     |
| ------------------ | --------------------------- | ------------------------------------------------- |
| ✅ **JOIN**        | **Fast** 🚀                 | When retrieving related data from multiple tables |
| ❌ **Subquery**    | **Slow (if correlated)** 🐢 | Only when aggregating unique results              |
| ❌ **N+1 Problem** | **Very Slow** 🔥            | Fix it with **joins** or **batch queries**        |
