package elevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Door behavior validation
 * Tests door opening, closing, and synchronization with elevator
 * 
 * @author FERKIOUI Akram, BOUSSEKINE Mohamed Ismail, HAMMOUTI Walid, BOUDISSA Farouk Radouane
 * @version 1.0
 */
public class DoorTest {
    
    private Door door;
    private Elevator elevator;
    private int testFloor = 1;
    
    @BeforeEach
    public void setUp() {
        // Initialize test environment
        elevator = new Elevator(0, 5); // floors 0 to 5
        door = new Door(testFloor, elevator);
    }
    
    /**
     * Test Case 1: Door opens only when elevator stops at its floor
     * Expected: Door remains closed until elevator arrives
     */
    @Test
    public void testDoorOpensWhenElevatorArrives() {
        // Initial state: door is closed
        assertFalse(door.isOpen(), 
            "Door should be closed initially");
        
        // Elevator is on different floor
        elevator.setCurrentFloor(0);
        door.checkElevatorArrival();
        assertFalse(door.isOpen(), 
            "Door should remain closed when elevator is on different floor");
        
        // Elevator arrives at door's floor
        elevator.setCurrentFloor(testFloor);
        elevator.stop();
        door.checkElevatorArrival();
        
        // Door should now open
        assertTrue(door.isOpen(), 
            "Door should open when elevator arrives and stops");
    }
    
    /**
     * Test Case 2: Door closes after timeout
     * Expected: Door closes automatically after configured time
     */
    @Test
    public void testDoorClosesAfterTimeout() throws InterruptedException {
        // Simulate elevator arrival
        elevator.setCurrentFloor(testFloor);
        elevator.stop();
        door.open();
        
        assertTrue(door.isOpen(), "Door should be open");
        
        // Wait for door timeout (simulated)
        door.startCloseTimer(1000); // 1 second timeout
        Thread.sleep(1200);
        door.checkTimeout();
        
        assertFalse(door.isOpen(), 
            "Door should close after timeout");
    }
    
    /**
     * Test Case 3: Door signals elevator after closing
     * Expected: Elevator receives restart signal
     */
    @Test
    public void testDoorSignalsElevatorAfterClosing() {
        elevator.setCurrentFloor(testFloor);
        elevator.stop();
        door.open();
        door.close();
        
        // Verify signal was sent
        assertTrue(elevator.canRestart(), 
            "Elevator should receive restart signal after door closes");
    }
    
    /**
     * Test Case 4: Door remains closed when elevator is elsewhere
     * Expected: Door does not open when elevator is on different floor
     */
    @Test
    public void testDoorRemainsClosedWhenElevatorElsewhere() {
        elevator.setCurrentFloor(3);
        elevator.stop();
        
        door.checkElevatorArrival();
        
        assertFalse(door.isOpen(), 
            "Door should remain closed when elevator is on different floor");
    }
    
    /**
     * Test Case 5: Door handles multiple open/close cycles
     * Expected: Door can be opened and closed multiple times
     */
    @Test
    public void testDoorHandlesMultipleCycles() {
        elevator.setCurrentFloor(testFloor);
        elevator.stop();
        
        // First cycle
        door.open();
        assertTrue(door.isOpen(), "Door should open on first cycle");
        door.close();
        assertFalse(door.isOpen(), "Door should close after first cycle");
        
        // Second cycle
        door.open();
        assertTrue(door.isOpen(), "Door should open on second cycle");
        door.close();
        assertFalse(door.isOpen(), "Door should close after second cycle");
        
        // Third cycle
        door.open();
        assertTrue(door.isOpen(), "Door should open on third cycle");
        door.close();
        assertFalse(door.isOpen(), "Door should close after third cycle");
    }
    
    /**
     * Test Case 6: Door synchronizes with elevator state
     * Expected: Door state is consistent with elevator state
     */
    @Test
    public void testDoorSynchronizesWithElevator() {
        // Elevator moving - door should not open
        elevator.setCurrentFloor(testFloor);
        elevator.setDirection(Direction.UP);
        door.checkElevatorArrival();
        assertFalse(door.isOpen(), 
            "Door should not open when elevator is moving");
        
        // Elevator stops - door should open
        elevator.stop();
        door.checkElevatorArrival();
        assertTrue(door.isOpen(), 
            "Door should open when elevator stops at floor");
    }
}
