package elevator;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a door at a specific floor in the elevator system
 * Handles opening, closing, and synchronization with the elevator
 * 
 * @author FERKIOUI Akram, BOUSSEKINE Mohamed Ismail, HAMMOUTI Walid, BOUDISSA Farouk Radouane
 * @version 1.0
 */
public class Door {
    private int floor;
    private boolean isOpen;
    private Elevator elevator;
    private Timer closeTimer;
    private List<User> usersEntering;
    private static final int DEFAULT_OPEN_TIME = 2000; // 2 seconds in milliseconds
    
    /**
     * Constructor for Door
     * 
     * @param floor the floor number where this door is located
     * @param elevator reference to the elevator this door serves
     */
    public Door(int floor, Elevator elevator) {
        this.floor = floor;
        this.isOpen = false;
        this.elevator = elevator;
        this.usersEntering = new ArrayList<>();
    }
    
    /**
     * Gets the floor number of this door
     * 
     * @return the floor number
     */
    public int getFloor() {
        return floor;
    }
    
    /**
     * Checks if the door is currently open
     * 
     * @return true if door is open, false otherwise
     */
    public boolean isOpen() {
        return isOpen;
    }
    
    /**
     * Checks if the elevator has arrived at this door's floor
     * If yes, opens the door
     */
    public void checkElevatorArrival() {
        if (elevator.getCurrentFloor() == this.floor && elevator.isStopped()) {
            open();
        }
    }
    
    /**
     * Opens the door
     * Starts a timer to automatically close the door after a delay
     */
    public void open() {
        if (!isOpen) {
            isOpen = true;
            System.out.println("Door at floor " + floor + " is opening");
            startCloseTimer(DEFAULT_OPEN_TIME);
        }
    }
    
    /**
     * Closes the door
     * Signals the elevator that it can restart after door closes
     */
    public void close() {
        if (isOpen) {
            isOpen = false;
            System.out.println("Door at floor " + floor + " is closing");
            if (closeTimer != null) {
                closeTimer.cancel();
                closeTimer = null;
            }
            signalElevator();
        }
    }
    
    /**
     * Starts a timer to automatically close the door
     * Checks if users are currently entering before closing
     * 
     * @param milliseconds time in milliseconds before door closes
     */
    public void startCloseTimer(int milliseconds) {
        if (closeTimer != null) {
            closeTimer.cancel();
        }
        
        closeTimer = new Timer();
        closeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if any user is actively entering
                if (!isUserCurrentlyEntering()) {
                    close();
                } else {
                    // Give users more time to enter
                    startCloseTimer(500);
                }
            }
        }, milliseconds);
    }
    
    /**
     * Checks if any user is currently in the process of entering
     * 
     * @return true if a user is entering, false otherwise
     */
    public boolean isUserCurrentlyEntering() {
        return !usersEntering.isEmpty();
    }
    
    /**
     * Marks that a user has started entering through this door
     * 
     * @param user the user who is entering
     */
    public void markUserEntering(User user) {
        if (!usersEntering.contains(user)) {
            usersEntering.add(user);
        }
    }
    
    /**
     * Marks that a user has finished entering through this door
     * 
     * @param user the user who has entered
     */
    public void markUserEntered(User user) {
        usersEntering.remove(user);
    }
    
    /**
     * Forces the door to check its timeout and close if time has elapsed
     * Used primarily for testing purposes
     */
    public void checkTimeout() {
        // This method is used in testing to simulate time passing
        if (isOpen && !isUserCurrentlyEntering()) {
            close();
        }
    }
    
    /**
     * Signals to the elevator that the door has closed and elevator can restart
     */
    private void signalElevator() {
        elevator.doorClosed(this.floor);
        System.out.println("Door at floor " + floor + " signals elevator can restart");
    }
    
    /**
     * Forces the door to close immediately without timer
     * Used for emergency or testing scenarios
     */
    public void forceClose() {
        if (closeTimer != null) {
            closeTimer.cancel();
            closeTimer = null;
        }
        isOpen = false;
    }
    
    /**
     * Returns string representation of door state
     * 
     * @return string describing door state
     */
    @Override
    public String toString() {
        return "Door[floor=" + floor + ", open=" + isOpen + "]";
    }
}
