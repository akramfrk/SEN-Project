package elevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for overall system behavior
 * Tests system-wide constraints and component interactions
 * 
 * @author FERKIOUI Akram, BOUSSEKINE Mohamed Ismail, HAMMOUTI Walid, BOUDISSA Farouk Radouane
 * @version 1.0
 */
public class SystemIntegrationTest {
    
    private ElevatorController controller;
    private Elevator elevator;
    private List<Door> doors;
    
    @BeforeEach
    public void setUp() {
        controller = new ElevatorController(6); // 6 floors (0-5)
        elevator = controller.getElevator();
        doors = controller.getDoors();
    }
    
    /**
     * Test Case 29: No doors open when elevator is moving
     * Expected: All doors closed during motion
     */
    @Test
    public void testNoDoorsOpenDuringMotion() {
        elevator.setCurrentFloor(0);
        elevator.addDestination(3);
        elevator.setDirection(Direction.UP);
        
        // Simulate movement through multiple floors
        for (int i = 1; i <= 3; i++) {
            if (elevator.canRestart() && !anyDoorOpen()) {
                elevator.move();
                
                // During movement, check all doors are closed
                if (!elevator.isStopped()) {
                    for (Door door : doors) {
                        assertFalse(door.isOpen(), 
                            "No doors should be open while elevator is moving (floor " + i + ")");
                    }
                }
            }
        }
    }
    
    /**
     * Test Case 30: Only one door open at a time
     * Expected: Maximum one door open at any moment
     */
    @Test
    public void testOnlyOneDoorOpenAtOnce() {
        // Create multiple users on different floors
        User user1 = new User(1, 3, Direction.UP);
        User user2 = new User(2, 4, Direction.UP);
        
        user1.setDistracted(false);
        user2.setDistracted(false);
        
        user1.callElevator(elevator);
        user2.callElevator(elevator);
        
        controller.addUser(user1);
        controller.addUser(user2);
        
        // Simulate system operation
        for (int step = 0; step < 100; step++) {
            controller.step();
            
            // Check at each step that max one door is open
            int openDoorCount = 0;
            for (Door door : doors) {
                if (door.isOpen()) {
                    openDoorCount++;
                }
            }
            
            assertTrue(openDoorCount <= 1, 
                "Maximum one door should be open at step " + step + 
                ", but found " + openDoorCount);
        }
    }
    
    /**
     * Test Case 31: User who requests elevator will enter
     * Expected: User eventually boards the elevator
     */
    @Test
    public void testUserWhoRequestsWillEnter() {
        User user = new User(1, 4, Direction.UP);
        user.setDistracted(false); // Ensure user is not distracted
        
        user.callElevator(elevator);
        controller.addUser(user);
        
        // Run system until user enters
        int maxSteps = 1000;
        boolean userEntered = false;
        
        for (int step = 0; step < maxSteps && !userEntered; step++) {
            controller.step();
            if (elevator.hasUserInside(user)) {
                userEntered = true;
            }
        }
        
        assertTrue(userEntered, 
            "User who requests elevator should eventually enter");
    }
    
    /**
     * Test Case 32: Travel distance equals source to destination
     * Expected: User's travel distance matches expected distance
     */
    @Test
    public void testTravelDistanceCorrect() {
        int startFloor = 1;
        int destFloor = 4;
        int expectedDistance = Math.abs(destFloor - startFloor);
        
        User user = new User(startFloor, destFloor, Direction.UP);
        user.setDistracted(false);
        
        // Complete journey
        controller.addUser(user);
        user.callElevator(elevator);
        
        boolean reachedDestination = controller.runUntilUserReachesDestination(user, 2000);
        
        assertTrue(reachedDestination, "User should reach destination");
        assertEquals(expectedDistance, user.getTravelDistance(), 
            "User travel distance should equal " + expectedDistance + " floors");
        assertEquals(destFloor, user.getCurrentFloor(), 
            "User should be at destination floor " + destFloor);
    }
    
