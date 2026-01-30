package elevator;

import java.util.ArrayList;
import java.util.List;

/**
 * Main controller for the elevator system
 * Coordinates the elevator, doors, and users
 * 
 * @author FERKIOUI Akram, BOUSSEKINE Mohamed Ismail, HAMMOUTI Walid, BOUDISSA Farouk Radouane
 * @version 1.0
 */
public class ElevatorController {
    private Elevator elevator;
    private Door[] doors;
    private List<User> users;
    private int numberOfFloors;
    private int currentStep;
    
    /**
     * Constructor for ElevatorController
     * 
     * @param numberOfFloors the total number of floors in the building (0 to numberOfFloors-1)
     */
    public ElevatorController(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
        this.elevator = new Elevator(0, numberOfFloors - 1);
        this.doors = new Door[numberOfFloors];
        this.users = new ArrayList<>();
        this.currentStep = 0;
        
        // Initialize doors for each floor
        for (int i = 0; i < numberOfFloors; i++) {
            doors[i] = new Door(i, elevator);
        }
        
        System.out.println("Elevator controller initialized with " + numberOfFloors + " floors");
    }
    
    /**
     * Gets the elevator
     * 
     * @return the elevator instance
     */
    public Elevator getElevator() {
        return elevator;
    }
    
    /**
     * Gets all doors
     * 
     * @return array of all doors
     */
    public List<Door> getDoors() {
        List<Door> doorList = new ArrayList<>();
        for (Door door : doors) {
            doorList.add(door);
        }
        return doorList;
    }
    
    /**
     * Gets the door at a specific floor
     * 
     * @param floor the floor number
     * @return the door at that floor, or null if floor is invalid
     */
    public Door getDoorAtFloor(int floor) {
        if (floor >= 0 && floor < numberOfFloors) {
            return doors[floor];
        }
        return null;
    }
    
