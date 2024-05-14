package net.jadedmc.cactusrush.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {

    /**
     * Runs when an entity's food level changes.
     * @param event Food Level Change Event.
     */
    @EventHandler
    public void onEvent(FoodLevelChangeEvent event) {
        // Fill the hunger bar and cancel the event.
        event.getEntity().setFoodLevel(20);
        event.setCancelled(true);
    }
}