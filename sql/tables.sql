--Each value stored in an SQLite database (or manipulated by the database engine) has one of the following storage classes:
--NULL. The value is a NULL value.
--INTEGER. The value is a signed integer, stored in 1, 2, 3, 4, 6, or 8 bytes depending on the magnitude of the value.
--REAL. The value is a floating point value, stored as an 8-byte IEEE floating point number.
--TEXT. The value is a text string, stored using the database encoding (UTF-8, UTF-16BE or UTF-16LE).
--BLOB. The value is a blob of data, stored exactly as it was input.
CREATE TABLE mall (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, hours TEXT, address TEXT, phone TEXT);
CREATE TABLE store (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, category INTEGER);
CREATE TABLE mall_store (mall_id INTEGER PRIMARY KEY, store_id INTEGER PRIMARY KEY, phone TEXT);