    /**
     * Adds a user to the system
     * 
     * @param user the user to add
     */
    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            System.out.println("User added to system: " + user);
        }
    }
    
    /**
     * Executes one step of the simulation
     * Coordinates elevator movement, door operations, and user actions
     */
    public void step() {
        currentStep++;
        
        // Check if elevator should choose a new direction
        if (elevator.isStopped() && elevator.canRestart()) {
            elevator.chooseDirection();
        }
        
        // Move elevator if it has a direction and can move
        if (elevator.getDirection() != Direction.NONE && elevator.canRestart()) {
            // Ensure no doors are open before moving
            if (!anyDoorOpen()) {
                elevator.move();
            }
        }
        
        // If elevator stopped, check if door should open
        if (elevator.isStopped()) {
            Door currentDoor = doors[elevator.getCurrentFloor()];
            if (!currentDoor.isOpen()) {
                currentDoor.checkElevatorArrival();
            }
        }
        
        // Process user actions
        processUsers();
        
        // Ensure only one door is open at a time
        enforceOneDoorConstraint();
    }
    
    /**
     * Processes actions for all users in the system
     */
    private void processUsers() {
        for (User user : users) {
            if (user.hasReachedDestination()) {
                continue; // User has completed journey
            }
            
            if (!user.isInElevator()) {
                // User is waiting for elevator
                Door userDoor = doors[user.getCurrentFloor()];
                
                if (userDoor.isOpen() && elevator.getCurrentFloor() == user.getCurrentFloor()) {
                    // Try to enter
                    if (user.tryToEnter(elevator, userDoor)) {
                        user.enterDestination(elevator);
                    }
                }
            } else {
                // User is in elevator
                if (elevator.getCurrentFloor() == user.getDestination() && elevator.isStopped()) {
                    Door destDoor = doors[user.getDestination()];
                    if (destDoor.isOpen()) {
                        user.exit(elevator);
                    }
                }
            }
        }
    }
    
    /**
     * Checks if any door is currently open
     * 
     * @return true if at least one door is open, false otherwise
     */
    private boolean anyDoorOpen() {
        for (Door door : doors) {
            if (door.isOpen()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Enforces the constraint that only one door can be open at a time
     * If multiple doors are open, closes all except the one at elevator's current floor
     */
    private void enforceOneDoorConstraint() {
        int openDoorCount = 0;
        Door currentFloorDoor = doors[elevator.getCurrentFloor()];
        
        for (Door door : doors) {
            if (door.isOpen()) {
                openDoorCount++;
                // Close any door that's not at elevator's current floor
                if (door != currentFloorDoor) {
                    door.forceClose();
                    System.out.println("WARNING: Door at floor " + door.getFloor() + 
                                     " forcibly closed to maintain one-door constraint");
                }
            }
        }
        
        if (openDoorCount > 1) {
            System.out.println("WARNING: Multiple doors were open simultaneously!");
        }
    }
    
    /**
     * Runs the simulation for a specified number of steps
     * 
     * @param steps the number of simulation steps to run
     */
    public void run(int steps) {
        System.out.println("\n=== Starting simulation for " + steps + " steps ===\n");
        for (int i = 0; i < steps; i++) {
            step();
            
            // Small delay for readability in console output
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("\n=== Simulation completed after " + steps + " steps ===\n");
    }
    
    /**
     * Runs the simulation until a specific user reaches their destination
     * 
     * @param user the user to track
     * @param maxSteps maximum number of steps before timeout
     * @return true if user reached destination, false if timeout
     */
    public boolean runUntilUserReachesDestination(User user, int maxSteps) {
        System.out.println("\n=== Running simulation until user reaches destination ===");
        System.out.println("User: " + user);
        System.out.println("Max steps: " + maxSteps + "\n");
        
        for (int i = 0; i < maxSteps; i++) {
            step();
            
            if (user.hasReachedDestination()) {
                System.out.println("\n=== User reached destination at step " + (i + 1) + " ===\n");
                return true;
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("\n=== Timeout: User did not reach destination within " + 
                         maxSteps + " steps ===\n");
        return false;
    }
    
    /**
     * Validates system constraints
     * 
     * @return list of constraint violations (empty if all constraints satisfied)
     */
    public List<String> validateConstraints() {
        List<String> violations = new ArrayList<>();
        
        // Check: No doors open when elevator is moving
        if (!elevator.isStopped()) {
            for (Door door : doors) {
                if (door.isOpen()) {
                    violations.add("Door at floor " + door.getFloor() + 
                                 " is open while elevator is moving");
                }
            }
        }
        
        // Check: Only one door open at a time
        int openDoorCount = 0;
        for (Door door : doors) {
            if (door.isOpen()) {
                openDoorCount++;
            }
        }
        if (openDoorCount > 1) {
            violations.add("Multiple doors are open simultaneously: " + openDoorCount);
        }
        
        return violations;
    }
    
    /**
     * Gets the current simulation step number
     * 
     * @return current step
     */
    public int getCurrentStep() {
        return currentStep;
    }
    
    /**
     * Resets the simulation
     */
    public void reset() {
        currentStep = 0;
        elevator = new Elevator(0, numberOfFloors - 1);
        users.clear();
        for (int i = 0; i < numberOfFloors; i++) {
            doors[i] = new Door(i, elevator);
        }
        System.out.println("Elevator controller reset");
    }
    
    /**
     * Prints current system state
     */
    public void printState() {
        System.out.println("\n--- System State at Step " + currentStep + " ---");
        System.out.println("Elevator: " + elevator);
        System.out.println("Open doors:");
        for (Door door : doors) {
            if (door.isOpen()) {
                System.out.println("  " + door);
            }
        }
        System.out.println("Users:");
        for (User user : users) {
            System.out.println("  " + user);
        }
        System.out.println("-----------------------------------\n");
    }
    
    /**
     * Main method for running a simple simulation
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Create controller with 6 floors (0-5)
        ElevatorController controller = new ElevatorController(6);
        
        // Create users
        User user1 = new User(0, 3, Direction.UP);
        user1.setDistracted(false);
        
        User user2 = new User(1, 0, Direction.DOWN);
        user2.setDistracted(false);
        
        // Add users to system
        controller.addUser(user1);
        controller.addUser(user2);
        
        // Users call elevator
        user1.callElevator(controller.getElevator());
        user2.callElevator(controller.getElevator());
        
        // Run simulation
        controller.run(100);
        
        // Print final state
        controller.printState();
    }
}
