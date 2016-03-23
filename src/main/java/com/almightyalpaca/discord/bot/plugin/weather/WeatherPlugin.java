package com.almightyalpaca.discord.bot.plugin.weather;

import com.almightyalpaca.discord.bot.system.command.AbstractCommand;
import com.almightyalpaca.discord.bot.system.command.annotation.Command;
import com.almightyalpaca.discord.bot.system.events.CommandEvent;
import com.almightyalpaca.discord.bot.system.exception.PluginLoadingException;
import com.almightyalpaca.discord.bot.system.exception.PluginUnloadingException;
import com.almightyalpaca.discord.bot.system.plugins.Plugin;
import com.almightyalpaca.discord.bot.system.plugins.PluginInfo;
import com.github.dvdme.ForecastIOLib.FIOCurrently;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

public class WeatherPlugin extends Plugin {// FIXME
	class WeatherCommand extends AbstractCommand { // FIXME HARDER

		public WeatherCommand() {
			super("weather", "Returns the current weather", "weather [location]");
		}

		@Command(dm = true, guild = true, async = true)
		public void onCommand(final CommandEvent event, final String location) {
			try {

				String output = "";

				final GeocodingResult[] results = GeocodingApi.geocode(WeatherPlugin.this.context, location).await();

				final String LATITUDE = String.valueOf(results[0].geometry.location.lat);
				final String LONGITUDE = String.valueOf(results[0].geometry.location.lng);

				WeatherPlugin.this.io.setUnits(ForecastIO.UNITS_SI);
				WeatherPlugin.this.io.setLang(ForecastIO.LANG_ENGLISH);
				WeatherPlugin.this.io.getForecast(LATITUDE, LONGITUDE);

				final FIOCurrently currently = new FIOCurrently(WeatherPlugin.this.io);

				output += "**" + results[0].formattedAddress + "**: " + currently.get().summary().replace("\"", "") + "\n";
				output += "Humidity: " + currently.get().humidity() + "\n";
				output += "Temperature: " + currently.get().temperature() + "\n";
				output += "Sunrise: " + currently.get().sunriseTime() + "\n";
				output += "Sunset: " + currently.get().sunsetTime() + "\n";
				output += "Pressure: " + currently.get().pressure() + "\n";
				output += "Timezone: " + currently.get().getTimezone() + "\n";
				output += "Nearest storm bearing: " + currently.get().nearestStormBearing() + "\n";
				output += "Nearest storm distance: " + currently.get().nearestStormDistance() + "\n";
				output += "Cloud cover: " + currently.get().cloudCover() * 100 + "%\n";
				output += "Visibility: " + currently.get().visibility() + "\n";
				output += "Wind bearing: " + currently.get().windBearing() + "\n";
				output += "Wind speed: " + currently.get().windSpeed() + "\n";

				event.sendMessage(output);

			} catch (final Exception e) {}
		}

	}

	private static final PluginInfo	INFO	= new PluginInfo("com.almightyalpaca.discord.bot.plugin.weather", "1.0.0", "Almighty Alpaca", "Weather Plugin",
			"Returns the weather using <http://forecast.io>");

	ForecastIO						io;
	GeoApiContext					context;

	public WeatherPlugin() {
		super(WeatherPlugin.INFO);
	}

	@Override
	public void load() throws PluginLoadingException {
		this.io = new ForecastIO(this.getBridge().getSecureConfig("ForecastIO").getString("API_KEY"));
		this.context = new GeoApiContext().setApiKey(this.getBridge().getSecureConfig("google").getString("API_KEY"));
		this.registerCommand(new WeatherCommand());
	}

	@Override
	public void unload() throws PluginUnloadingException {}

}
