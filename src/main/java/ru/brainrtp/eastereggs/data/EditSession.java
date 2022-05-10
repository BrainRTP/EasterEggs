package ru.brainrtp.eastereggs.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.brainrtp.eastereggs.EasterEggs;
import ru.brainrtp.eastereggs.configuration.Language;
import ru.brainrtp.eastereggs.data.eggs.EasterEgg;
import ru.brainrtp.eastereggs.protocol.hologram.Hologram;
import ru.brainrtp.eastereggs.protocol.hologram.Holograms;
import ru.brainrtp.eastereggs.util.highlighter.Highlighter;
import ru.brainrtp.eastereggs.util.highlighter.Highlighters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EditSession {

    private final Language language;

    private final Player player;
    private final Map<Integer, Hologram> holograms = new HashMap<>();
    private final HashMap<Integer, Highlighter> highlighterMap = new HashMap<>();
    private final String category;

    public EditSession(Language language, Player player, String category) {
        this.language = language;
        this.player = player;
        this.category = category;
    }

    public String getEditableCategory() {
        return category;
    }

    public Player getPlayer() {
        return player;
    }

    public void start() {
        Optional<EasterEggCategory> category = EasterEggs.getEggService().getCategory(this.category);
        Collection<EasterEgg> eggs = category.get().getEggs().values();
        eggs.forEach(easterEgg -> highlighterMap.put(easterEgg.getId(), Highlighters.create(easterEgg, player)));
    }

    public void end() {
        highlighterMap.forEach((key, highlighter) -> highlighter.clear(player));
    }

    public void addNewEgg(EasterEgg egg) {
        highlighterMap.put(egg.getId(), Highlighters.create(egg, player));
    }

    public void addHologram(EasterEgg egg) {
        Location loc = egg.getLocation().clone();

        if (EggTypes.BLOCK.equals(egg.getType())) {
            loc = loc.add(0.5, 1.7, 0.5);
        } else {
            loc = loc.add(0, 2.2, 0);
        }

        Hologram hologram = Holograms.create(loc);
        hologram.setText(language.getSingleMessageWithoutPrefix("edit", "hologram").replace("{number}", String.valueOf(egg.getId())));
        hologram.show(player);

        holograms.put(egg.getId(), hologram);
    }

    // TODO: (19.02 20:38) change the name of the method as it is responsible for something else? For example removeHighlighter?
    public void removeHologram(int eggId) {
        highlighterMap.get(eggId).clear(player);
    }

    // TODO: (19.02 20:38) Why isn't it called anywhere? Is it because there is #end() above?
    public void removeAllHolograms() {
        for (Hologram hologram : holograms.values()) {
            hologram.destroy();
        }

        holograms.clear();
    }

}
