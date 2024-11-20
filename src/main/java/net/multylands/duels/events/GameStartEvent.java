package net.multylands.duels.events;


import net.multylands.duels.object.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event {

    private Game game;
    private Player player1;
    private Player player2;

    public GameStartEvent(Game game, Player player1, Player player2) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    private static final HandlerList handlers = new HandlerList();


}