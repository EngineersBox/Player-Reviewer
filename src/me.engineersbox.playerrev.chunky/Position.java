package me.engineersbox.playerrev.chunky;

public class Position {

    public double x = 0;
    public double y = 0;
    public double z = 0;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
    
    public String toString() {
    	return "position: { x:" + this.x + ", y: " + this.y + ", z: " + this.z + " }";
    }
	
}
