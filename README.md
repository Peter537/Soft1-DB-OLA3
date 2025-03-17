# Database OLA3

## Gruppe

- Oskar (Ossi-1337, cph-oo221)
- Peter (Peter537, cph-pa153)
- Yusuf (StylizedAce, cph-ya56)

---

## Part 1

Vores database og noget start data er sat op i [database-schema.sql](./database-schema.sql)

Vores rapport er lavet i [concurrency-report.md](./concurrency-report.md)

---

## Part 2

Vores opgave omkring Denormalisation & Partitioning er lavet i [denormalisation-partitioning.md](./denormalisation-partitioning.md)

Vores opgave omkring Query Optimization er lavet i [query-optimization.md](./query-optimization.md)

### Reflektionsnote

When considering when to use Query Optimization, it is useful to know how large your dataset is, since if we have a small dataset then the performance gain is not significant compared to how we otherwise can spend our time, which in a business sense is more valuable. Though if we have a large dataset and we (either ourself or users) have seen that our database is slow, then we can optimize the performance by using stuff like indexing, partitioning or JOINs.

Denormalization is a smart thing to do if we use the same data often, since we can save time by not having to JOIN tables to get the data we need. Though we have to be careful with denormalization, since we can get data inconsistency if we don't update all the tables that have the same data. We can use triggers to update the data in all the tables that have the same data, but this can be a hassle to maintain.

In Partitioning, if our partitioning key are changing a lot, it can be difficult to maintain it correctly, but if they stay the same (for instance regions like Region Hovedstaden), then it can be a good idea to use partitioning.

We are using PostgreSQL, and from what we have seen, the main differences between MySQL and PostgreSQL is the syntax.
