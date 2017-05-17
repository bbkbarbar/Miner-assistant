package hu.barbar.miner_assistant;

import static org.junit.Assert.assertTrue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import hu.barbar.util.FileHandler;
import junit.framework.TestSuite;

public class CreateSampleJson extends TestSuite {

	@SuppressWarnings("unchecked")
	@Test
	  public void createInputJsonSample(){
    	JSONObject json = new JSONObject();
    	
    	json.put("Energy cost per kwh", 0.155);
    	
    	JSONArray array = new JSONArray();
    	
    	JSONObject obj = null;
    	obj = new JSONObject();
    	obj.put("config_name", "Eth");
    	obj.put("short name", "eth");
    	obj.put("total_hashing_power", 51.2f);
    	obj.put("power_consumption", 285f);
    	array.add(obj);
    	json.put("Coins to check", array);
    	
    	
    	array = new JSONArray()
    			;
    	obj = new JSONObject();
    	obj.put("config name", "Eth 2 GPU");
    	obj.put("total_hashing_power", 51.2);
    	obj.put("unit", "MH");
    	obj.put("short name", "eth");
    	obj.put("power_consumption", 285.0);
    	array.add(obj);
    	
    	obj = new JSONObject();
    	obj.put("config name", "Eth 3 GPU");
    	obj.put("total_hashing_power", 74.5);
    	obj.put("unit", "MH");
    	obj.put("short name", "eth");
    	obj.put("power_consumption", 442.0);
    	array.add(obj);
    	
    	obj = new JSONObject();
    	obj.put("config name", "ETC 3 GPU");
    	obj.put("total_hashing_power", 74.5);
    	obj.put("unit", "MH");
    	obj.put("short name", "etc");
    	obj.put("power_consumption", 442.0);
    	array.add(obj);
    	
    	json.put("Configs to check", array);

    	assertTrue(FileHandler.storeJSON("target" + FileHandler.getPathSeparator() + "sample.json", json));
    	
    }
	
}