    /**
     * Test Case 33: Multi-user coordination
     * Expected: Multiple users are served correctly
     */
    @Test
    public void testMultiUserCoordination() {
        User user1 = new User(0, 5, Direction.UP);
        User user2 = new User(2, 4, Direction.UP);
        User user3 = new User(5, 1, Direction.DOWN);
        
        user1.setDistracted(false);
        user2.setDistracted(false);
        user3.setDistracted(false);
        
        controller.addUser(user1);
        controller.addUser(user2);
        controller.addUser(user3);
        
        user1.callElevator(elevator);
        user2.callElevator(elevator);
        user3.callElevator(elevator);
        
        // Run simulation
        controller.run(500);
        
        // At least some users should have been served
        int servedUsers = 0;
        if (user1.hasReachedDestination()) servedUsers++;
        if (user2.hasReachedDestination()) servedUsers++;
        if (user3.hasReachedDestination()) servedUsers++;
        
        assertTrue(servedUsers >= 1, 
            "At least one user should be served in multi-user scenario");
    }
    
    /**
     * Test Case 34: Concurrent call handling
     * Expected: System handles multiple simultaneous calls
     */
    @Test
    public void testConcurrentCallHandling() {
        // Create calls from multiple floors
        elevator.addCall(0, Direction.UP);
        elevator.addCall(2, Direction.UP);
        elevator.addCall(4, Direction.DOWN);
        elevator.addCall(5, Direction.DOWN);
        
        // All calls should be registered
        assertTrue(elevator.hasCallAtFloor(0, Direction.UP));
        assertTrue(elevator.hasCallAtFloor(2, Direction.UP));
        assertTrue(elevator.hasCallAtFloor(4, Direction.DOWN));
        assertTrue(elevator.hasCallAtFloor(5, Direction.DOWN));
        
        // System should handle calls without crashing
        controller.run(200);
        
        // After processing, some or all calls should be cleared
        boolean someCallsCleared = 
            !elevator.hasCallAtFloor(0, Direction.UP) ||
            !elevator.hasCallAtFloor(2, Direction.UP) ||
            !elevator.hasCallAtFloor(4, Direction.DOWN) ||
            !elevator.hasCallAtFloor(5, Direction.DOWN);
        
        assertTrue(someCallsCleared, 
            "System should process and clear some calls");
    }
    
    /**
     * Test Case 35: System recovery from edge cases
     * Expected: System continues functioning after edge conditions
     */
    @Test
    public void testSystemRecovery() {
        // Test recovery from boundary conditions
        elevator.setCurrentFloor(0);
        elevator.setDirection(Direction.DOWN);
        
        // Try to move down from lowest floor
        elevator.move();
        
        // System should recover and reverse direction
        assertEquals(Direction.UP, elevator.getDirection(), 
            "System should recover by reversing direction at boundary");
        
        // Test at upper boundary
        elevator.setCurrentFloor(5);
        elevator.setDirection(Direction.UP);
        elevator.move();
        
        assertEquals(Direction.DOWN, elevator.getDirection(), 
            "System should recover by reversing at upper boundary");
    }
    
    /**
     * Test Case 36: Performance under load
     * Expected: System handles many operations efficiently
     */
    @Test
    public void testPerformanceUnderLoad() {
        long startTime = System.currentTimeMillis();
        
        // Create multiple users
        for (int i = 0; i < 10; i++) {
            int start = i % 6;
            int dest = (i + 3) % 6;
            Direction dir = dest > start ? Direction.UP : Direction.DOWN;
            
            User user = new User(start, dest, dir);
            user.setDistracted(false);
            controller.addUser(user);
            user.callElevator(elevator);
        }
        
        // Run simulation
        controller.run(500);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete in reasonable time (< 10 seconds for this test)
        assertTrue(duration < 10000, 
            "System should handle load efficiently (took " + duration + "ms)");
    }
    
    /**
     * Helper method to check if any door is open
     */
    private boolean anyDoorOpen() {
        for (Door door : doors) {
            if (door.isOpen()) {
                return true;
            }
        }
        return false;
    }
}
