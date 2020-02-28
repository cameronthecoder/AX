package us.cameron.xraysuspicion;

import org.bukkit.entity.Player;

public class Event {
    private Player p;
    private int diamondsMined = 0;
    private long firstDiamondMined;


    public Event(Player p, long firstDiamondMined) {
        this.p = p;
        this.firstDiamondMined = firstDiamondMined;
    }

    public int getDiamondsMined() {
        return diamondsMined;
    }

    public long getFirstDiamondMined() {
        return firstDiamondMined;
    }

    public void setFirstDiamondMined(long time) {
        firstDiamondMined = time;
    }

    public void reset () {
        diamondsMined = 0;
        firstDiamondMined = 0;
    }

    public Player getP() {
        return p;
    }

    public void addDiamond() {
        diamondsMined++;
    }
}
