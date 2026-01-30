package elevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for User behavior validation
 * Tests user calling elevator, entering, and reaching destination
 * 
 * @author FERKIOUI Akram, BOUSSEKINE Mohamed Ismail, HAMMOUTI Walid, BOUDISSA Farouk Radouane
 * @version 1.0
 */
public class UserTest {
    
    private User user;
    private Elevator elevator;
    private Door door;
    
    @BeforeEach
    public void setUp() {
        elevator = new Elevator(0, 5);
        user = new User(1, 3, Direction.UP); // floor 1, destination 3, going UP
        door = new Door(1, elevator);
    }
    
    /**
     * Test Case 7: User calls elevator
     * Expected: Call is registered in elevator's call list
     */
    @Test
    public void testUserCallsElevator() {
        user.callElevator(elevator);
        
        assertTrue(elevator.hasCallAtFloor(1, Direction.UP), 
            "Elevator should have registered call at floor 1 going UP");
    }
    
    /**
     * Test Case 8: User waits for opposite direction call on same floor
     * Expected: User does not make new call if opposite direction exists
     */
    @Test
    public void testUserWaitsForOppositeDirectionCall() {
        // Another user called from same floor going DOWN
        User otherUser = new User(1, 0, Direction.DOWN);
        otherUser.callElevator(elevator);
        
        // Our user wants to go UP
        boolean shouldWait = user.shouldWaitForOppositeCall(elevator);
        
        assertTrue(shouldWait, 
            "User should wait when opposite direction call exists on same floor");
    }
    
    /**
     * Test Case 9: User enters elevator when door is open
     * Expected: User successfully enters if not distracted
     */
    @Test
    public void testUserEntersWhenDoorOpen() {
        elevator.setCurrentFloor(1);
        elevator.stop();
        door.open();
        
        // User decides to enter (not distracted in this test)
        user.setDistracted(false);
        boolean entered = user.tryToEnter(elevator, door);
        
        assertTrue(entered, "User should enter when door is open and not distracted");
        assertTrue(elevator.hasUserInside(user), 
            "Elevator should contain the user");
    }
    
    /**
     * Test Case 10: User may be distracted and miss entry
     * Expected: User does not enter if distracted
     */
    @Test
    public void testUserMayBeDistracted() {
        elevator.setCurrentFloor(1);
        elevator.stop();
        door.open();
        
        // User is distracted
        user.setDistracted(true);
        boolean entered = user.tryToEnter(elevator, door);
        
        assertFalse(entered, 
            "Distracted user should not enter elevator");
    }
    
    /**
     * Test Case 11: User enters destination after boarding
     * Expected: Destination is added to elevator's destination list
     */
    @Test
    public void testUserEntersDestination() {
        elevator.setCurrentFloor(1);
        user.setDistracted(false);
        door.open();
        user.tryToEnter(elevator, door);
        
        user.enterDestination(elevator);
        
        assertTrue(elevator.hasDestination(3), 
            "Elevator should have destination floor 3");
    }
    
    /**
     * Test Case 12: User completes full journey
     * Expected: User travels from source to destination
     */
    @Test
    public void testUserCompleteJourney() {
        int startFloor = user.getCurrentFloor();
        int destinationFloor = user.getDestination();
        
        // Simulate complete journey
        user.callElevator(elevator);
        elevator.setCurrentFloor(startFloor);
        elevator.stop();
        door.open();
        user.setDistracted(false);
        user.tryToEnter(elevator, door);
        user.enterDestination(elevator);
        door.close();
        
        // Elevator travels to destination
        elevator.moveToFloor(destinationFloor);
        elevator.stop();
        
        Door destDoor = new Door(destinationFloor, elevator);
        destDoor.open();
        user.exit(elevator);
        
        assertEquals(destinationFloor, user.getCurrentFloor(), 
            "User should be at destination floor after journey");
        
        // Verify travel distance
        int expectedDistance = Math.abs(destinationFloor - startFloor);
        int actualDistance = user.getTravelDistance();
        assertEquals(expectedDistance, actualDistance, 
            "Travel distance should equal source to destination");
    }
    
    /**
     * Test Case 13: User travel distance validation
     * Expected: Distance matches expected calculation
     */
    @Test
    public void testUserTravelDistanceValidation() {
        User testUser = new User(0, 5, Direction.UP);
        testUser.setDistracted(false);
        
        testUser.callElevator(elevator);
        elevator.setCurrentFloor(0);
        elevator.stop();
        
        Door door0 = new Door(0, elevator);
        door0.open();
        testUser.tryToEnter(elevator, door0);
        testUser.enterDestination(elevator);
        door0.close();
        
        elevator.moveToFloor(5);
        Door door5 = new Door(5, elevator);
        door5.open();
        testUser.exit(elevator);
        
        assertEquals(5, testUser.getTravelDistance(), 
            "Travel distance should be 5 floors");
    }
    
    /**
     * Test Case 14: User handles door closing during entry
     * Expected: User successfully enters before door closes
     * NOTE: This test may fail due to timing issues - see error analysis in report
     */
    @Test
    public void testUserHandlesDoorClosing() throws InterruptedException {
        elevator.setCurrentFloor(1);
        elevator.stop();
        door.open();
        
        user.setDistracted(false);
        
        // Start door close timer with very short timeout
        door.startCloseTimer(100); // 100ms timeout
        
        // User tries to enter (has built-in delay)
        boolean entered = user.tryToEnter(elevator, door);
        
        // This assertion may fail if user entry takes longer than door timeout
        assertTrue(entered, 
            "User should successfully enter before door closes");
    }
    
    /**
     * Test Case 15: User waits for door to open
     * Expected: User can wait for door and detect when it opens
     */
    @Test
    public void testUserWaitsForDoorToOpen() {
        elevator.setCurrentFloor(1);
        elevator.stop();
        
        // Door is initially closed
        assertFalse(door.isOpen(), "Door should be closed initially");
        
        // Open door in separate thread after delay
        new Thread(() -> {
            try {
                Thread.sleep(200);
                door.open();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        // User waits for door
        boolean doorOpened = user.waitForDoorToOpen(door, 1000);
        
        assertTrue(doorOpened, "User should detect door opening within timeout");
        assertTrue(door.isOpen(), "Door should be open");
    }
    
    /**
     * Test Case 16: Multiple users on different floors
     * Expected: Each user operates independently
     */
    @Test
    public void testMultipleUsersOnDifferentFloors() {
        User user1 = new User(0, 3, Direction.UP);
        User user2 = new User(2, 5, Direction.UP);
        User user3 = new User(4, 1, Direction.DOWN);
        
        user1.callElevator(elevator);
        user2.callElevator(elevator);
        user3.callElevator(elevator);
        
        assertTrue(elevator.hasCallAtFloor(0, Direction.UP), 
            "Call from floor 0 should be registered");
        assertTrue(elevator.hasCallAtFloor(2, Direction.UP), 
            "Call from floor 2 should be registered");
        assertTrue(elevator.hasCallAtFloor(4, Direction.DOWN), 
            "Call from floor 4 should be registered");
    }
}
