package com.github.kotooriiii.weather;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;



public class WeatherManagerListener implements Listener {


    public static int frequencyCounter = 0;
    public final static int frequencyMax = 3;
    @EventHandler
    public void onLowerFrequencyRain(WeatherChangeEvent event)
    {
        boolean isRaining = event.toWeatherState();
        //If its raining
        if(isRaining)
        {
            //If hit 3rd
            if(frequencyCounter == frequencyMax)
            {
                //reset
                frequencyCounter = 0;
                return;
            }

            //increment and cancel event
            frequencyCounter++;
            event.setCancelled(true);
        }
    }
}
