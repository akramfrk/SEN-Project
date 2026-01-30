# Elevator Controller Testing Project

## Project Information
- **School:** École Nationale Supérieure de Technologies Avancées (ENSTA Alger)
- **Course:** Software Engineering
- **Instructor:** Mrs. Souad KHERROUBI
- **Academic Year:** 2025-2026

## Team Members
- FERKIOUI Akram
- BOUSSEKINE Mohamed Ismail
- HAMMOUTI Walid
- BOUDISSA Farouk Radouane

## Project Description
This project implements and tests an elevator controller system in Java. The system simulates an elevator serving multiple floors with users requesting service in different directions. The project focuses on comprehensive testing of the controller logic, door behavior, user interactions, and system-wide constraints.

## Project Structure
```
elevator-testing/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── elevator/
│   │           ├── ElevatorController.java  # Main controller coordinating the system
│   │           ├── Elevator.java            # Elevator logic and movement
│   │           ├── Door.java                # Door operations and timing
│   │           ├── User.java                # User behavior and journey
│   │           └── Direction.java           # Direction enum (UP/DOWN/NONE)
│   │
│   └── test/
│       └── java/
│           └── elevator/
│               ├── DoorTest.java            # Door behavior tests
│               ├── UserTest.java            # User behavior tests
│               ├── ElevatorTest.java        # Elevator logic tests
│               ├── SystemIntegrationTest.java # System-wide tests
│               └── ScenarioTest.java        # Scenario-based tests
│
├── docs/
│   └── Elevator_Controller_Testing_Report.md  # Detailed project report
│
├── pom.xml                                  # Maven configuration
└── README.md                                # This file
```

## System Components

### 1. Direction (Enum)
Represents movement direction:
- `UP`: Upward movement
- `DOWN`: Downward movement
- `NONE`: No direction/stationary

### 2. Door
Manages door operations at each floor:
- Opens when elevator arrives
- Closes after timeout
- Signals elevator when closed
- Tracks users entering

### 3. User
Represents a user in the system:
- Calls elevator from current floor
- Enters elevator when door opens
- May be distracted (miss the elevator)
- Travels to destination floor
- Exits at destination

### 4. Elevator
Core elevator controller:
- Moves between floors
- Manages calls and destinations
- Chooses direction based on algorithm
- Stops at requested floors
- Reverses at boundaries

### 5. ElevatorController
System coordinator:
- Manages elevator, doors, and users
- Runs simulation steps
- Enforces system constraints
- Validates system state

## System Specifications

### Door Behavior
1. Waits for elevator to stop at its floor
2. Opens the door
3. Waits a certain time
4. Closes the door
5. Signals elevator to restart

### User Behavior
1. Checks for opposite direction calls on same floor
2. Calls elevator if no conflict
3. Waits for door to open
4. May be distracted and not enter
5. Enters elevator if door still open
6. Enters destination
7. Waits for arrival
8. Exits at destination

### Elevator Behavior
1. Chooses direction based on calls/destinations
2. Moves one floor per step
3. Reverses at highest/lowest floor
4. Stops when reaching call or destination
5. Clears calls/destinations at current floor
6. Opens door at stopped floor

### System Constraints
- No doors open when elevator is moving
- Only one door open at a time
- User who requests will eventually enter
- Travel distance equals source to destination

## Prerequisites
- Java JDK 11 or higher
- Maven 3.6 or higher
- Internet connection (for first build to download dependencies)

## Installation and Setup

### 1. Extract/Clone the Project
```bash
cd elevator-testing
```

### 2. Build the Project
```bash
mvn clean compile
```

### 3. Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=DoorTest
mvn test -Dtest=UserTest
mvn test -Dtest=ElevatorTest
mvn test -Dtest=SystemIntegrationTest
mvn test -Dtest=ScenarioTest

