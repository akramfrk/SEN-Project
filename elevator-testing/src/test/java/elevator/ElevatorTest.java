package elevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Elevator controller logic
 * Tests direction selection, stopping logic, and queue management
 * 
 * @author FERKIOUI Akram, BOUSSEKINE Mohamed Ismail, HAMMOUTI Walid, BOUDISSA Farouk Radouane
 * @version 1.0
 */
public class ElevatorTest {
    
    private Elevator elevator;
    
    @BeforeEach
    public void setUp() {
        elevator = new Elevator(0, 5); // 6 floors: 0 to 5
        elevator.setCurrentFloor(2); // Start at middle floor
    }
    
    /**
     * Test Case 17: Elevator chooses direction based on calls
     * Expected: Moves in direction of nearest call
     */
    @Test
    public void testElevatorChoosesDirectionForCall() {
        elevator.addCall(4, Direction.UP); // Call from floor 4
        
        Direction chosenDirection = elevator.chooseDirection();
        
        assertEquals(Direction.UP, chosenDirection, 
            "Elevator should move UP toward call at floor 4");
    }
    
    /**
     * Test Case 18: Elevator reverses at highest floor
     * Expected: Direction changes to DOWN at top
     */
    @Test
    public void testElevatorReversesAtHighestFloor() {
        elevator.setCurrentFloor(5); // Top floor
        elevator.setDirection(Direction.UP);
        
        elevator.move();
        
        assertEquals(Direction.DOWN, elevator.getDirection(), 
            "Elevator should reverse to DOWN at highest floor");
    }
    
    /**
     * Test Case 19: Elevator reverses at lowest floor
     * Expected: Direction changes to UP at bottom
     */
    @Test
    public void testElevatorReversesAtLowestFloor() {
        elevator.setCurrentFloor(0); // Bottom floor
        elevator.setDirection(Direction.DOWN);
        
        elevator.move();
        
        assertEquals(Direction.UP, elevator.getDirection(), 
            "Elevator should reverse to UP at lowest floor");
    }
    
    /**
     * Test Case 20: Elevator stops when reaching call floor
     * Expected: Elevator stops and opens door
     */
    @Test
    public void testElevatorStopsAtCallFloor() {
        elevator.setCurrentFloor(2);
        elevator.addCall(3, Direction.UP);
        elevator.setDirection(Direction.UP);
        
        elevator.move(); // Move to floor 3
        
        assertEquals(3, elevator.getCurrentFloor(), 
            "Elevator should be at floor 3");
        assertTrue(elevator.isStopped(), 
            "Elevator should stop at call floor");
        assertFalse(elevator.hasCallAtFloor(3, Direction.UP), 
            "Call at floor 3 should be cleared after stopping");
    }
    
    /**
     * Test Case 21: Elevator clears destination when reached
     * Expected: Destination removed from list
     */
    @Test
    public void testElevatorClearsDestinationWhenReached() {
        elevator.setCurrentFloor(2);
        elevator.addDestination(4);
        elevator.setDirection(Direction.UP);
        
        // Move to floor 3
        elevator.move();
        assertTrue(elevator.hasDestination(4), 
            "Destination should still exist before reaching");
        
        // Move to floor 4
        elevator.move();
        assertEquals(4, elevator.getCurrentFloor());
        assertFalse(elevator.hasDestination(4), 
            "Destination should be cleared after reaching floor 4");
    }
    
    /**
     * Test Case 22: Elevator maintains direction with calls ahead
     * Expected: Continues in same direction
     */
    @Test
    public void testElevatorMaintainsDirectionWithCallsAhead() {
        elevator.setCurrentFloor(2);
        elevator.setDirection(Direction.UP);
        elevator.addCall(3, Direction.UP);
        elevator.addCall(4, Direction.UP);
        
        elevator.move(); // Move to 3
        elevator.chooseDirection();
        
        assertEquals(Direction.UP, elevator.getDirection(), 
            "Elevator should maintain UP direction with more calls ahead");
    }
    
