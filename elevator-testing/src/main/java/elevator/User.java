package elevator;

import java.util.Random;

/**
 * Represents a user in the elevator system
 * Manages user behavior including calling elevator, entering, and exiting
 * 
 * @author FERKIOUI Akram, BOUSSEKINE Mohamed Ismail, HAMMOUTI Walid, BOUDISSA Farouk Radouane
 * @version 1.0
 */
public class User {
    private int currentFloor;
    private int destination;
    private Direction direction;
    private boolean isInElevator;
    private boolean isDistracted;
    private boolean hasCalledElevator;
    private int travelDistance;
    private int startFloor;
    private Random random;
    
    /**
     * Constructor for User
     * 
     * @param currentFloor the floor where the user is currently located
     * @param destination the floor where the user wants to go
     * @param direction the direction the user wants to travel (UP or DOWN)
     */
    public User(int currentFloor, int destination, Direction direction) {
        this.currentFloor = currentFloor;
        this.startFloor = currentFloor;
        this.destination = destination;
        this.direction = direction;
        this.isInElevator = false;
        this.isDistracted = false;
        this.hasCalledElevator = false;
        this.travelDistance = 0;
        this.random = new Random();
    }
    
    /**
     * Gets the user's current floor
     * 
     * @return current floor number
     */
    public int getCurrentFloor() {
        return currentFloor;
    }
    
    /**
     * Sets the user's current floor
     * 
     * @param floor the new floor number
     */
    public void setCurrentFloor(int floor) {
        this.currentFloor = floor;
    }
    
    /**
     * Gets the user's destination floor
     * 
     * @return destination floor number
     */
    public int getDestination() {
        return destination;
    }
    
    /**
     * Gets the user's travel direction
     * 
     * @return direction (UP or DOWN)
     */
    public Direction getDirection() {
        return direction;
    }
    
    /**
     * Checks if the user is currently in the elevator
     * 
     * @return true if user is in elevator, false otherwise
     */
    public boolean isInElevator() {
        return isInElevator;
    }
    
    /**
     * Sets whether the user is distracted
     * A distracted user may not enter the elevator when the door opens
     * 
     * @param distracted true if user is distracted, false otherwise
     */
    public void setDistracted(boolean distracted) {
        this.isDistracted = distracted;
    }
    
    /**
     * Checks if the user is distracted
     * 
     * @return true if distracted, false otherwise
     */
    public boolean isDistracted() {
        return isDistracted;
    }
    
    /**
     * Gets the total distance traveled by the user
     * 
     * @return distance in floors
     */
    public int getTravelDistance() {
        return travelDistance;
    }
    
    /**
     * Calls the elevator from the user's current floor
     * Checks if there's an opposite direction call on the same floor
     * 
     * @param elevator the elevator to call
     */
    public void callElevator(Elevator elevator) {
        if (!hasCalledElevator && !shouldWaitForOppositeCall(elevator)) {
            elevator.addCall(currentFloor, direction);
            hasCalledElevator = true;
            System.out.println("User at floor " + currentFloor + " called elevator going " + direction);
        }
    }
    
    /**
     * Determines if the user should wait because there's an opposite direction call
     * on the same floor
     * 
     * @param elevator the elevator to check
     * @return true if user should wait, false otherwise
     */
    public boolean shouldWaitForOppositeCall(Elevator elevator) {
        Direction opposite = direction.opposite();
        if (opposite == Direction.NONE) {
            return false;
        }
        return elevator.hasCallAtFloor(currentFloor, opposite);
    }
    
    /**
     * Attempts to enter the elevator
     * User may be distracted and not enter
     * 
     * @param elevator the elevator to enter
     * @param door the door to enter through
     * @return true if user successfully entered, false otherwise
     */
    public boolean tryToEnter(Elevator elevator, Door door) {
        if (!door.isOpen()) {
            return false;
        }
        
        // Notify door that entry is in progress
        door.markUserEntering(this);
        
        // Simulate user decision and entry time
        simulateEntryDelay();
        
        // Check if user is distracted (may not enter)
        if (isDistracted) {
            System.out.println("User at floor " + currentFloor + " is distracted and doesn't enter");
            door.markUserEntered(this);
            return false;
        }
        
        // Check if door is still open and elevator is stopped
        if (elevator.isStopped() && door.isOpen()) {
            isInElevator = true;
            elevator.addUser(this);
            door.markUserEntered(this);
            System.out.println("User entered elevator at floor " + currentFloor);
            return true;
        }
        
        door.markUserEntered(this);
        return false;
    }
    
    /**
     * Simulates the time it takes for a user to enter the elevator
     * This includes decision making and physical entry time
     */
    private void simulateEntryDelay() {
        try {
            // Random delay between 100ms and 500ms
            int delay = 100 + random.nextInt(400);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * User enters their destination into the elevator
     * 
     * @param elevator the elevator to enter destination into
     */
    public void enterDestination(Elevator elevator) {
        if (isInElevator) {
            elevator.addDestination(destination);
            System.out.println("User entered destination: floor " + destination);
        }
    }
    
    /**
     * User exits the elevator
     * Calculates travel distance
     * 
     * @param elevator the elevator to exit from
     */
    public void exit(Elevator elevator) {
        if (isInElevator && elevator.getCurrentFloor() == destination) {
            isInElevator = false;
            elevator.removeUser(this);
            currentFloor = destination;
            travelDistance = Math.abs(destination - startFloor);
            System.out.println("User exited elevator at floor " + currentFloor);
        }
    }
    
    /**
     * Checks if the user has reached their destination
     * 
     * @return true if at destination, false otherwise
     */
    public boolean hasReachedDestination() {
        return currentFloor == destination && !isInElevator;
    }
    
    /**
     * Waits for the door to open
     * This is a blocking operation used in testing
     * 
     * @param door the door to wait for
     * @param timeoutMs maximum time to wait in milliseconds
     * @return true if door opened, false if timeout
     */
    public boolean waitForDoorToOpen(Door door, long timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (!door.isOpen()) {
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                return false;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }
    
    /**
     * Simulates complete user journey from current floor to destination
     * 
     * @param elevator the elevator system
     * @param doors array of doors at each floor
     */
    public void completeJourney(Elevator elevator, Door[] doors) {
        // Call elevator
        callElevator(elevator);
        
        // Wait for elevator and door
        Door currentDoor = doors[currentFloor];
        waitForDoorToOpen(currentDoor, 10000);
        
        // Try to enter
        if (tryToEnter(elevator, currentDoor)) {
            // Enter destination
            enterDestination(elevator);
            
            // Wait to reach destination
            while (!elevator.hasReachedFloor(destination)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // Exit
            Door destDoor = doors[destination];
            if (destDoor.isOpen()) {
                exit(elevator);
            }
        }
    }
    
    /**
     * Returns string representation of user state
     * 
     * @return string describing user state
     */
    @Override
    public String toString() {
        return "User[floor=" + currentFloor + ", destination=" + destination + 
               ", direction=" + direction + ", inElevator=" + isInElevator + "]";
    }
}
