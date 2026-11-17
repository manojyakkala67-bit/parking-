# Smart Parking Management System (SPMS)

An enterprise-grade, commercial-quality desktop application developed in **Java Swing** with **MySQL Database** via **JDBC** and built with **Maven**. Designed with a clean layered architecture, rich OOP design patterns, cryptographic security, real-time visual telemetry, automated slot assignment, and invoicing capabilities.

---

## 1. System Architecture & Design

The application follows an enterprise **Layered Architecture**:

```
+------------------------------------------------------------------------------------+
|                                    PRESENTATION LAYER                              |
|   MainFrame | LoginFrame | DashboardPanel | VehicleEntryPanel | VehicleExitPanel   |
|   SlotManagementPanel | VehicleManagementPanel | ParkingHistoryPanel | RevenuePanel|
+-----------------------------------------+------------------------------------------+
                                          |
                                          v
+------------------------------------------------------------------------------------+
|                                      SERVICE LAYER                                 |
|   IAuthService      <-- AuthServiceImpl                                            |
|   IVehicleService   <-- VehicleServiceImpl                                         |
|   IParkingSlotService<-- ParkingSlotServiceImpl                                    |
|   IParkingService   <-- ParkingServiceImpl (Transactional Entry / Exit / Billing)  |
|   IRevenueService   <-- RevenueServiceImpl                                         |
+-----------------------------------------+------------------------------------------+
                                          |
                                          v
+------------------------------------------------------------------------------------+
|                                DATA ACCESS LAYER (DAO)                             |
|   IAdminDAO         <-- AdminDAOImpl                                               |
|   IVehicleDAO       <-- VehicleDAOImpl                                             |
|   IParkingSlotDAO   <-- ParkingSlotDAOImpl                                         |
|   IParkingRecordDAO <-- ParkingRecordDAOImpl                                       |
|   IPaymentDAO       <-- PaymentDAOImpl                                             |
+-----------------------------------------+------------------------------------------+
                                          |
                                          v
+------------------------------------------------------------------------------------+
|                         DATABASE & PERSISTENCE (MySQL 8.0+)                        |
|   smart_parking_db (admins, vehicles, parking_slots, parking_records, payments)    |
+------------------------------------------------------------------------------------+
```

---

## 2. Object-Oriented Programming (OOP) Implementation

| OOP Concept | Project Implementation |
|---|---|
| **Abstraction** | `Vehicle` abstract base class defining abstract contract `calculateFee(long hours)` and `getHourlyRate()`. |
| **Inheritance** | `Bike extends Vehicle`, `Car extends Vehicle`, `Truck extends Vehicle`. Each class inherits attributes while specializing behavior. |
| **Polymorphism** | `VehicleFactory.createVehicle(...)` produces polymorphic instances at runtime. Billing logic calls `vehicle.calculateFee(duration)` without knowing concrete vehicle type. |
| **Encapsulation** | Strict private fields with validated public getters and setters across all entity models (`ParkingSlot`, `ParkingRecord`, `Payment`, `Admin`, etc.). |
| **Interfaces** | Interface-driven service and DAO design (`IVehicleDAO`, `IParkingSlotDAO`, `IParkingService`, etc.) ensuring modular decoupling. |
| **Custom Exceptions** | `ParkingException`, `DuplicateVehicleException`, `SlotUnavailableException`, `ValidationException`, `DatabaseException`, `InvalidCredentialsException`. |
| **Java Date & Time API** | Robust use of `java.time.LocalDateTime`, `Duration`, `LocalDate`, and `DateTimeFormatter` for nanosecond-accurate duration and billable hours calculation. |
| **Collections Framework** | `ArrayList`, `EnumMap`, `Map`, `List` utilized throughout DAOs, statistics aggregators, and dynamic tables. |
| **Transaction Integrity** | `Connection.setAutoCommit(false)`, `commit()`, and `rollback()` ensure atomic vehicle entry (record + slot occupy) and exit (record + payment + slot release). |

