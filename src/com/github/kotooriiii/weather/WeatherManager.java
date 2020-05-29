package com.github.kotooriiii.weather;

import java.util.HashMap;

public class WeatherManager {

    private WeatherFrequency weatherFrequency;

    public static class WeatherFrequency
    {
        private int counter = 1;
        private final int frequency; //The integer here is represented as 1/frequency.

        public WeatherFrequency(int frequency)
        {
            this.frequency = frequency;
        }

        public int getFrequency() {
            return frequency;
        }

        public boolean isGoingToRain()
        {
            if(counter == frequency)
            {
                counter = 1;
                return true;
            } else {
                counter++;
                return false;
            }
        }
    }

    public WeatherManager()
    {

    }

    public WeatherFrequency getWeatherFrequency() {
        return weatherFrequency;
    }

    public void setWeatherFrequency(WeatherFrequency weatherFrequency) {
        this.weatherFrequency = weatherFrequency;
    }
}
