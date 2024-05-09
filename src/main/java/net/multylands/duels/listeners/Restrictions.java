package net.multylands.duels.listeners;

import net.multylands.duels.Duels;
import net.multylands.duels.object.DuelRequest;
import net.multylands.duels.utils.Chat;
import net.multylands.duels.utils.RequestUtils;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class Restrictions implements Listener {
    Duels plugin;

    public Restrictions(Duels plugin) {
        this.plugin = plugin;
    }

    //anti enchanted gp
    @EventHandler(ignoreCancelled = true)
    public void onEnchantedGoldenAppleEat(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.ENCHANTED_GOLDEN_APPLE) {
            return;
        }
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isNotchAllowed()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions.deny-message.enchanted-golden-apple"));
        event.setCancelled(true);
    }

    //anti-bow
    @EventHandler(ignoreCancelled = true)
    public void arrowLaunch(ProjectileLaunchEvent event) {
        Entity projectile = event.getEntity();
        if (!(projectile instanceof Arrow)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) event.getEntity().getShooter();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(shooter.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isBowAllowed()) {
            return;
        }
        Chat.sendMessage(shooter, (plugin.languageConfig.getString("duel.restrictions.deny-message.arrow")));
        event.setCancelled(true);
    }

    //anti totem
    @EventHandler(ignoreCancelled = true)
    public void onTotemUse(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player playerWhoUsedTotem = (Player) event.getEntity();
        UUID playerWhoUsedTotemUUID = playerWhoUsedTotem.getUniqueId();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerWhoUsedTotem.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isTotemAllowed()) {
            return;
        }
        UUID winner = request.getOpponent(playerWhoUsedTotemUUID);
        event.setCancelled(true);
        request.getGame().endGame(winner);
    }

    //antielytra
    @EventHandler(ignoreCancelled = true)
    public void onGliding(EntityToggleGlideEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = ((Player) entity).getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isElytraAllowed()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions.deny-message.elytra"));
        event.setCancelled(true);
    }

    //antipearl
    @EventHandler(ignoreCancelled = true)
    public void enderPearlLaunch(ProjectileLaunchEvent event) {
        Entity projectile = event.getEntity();
        if (!(projectile instanceof EnderPearl)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player shooter = (Player) event.getEntity().getShooter();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(shooter.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isEnderPearlAllowed()) {
            return;
        }
        Chat.sendMessage(shooter, plugin.languageConfig.getString("duel.restrictions.deny-message.ender-pearl"));
        event.setCancelled(true);
    }

    //anti potion
    @EventHandler(ignoreCancelled = true)
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) {
            return;
        }
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isPotionAllowed()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions.deny-message.potion"));
        event.setCancelled(true);
    }

    //anti potion splash
    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isPotionAllowed()) {
            return;
        }
        player.getWorld().dropItem(player.getLocation(), event.getPotion().getItem());
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions.deny-message.potion"));
        event.setCancelled(true);
    }

    //anti gp
    @EventHandler(ignoreCancelled = true)
    public void onGoldenAppleEat(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isGoldenAppleAllowed()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions.deny-message.golden-apple"));
        event.setCancelled(true);
    }

    //anti ender pearl
    @EventHandler(ignoreCancelled = true)
    public void onPearl(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(player.getUniqueId());
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        if (request.getGame().getRestrictions().isEnderPearlAllowed()) {
            return;
        }
        event.setCancelled(true);
    }
}
