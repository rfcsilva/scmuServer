package pt.agroSmart.resources.Sensor.Regist;

import com.google.appengine.api.datastore.Entity;
import pt.agroSmart.StorableObject;
import pt.agroSmart.resources.GreenHouse.GreenHouse;

public class Regist extends StorableObject {

    public static final String AIR_HUMIDITY = "Air humidity";
    private static final String TYPE = "regist";
    public static final String GRENN_HOUSE = "grennHouse";
    public static final String TEMPERATURE = "Temperature";
    public static final String SOIL_HUMIDITY = "Soil Humidity";
    public static final String WATER_LEVEL = "Water Level";
    public static final String LUMINOSITY = "Luminosity";
    public static final String STEAM = "Steam";
    public static final String TIME_STAMP = "TimeStamp";

    public String greenHouseId;
    public float temperature;
    public float soilHumidity;
    public float airHumidity;
    public float waterLevel;
    public double luminosity;
    public double steam;
    public long timeStamp;

    public Regist() { super(); }

    public Regist(String greenHouseId, float temprature, float soilHumidity, float airHumidity, float waterLevel, double luminosity, double steam, long timeStamp){
        super(TYPE, GreenHouse.generateKey(greenHouseId));
        this.greenHouseId = greenHouseId;
        this.temperature = temprature;
        this.soilHumidity = soilHumidity;
        this.airHumidity = airHumidity;
        this.waterLevel = waterLevel;
        this.luminosity = luminosity;
        this.steam = steam;
        this.timeStamp = timeStamp;
    }

    @Override
    protected Entity encodeEntity() {

        Entity entity = new Entity(TYPE, GreenHouse.generateKey(greenHouseId));
        entity.setIndexedProperty(GRENN_HOUSE, greenHouseId);
        entity.setUnindexedProperty(TEMPERATURE, temperature);
        entity.setUnindexedProperty(SOIL_HUMIDITY, soilHumidity);
        entity.setUnindexedProperty(AIR_HUMIDITY, airHumidity);
        entity.setUnindexedProperty(WATER_LEVEL, waterLevel);
        entity.setUnindexedProperty(LUMINOSITY, luminosity);
        entity.setUnindexedProperty(STEAM, steam);
        entity.setIndexedProperty(TIME_STAMP, timeStamp);

        return entity;
    }

    public static Regist fromEntity(Entity e){

        return new Regist(
                (String) e.getProperty(GRENN_HOUSE),
                (float) e.getProperty(TEMPERATURE),
                (float) e.getProperty(SOIL_HUMIDITY),
                (float) e.getProperty(AIR_HUMIDITY),
                (float) e.getProperty(WATER_LEVEL),
                (double) e.getProperty(LUMINOSITY),
                (double) e.getProperty(STEAM),
                (long) e.getProperty(TIME_STAMP)
        );

    }

}
