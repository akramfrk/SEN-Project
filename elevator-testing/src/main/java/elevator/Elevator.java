package elevator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the elevator in the system
 * Manages movement, calls, destinations, and direction selection logic
 * 
 * @author FERKIOUI Akram, BOUSSEKINE Mohamed Ismail, HAMMOUTI Walid, BOUDISSA Farouk Radouane
 * @version 1.0
 */
public class Elevator {
    private int currentFloor;
    private Direction direction;
    private boolean isStopped;
    private List<Integer> destinations;
    private Map<Integer, List<Direction>> calls; // floor -> list of directions
    private List<User> usersInside;
    private int lowestFloor;
    private int highestFloor;
    private boolean canRestart;
    
    /**
     * Constructor for Elevator
     * 
     * @param lowestFloor the lowest floor the elevator can reach
     * @param highestFloor the highest floor the elevator can reach
     */
    public Elevator(int lowestFloor, int highestFloor) {
        this.lowestFloor = lowestFloor;
        this.highestFloor = highestFloor;
        this.currentFloor = lowestFloor;
        this.direction = Direction.NONE;
        this.isStopped = true;
        this.destinations = new ArrayList<>();
        this.calls = new HashMap<>();
        this.usersInside = new ArrayList<>();
        this.canRestart = true;
    }
    
    /**
     * Gets the current floor of the elevator
     * 
     * @return current floor number
     */
    public int getCurrentFloor() {
        return currentFloor;
    }
    
    /**
     * Sets the current floor of the elevator
     * Used primarily for testing
     * 
     * @param floor the floor number to set
     */
    public void setCurrentFloor(int floor) {
        if (floor >= lowestFloor && floor <= highestFloor) {
            this.currentFloor = floor;
        }
    }
    
    /**
     * Gets the current direction of the elevator
     * 
     * @return current direction (UP, DOWN, or NONE)
     */
    public Direction getDirection() {
        return direction;
    }
    
    /**
     * Sets the direction of the elevator
     * 
     * @param direction the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    /**
     * Checks if the elevator is stopped
     * 
     * @return true if stopped, false if moving
     */
    public boolean isStopped() {
        return isStopped;
    }
    
    /**
     * Stops the elevator at current floor
     */
    public void stop() {
        this.isStopped = true;
        System.out.println("Elevator stopped at floor " + currentFloor);
    }
    
    /**
     * Checks if the elevator can restart (door has closed)
     * 
     * @return true if can restart, false otherwise
     */
    public boolean canRestart() {
        return canRestart;
    }
    
    /**
     * Adds a call from a specific floor in a specific direction
     * 
     * @param floor the floor where the call is made
     * @param direction the direction requested (UP or DOWN)
     */
    public void addCall(int floor, Direction direction) {
        if (!calls.containsKey(floor)) {
            calls.put(floor, new ArrayList<>());
        }
        if (!calls.get(floor).contains(direction)) {
            calls.get(floor).add(direction);
            System.out.println("Call added: floor " + floor + " going " + direction);
        }
    }
    
    /**
     * Adds a destination floor
     * 
     * @param floor the destination floor
     */
    public void addDestination(int floor) {
        if (!destinations.contains(floor) && floor != currentFloor) {
            destinations.add(floor);
            System.out.println("Destination added: floor " + floor);
        }
    }
    
    /**
     * Checks if there's a call at a specific floor in a specific direction
     * 
     * @param floor the floor to check
     * @param direction the direction to check
     * @return true if call exists, false otherwise
     */
    public boolean hasCallAtFloor(int floor, Direction direction) {
        return calls.containsKey(floor) && calls.get(floor).contains(direction);
    }
    
    /**
     * Checks if a specific floor is in the destinations list
     * 
     * @param floor the floor to check
     * @return true if floor is a destination, false otherwise
     */
    public boolean hasDestination(int floor) {
        return destinations.contains(floor);
    }
    
    /**
     * Adds a user to the elevator
     * 
     * @param user the user to add
     */
    public void addUser(User user) {
        if (!usersInside.contains(user)) {
            usersInside.add(user);
        }
    }
    
    /**
     * Removes a user from the elevator
     * 
     * @param user the user to remove
     */
    public void removeUser(User user) {
        usersInside.remove(user);
    }
    
    /**
     * Checks if a specific user is inside the elevator
     * 
     * @param user the user to check
     * @return true if user is inside, false otherwise
     */
    public boolean hasUserInside(User user) {
        return usersInside.contains(user);
    }
    
    /**
     * Moves the elevator one floor in the current direction
     * Handles direction reversal at boundaries
     */
    public void move() {
        if (!canRestart) {
            return;
        }
        
        isStopped = false;
        
        // Move based on direction
        if (direction == Direction.UP) {
            if (currentFloor < highestFloor) {
                currentFloor++;
                System.out.println("Elevator moving UP to floor " + currentFloor);
            } else {
                // Reached highest floor, reverse direction
                direction = Direction.DOWN;
                System.out.println("Elevator at highest floor, reversing to DOWN");
            }
        } else if (direction == Direction.DOWN) {
            if (currentFloor > lowestFloor) {
                currentFloor--;
                System.out.println("Elevator moving DOWN to floor " + currentFloor);
            } else {
                // Reached lowest floor, reverse direction
                direction = Direction.UP;
                System.out.println("Elevator at lowest floor, reversing to UP");
            }
        }
        
        // Check if should stop at this floor
        if (shouldStopAtCurrentFloor()) {
            stop();
            clearCallsAndDestinations();
            canRestart = false; // Wait for door to close
        }
    }
    
