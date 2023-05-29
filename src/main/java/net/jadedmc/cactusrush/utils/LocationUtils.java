package net.jadedmc.cactusrush.utils;

import net.jadedmc.cactusrush.CactusRushPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A collection of tools to help deal with locations.
 */
public class LocationUtils {

    /**
     * Get a location from a configuration section.
     * @param config ConfigurationSection for the location.
     * @return Location object stored.
     */
    public static Location fromConfig(ConfigurationSection config) {
        World world;
        if(config.isSet("world")) {
            world = Bukkit.getWorld(config.getString("world"));
        }
        else {
            world = Bukkit.getWorld(config.getString("World"));
        }

        double x,y,z;
        float yaw,pitch;

        if(config.isSet("x")) {
            x = config.getDouble("x");
        }
        else {
            x = config.getDouble("X");
        }

        if(config.isSet("y")) {
            y = config.getDouble("y");
        }
        else {
            y = config.getDouble("Y");
        }

        if(config.isSet("z")) {
            z = config.getDouble("z");
        }
        else {
            z = config.getDouble("Z");
        }

        if(config.isSet("yaw")) {
            yaw = (float) config.getDouble("yaw");
        }
        else {
            yaw = (float) config.getDouble("Yaw");
        }

        if(config.isSet("pitch")) {
            pitch = (float) config.getDouble("pitch");
        }
        else {
            pitch = (float) config.getDouble("Pitch");
        }

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Get the spawn Location from the Config
     * @return Spawn Location
     */
    public static Location getSpawn(CactusRushPlugin plugin) {
        String world = plugin.getSettingsManager().getConfig().getString("Spawn.World");
        double x = plugin.getSettingsManager().getConfig().getDouble("Spawn.X");
        double y = plugin.getSettingsManager().getConfig().getDouble("Spawn.Y");
        double z = plugin.getSettingsManager().getConfig().getDouble("Spawn.Z");
        float pitch = (float) plugin.getSettingsManager().getConfig().getDouble("Spawn.Pitch");
        float yaw = (float) plugin.getSettingsManager().getConfig().getDouble("Spawn.Yaw");

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    /**
     * Set the server spawn to the current location.
     * @param loc Location
     */
    public static void setSpawn(CactusRushPlugin plugin, Location loc) {
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float pitch = loc.getPitch();
        float yaw = loc.getYaw();

        plugin.getSettingsManager().getConfig().set("Spawn.World", world);
        plugin.getSettingsManager().getConfig().set("Spawn.X", x);
        plugin.getSettingsManager().getConfig().set("Spawn.Y", y);
        plugin.getSettingsManager().getConfig().set("Spawn.Z", z);
        plugin.getSettingsManager().getConfig().set("Spawn.Pitch", pitch);
        plugin.getSettingsManager().getConfig().set("Spawn.Yaw", yaw);
        plugin.getSettingsManager().getConfig().set("Spawn.Set", true);

        plugin.getSettingsManager().reloadConfig();
    }
}