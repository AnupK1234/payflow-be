INSERT INTO users (username, email, password_hash, role, must_reset_password, enabled)
VALUES ('bank-admin', 'bankadmin@example.com', '$2a$10$E8xD2.LVoDlKFm5qlJmejeJSy.QKr.UbmAfiP9l5EG46MCjt13uPu', 'BANK_ADMIN', 0, 1);


Password: Admin@123



Add the following to Run Configuration Environment:
DB_PASSWORD = your mysql password
DB_USER = your mysql username
DB_URL = jdbc:mysql://localhost:3306/payflow?createDatabaseIfNotExist=true
APP_JWT_SECRET = 024f79e5cb4e467cb87832900ad41b6c




this is written by pragati