---

## 3. Technology Stack

- **Language:** Java (JDK 17 / JDK 21 LTS)
- **GUI Toolkit:** Java Swing (Custom High-DPI anti-aliased UI components)
- **Database:** MySQL Server 8.0+
- **Connectivity:** JDBC (`mysql-connector-j` 9.1.0)
- **Build & Dependency Tool:** Apache Maven
- **Security:** SHA-256 with 16-character cryptographic salt
- **Testing:** JUnit 4.13.2 / Hamcrest 2.2

---

## 4. Parking Tariff Structure

Charges are calculated based on **started hours**, with a **minimum charge of one hour**.

| Vehicle Category | Tariff Rate | Minimum Charge | Started Hour Example |
|---|---|---|---|
| 🏍 **Bike** | **₹40.00 / hour** | ₹40.00 (1 hr) | 1 hr 10 mins = 2 hrs (₹80.00) |
| 🚗 **Car** | **₹100.00 / hour** | ₹100.00 (1 hr) | 2 hrs 45 mins = 3 hrs (₹300.00) |
| 🚚 **Truck** | **₹150.00 / hour** | ₹150.00 (1 hr) | 1 hr 01 min = 2 hrs (₹300.00) |

---

## 5. Storage Architecture (Zero Database Setup Required)

The application has been engineered to run in **Standalone Embedded Mode**:
- **No MySQL installation or server required.**
- **No credentials, ports, or services to configure.**
- **Zero network socket or access denied errors.**

All entities, parking slots (45 pre-seeded slots across Floor 1 and Floor 2), registered vehicles, live spatial grids, tariffs, and historical transactions are managed by a thread-safe embedded repository (`DataStore`) with the full layered DAO and Service architecture intact.

---

## 6. Default Admin Credentials

Sign in immediately on the login screen using:

- **Username:** `admin`
- **Password:** `admin123`
- **Role:** `SUPER_ADMIN`

*(Password authentication uses SHA-256 with cryptographic salt).*

---

## 7. How to Run the Application

### Option A: Using Maven
```bash
# Compile and build JAR
mvn clean package

# Run via Maven Exec plugin
mvn exec:java -Dexec.mainClass="com.smartparking.Main"
```

### Option B: Using Windows Batch Scripts
- **Compile:** Double-click or run `compile.bat`
- **Run Application:** Double-click or run `run.bat`
- **Run Unit Tests:** Double-click or run `test.bat`

### Option C: In Antigravity IDE / IntelliJ / Eclipse / VS Code
1. Open the project folder (`smart-parking-system`).
2. The IDE will recognize `pom.xml` as a standard Maven project.
3. Run `src/main/java/com/smartparking/Main.java`.

---

## 8. Screen & Feature Walkthrough

### 1. Login Screen
- Admin credential validation.
- Password show/hide toggle.
- Direct access to Database Setup Wizard.

### 2. Operations Dashboard
- **6 Modern Metric Cards:** Total Slots, Available Slots, Occupied Slots, Currently Parked, Today's Revenue, Vehicles Parked Today.
- **Custom Donut Chart:** Visualizes capacity occupancy (Green = Available, Red = Occupied, Amber = Maintenance).
- **Custom Bar Chart:** Category distribution (Bikes vs Cars vs Trucks).
- **Live Activity Feed:** Real-time stream of latest parking operations.

### 3. Vehicle Entry Gate
- Form intake: Registration number, vehicle type, owner name, phone number.
- Fast "Lookup" button auto-fills data if vehicle previously visited.
- **Auto-Assign Nearest Slot:** Automatically finds the closest vacant bay matching the vehicle category.
- **Printable Entry Pass Dialog:** Instant generation of admission ticket with barcode simulation, bay identifier, and terms.
- Duplicate parking guard: Prevents admitting a vehicle already parked.

