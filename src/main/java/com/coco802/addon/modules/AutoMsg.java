package com.coco802.addon.modules;

import com.coco802.addon.Coco802Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;

import java.util.*;

/**
 * Envoie automatiquement un /msg a chaque joueur connecte, un par un.
 * - message configurable ({player} est remplace par le pseudo)
 * - liste de pseudos ignores (admins, media, amis...)
 * - delai entre chaque message
 * - chaque joueur ne recoit le message qu'une seule fois par session
 */
public class AutoMsg extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> message = sgGeneral.add(new StringSetting.Builder()
        .name("message")
        .description("Message a envoyer. {player} = pseudo du joueur.")
        .defaultValue("Salut {player} !")
        .build()
    );

    private final Setting<List<String>> ignored = sgGeneral.add(new StringListSetting.Builder()
        .name("ignored-players")
        .description("Pseudos a ne jamais contacter (admins, media, amis...).")
        .defaultValue(new ArrayList<>())
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Ticks entre deux messages (20 ticks = 1 seconde).")
        .defaultValue(200)
        .min(40)
        .sliderRange(40, 1200)
        .build()
    );

    private final Setting<Boolean> notify = sgGeneral.add(new BoolSetting.Builder()
        .name("notifications")
        .description("Affiche dans le chat a qui le message a ete envoye.")
        .defaultValue(true)
        .build()
    );

    private final Set<String> alreadySent = new HashSet<>();
    private int timer = 0;

    public AutoMsg() {
        // Categorie Coco802_ definie dans Coco802Addon
        super(Coco802Addon.CATEGORY, "auto-msg", "Envoie un /msg a chaque joueur connecte, avec une liste d'ignores.");
    }

    @Override
    public void onActivate() {
        alreadySent.clear();
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        if (timer > 0) {
            timer--;
            return;
        }

        String self = mc.player.getGameProfile().name();
        Set<String> skip = new HashSet<>();
        for (String s : ignored.get()) skip.add(s.toLowerCase(Locale.ROOT));

        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            String name = entry.getProfile().name();
            String key = name.toLowerCase(Locale.ROOT);

            if (name.equals(self)) continue;
            if (skip.contains(key)) continue;
            if (alreadySent.contains(key)) continue;

            String text = message.get().replace("{player}", name);
            mc.getNetworkHandler().sendChatCommand("msg " + name + " " + text);
            alreadySent.add(key);
            timer = delay.get();

            if (notify.get()) info("Message envoye a " + name);
            return; // un seul joueur par cycle
        }
    }
}
