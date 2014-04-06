package ca.pluszero.emotive.models;

import java.util.Collections;
import java.util.List;

import ca.pluszero.emotive.utils.ConversionUtils;
import ca.pluszero.emotive.utils.DateTimeUtils;

public class Forecast {
    private final String summary;
    private final Temperature temperature;
    private final Temperature apparentTemperature;
    private final int humidity;
    private final int precipitationPercentage;
    private final List<FutureWeather> hourlyWeatherList;
    private final List<FutureWeather> dailyWeatherList;
    private WeatherIcon icon;

    public Forecast(String summary, int temperatureInFahrenheit, int apparentTemperatureInFahrenheit, int humidity, int precip, List<FutureWeather> hourlyWeatherList, List<FutureWeather> dailyWeatherList, WeatherIcon icon) {
        this.summary = summary;
        this.temperature = new Temperature(temperatureInFahrenheit);
        this.apparentTemperature = new Temperature(apparentTemperatureInFahrenheit);
        this.humidity = humidity;
        this.precipitationPercentage = precip;
        this.hourlyWeatherList = hourlyWeatherList;
        this.dailyWeatherList = dailyWeatherList;
        this.icon = icon;
    }

    public String getSummary() {
        return summary;
    }

    public int getTemperatureInCelsius() {
        return temperature.toCelsius();
    }

    public int getTemperatureInFahrenheit() {
        return temperature.getValue();
    }

    public WeatherIcon getIcon() {
        return icon;
    }

    public int getApparentTemperatureInFahrenheit() {
        return apparentTemperature.getValue();
    }

    public int getApparentTemperatureInCelsius() {
        return apparentTemperature.toCelsius();
    }

    public int getHumidity() {
        return humidity;
    }

    public int getPrecipitationPercentage() {
        return precipitationPercentage;
    }

    public List<FutureWeather> getHourlyWeatherList() {
        return Collections.unmodifiableList(hourlyWeatherList);
    }

    public List<FutureWeather> getDailyWeatherList() {
        return Collections.unmodifiableList(dailyWeatherList);
    }

    public static class FutureWeather {

        private final Temperature temp;
        private final WeatherIcon icon;
        private final int timeInMs;

        public FutureWeather(Temperature temp, WeatherIcon icon, int timeInMs) {
            this.temp = temp;
            this.icon = icon;
            this.timeInMs = timeInMs;
        }

        public Temperature getTemp() {
            return temp;
        }

        public WeatherIcon getIcon() {
            return icon;
        }

        public String getHourAsString() {
            return DateTimeUtils.formatMillisToHourOfDay(timeInMs);
        }

        public String getDayAsString() {
            return DateTimeUtils.formatMillisToDayOfWeek(timeInMs);
        }
    }

    public static class Temperature {

        private final int temperatureInFahrenheit;

        public Temperature(int temperatureInFahrenheit) {
            this.temperatureInFahrenheit = temperatureInFahrenheit;
        }

        public int toCelsius() {
            return ConversionUtils.fahrenheitToCelsius(temperatureInFahrenheit);
        }

        public int getValue() {
            return temperatureInFahrenheit;
        }
    }
}