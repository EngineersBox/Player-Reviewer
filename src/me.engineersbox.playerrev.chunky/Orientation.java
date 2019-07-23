package me.engineersbox.playerrev.chunky;

public class Orientation {

	public double roll = 0;
    public double pitch = 0;
    public double yaw = 0;

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }
    
    public String toString() {
    	return "orientation: { roll: " + this.roll + ", pitch: " + this.pitch + ", yaw: " + this.yaw + " }";
    }
	
}
