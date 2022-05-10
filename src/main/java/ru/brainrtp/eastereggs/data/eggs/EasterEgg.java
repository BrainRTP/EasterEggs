package ru.brainrtp.eastereggs.data.eggs;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.brainrtp.eastereggs.EasterEggs;
import ru.brainrtp.eastereggs.data.EggTypes;
import ru.brainrtp.eastereggs.data.action.Action;
import ru.brainrtp.eastereggs.data.action.Actions;

@Data
public abstract class EasterEgg {

    private int id;

    private String category;

    private EggTypes type;

    private Location location;

    private Actions actions;

    public void addAction(Action action) {
        if (actions == null) {
            actions = new Actions();
        }
        actions.addAction(action);
    }

    public void removeAction(String actionName) {
        if (actions == null) {
            actions = new Actions();
        }
        actions.removeAction(actionName);
    }

    public void activate(Player player) {
        if (!player.hasPermission("ee.type." + category)) {
            return;
        }

        if (actions != null) {
            actions.activate(player);
        }
        EasterEggs.getEggService().addEggToPlayer(player, this);

    }
}
