CREATE DATABASE IF NOT EXISTS library_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_management;
-- JPA can create and update the tables. This schema also supports manual review.
CREATE TABLE IF NOT EXISTS books (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(150) NOT NULL, author VARCHAR(150) NOT NULL,
 category VARCHAR(80) NOT NULL, isbn VARCHAR(30), availability_status VARCHAR(20) NOT NULL
);
CREATE TABLE IF NOT EXISTS members (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(150) NOT NULL, email VARCHAR(180) NOT NULL,
 phone VARCHAR(20) NOT NULL, registration_date DATE NOT NULL
);
CREATE TABLE IF NOT EXISTS loans (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, book_id BIGINT NOT NULL, member_id BIGINT NOT NULL,
 issue_date DATE NOT NULL, due_date DATE NOT NULL, return_date DATE NULL,
 fine_amount DECIMAL(12,2) NOT NULL DEFAULT 0, status VARCHAR(20) NOT NULL,
 CONSTRAINT fk_loan_book FOREIGN KEY (book_id) REFERENCES books(id),
 CONSTRAINT fk_loan_member FOREIGN KEY (member_id) REFERENCES members(id)
);
CREATE INDEX IF NOT EXISTS idx_loan_status ON loans(status);
CREATE TABLE IF NOT EXISTS admin_users (
 id BIGINT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(100) NOT NULL UNIQUE, password_hash VARCHAR(255) NOT NULL
);
