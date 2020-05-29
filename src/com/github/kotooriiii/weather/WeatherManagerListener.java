package com.github.kotooriiii.weather;

import com.github.kotooriiii.LostShardPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;


public class WeatherManagerListener implements Listener {

    @EventHandler
    public void onLowerFrequencyRain(WeatherChangeEvent event) {

        event.setCancelled(true);
        boolean forceOff = true;
        if(forceOff)
            return;

        boolean isRaining = event.toWeatherState();
        //If its raining
        if (isRaining) {

            WeatherManager weatherManager = LostShardPlugin.getWeatherManager();
            WeatherManager.WeatherFrequency weatherFrequency = weatherManager.getWeatherFrequency();

            if (weatherFrequency.isGoingToRain())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLowerFrequencyStorm(ThunderChangeEvent event)
    {
        event.setCancelled(true);
        boolean forceOff = true;
        if(forceOff)
            return;

        boolean isRaining = event.toThunderState();
        //If its raining
        if (isRaining) {

            WeatherManager weatherManager = LostShardPlugin.getWeatherManager();
            WeatherManager.WeatherFrequency weatherFrequency = weatherManager.getWeatherFrequency();

            if (weatherFrequency.isGoingToRain())
                event.setCancelled(true);
        }    }
}
