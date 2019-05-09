package pt.agroSmart.resources.GreenHouse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import pt.agroSmart.StorableObject;

import javax.crypto.KeyGenerator;
import java.util.UUID;

public class Config extends StorableObject {


    private static final String TYPE = "config";

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
        this.avgSteam = avgSteam;
        this.steamDeviation = steamDeviation;
        
    }
    
    private Config(float avgTemperature, float tempDeviation, float avgAirHumidity, float airHumidityDeviation,
                  float avgSoilHumidity, float soilHumidityDeviation, float avgLuminosity, float luminosityDeviation,
                  float avgSteam, float steamDeviation, String id) {
    	
    	this.id = id;
        this.avgTemperature = avgTemperature;
        this.tempDeviation = tempDeviation;
        this.avgAirHumidity = avgAirHumidity;
        this.airHumidityDeviation = airHumidityDeviation;
        this.avgSoilHumidity = avgSoilHumidity;
        this.soilHumidityDeviation = soilHumidityDeviation;
        this.avgSteam = avgSteam;
        this.steamDeviation = steamDeviation;
    	
    }

    private static Key generateKey(String greenHouseId, String id){
        return KeyFactory.createKey(GreenHouse.generateKey(greenHouseId), TYPE, id);
    }



    @Override
    protected Entity encodeEntity() {

        Entity entity = new Entity(TYPE, id, GreenHouse.generateKey(greenHouseId));
        entity.setProperty("id", id);
        entity.setUnindexedProperty("GreenHouse", greenHouseId);
        entity.setUnindexedProperty("Average Temperature", avgTemperature);
        entity.setUnindexedProperty("Temperature Deviation", tempDeviation);
        entity.setUnindexedProperty("Average Air Humidity", avgAirHumidity);
        entity.setUnindexedProperty("Air Humidity Deviation", airHumidityDeviation);
        entity.setUnindexedProperty("Average Soil Humidity", avgSoilHumidity);
        entity.setUnindexedProperty("Soil Humidity Deviation", soilHumidityDeviation);
        entity.setUnindexedProperty("Average Steam", avgSteam);
        entity.setUnindexedProperty("Steam Deviation", steamDeviation);
        
        return entity;

    }
}
