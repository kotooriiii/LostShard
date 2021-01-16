package com.github.kotooriiii.sorcery.spells;

import org.bukkit.util.Vector;

public class KVectorUtils {
    private KVectorUtils()
    {

    }

    public static Vector perp(Vector onto, Vector u) {
        return u.clone().subtract(proj(onto, u));
    }

    public static Vector proj(Vector onto, Vector u) {
        return onto.clone().multiply(onto.dot(u) / onto.lengthSquared());
    }

}
