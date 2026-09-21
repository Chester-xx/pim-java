DROP DATABASE IF EXISTS pim;
CREATE DATABASE pim;
USE pim;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

CREATE TABLE suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

CREATE TABLE medicines (
    medicine_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    company VARCHAR(100),
    medicine_type VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 0,
    expiry_date DATE,
    supplier_id INT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

CREATE TABLE sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE sale_items (
    sale_item_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity_sold INT NOT NULL,
    price_at_sale DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id),
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
);

-- Seed Data --
INSERT INTO users (username, password, role, full_name) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Admin', 'System Administrator'),
('cashier', 'c246650737293ddc18fc357393db78d1ecc9d1fd1af95469115e4a29f983359a', 'Cashier', 'Default Cashier');

INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('Checkers', 'John Naidoo', '0110001111', 'john@checkers.co.za', '12 Main Rd, Johannesburg'),
('Dischem', 'Sarah Roberts', '0119992222', 'sarah@dischem.co.za', '45 Health St, Pretoria');

INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Panado', 'Adcock Ingram', 'Tablet', 25.99, 150, 20, '2027-06-30', 1),
('Nasaline', 'Dischem', 'Spray', 89.50, 60, 15, '2026-11-15', 1),
('Benylin Cough Syrup', 'Johnson & Johnson', 'Syrup', 65.00, 40, 10, '2026-10-01', 2),
('Insulin Injection', 'Novo Nordisk', 'Injection', 210.00, 25, 5, '2026-09-30', 2),
('Betamethasone Cream', 'GSK', 'Cream', 45.75, 30, 10, '2027-01-20', 1);

INSERT INTO users (username, password, role, full_name) VALUES
('jsmith', '008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'Cashier', 'Jane Smith'),
('mberg', '004a6b81e6bad703d1f8fd933a9fc494556dba50fb6f15e2570894a5dc498e38', 'Admin', 'Max Berg');

INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('Pharmacare', 'Max Bergmann', '0123456789', 'maxbergmann@pharma.co.za', '78 Industrial Ave, Durban'),
('CapeMed Distributors', 'Sergio Daniels', '0219876543', 'sdaniels@capemed.co.za', '5 Harbour Rd, Cape Town');

INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Aspirin for my headaches', 'Bayer', 'Tablet', 15.50, 5, 20, '2026-10-05', 3),
('Ibuprofen', 'Pharmaco', 'Tablet', 22.00, 8, 15, '2027-03-15', 3),
('Ventolin Syrup', 'GSK', 'Syrup', 55.00, 0, 10, '2027-05-01', 4),
('Paracetamol Suspension', 'Adcock Ingram', 'Syrup', 32.00, 100, 20, '2026-12-31', 1),
('Metformin', 'Aspen', 'Tablet', 45.00, 12, 15, '2026-09-25', 3),
('Hydrocortisone Cream', 'CapeMed', 'Cream', 38.50, 60, 10, '2027-08-10', 4),
('Vitamin C', 'PharmaCorp', 'Tablet', 18.00, 200, 30, '2028-01-01', 2),
('Loraadine', 'Bayer', 'Tablet', 27.50, 18, 20, '2026-11-20', 3);

INSERT INTO sales (sale_date, total_amount, user_id) VALUES
('2026-09-18 09:15:00', 108.97, 2),
('2026-09-19 11:40:00', 65.00, 2),
('2026-09-20 14:05:00', 404.00, 3),
('2026-09-20 16:30:00', 180.00, 3);

INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES
(1, 1, 3, 25.99),
(1, 6, 2, 15.50),
(2, 3, 1, 65.00),
(3, 9, 5, 45.00),
(3, 2, 2, 89.50),
(4, 11, 10, 18.00);
