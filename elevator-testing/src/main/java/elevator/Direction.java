package elevator;

/**
 * Enum representing the direction of elevator or user movement
 * 
 * @author FERKIOUI Akram, BOUSSEKINE Mohamed Ismail, HAMMOUTI Walid, BOUDISSA Farouk Radouane
 * @version 1.0
 */
public enum Direction {
    /**
     * Upward direction (toward higher floors)
     */
    UP,
    
    /**
     * Downward direction (toward lower floors)
     */
    DOWN,
    
    /**
     * No direction - elevator is stationary or direction not determined
     */
    NONE;
    
    /**
     * Returns the opposite direction
     * UP becomes DOWN, DOWN becomes UP, NONE remains NONE
     * 
     * @return the opposite direction
     */
    public Direction opposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            default:
                return NONE;
        }
    }
    
    /**
     * Checks if this direction is opposite to another direction
     * 
     * @param other the other direction to compare
     * @return true if directions are opposite, false otherwise
     */
    public boolean isOpposite(Direction other) {
        return (this == UP && other == DOWN) || (this == DOWN && other == UP);
    }
}
