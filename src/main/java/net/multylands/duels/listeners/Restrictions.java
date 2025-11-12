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
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;

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
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isNotchAllowed()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.enchanted-golden-apple"));
        event.setCancelled(true);
    }

    //anti gp
    @EventHandler(ignoreCancelled = true)
    public void onGoldenAppleEat(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isGoldenAppleAllowed()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.golden-apple"));
        event.setCancelled(true);
    }

    //anti-bow
    @EventHandler(ignoreCancelled = true)
    public void arrowLaunch(ProjectileLaunchEvent event) {
        Entity projectile = event.getEntity();
        if (!(projectile instanceof Arrow)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isBowAllowed()) {
            return;
        }
        Chat.sendMessage(player, (plugin.languageConfig.getString("duel.restrictions-modules.deny-message.arrow")));
        event.setCancelled(true);
    }

    //not let player who is in 10 second spawn tp period launch projectiles for example ender pearl and stay in the arena
    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (request == null) {
            return;
        }
        if (!request.getGame().getIsAboutToTeleportedToSpawn()) {
            return;
        }
        event.setCancelled(true);
    }

    //anti totem
    @EventHandler(ignoreCancelled = true)
    public void onTotemUse(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isTotemAllowed()) {
            return;
        }
        event.setCancelled(true);
    }

    //anti elytra
    @EventHandler(ignoreCancelled = true)
    public void onGliding(EntityToggleGlideEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isElytraAllowed()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.elytra"));
        event.setCancelled(true);
    }

    //anti potion
    @EventHandler(ignoreCancelled = true)
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) {
            return;
        }
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isPotionAllowed()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.potion"));
        event.setCancelled(true);
    }

    //anti potion splash
    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player shooter)) {
            return;
        }
        UUID shooterUUID = shooter.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(shooterUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isPotionAllowed()) {
            return;
        }
        shooter.getWorld().dropItem(shooter.getLocation(), event.getPotion().getItem());
        Chat.sendMessage(shooter, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.potion"));
        event.setCancelled(true);
    }

    //anti pearl
    @EventHandler(ignoreCancelled = true)
    public void enderPearlLaunch(ProjectileLaunchEvent event) {
        Entity projectile = event.getEntity();
        if (!(projectile instanceof EnderPearl)) {
            return;
        }
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isEnderPearlAllowed()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.ender-pearl"));
        event.setCancelled(true);
    }

    //anti mace
    @EventHandler(ignoreCancelled = true)
    public void onMace(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (player.getInventory().getItemInMainHand().getType() != Material.MACE) {
            return;
        }
        if (request.getGame().getRestrictions().isMaceEnabled()) {
            return;
        }

        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.mace"));
        event.setCancelled(true);
    }

    //anti ender pearl
    @EventHandler(ignoreCancelled = true)
    public void onPearl(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }
        if (request.getGame().getRestrictions().isEnderPearlAllowed()) {
            return;
        }

        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.ender-pearl"));
        event.setCancelled(true);
    }

    //anti exp
    @EventHandler(ignoreCancelled = true)
    public void onExpBottleLand(ExpBottleEvent event) {
        ProjectileSource source = event.getEntity().getShooter();
        if (!(source instanceof Player player)) {
            return;
        }
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isExpEnabled()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.exp-bottle"));
        event.setCancelled(true);
    }

    //anti exp by spectators
    @EventHandler(ignoreCancelled = true)
    public void onExpLevelChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        DuelRequest request = RequestUtils.getRequestOfTheDuelPlayerIsIn(playerUUID);
        if (!RequestUtils.isInGame(request)) {
            return;
        }
        if (request.getGame().getRestrictions().isExpEnabled()) {
            return;
        }
        Chat.sendMessage(player, plugin.languageConfig.getString("duel.restrictions-modules.deny-message.exp-bottle"));
        event.setAmount(0);
    }

}
