package dev.swaggerstudio.macerouletteog.arena;

public enum ArenaState {
    WAITING,      // Waiting for min-players to join
    STARTING,     // Countdown active before the match begins
    SPINNING,     // Roulette wheel spinning to pick the wielder(s)
    ACTIVE_ROUND, // Wielder is hunting survivors with the Mace
    RESETTING,    // Cleaning up entities and reverting dirty blocks
    DISABLED      // Arena is inactive or missing setup points
}