    /**
     * Test Case 23: Elevator switches direction when no calls in current direction
     * Expected: Changes to opposite direction if calls exist there
     */
    @Test
    public void testElevatorSwitchesDirectionWhenNoCallsAhead() {
        elevator.setCurrentFloor(4);
        elevator.setDirection(Direction.UP);
        elevator.addCall(2, Direction.DOWN); // Call below current position
        
        elevator.move(); // Try to move up (reaches top)
        Direction newDirection = elevator.chooseDirection();
        
        assertEquals(Direction.DOWN, newDirection, 
            "Elevator should switch to DOWN when no calls above");
    }
    
    /**
     * Test Case 24: Elevator indicates no direction when no calls exist
     * Expected: Direction set to NONE
     */
    @Test
    public void testElevatorNoDirectionWhenNoCalls() {
        elevator.setCurrentFloor(3);
        // No calls or destinations
        
        Direction direction = elevator.chooseDirection();
        
        assertEquals(Direction.NONE, direction, 
            "Elevator should have no direction when no calls exist");
    }
    
    /**
     * Test Case 25: Elevator handles call on current floor
     * Expected: Sets direction to NONE to allow entry
     */
    @Test
    public void testElevatorHandlesCallOnCurrentFloor() {
        elevator.setCurrentFloor(3);
        elevator.addCall(3, Direction.UP);
        
        elevator.chooseDirection();
        
        assertEquals(Direction.NONE, elevator.getDirection(), 
            "Elevator should set direction to NONE for call on current floor");
    }
    
    /**
     * Test Case 26: Elevator manages multiple destinations
     * Expected: All destinations are handled correctly
     */
    @Test
    public void testElevatorMultipleDestinations() {
        elevator.setCurrentFloor(1);
        elevator.addDestination(2);
        elevator.addDestination(3);
        elevator.addDestination(4);
        
        assertTrue(elevator.hasDestination(2), "Should have destination 2");
        assertTrue(elevator.hasDestination(3), "Should have destination 3");
        assertTrue(elevator.hasDestination(4), "Should have destination 4");
        
        // Move to each destination
        elevator.setDirection(Direction.UP);
        elevator.move(); // To floor 2
        assertFalse(elevator.hasDestination(2), "Destination 2 should be cleared");
        
        elevator.move(); // To floor 3
        assertFalse(elevator.hasDestination(3), "Destination 3 should be cleared");
        
        elevator.move(); // To floor 4
        assertFalse(elevator.hasDestination(4), "Destination 4 should be cleared");
    }
    
    /**
     * Test Case 27: Elevator prioritizes direction correctly
     * Expected: Serves calls in current direction before switching
     */
    @Test
    public void testElevatorPrioritizesCorrectly() {
        elevator.setCurrentFloor(2);
        elevator.setDirection(Direction.UP);
        
        // Calls in both directions
        elevator.addCall(4, Direction.UP);
        elevator.addCall(1, Direction.DOWN);
        
        Direction chosen = elevator.chooseDirection();
        
        assertEquals(Direction.UP, chosen, 
            "Should prioritize UP direction call when moving UP");
    }
    
    /**
     * Test Case 28: Elevator handles simultaneous calls
     * Expected: Processes multiple calls without errors
     */
    @Test
    public void testElevatorSimultaneousCalls() {
        elevator.addCall(0, Direction.UP);
        elevator.addCall(1, Direction.UP);
        elevator.addCall(3, Direction.DOWN);
        elevator.addCall(4, Direction.UP);
        elevator.addCall(5, Direction.DOWN);
        
        // Verify all calls are registered
        assertTrue(elevator.hasCallAtFloor(0, Direction.UP));
        assertTrue(elevator.hasCallAtFloor(1, Direction.UP));
        assertTrue(elevator.hasCallAtFloor(3, Direction.DOWN));
        assertTrue(elevator.hasCallAtFloor(4, Direction.UP));
        assertTrue(elevator.hasCallAtFloor(5, Direction.DOWN));
        
        // Elevator should choose a valid direction
        Direction direction = elevator.chooseDirection();
        assertNotEquals(Direction.NONE, direction, 
            "Elevator should choose a direction when calls exist");
    }
}