### 4. Vehicle Exit & Billing Desk
- Real-time list of all active vehicles.
- Interactive calculation card: entry time, exit time, billable started hours, tariff rate, total bill.
- Payment modes: **Cash, Card, UPI**.
- Tendered amount vs change returned calculator.
- **Tax Invoice & Receipt Dialog:** Generates itemized invoices with transaction reference, printable or exportable.
- Atomically marks parking completed, records payment, and frees the parking bay.

### 5. Parking Bay Map (Grid View)
- Spatial visual grid of all slots across multiple floors.
- Color coded: **Green** (Available), **Red** (Occupied with plate display), **Amber** (Maintenance).
- Filter by Floor, Category, or Status.
- Slot details modal with maintenance toggle and delete controls.
- "Add New Bay" dialog to expand facility capacity.

### 6. Vehicle Registry
- Complete database of registered vehicles.
- Search by plate number, owner name, or phone.
- Register new vehicles, update existing details, delete records with active-parking safety locks.

### 7. Audit History
- Detailed transaction log with search and timeframe filters (Today, Last 7 Days, This Month, All Time).
- One-click **"Export to CSV"** for spreadsheet analysis.

### 8. Revenue Intelligence & Reports
- Financial summary cards: Lifetime collection, Today's revenue, Monthly revenue, Settled bills count.
- Category realizations: Breakdown of revenue and trip counts by Bike, Car, and Truck.
- Filterable timeframe report table with CSV export.

---

## 9. Testing & Quality Verification

Run unit tests:
```bash
./test.bat
```
Or via Maven:
```bash
mvn test
```

### Verification Results:
```
JUnit version 4.13.2
.................
Time: 0.258s

OK (17 tests)
```
- `FeeCalculationTest`: Verified tariffs for Bike (₹40), Car (₹100), Truck (₹150), started-hour ceiling calculation, minimum 1-hour charge, and polymorphic behavior.
- `ValidationUtilTest`: Verified registration plate formats, international/national phone formats, owner names, and bay numbering.
- `PasswordUtilTest`: Verified cryptographic salt entropy, SHA-256 hash generation, and credential verification against seeded database hashes.

---

## 10. Project Directory Layout

