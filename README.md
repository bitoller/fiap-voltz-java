# Voltz Crypto Bank

A Java-based cryptocurrency banking application that allows users to manage their crypto assets, perform transactions, and track their portfolio.

## Features

- User Management
  - User registration and authentication
  - Profile management (edit/delete account)
  - Secure password handling with BCrypt encryption

- Wallet Management
  - Multi-cryptocurrency wallet support
  - Real-time balance tracking
  - Transaction history and statements

- Transaction Operations
  - Deposit cryptocurrencies
  - Withdraw cryptocurrencies
  - Transfer between users
  - Transaction history tracking

## Technical Stack

- Java 17
- Maven for dependency management
- Oracle Database (JDBC)
- Key Dependencies:
  - jBCrypt (0.4) for password encryption
  - Oracle JDBC Driver (23.2.0.0)
  - Gson (2.10.1) for JSON handling
  - JUnit (3.8.1) for testing

## Project Structure

```
src/main/java/br/com/voltz/
├── adapter/     # Data adapters
├── dao/         # Data Access Objects
├── factory/     # Factory classes
├── model/       # Domain models
├── util/        # Utility classes
└── App.java     # Main application class
```

## Models

- `Users`: User account information
- `Wallet`: User's cryptocurrency wallet
- `WalletEntry`: Individual cryptocurrency holdings
- `Transaction`: Transaction records
- `SupportedCrypto`: Supported cryptocurrency information

## Getting Started

1. Ensure you have Java 17 and Maven installed
2. Set up an Oracle Database instance
3. Configure the database connection in the application
4. Build the project:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   java -jar target/voltz-1.0-SNAPSHOT.jar
   ```

## Security Features

- Password encryption using BCrypt
- Input validation for all user inputs
- Secure database connections
- Transaction integrity checks

## License

This project is licensed under the MIT License - see the LICENSE file for details.