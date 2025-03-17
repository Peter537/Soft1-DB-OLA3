# Denormalisation & Partitioning

## Exercise 1

The performance benefits are that you don't have to JOIN tables to get a total_amount from an order, which can be useful if you need to query the total amount often instead of calculating it each time.

To ensure that total_amount is accurate, we can for instance use triggers to update the total amount each time something is added, deleted or updated in the order details table.

## Exercise 2

It would be useful to denormalize this when it is a common query.

We could have a trigger on the Customers table that when their name or email is updated, it updates the Orders table with the new name or email.

## Exercise 3

It's like querying in a smaller table, so it's faster.

It doesn't support foreign keys because it would introduce very complex referential problems, because foreign keys must be referenced from a separate table. Partitions are in essence smaller tables.

When a new year happens, we have to go in the database and manually create a new Partinion, which is a bit of a hassle and we have to maintain it.

## Exercise 4

- What types of queries does list partitioning optimize?

The types of queries that list partitioning optimizes are queries that are looking for a specific value in a column that is used for partitioning.

- What if a new region needs to be added?

Then we have to add it in the database manually.

- How does list partitioning compare to range partitioning?

List partitioning is when you pick out specific attribute(s) to check for, and range partitioning is when you check for a range of values.