```
smart-parking-system/
├── pom.xml                               # Maven project configuration
├── README.md                             # Comprehensive system documentation
├── database.sql                          # MySQL schema and seed data
├── compile.bat                           # Windows batch compiler
├── run.bat                               # Windows application launcher
├── test.bat                              # Windows unit test runner
├── run.sh                                # Unix/macOS launcher
├── lib/                                  # Runtime & test JAR dependencies
│   ├── mysql-connector-j.jar
│   ├── junit-4.13.2.jar
│   └── hamcrest-2.2.jar
└── src/
    ├── main/
    │   ├── java/com/smartparking/
    │   │   ├── Main.java                 # Main application launcher
    │   │   ├── model/                    # OOP domain models & enums
    │   │   │   ├── Vehicle.java          # Abstract base class
    │   │   │   ├── Bike.java             # Concrete Vehicle subclass (₹40/hr)
    │   │   │   ├── Car.java              # Concrete Vehicle subclass (₹100/hr)
    │   │   │   ├── Truck.java            # Concrete Vehicle subclass (₹150/hr)
    │   │   │   ├── VehicleFactory.java   # Factory Pattern
    │   │   │   ├── ParkingSlot.java      # Parking Bay model
    │   │   │   ├── ParkingRecord.java    # Parking Transaction model
    │   │   │   ├── Payment.java          # Payment model
    │   │   │   ├── Admin.java            # Admin user model
    │   │   │   ├── DashboardStats.java   # Aggregated analytics model
    │   │   │   ├── RevenueReport.java    # Financial report model
    │   │   │   └── Enums (VehicleType, SlotType, SlotStatus, RecordStatus, PaymentMethod, PaymentStatus)
    │   │   ├── dao/                      # Data Access Objects (JDBC)
    │   │   │   ├── IAdminDAO.java & AdminDAOImpl.java
    │   │   │   ├── IVehicleDAO.java & VehicleDAOImpl.java
    │   │   │   ├── IParkingSlotDAO.java & ParkingSlotDAOImpl.java
    │   │   │   ├── IParkingRecordDAO.java & ParkingRecordDAOImpl.java
    │   │   │   └── IPaymentDAO.java & PaymentDAOImpl.java
    │   │   ├── service/                  # Business Logic & Transactions
    │   │   │   ├── IAuthService.java & AuthServiceImpl.java
    │   │   │   ├── IVehicleService.java & VehicleServiceImpl.java
    │   │   │   ├── IParkingSlotService.java & ParkingSlotServiceImpl.java
    │   │   │   ├── IParkingService.java & ParkingServiceImpl.java
    │   │   │   └── IRevenueService.java & RevenueServiceImpl.java
    │   │   ├── ui/                       # Modern Java Swing UI
    │   │   │   ├── MainFrame.java        # Sidebar, top status bar, screen coordinator
    │   │   │   ├── auth/LoginFrame.java
    │   │   │   ├── dashboard/DashboardPanel.java
    │   │   │   ├── entry/VehicleEntryPanel.java & EntryPassDialog.java
    │   │   │   ├── exit/VehicleExitPanel.java & ExitInvoiceDialog.java
    │   │   │   ├── slot/SlotManagementPanel.java, SlotDetailDialog.java, AddSlotDialog.java
    │   │   │   ├── vehicle/VehicleManagementPanel.java & VehicleDialog.java
    │   │   │   ├── history/ParkingHistoryPanel.java
    │   │   │   ├── revenue/RevenuePanel.java
    │   │   │   ├── settings/DatabaseSettingsDialog.java & ChangePasswordDialog.java
    │   │   │   └── common/ (ModernButton, CardPanel, StatCard, StyledTable, Charts, DialogUtil)
    │   │   └── util/                     # Utilities & Exceptions
    │   │       ├── DatabaseConnection.java# Singleton JDBC Connection Manager
    │   │       ├── PasswordUtil.java     # SHA-256 with Salt
    │   │       ├── DateTimeUtil.java     # Date & Time API Helpers
    │   │       ├── ValidationUtil.java   # Input Validation
    │   │       ├── ExportUtil.java       # CSV & Printable Invoices
    │   │       ├── UITheme.java          # Palette & Fonts
    │   │       └── Custom Exceptions (ParkingException, DuplicateVehicleException, etc.)
    │   └── resources/
    │       ├── db.properties             # Database configuration
    │       └── database/schema.sql       # MySQL initialization script
    └── test/java/com/smartparking/
        ├── FeeCalculationTest.java       # Tariff & duration test suite
        ├── ValidationUtilTest.java       # Input validation test suite
        └── PasswordUtilTest.java         # Security & hashing test suite
```

---

## 11. University Demonstration Guide

When presenting this project:
1. **Show the OOP Architecture:** Point to `Vehicle.java` (abstract class), polymorphic subclasses `Bike`, `Car`, `Truck`, and how `calculateFee()` dynamically executes the correct formula based on vehicle type.
2. **Show Database Transactions:** Point to `ParkingServiceImpl.java` lines where `setAutoCommit(false)`, `commit()`, and `rollback()` guarantee that entering or exiting a vehicle never leaves orphan records or inaccurate slot states.
3. **Show Security:** Point to `PasswordUtil.java` showing salted SHA-256 hashing and PreparedStatement queries preventing SQL Injection.
4. **Show Real-time Visual UI:** Demonstrate the **Parking Bays** spatial grid turning Red when occupied and Green when freed, and the **Dashboard** charts dynamically recalculating occupancy and revenue.
