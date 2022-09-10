package ru.brainrtp.eastereggs.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import ru.brainrtp.eastereggs.EasterEggs;
import ru.brainrtp.eastereggs.configuration.Language;
import ru.brainrtp.eastereggs.data.eggs.EasterEgg;
import ru.brainrtp.eastereggs.data.eggs.EggBlock;
import ru.brainrtp.eastereggs.services.EasterEggService;

import java.util.Optional;

public class PlayerInteractListener implements Listener {

    private final Language language;
    private final EasterEggService service;

    public PlayerInteractListener() {
        language = EasterEggs.getLanguage();
        service = EasterEggs.getEggService();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (GameMode.SPECTATOR.equals(player.getGameMode())) {
            return;
        }

        if (service.getEditor().isEditMode(player.getUniqueId())) {
            event.setCancelled(true);

            String category = service.getEditor().getEditCategory(player.getUniqueId());

            if (Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
                if (EquipmentSlot.OFF_HAND.equals(event.getHand())) {
                    return;
                }

                // Create block egg
                Optional<EasterEgg> eggOpt = service.getEasterEgg(event.getClickedBlock().getLocation());

                if (eggOpt.isEmpty()) {
                    EggBlock egg = new EggBlock();
                    egg.setBlockLocation(event.getClickedBlock().getLocation());
                    service.createNewEgg(category, egg);

                    player.sendMessage(language.getSingleMessage("egg", "create", "success")
                            .replace("{egg}", String.valueOf(egg.getId())));
                    return;
                }

                player.sendMessage(language.getSingleMessage("egg", "create", "already"));
                return;
            }

            if (Action.LEFT_CLICK_BLOCK.equals(event.getAction())) {
                if (EquipmentSlot.OFF_HAND.equals(event.getHand())) {
                    return;
                }

                // Remove block egg
                Location location = event.getClickedBlock().getLocation();
                Optional<EasterEgg> egg = service.getCategory(category).get().getEggByLocation(location);

                if (egg.isPresent()) {
                    service.deleteEgg(category, egg.get());
                    player.sendMessage(language.getSingleMessage("egg", "delete", "success")
                            .replace("{egg}", String.valueOf(egg.get().getId()))
                            .replace("{category}", egg.get().getCategory()));
                }
                return;
            }

            return;
        }

        // Activate egg
        if (Action.LEFT_CLICK_BLOCK.equals(event.getAction()) || Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
            if (EquipmentSlot.OFF_HAND.equals(event.getHand())) {
                return;
            }

            Optional<EasterEgg> egg = service.getEasterEgg(event.getClickedBlock().getLocation());

            if (egg.isPresent()) {
                event.setCancelled(true);

                if (!service.getPlayerService().isCompletedEgg(player.getUniqueId(), egg.get())) {
                    egg.get().activate(player);
                    return;
                }

                player.sendMessage(language.getSingleMessage("egg", "find", "already"));
            }
        }
    }
}
