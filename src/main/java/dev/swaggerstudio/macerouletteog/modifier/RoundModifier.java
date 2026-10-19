package dev.swaggerstudio.macerouletteog.modifier;

import dev.swaggerstudio.macerouletteog.arena.Arena;
import org.bukkit.configuration.ConfigurationSection;

public abstract class RoundModifier {

    protected String id;
    protected String name;
    protected String description;
    protected int weight;

    public RoundModifier(String id, ConfigurationSection section) {
        this.id = id;
        this.name = section.getString("name", id);
        this.description = section.getString("description", "");
        this.weight = section.getInt("weight", 10);
    }

    public abstract void onRoundStart(Arena arena);

    public abstract void onRoundEnd(Arena arena);

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getWeight() { return weight; }
}