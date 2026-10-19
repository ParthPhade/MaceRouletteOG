package dev.swaggerstudio.macerouletteog.storage;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private int wins;
    private int kills;
    private int deaths;
    private int roundsSurvived;

    public PlayerStats(UUID uuid, int wins, int kills, int deaths, int roundsSurvived) {
        this.uuid = uuid;
        this.wins = wins;
        this.kills = kills;
        this.deaths = deaths;
        this.roundsSurvived = roundsSurvived;
    }

    public UUID getUuid() { return uuid; }
    public int getWins() { return wins; }
    public void addWin() { this.wins++; }
    public int getKills() { return kills; }
    public void addKill() { this.kills++; }
    public int getDeaths() { return deaths; }
    public void addDeath() { this.deaths++; }
    public int getRoundsSurvived() { return roundsSurvived; }
    public void addRoundSurvived() { this.roundsSurvived++; }
}