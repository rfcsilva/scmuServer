package pt.agroSmart.resources.GreenHouse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import pt.agroSmart.StorableObject;

import javax.crypto.KeyGenerator;
import java.util.UUID;

public class Config extends StorableObject {


    public static final String TYPE = "config";
    private static final String GREEN_HOUSE = "GreenHouse";
    private static final String AVERAGE_TEMPERATURE = "Average Temperature";
    private static final String TEMPERATURE_DEVIATION = "Temperature Deviation";
    private static final String AVERAGE_AIR_HUMIDITY = "Average Air Humidity";
    private static final String AIR_HUMIDITY_DEVIATION = "Air Humidity Deviation";
    private static final String AVERAGE_SOIL_HUMIDITY = "Average Soil Humidity";
    private static final String SOIL_HUMIDITY_DEVIATION = "Soil Humidity Deviation";
    private static final String AVERAGE_STEAM = "Average Steam";
    private static final String STEAM_DEVIATION = "Steam Deviation";
    private static final String AVERAGE_LUMINOSITY = "AVERAGE LUMINOSITY";
    private static final String LUMINOSITY_DEVIATION = "LUMINOSITY DEVIATION";
    private static final String ID = "id";

    private String id;
    private String greenHouseId;

    public float avgTemperature;
    public float tempDeviation;

    public float avgAirHumidity;
    public float airHumidityDeviation;

    public float avgSoilHumidity;
    public float soilHumidityDeviation;

    public float avgLuminosity;
    public float luminosityDeviation;

    public float avgSteam;
    public float steamDeviation;

    public Config( ){ }

    public Config(String id, String greenHouseId){
        super(TYPE, generateKey(greenHouseId, id));
    }

    public Config(float avgTemperature, float tempDeviation, float avgAirHumidity, float airHumidityDeviation,
                  float avgSoilHumidity, float soilHumidityDeviation, float avgLuminosity, float luminosityDeviation,
                  float avgSteam, float steamDeviation, String id, String greenHouseId){

        super(TYPE, generateKey(greenHouseId, id));
        this.id = id;
        this.greenHouseId = greenHouseId;
        this.avgTemperature = avgTemperature;
        this.tempDeviation = tempDeviation;
        this.avgAirHumidity = avgAirHumidity;
        this.airHumidityDeviation = airHumidityDeviation;
        this.avgSoilHumidity = avgSoilHumidity;
        this.soilHumidityDeviation = soilHumidityDeviation;
        this.avgLuminosity = avgLuminosity;
        this.luminosityDeviation = luminosityDeviation;
        this.avgSteam = avgSteam;
        this.steamDeviation = steamDeviation;
        
    }
    
    private Config(String id, String greenHouseId, float avgTemperature, float tempDeviation, float avgAirHumidity, float airHumidityDeviation,
                  float avgSoilHumidity, float soilHumidityDeviation, float avgLuminosity, float luminosityDeviation,
                  float avgSteam, float steamDeviation) {
    	
    	this.id = id;
    	this.greenHouseId = greenHouseId;
        this.avgTemperature = avgTemperature;
        this.tempDeviation = tempDeviation;
        this.avgAirHumidity = avgAirHumidity;
        this.airHumidityDeviation = airHumidityDeviation;
        this.avgSoilHumidity = avgSoilHumidity;
        this.soilHumidityDeviation = soilHumidityDeviation;
        this.avgLuminosity = avgLuminosity;
        this.luminosityDeviation = luminosityDeviation;
        this.avgSteam = avgSteam;
        this.steamDeviation = steamDeviation;
    	
    }

    private static Key generateKey(String greenHouseId, String id){
        return KeyFactory.createKey(GreenHouse.generateKey(greenHouseId), TYPE, id);
    }



    @Override
    protected Entity encodeEntity() {

        Entity entity = new Entity(TYPE, id, GreenHouse.generateKey(greenHouseId));
        entity.setProperty(ID, id);
        entity.setUnindexedProperty(GREEN_HOUSE, greenHouseId);
        entity.setUnindexedProperty(AVERAGE_TEMPERATURE, avgTemperature);
        entity.setUnindexedProperty(TEMPERATURE_DEVIATION, tempDeviation);
        entity.setUnindexedProperty(AVERAGE_AIR_HUMIDITY, avgAirHumidity);
        entity.setUnindexedProperty(AIR_HUMIDITY_DEVIATION, airHumidityDeviation);
        entity.setUnindexedProperty(AVERAGE_SOIL_HUMIDITY, avgSoilHumidity);
        entity.setUnindexedProperty(SOIL_HUMIDITY_DEVIATION, soilHumidityDeviation);
        entity.setUnindexedProperty(AVERAGE_LUMINOSITY, avgLuminosity);
        entity.setUnindexedProperty(LUMINOSITY_DEVIATION, luminosityDeviation);
        entity.setUnindexedProperty(AVERAGE_STEAM, avgSteam);
        entity.setUnindexedProperty(STEAM_DEVIATION, steamDeviation);
        
        return entity;

    }

    public static Config fromEntity(Entity e){

        return new Config(
                (String) e.getProperty(ID),
                (String) e.getProperty(GREEN_HOUSE),
                (float) e.getProperty(AVERAGE_TEMPERATURE),
                (float) e.getProperty(TEMPERATURE_DEVIATION),
                (float) e.getProperty(AVERAGE_AIR_HUMIDITY),
                (float) e.getProperty(AIR_HUMIDITY_DEVIATION),
                (float) e.getProperty(AVERAGE_SOIL_HUMIDITY),
                (float) e.getProperty(SOIL_HUMIDITY_DEVIATION),
                (float) e.getProperty(AVERAGE_LUMINOSITY),
                (float) e.getProperty(LUMINOSITY_DEVIATION),
                (float) e.getProperty(AVERAGE_STEAM),
                (float) e.getProperty(STEAM_DEVIATION)
        );

    }

    public String getId() {
        return id;
    }
}