    /**
     * Checks if the elevator should stop at the current floor
     * 
     * @return true if should stop, false otherwise
     */
    private boolean shouldStopAtCurrentFloor() {
        // Stop if there's a destination for this floor
        if (destinations.contains(currentFloor)) {
            return true;
        }
        
        // Stop if there's a call in the current direction
        if (calls.containsKey(currentFloor)) {
            List<Direction> callDirections = calls.get(currentFloor);
            if (callDirections.contains(direction) || direction == Direction.NONE) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Clears calls and destinations for the current floor
     */
    private void clearCallsAndDestinations() {
        // Remove destination for current floor
        destinations.remove(Integer.valueOf(currentFloor));
        
        // Remove calls for current floor in current direction
        if (calls.containsKey(currentFloor)) {
            if (direction != Direction.NONE) {
                calls.get(currentFloor).remove(direction);
            }
            if (calls.get(currentFloor).isEmpty()) {
                calls.remove(currentFloor);
            }
        }
        
        System.out.println("Cleared calls and destinations for floor " + currentFloor);
    }
    
    /**
     * Chooses the direction for the elevator to move
     * Implements the direction selection algorithm from the specification
     * 
     * @return the chosen direction
     */
    public Direction chooseDirection() {
        // If there's a call on current floor, set direction to NONE to handle it
        if (calls.containsKey(currentFloor) && !calls.get(currentFloor).isEmpty()) {
            direction = Direction.NONE;
            System.out.println("Call on current floor, direction set to NONE");
            return direction;
        }
        
        // If no calls or destinations, no direction needed
        if (calls.isEmpty() && destinations.isEmpty()) {
            direction = Direction.NONE;
            System.out.println("No calls or destinations, direction set to NONE");
            return direction;
        }
        
        // Check for calls or destinations in current direction
        if (direction != Direction.NONE) {
            if (hasCallsOrDestinationsInDirection(direction)) {
                // Check if we're at boundary
                if ((direction == Direction.UP && currentFloor == highestFloor) ||
                    (direction == Direction.DOWN && currentFloor == lowestFloor)) {
                    // At boundary, check opposite direction
                    Direction opposite = direction.opposite();
                    if (hasCallsOrDestinationsInDirection(opposite)) {
                        direction = opposite;
                        System.out.println("At boundary, switching to " + direction);
                    } else {
                        direction = Direction.NONE;
                        System.out.println("At boundary, no calls in opposite direction, set to NONE");
                    }
                } else {
                    // Continue in current direction
                    System.out.println("Maintaining direction: " + direction);
                }
                return direction;
            }
        }
        
        // No calls in current direction, check opposite
        if (direction != Direction.NONE) {
            Direction opposite = direction.opposite();
            if ((opposite == Direction.UP && currentFloor < highestFloor) ||
                (opposite == Direction.DOWN && currentFloor > lowestFloor)) {
                if (hasCallsOrDestinationsInDirection(opposite)) {
                    direction = opposite;
                    System.out.println("No calls ahead, switching to " + direction);
                    return direction;
                }
            }
        }
        
        // No common direction, start looking upwards
        if (currentFloor < highestFloor && hasCallsOrDestinationsInDirection(Direction.UP)) {
            direction = Direction.UP;
            System.out.println("Default: choosing UP direction");
        } else if (currentFloor > lowestFloor && hasCallsOrDestinationsInDirection(Direction.DOWN)) {
            direction = Direction.DOWN;
            System.out.println("Default: choosing DOWN direction");
        } else {
            direction = Direction.NONE;
            System.out.println("No valid direction found, set to NONE");
        }
        
        return direction;
    }
    
    /**
     * Checks if there are any calls or destinations in a specific direction
     * 
     * @param dir the direction to check
     * @return true if there are calls or destinations in that direction
     */
    private boolean hasCallsOrDestinationsInDirection(Direction dir) {
        if (dir == Direction.UP) {
            // Check for calls above current floor
            for (Integer floor : calls.keySet()) {
                if (floor > currentFloor) {
                    return true;
                }
            }
            // Check for destinations above current floor
            for (Integer floor : destinations) {
                if (floor > currentFloor) {
                    return true;
                }
            }
        } else if (dir == Direction.DOWN) {
            // Check for calls below current floor
            for (Integer floor : calls.keySet()) {
                if (floor < currentFloor) {
                    return true;
                }
            }
            // Check for destinations below current floor
            for (Integer floor : destinations) {
                if (floor < currentFloor) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Called by a door when it has closed
     * Signals that the elevator can restart
     * 
     * @param floor the floor where the door closed
     */
    public void doorClosed(int floor) {
        if (floor == currentFloor) {
            canRestart = true;
            System.out.println("Elevator received door closed signal, can restart");
        }
    }
    
    /**
     * Moves the elevator directly to a specific floor
     * Used primarily for testing
     * 
     * @param targetFloor the floor to move to
     */
    public void moveToFloor(int targetFloor) {
        while (currentFloor != targetFloor && canRestart) {
            if (targetFloor > currentFloor) {
                direction = Direction.UP;
            } else {
                direction = Direction.DOWN;
            }
            move();
        }
        stop();
    }
    
    /**
     * Checks if the elevator has reached a specific floor
     * 
     * @param floor the floor to check
     * @return true if at that floor and stopped, false otherwise
     */
    public boolean hasReachedFloor(int floor) {
        return currentFloor == floor && isStopped;
    }
    
    /**
     * Gets the number of users currently in the elevator
     * 
     * @return number of users
     */
    public int getUserCount() {
        return usersInside.size();
    }
    
    /**
     * Returns string representation of elevator state
     * 
     * @return string describing elevator state
     */
    @Override
    public String toString() {
        return "Elevator[floor=" + currentFloor + ", direction=" + direction + 
               ", stopped=" + isStopped + ", users=" + usersInside.size() + 
               ", destinations=" + destinations.size() + ", calls=" + calls.size() + "]";
    }
}