# Run specific test method
mvn test -Dtest=DoorTest#testDoorOpensWhenElevatorArrives
```

### 4. Generate Coverage Report
```bash
mvn test jacoco:report
```
Then open `target/site/jacoco/index.html` in a web browser.

### 5. Run the Simulation
```bash
# Compile and run
mvn package
java -cp target/elevator-testing-1.0-SNAPSHOT.jar elevator.ElevatorController
```

## Test Categories

### Door Tests (6 tests)
- Door opens when elevator arrives
- Door closes after timeout
- Door signals elevator after closing
- Door remains closed when elevator elsewhere
- Door handles multiple cycles
- Door synchronizes with elevator

### User Tests (10 tests)
- User calls elevator
- User waits for opposite direction
- User enters when door open
- User may be distracted
- User enters destination
- User completes journey
- Travel distance validation
- Door closing timing (fails - see report)
- User waits for door
- Multiple users on different floors

### Elevator Tests (12 tests)
- Chooses direction based on calls
- Reverses at highest floor
- Reverses at lowest floor
- Stops at call floor
- Clears destination when reached
- Maintains direction with calls ahead
- Switches direction when no calls ahead
- No direction when no calls
- Handles call on current floor
- Manages multiple destinations
- Prioritizes direction correctly
- Handles simultaneous calls

### System Integration Tests (8 tests)
- No doors open during motion
- Only one door open at a time
- User who requests will enter
- Travel distance correct
- Multi-user coordination
- Concurrent call handling
- System recovery
- Performance under load

### Scenario Tests (6 tests)
- Execution trace validation
- Two users same floor different directions
- Multiple floors sequence
- Full building traversal
- Rush hour simulation
- Random user pattern

## Test Results Summary
- **Total Tests:** 42
- **Passed:** 41
- **Failed:** 1
- **Pass Rate:** 97.6%
- **Line Coverage:** 92.5%
- **Branch Coverage:** 85.8%

## Known Issues

### Failed Test: testUserHandlesDoorClosing
**Issue:** Race condition between door closing timer and user entry process.

**Cause:** Door closes after fixed timeout regardless of whether user is actively entering.

**Impact:** Users may miss elevator despite calling it, violating specification that "user who requests will inevitably enter."

**Proposed Fix:** Implement user entry state detection to prevent door closure during active entry.

See detailed error analysis in the project report.

## Usage Examples

### Example 1: Simple Simulation
```java
ElevatorController controller = new ElevatorController(6); // 6 floors

User user = new User(0, 3, Direction.UP);
user.setDistracted(false);

controller.addUser(user);
user.callElevator(controller.getElevator());

controller.run(100); // Run for 100 steps
```

### Example 2: Multiple Users
```java
ElevatorController controller = new ElevatorController(6);

User user1 = new User(0, 5, Direction.UP);
User user2 = new User(3, 1, Direction.DOWN);

user1.setDistracted(false);
user2.setDistracted(false);

controller.addUser(user1);
controller.addUser(user2);

user1.callElevator(controller.getElevator());
user2.callElevator(controller.getElevator());

controller.run(200);
controller.printState();
```

### Example 3: Run Until User Reaches Destination
```java
ElevatorController controller = new ElevatorController(6);
User user = new User(1, 4, Direction.UP);
user.setDistracted(false);

controller.addUser(user);
user.callElevator(controller.getElevator());

boolean reached = controller.runUntilUserReachesDestination(user, 1000);
if (reached) {
    System.out.println("User reached destination!");
}
```

## Maven Commands Reference

```bash
# Clean build artifacts
mvn clean

# Compile source code
mvn compile

# Run tests
mvn test

# Generate test coverage report
mvn jacoco:report

# Package as JAR
mvn package

# Run all: clean, compile, test, package
mvn clean package

# Skip tests (not recommended)
mvn package -DskipTests

# Verbose output
mvn test -X

# Run with specific Java version
mvn test -Djava.version=11
```

## Code Quality

The project follows these quality practices:
- Clear and descriptive naming conventions
- Comprehensive JavaDoc comments
- Proper error handling
- Unit test isolation
- High code coverage (92.5%)
- Adherence to Java coding standards

## Documentation

Detailed documentation is available in:
- **docs/Elevator_Controller_Testing_Report.md** - Complete project report with analysis, design, implementation, testing strategy, results, and error analysis

## Future Enhancements

Potential improvements identified:
1. Fix door closing race condition
2. Add configurable timing parameters
3. Implement better user entry state tracking
4. Optimize destination queue management
5. Add logging and monitoring capabilities
6. Implement visualization tool for debugging
7. Add stress testing with many concurrent users
8. Create property-based testing for state transitions

## Contributing

Team members contributed to:
- **FERKIOUI Akram:** System design and elevator logic
- **BOUSSEKINE Mohamed Ismail:** User behavior and testing
- **HAMMOUTI Walid:** Door implementation and integration
- **BOUDISSA Farouk Radouane:** System integration and documentation

## License

This is an academic project for ENSTA Alger - Software Engineering Course.

## Contact

For questions or issues, contact:
- Instructor: Mrs. Souad KHERROUBI (souad.kherroubi@ensta.edu.dz)

---

**Last Updated:** January 30, 2026
