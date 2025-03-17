# **Exercises: Practicing Denormalization and Partitioning**

## **Scenario: Global Online Store**

You are designing a **database for an e-commerce platform** that handles **millions of transactions per month** across different regions.

The system includes:

- **Customers** who make orders.
- **Orders** that contain multiple products.
- **OrderDetails** storing items in each order.
- **Products** available in multiple categories.
- **SalesRegions** tracking sales per geographic region.

---

## **Exercise 1: Denormalizing Total Sales per Order**

### **🎯 Problem Statement**

Currently, the database stores each **order and its items separately**. The total order value must be computed **using a `SUM()` query**, which slows down reporting.

### **✅ Task**

1. Modify the **`Orders`** table to store a **precomputed total amount**.
2. Write an **SQL statement** to update this field.
3. Discuss the **pros and cons** of this denormalization.

### **✅ Normalized Schema (Before)**

```sql
CREATE TABLE Orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    order_date DATE
);

CREATE TABLE OrderDetails (
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT,
    price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES Orders(order_id)
);
```

### **✅ Denormalized Schema (After)**

```sql
ALTER TABLE Orders ADD COLUMN total_amount DECIMAL(10,2);

UPDATE Orders o
SET total_amount = (
    SELECT SUM(quantity * price)
    FROM OrderDetails
    WHERE order_id = o.order_id
);
```

### **📌 Discussion Questions**

- What are the **performance benefits** of this approach?
- How should we ensure the **total_amount** stays **accurate** when an order is updated?

---

## **Exercise 2: Denormalizing Customer Data in Orders**

### **🎯 Problem Statement**

Order processing queries frequently need to fetch **customer information** (e.g., name, email) along with order details. Currently, this requires a **join** between `Customers` and `Orders`.

### **✅ Task**

1. Modify the `Orders` table to **embed customer details**.
2. Write an **UPDATE statement** to copy data from `Customers` to `Orders`.
3. Discuss the **downsides** of this approach.

### **✅ Normalized Schema (Before)**

```sql
CREATE TABLE Customers (
    customer_id INT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE Orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    order_date DATE,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);
```

### **✅ Denormalized Schema (After)**

```sql
ALTER TABLE Orders ADD COLUMN customer_name VARCHAR(100);
ALTER TABLE Orders ADD COLUMN customer_email VARCHAR(100);

UPDATE Orders o
JOIN Customers c ON o.customer_id = c.customer_id
SET o.customer_name = c.name, o.customer_email = c.email;
```

### **📌 Discussion Questions**

- When would this **denormalization** be useful?
- How should updates to `Customers` be **handled** in this case?

---

## **Exercise 3: Using Partitioning for Sales Data**

### **🎯 Problem Statement**

The **`Sales`** table has **millions of rows**, and querying data for a single **year** is slow.

### **✅ Task**

1. Modify the `Sales` table to use **partitioning by year**.
2. Write a **query** that benefits from partitioning.
3. Discuss when **partitioning is useful**.

### **✅ Normalized Schema (Before)**

```sql
CREATE TABLE Sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    region_id INT,
    sale_date DATE,
    total DECIMAL(10,2)
);
```

### **✅ Partitioned Schema (After)**

```sql
CREATE TABLE Sales (
    sale_id INT NOT NULL,
    region_id INT NOT NULL,
    sale_date DATE NOT NULL,
    total DECIMAL(10,2) NOT NULL
)
PARTITION BY RANGE (YEAR(sale_date)) (
    PARTITION p2021 VALUES LESS THAN (2022),
    PARTITION p2022 VALUES LESS THAN (2023),
    PARTITION p2023 VALUES LESS THAN (2024)
);
```

You can use these [test data](sales_partitioned.csv) to insert into the `Sales` table.

### **✅ Query Example (Efficient Query on Partitioned Table)**

```sql
SELECT * FROM Sales PARTITION (p2022) WHERE region_id = 5;
```

### **✅ Query Example Without the Partitioned Table**

```sql
SELECT * FROM Sales WHERE region_id = 5; --different query result
```

### **✅ Show Partitions on Database**

```sql
SELECT TABLE_NAME, PARTITION_NAME, TABLE_ROWS, PARTITION_METHOD
FROM information_schema.partitions
WHERE TABLE_SCHEMA = DATABASE();
```

### **📌 Discussion Questions**

- How does **partitioning** improve query speed?
- Why does MySQL **not allow foreign keys** in partitioned tables?
- What happens when a new **year starts**?

---

## **Exercise 4: Using List Partitioning for Regional Data**

### **🎯 Problem Statement**

The company operates in **multiple regions** (EU, US, Asia). Queries on **a single region’s sales** are slow because **all data is stored in one table**.

### **✅ Task**

1. Modify the `Sales` table to use **list partitioning** by region.
2. Write a **query that benefits from this partitioning**.
3. Discuss when **list partitioning** is preferred over range partitioning.

### **✅ Partitioned Schema**

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

You can use these [test data](sales_list_partitioned.csv) to insert into the `Sales` table.

### **✅ Query Example (Efficient Regional Query)**

```sql
SELECT * FROM Sales PARTITION (pEU) WHERE sale_date BETWEEN '2023-01-01' AND '2023-12-31';
```

### **📌 Discussion Questions**

- What **types of queries** does list partitioning optimize?
- What if a **new region** needs to be added?
- How does **list partitioning compare to range partitioning**?

---

## **Exercise 5: Checking Query Performance with Partitioning**

[See this guide](checking_query_performance_partitioning.md)
