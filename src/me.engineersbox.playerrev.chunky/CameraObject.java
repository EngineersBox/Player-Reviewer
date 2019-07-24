package me.engineersbox.playerrev.chunky;

public class CameraObject {

    public String name = "camera";
    public position position;
    public orientation orientation;
    public String projectionMode = "PINHOLE";
    public float fov = 90.0f;
    public String dof = "Infinity";
    public float focalOffset = 2.0f;
    
    public CameraObject(String name) {
    	this.name = name;
    }
    
    public CameraObject() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public me.engineersbox.playerrev.chunky.position getPosition() {
        return position;
    }

    public void setPosition(me.engineersbox.playerrev.chunky.position position) {
        this.position = position;
    }

    public me.engineersbox.playerrev.chunky.orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(me.engineersbox.playerrev.chunky.orientation orientation) {
        this.orientation = orientation;
    }

    public String getProjectionMode() {
        return projectionMode;
    }

    public void setProjectionMode(String projectionMode) {
        this.projectionMode = projectionMode;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public String getDof() {
        return dof;
    }

    public void setDof(String dof) {
        this.dof = dof;
    }

    public float getFocalOffset() {
        return focalOffset;
    }

    public void setFocalOffset(float focalOffset) {
        this.focalOffset = focalOffset;
    }
    
    public String toString() {
    	return "\"" + this.name +"\": { " + this.position.toString() + ", " + this.orientation.toString() + ", \"projectionMode\": \"" + this.projectionMode + "\", \"fov\": " + fov + ", \"dof\": \"" + dof + "\", \"focalOffset\": " + focalOffset + " }";
    }
	
}
