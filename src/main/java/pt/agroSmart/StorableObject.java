package pt.agroSmart;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;

public abstract class StorableObject {

    /**
     * A Logger Object
     */
    private static final Logger LOG = Logger.getLogger(StorableObject.class.getName());


    /**
     * DatastoreService object for Datastore access.
     */
    private static final DatastoreService DS = DatastoreServiceFactory
            .getDatastoreService();


    /**
     * Maximum number of attempts to modify a single entity in the datastore
     */
    private static int MAXIMUM_ATTEMPTS = 5;


    private final String DataType;

    private Key key;

    public StorableObject(String dataType) {
        this.DataType = dataType;
    }

    public StorableObject(String dataType, Key key) {
        this.key = key;
        this.DataType = dataType;
        Entity e = this.ds_get();
        this.fromEntity(e);
    }

    abstract protected Entity encodeEntity();

    abstract protected void fromEntity(Entity e);


    public boolean ds_delete() {
        for( int i = 0; i < StorableObject.MAXIMUM_ATTEMPTS; i++) {
            Transaction tx = null;
            try {
                tx = DS.beginTransaction();
                DS.delete(tx, key);
                tx.commit();
                return true;
            } catch (Exception e) {
                if(tx != null && tx.isActive()) tx.rollback();
                e.printStackTrace();
                LOG.severe("Failed to delete " + this.DataType + ". Key: " + this.key.toString());
            }
        }
        return false;
    }

    public final boolean ds_delete(Transaction tx) {
        try {
            DS.delete(tx, key);
            return true;
        } catch (Exception e) {
            LOG.severe("Failed to delete " + this.DataType + ". Key: " + this.key.toString());
            return false;
        }
    }

    public boolean ds_save() {
        for( int i = 0; i < StorableObject.MAXIMUM_ATTEMPTS; i++) {
            Transaction tx = null;
            try {
                tx = DS.beginTransaction();
                Entity entity = this.encodeEntity();
                DS.put(tx, entity);
                tx.commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                if(tx != null && tx.isActive()) tx.rollback();
                LOG.severe("Failed to store " + this.DataType + ". Key: " + this.key.toString());
            }
        }
        return false;
    }

    public boolean ds_save(Transaction tx) {
        try {
            Entity entity = this.encodeEntity();
            DS.put(tx, entity);
            return true;
        } catch (Exception e) {
            LOG.severe("Failed to store " + this.DataType + ". Key: " + this.key.toString());
            e.printStackTrace();
            return false;
        }
    }

    public Entity ds_get() {
        try {
            return DS.get(key);
        } catch (Exception e) {
            LOG.warning("Attempt to get non existent " + this.DataType + ". Key: " + key);
            return null;
        }

    }

    public List<StorableObject> list(){
        return null;
    }

}