# PIMS - Pharmacy Inventory Management System
### Java Swing Desktop Application
Java desktop pharmacy management system, using a MySQL instance backed by Workbench and Maven dependencies. Allows basic functionality of managing medicine inventory, suppliers, cashier sales (POS), and reporting.

## Dependencies

- JDK 26
- Apache Maven
- MySQL Server 8.0
- MySQL Workbench to run `database/database.sql`

## Setup

1. Start MySQL Server
2. Run `database/database.sql` from MySQL Workbench (creates the `pim` schema, tables, and seed data)
3. Change NB: `src/db/DBConnection.java` variable `DB_PASSWORD` to needed database password
4. Run via `mvn clean compile exec:java`

## Default Login Credentials

| Username | Password | Role    |
|----------|----------|---------|
| admin    | admin123 | Admin   |
| cashier  | cash123  | Cashier |

## Repository Structure

```
pharmacy-inventory-manager/
├── pom.xml                             # maven build config & dependencies
│
├── src/
│   ├── Main.java                       # entry point
│   │
│   ├── db/
│   │   └── DBConnection.java           # JDBC connection layer
│   │
│   ├── model/
│   │   ├── User.java                   # user model
│   │   ├── Supplier.java               # supplier model
│   │   ├── Medicine.java               # medicine model
│   │   ├── Sale.java                   # sale model
│   │   ├── SaleItem.java               # sale line item model
│   │   └── ItemSalesSummary.java       # aggregated report row
│   │
│   ├── service/                        # service models for business logic
│   │   ├── UserService.java    
│   │   ├── SupplierService.java
│   │   ├── MedicineService.java
│   │   └── SalesService.java   
│   │
│   ├── ui/
│   │   ├── Theme.java                  # Theme model
│   │   ├── LoginFrame.java             # login screen
│   │   ├── AdminDashboard.java         # admin tab
│   │   ├── CashierDashboard.java       # cashier point of sale
│   │   ├── MedicineManagementPanel.java
│   │   ├── SupplierManagementPanel.java
│   │   ├── UserManagementPanel.java
│   │   ├── POSPanel.java               # cashier search/cart/checkout
│   │   ├── ReceiptDialog.java          # printable receipt
│   │   ├── ReportsPanel.java           # reports container
│   │   ├── LowStockReportPanel.java
│   │   ├── ExpiringSoonReportPanel.java
│   │   ├── ItemWiseSalesReportPanel.java
│   │   └── SalesByDateRangeReportPanel.java
│   │
│   └── util/
│       └── PasswordUtil.java           # password hashing
│
└── database/
    └── database.sql                    # db schema + seed data
```

## Features

**Admin**
- Manage Medicines, Suppliers, and Users (CRUD), each with search
- Cannot change the role of another Admin, only their own
- View Low Stock, Expiring Soon, Item-Wise Sales, and Sales by Date Range reports

**Cashier**
- Search medicines and add to cart
- Checkout generates a sale record and printable receipt
- Stock is checked against live data and reduced automatically at checkout

## Notes

- Passwords are hashed with SHA-256 before storage, never stored in plain text
- An Admin who changes their own role to Cashier is force logged out
- Stock reduction after checkout is a separate call from the sale record insert, not one shared transaction
