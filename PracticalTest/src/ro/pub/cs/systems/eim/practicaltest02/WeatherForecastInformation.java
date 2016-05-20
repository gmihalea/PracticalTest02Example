package ro.pub.cs.systems.eim.practicaltest02;

public class WeatherForecastInformation {

	final public static String TEMPERATURE = "temperature";
    final public static String WIND_SPEED = "wind_speed";
    final public static String CONDITION = "condition";
    final public static String PRESSURE = "pressure";
    final public static String HUMIDITY = "humidity";
	
	 private String temperature;
	    private String windSpeed;
	    private String condition;
	    private String pressure;
	    private String humidity;

	    public WeatherForecastInformation() {
	        this.temperature = null;
	        this.windSpeed = null;
	        this.condition = null;
	        this.pressure = null;
	        this.humidity = null;
	    }

	    public WeatherForecastInformation(
	            String temperature,
	            String windSpeed,
	            String condition,
	            String pressure,
	            String humidity) {
	        this.temperature = temperature;
	        this.windSpeed = windSpeed;
	        this.condition = condition;
	        this.pressure = pressure;
	        this.humidity = humidity;
	    }

	    public void setTemperature(String temperature) {
	        this.temperature = temperature;
	    }

	    public String getTemperature() {
	        return temperature;
	    }

	    public void setWindSpeed(String windSpeed) {
	        this.windSpeed = windSpeed;
	    }

	    public String getWindSpeed() {
	        return windSpeed;
	    }

	    public void setCondition(String condition) {
	        this.condition = condition;
	    }

	    public String getCondition() {
	        return condition;
	    }

	    public void setPressure(String pressure) {
	        this.pressure = pressure;
	    }

	    public String getPressure() {
	        return pressure;
	    }

	    public void setHumidity(String humidity) {
	        this.humidity = humidity;
	    }

	    public String getHumidity() {
	        return humidity;
	    }

	    @Override
	    public String toString() {
	        return TEMPERATURE + ": " + temperature + "\n\r" +
	                WIND_SPEED + ": " + windSpeed + "\n\r" +
	                CONDITION + ": " + condition + "\n\r" +
	                PRESSURE + ": " + pressure + "\n\r" +
	                HUMIDITY + ": " + humidity;
	    }
}
