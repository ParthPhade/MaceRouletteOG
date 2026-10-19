package dev.swaggerstudio.macerouletteog.arena.setup;

import org.bukkit.Location;

public class SetupSession {
    private final String arenaName;
    private Location pos1;
    private Location pos2;
    private Location center;
    private Location lobby;
    private Location spectator;

    public SetupSession(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getArenaName() { return arenaName; }
    public Location getPos1() { return pos1; }
    public void setPos1(Location pos1) { this.pos1 = pos1; }
    public Location getPos2() { return pos2; }
    public void setPos2(Location pos2) { this.pos2 = pos2; }
    public Location getCenter() { return center; }
    public void setCenter(Location center) { this.center = center; }
    public Location getLobby() { return lobby; }
    public void setLobby(Location lobby) { this.lobby = lobby; }
    public Location getSpectator() { return spectator; }
    public void setSpectator(Location spectator) { this.spectator = spectator; }
}