package me.engineersbox.playerrev.chunky;

import org.bukkit.Location;

public class CoordsObject {

    public Location Position1 = null;
    public Location Position2 = null;

    public Location getPosition1() {
        return Position1;
    }

    public void setPosition1(Location position1) {
        Position1 = position1;
    }

    public Location getPosition2() {
        return Position2;
    }

    public void setPosition2(Location position2) {
        Position2 = position2;
    }
}
