package com.github.kotooriiii.enderdragon.strategy.fight;

import com.github.kotooriiii.enderdragon.EnderDragonManager;

public abstract  class FightStrategy {

    protected EnderDragonManager manager;

    public FightStrategy(EnderDragonManager manager)
    {
        this.manager =  manager;
    }

    public abstract void damage(double oldRatio, double newRatio);


    public abstract void heal(double oldRatio, double newRatio);

    public abstract void end();
}
