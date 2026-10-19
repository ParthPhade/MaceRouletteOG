package dev.swaggerstudio.macerouletteog.display;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class RouletteWheelController {

    private final MaceRouletteOG plugin;
    private final Location center;
    private ItemDisplay displayEntity;

    public RouletteWheelController(MaceRouletteOG plugin, Location center) {
        this.plugin = plugin;
        this.center = center;
    }

    public void spawnWheel() {
        plugin.getFoliaScheduler().runAtLocation(center, () -> {
            if (center.getWorld() == null) return;

            // Clean up any existing orphan display at this location
            center.getWorld().getNearbyEntities(center, 2, 2, 2).stream()
                    .filter(e -> e instanceof ItemDisplay && e.getPersistentDataContainer().has(plugin.getTempDisplayKey()))
                    .forEach(org.bukkit.entity.Entity::remove);

            this.displayEntity = center.getWorld().spawn(center.clone().add(0, 1.5, 0), ItemDisplay.class, entity -> {
                entity.setItemStack(new ItemStack(Material.MACE));
                entity.setPersistent(false);
                entity.getPersistentDataContainer().set(plugin.getTempDisplayKey(), org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);

                // Initial scale
                Transformation transform = new Transformation(
                        new Vector3f(0, 0, 0),
                        new AxisAngle4f(0, 0, 1, 0),
                        new Vector3f(1.5f, 1.5f, 1.5f),
                        new AxisAngle4f(0, 0, 0, 1)
                );
                entity.setTransformation(transform);
            });
        });
    }

    public void spinToTarget(float targetYawDegrees, int durationTicks) {
        if (displayEntity == null || displayEntity.isDead()) return;

        plugin.getFoliaScheduler().runAtEntity(displayEntity, () -> {
            // Configure client-side interpolation duration for butter-smooth rotation
            displayEntity.setInterpolationDuration(durationTicks);
            displayEntity.setInterpolationDelay(0);

            float radians = (float) Math.toRadians(targetYawDegrees + 720.0f); // 2 full spins + target angle
            Transformation newTransform = new Transformation(
                    new Vector3f(0, 0, 0),
                    new AxisAngle4f(radians, 0, 1, 0),
                    new Vector3f(1.5f, 1.5f, 1.5f),
                    new AxisAngle4f(0, 0, 0, 1)
            );
            displayEntity.setTransformation(newTransform);
        });
    }

    public void removeWheel() {
        if (displayEntity != null && !displayEntity.isDead()) {
            plugin.getFoliaScheduler().runAtEntity(displayEntity, displayEntity::remove);
        }
    }
}