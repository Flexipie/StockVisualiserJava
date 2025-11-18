# Stock Visualiser - Portfolio Management System

**Computer Programming 2 - Group Project**  
**JavaFX Desktop Application with SQLite Database**

---

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Technical Architecture](#technical-architecture)
- [Requirements Checklist](#requirements-checklist)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [Default Login Credentials](#default-login-credentials)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Design Patterns & OOP Principles](#design-patterns--oop-principles)
- [Database Schema](#database-schema)
- [Screenshots & Demo](#screenshots--demo)

---

## 🎯 Project Overview

Stock Visualiser is a comprehensive desktop portfolio management system built with JavaFX and SQLite. The application allows users to manage stock portfolios, execute buy/sell transactions, track performance with interactive charts, and maintain a watchlist of stocks.

The system supports two user roles:
- **Traders/Investors**: Manage personal portfolios, execute transactions, view analytics
- **Administrators**: All trader features plus ability to add new stocks to the system

---

## ✨ Features

### User Authentication & Management
- ✅ Secure login with BCrypt password hashing
- ✅ User registration with role-based access control (Admin/Trader)
- ✅ Session management

### Portfolio Management (CRUD Operations)
- ✅ View all stock holdings with real-time profit/loss calculations
- ✅ Buy stocks with confirmation dialogs
- ✅ Sell stocks with quantity validation
- ✅ Automatic portfolio updates with weighted average pricing

### Stock Marketplace
- ✅ Browse all available stocks
- ✅ **Real-time search/filter** by symbol, company name, or sector
- ✅ View current stock prices and company information
- ✅ Add stocks to watchlist

### Transaction History
- ✅ Complete transaction log with date/time stamps
- ✅ **Real-time search/filter** for transactions
- ✅ Buy/Sell transaction tracking
- ✅ Transaction details including price per share and total amount

### Analytics & Data Visualization
- ✅ **Portfolio Allocation Pie Chart** - Visual breakdown of holdings
- ✅ **Stock Performance Bar Chart** - Compare investment vs. current value
- ✅ Portfolio statistics: Total Value, Investment, Profit/Loss, P/L %
- ✅ Recent transactions summary
- ✅ Color-coded profit/loss indicators

### Watchlist
- ✅ Add/remove stocks from personal watchlist
- ✅ Monitor stock prices without purchasing
- ✅ Quick access to interesting stocks

### Admin Features
- ✅ Add new stocks to the marketplace
- ✅ System management capabilities
- ✅ View all user activities

---

## 🏗️ Technical Architecture

### Object-Oriented Design

#### Inheritance Hierarchy
```
User (Abstract Base Class)
├── Admin (extends User)
└── Trader (extends User)
```

**Polymorphism Demonstration:**
- `getDisplayRole()` - Returns role-specific display name
- `canManageUsers()` - Permission checking
- `canViewAllPortfolios()` - Access control

#### Encapsulation
- All model classes use private fields with public getters/setters
- JavaFX Property wrappers for table binding
- Service layer abstracts business logic from UI

### Layered Architecture
```
Presentation Layer (JavaFX)
    ↓
Controller Layer
    ↓
Service Layer (Business Logic)
    ↓
Data Access Layer (DatabaseManager)
    ↓
SQLite Database
```

---

## ✅ Requirements Checklist

### Mandatory Requirements

| Requirement | Status | Implementation |
|------------|--------|----------------|
| **User Authentication** | ✅ Complete | Login/Registration with BCrypt, Role-based access |
| **SQLite Data Persistence** | ✅ Complete | All data stored in `stockvisualiser.db` |
| **Interactive JavaFX GUI** | ✅ Complete | Multi-screen UI with 6 major views |
| **Multiple Views/Scenes** | ✅ Complete | Login, Dashboard, Portfolio, Stocks, Transactions, Watchlist, Admin |
| **JavaFX Components** | ✅ Complete | TableView, ListView, DatePicker, ComboBox, Charts, etc. |
| **CRUD Operations** | ✅ Complete | Create (Buy/Register), Read (View), Update (Quantities), Delete (Sell/Remove) |
| **Runnable JAR File** | ✅ Complete | Maven Shade plugin configured |
| **Analytics & Visualization** | ✅ Complete | PieChart, BarChart with live data |
| **Real-time Search/Filter** | ✅ Complete | TextField listeners on Stocks and Transactions tables |

### OOP Principles

| Principle | Implementation |
|-----------|----------------|
| **Inheritance** | User → Admin/Trader hierarchy |
| **Polymorphism** | Abstract methods in User class with role-specific implementations |
| **Encapsulation** | Private fields, public methods, property wrappers |
| **Abstraction** | Service layer abstracts database operations |

---

## 🚀 Setup & Installation

### Prerequisites
- **Java 21** or higher
- **Maven 3.8+** (included with Maven Wrapper)
- **Git** (for version control)

### Installation Steps

1. **Clone the Repository**
```bash
cd "/Users/flexipie/Desktop/Code/School/Year 3/Programming 2/StockVisualiser"
```

2. **Build the Project**
```bash
# On macOS/Linux
./mvnw clean package

# On Windows
mvnw.cmd clean package
```

3. **The Application is Ready!**
- Database will be automatically created on first run
- Sample data (stocks and users) will be populated

---

## ▶️ Running the Application

### Option 1: Run from IDE (IntelliJ IDEA)
1. Open project in IntelliJ IDEA
2. Wait for Maven dependencies to download
3. Run `StockVisualiserApp.java` or `Launcher.java`

### Option 2: Run with Maven
```bash
./mvnw javafx:run
```

### Option 3: Run Executable JAR
```bash
java -jar target/StockVisualiser-1.0-SNAPSHOT.jar
```

---

## 🔑 Default Login Credentials

### Administrator Account
- **Username:** `admin`
- **Password:** `admin123`
- **Capabilities:** Full system access, can add new stocks

### Demo Trader Account
- **Username:** `demo`
- **Password:** `demo123`
- **Capabilities:** Portfolio management, trading, analytics

---

## 📁 Project Structure

```
StockVisualiser/
├── src/main/java/com/example/stockvisualiser/
│   ├── StockVisualiserApp.java          # Main application entry point
│   ├── Launcher.java                    # JAR launcher
│   ├── controller/                      # JavaFX Controllers
│   │   ├── LoginController.java
│   │   └── DashboardController.java
│   ├── model/                           # Data models with OOP
│   │   ├── User.java                    # Abstract base class
│   │   ├── Admin.java                   # Extends User
│   │   ├── Trader.java                  # Extends User
│   │   ├── Stock.java
│   │   ├── Portfolio.java
│   │   ├── Transaction.java
│   │   └── Watchlist.java
│   ├── service/                         # Business logic layer
│   │   ├── AuthenticationService.java
│   │   ├── StockService.java
│   │   ├── PortfolioService.java
│   │   ├── TransactionService.java
│   │   └── WatchlistService.java
│   ├── database/                        # Data access layer
│   │   └── DatabaseManager.java         # Singleton pattern
│   └── util/                            # Utility classes
│       └── SceneManager.java
├── src/main/resources/com/example/stockvisualiser/
│   ├── view/                            # FXML files
│   │   ├── login.fxml
│   │   └── dashboard.fxml
│   └── style/
│       └── style.css                    # Application styling
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

---

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | Core language |
| JavaFX | 21.0.6 | GUI framework |
| SQLite JDBC | 3.44.1.0 | Database connectivity |
| BCrypt | 0.4 | Password hashing |
| Maven | 3.8+ | Build automation |
| ControlsFX | 11.2.1 | Enhanced UI controls |

---

## 🎨 Design Patterns & OOP Principles

### Design Patterns Implemented

1. **Singleton Pattern**
   - `DatabaseManager`: Ensures single database connection
   
2. **MVC Pattern**
   - Models: Data representation
   - Views: FXML files
   - Controllers: Business logic handlers

3. **Service Layer Pattern**
   - Separates business logic from UI
   - Promotes code reusability

### OOP Principles Demonstrated

1. **Inheritance**
   ```java
   public abstract class User { }
   public class Admin extends User { }
   public class Trader extends User { }
   ```

2. **Polymorphism**
   ```java
   @Override
   public String getDisplayRole() {
       // Admin returns "System Administrator"
       // Trader returns "Trader/Investor"
   }
   ```

3. **Encapsulation**
   - Private fields with public accessor methods
   - JavaFX Properties for binding

4. **Abstraction**
   - Abstract User class defines contract
   - Service interfaces hide implementation details

---

## 💾 Database Schema

### Tables

#### users
- `user_id` (PRIMARY KEY, AUTOINCREMENT)
- `username` (UNIQUE, NOT NULL)
- `password_hash` (NOT NULL)
- `email` (UNIQUE, NOT NULL)
- `full_name` (NOT NULL)
- `role` (CHECK: 'ADMIN' or 'TRADER')
- `created_at` (TIMESTAMP)
- `last_login` (TIMESTAMP)

#### stocks
- `stock_id` (PRIMARY KEY, AUTOINCREMENT)
- `symbol` (UNIQUE, NOT NULL)
- `company_name` (NOT NULL)
- `sector`
- `current_price` (REAL)
- `last_updated` (TIMESTAMP)

#### portfolio
- `portfolio_id` (PRIMARY KEY, AUTOINCREMENT)
- `user_id` (FOREIGN KEY → users)
- `stock_id` (FOREIGN KEY → stocks)
- `quantity` (INTEGER)
- `purchase_price` (REAL)
- `purchase_date` (DATE)

#### transactions
- `transaction_id` (PRIMARY KEY, AUTOINCREMENT)
- `user_id` (FOREIGN KEY → users)
- `stock_id` (FOREIGN KEY → stocks)
- `transaction_type` (CHECK: 'BUY' or 'SELL')
- `quantity` (INTEGER)
- `price_per_share` (REAL)
- `total_amount` (REAL)
- `transaction_date` (TIMESTAMP)

#### watchlist
- `watchlist_id` (PRIMARY KEY, AUTOINCREMENT)
- `user_id` (FOREIGN KEY → users)
- `stock_id` (FOREIGN KEY → stocks)
- `added_date` (DATE)
- UNIQUE constraint on (user_id, stock_id)

---

## 📊 Key Features Demonstration

### Real-time Search/Filter
The application implements **live filtering** on two major views:

1. **Stocks Tab**: Filter by symbol, company name, or sector as you type
2. **Transactions Tab**: Search through transaction history instantly

Implementation uses JavaFX Property listeners:
```java
stockSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
    filterStocks(newValue);
});
```

### Data Visualization
- **PieChart**: Shows portfolio allocation by stock
- **BarChart**: Compares investment amount vs. current value
- **Statistics Cards**: Display key metrics with color-coded profit/loss

### Transaction Management
- **Atomic Operations**: Database transactions ensure data consistency
- **Weighted Average Pricing**: Multiple purchases of same stock calculated correctly
- **Validation**: Prevents selling more shares than owned

---

## 👥 Team Contribution

This project demonstrates collaborative development using Git:
- Feature branches for each major component
- Proper commit messages
- Code organization and documentation

---

## 📝 Notes for Evaluation

### Design Justifications

1. **Why Inheritance for User Types?**
   - Different users have different permissions
   - Reduces code duplication
   - Allows polymorphic behavior

2. **Why Service Layer?**
   - Separates business logic from UI
   - Makes testing easier
   - Promotes code reusability

3. **Why Singleton for DatabaseManager?**
   - Ensures only one database connection
   - Prevents connection leaks
   - Centralized configuration

4. **Database Choice: SQLite**
   - Lightweight and portable
   - No server setup required
   - Perfect for desktop applications
   - File-based storage

### Challenges Overcome

1. **JavaFX Module System**: Configured proper module requirements
2. **Table Binding**: Used JavaFX Properties for live updates
3. **Transaction Integrity**: Implemented atomic database operations
4. **Real-time Filtering**: Efficient search without database queries

---

## 🔮 Future Enhancements

Potential improvements for portfolio-ready version:
- Cloud database integration (Firebase/Supabase)
- Real-time stock price API integration
- Email notifications for price alerts
- Export portfolio reports to PDF
- Multi-currency support
- Stock price history charts

---

## 📄 License

This is an academic project created for Computer Programming 2 course.

---

## 👨‍💻 Development Information

- **Course**: Computer Programming 2
- **Submission Deadline**: December 3, 2025
- **Presentation Date**: December 4, 2025
- **IDE**: IntelliJ IDEA
- **Version Control**: Git with feature branches

---

**Built with ❤️ using JavaFX and SQLite**
