package ru.brainrtp.eastereggs.protocol.npcs;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.event.PlayerNPCHideEvent;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import ru.brainrtp.eastereggs.data.NPCData;
import ru.brainrtp.eastereggs.data.Skin;

import java.util.Random;
import java.util.UUID;

public class NPCBuilder implements Listener {

    private final NPCPool npcPool;

    private final Random random;

    public NPCBuilder(Plugin plugin, NPCPool npcPool) {

        this.npcPool = npcPool;
        this.random = new Random();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public NPC appendNPC(NPCData npcData) {
        return NPC.builder()
                .profile(this.createProfile(npcData))
                .location(npcData.getLocation())
                .imitatePlayer(false)
                .lookAtPlayer(false)
                .build(this.npcPool);
    }

    public Profile createProfile(NPCData npcData) {
        Skin skin = npcData.getSkin();
        Profile.Property property = new Profile.Property("textures", skin.getValue(), skin.getSignature());
        Profile profile = new Profile(new UUID(this.random.nextLong(), 0));
        profile.setProperty(property);

        // Synchronously completing the profile, as we only created the profile with a UUID.
        // Through this, the name and textures will be filled in.
        profile.complete();

        // we want to keep the textures, but change the name and UUID.
        profile.setName(npcData.getName());
        // with a UUID like this, the NPC can play LabyMod emotes!
        profile.setUniqueId(new UUID(this.random.nextLong(), 0));

        return profile;
    }


    /**
     * Doing something when a NPC is hidden for a certain player.
     *
     * @param event The event instance
     */
    @EventHandler
    public void handleNPCHide(PlayerNPCHideEvent event) {
        Player player = event.getPlayer();
        NPC npc = event.getNPC();

        // if the player has been excluded from seeing the NPC, removing him from the excluded players
        if (event.getReason() == PlayerNPCHideEvent.Reason.EXCLUDED) {
            npc.removeExcludedPlayer(player);
        }
    }

    /**
     * Doing something when a player interacts with a NPC.
     *
     * @param event The event instance
     */
    @EventHandler
    public void handleNPCInteract(PlayerNPCInteractEvent event) {
        Player player = event.getPlayer();
        NPC npc = event.getNPC();

        // checking if the player hit the NPC
        if (event.getUseAction() == PlayerNPCInteractEvent.EntityUseAction.ATTACK) {
            player.sendMessage("Why are you hitting me? :(");
            // making the NPC look at the player
            npc.rotation()
                    .queueLookAt(player.getEyeLocation())
                    // sending the change only to the player who interacted with the NPC
                    .send(player);
        }
    }

}