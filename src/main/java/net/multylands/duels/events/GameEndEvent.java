package net.multylands.duels.events;


import net.multylands.duels.object.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {

    private Game game;
    private Player winner;
    private Player loser;

    public GameEndEvent(Game game, Player winner, Player loser) {
        this.game = game;
        this.winner = winner;
        this.loser = loser;
    }

    public Game getGame() {
        return game;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLoser() {
        return loser;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    private static final HandlerList handlers = new HandlerList();


}