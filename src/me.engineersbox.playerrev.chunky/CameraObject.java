package me.engineersbox.playerrev.chunky;

public class CameraObject {

    public String name = "camera";
    public Position position;
    public Orientation orientation;
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

    public me.engineersbox.playerrev.chunky.Position getPosition() {
        return position;
    }

    public void setPosition(me.engineersbox.playerrev.chunky.Position position) {
        this.position = position;
    }

    public me.engineersbox.playerrev.chunky.Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(me.engineersbox.playerrev.chunky.Orientation orientation) {
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
    	return "camera: { " + this.position.toString() + ", " + this.orientation.toString() + ", projectionMode: " + this.projectionMode + ", fov: " + fov + ", dof: " + dof + ", focalOffset: " + focalOffset + " }";
    }
	
}
