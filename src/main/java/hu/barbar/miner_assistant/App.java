package hu.barbar.miner_assistant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import hu.barbar.miner_assistant.util.IncomeResultForCoin;
import hu.barbar.miner_assistant.util.ProfitForConfig;
import hu.barbar.util.FileHandler;

/**
 *
 */
public class App{
	
	public static final String LOCATION_OF_CHROME_DRIVER = "c:\\Tools\\chromedriver.exe";
	
	public static int DEFAULT_WAIT_TIME_IN_SEC = 10;
	
	public static final String DEFAULT_HASHING_POWER_UNIT = "MH";
	
	public static final SimpleDateFormat df = new SimpleDateFormat("YYYY.MM.dd.");
	public static final SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
	
	
	
    public static void main( String[] args ){
    	
    	//getDataFor("coins.json");
    	double hp = 74.2;
    	String unit = "MH";
    	double power = 440;
    	double energyPrice = 0.155;
    	ProfitForConfig profit = ProfitabilityChecker.getProfitForConfig("eth", hp, unit, power, energyPrice);
    	System.out.println("ETH\t" + hp + unit + "\t with " + power + "Wh @ " + energyPrice + "$/KWh =>\t" + profit.getProfitPerMonthInUSD() + "\tIncome: " + profit.getIncomePerMonth() + "\tCosts: " + profit.getCostPerMonthInUSD());
        
    }
    
    
    
    public static void getDataFor(String configFile){
    	JSONObject config = FileHandler.readJSON(configFile);

    	HashMap<String, IncomeResultForCoin> incomeResults = null;
    	if(config.containsKey("Coins to check")){
    		incomeResults = new HashMap<String, IncomeResultForCoin>();
    		
    		JSONArray coinsToCheck = (JSONArray) config.get("Coins to check");
    		for(int i=0; i<coinsToCheck.size(); i++){
    			
    			JSONObject obj = (JSONObject) coinsToCheck.get(i);
    			if(obj.get("coin name") != null){

    				String hashingPowerUnit = (String) obj.get("unit");
    				if(hashingPowerUnit == null){
    					hashingPowerUnit = DEFAULT_HASHING_POWER_UNIT;
    				}
    				IncomeResultForCoin incomeResult = new IncomeResultForCoin(new Date(), (String)obj.get("short name"), (String)obj.get("coin name"), (ProfitabilityChecker.getIncomeValuePerMonthFor((String)(obj.get("short name")), 1000, hashingPowerUnit, 0, 0.15f)/1000) );
    				incomeResults.put((String)obj.get("short name"), incomeResult);
    				
	    			System.out.println(incomeResult.getLine("\t"));
    			}
    			
    		}
    	}
    	
    	
    	if((config.get("Configs to check") != null) && (config.get("Energy cost per kwh") != null)){
    		JSONArray configsToCheck = (JSONArray) config.get("Configs to check");
    		for(int i=0; i<configsToCheck.size(); i++){
    			
    			JSONObject configToCheck = (JSONObject) configsToCheck.get(i);
    			if(configToCheck.get("config name") != null){
    				// check the current income value for specified config
    				String hashingPowerUnit = (String) configToCheck.get("unit");
    				if(hashingPowerUnit == null){
    					hashingPowerUnit = DEFAULT_HASHING_POWER_UNIT;
    				}
    				
    				float incomeValue = -1f;
    				if(incomeResults.containsKey(configToCheck.get("short name"))){
    					incomeValue = incomeResults.get(configToCheck.get("short name")).calculateProfit((Double)configToCheck.get("total_hashing_power"), (Double)configToCheck.get("power_consumption"), (Double)config.get("Energy cost per kwh"));
    					System.out.println("IncomeValue used from previous query");
    				}else{
    					incomeValue = ProfitabilityChecker.getIncomeValuePerMonthFor(
								(String)(configToCheck.get("short name")), 
								(Double)configToCheck.get("total_hashing_power"),
								hashingPowerUnit,
								(Double)configToCheck.get("power_consumption"),
								(Double)config.get("Energy cost per kwh")
					);
    				}
    				//incomeResults
    				
	    			String line = getCurrentDateStr() + "\t" + (String)(configToCheck.get("config name")) + "\t" + Float.toString(incomeValue);
	    			System.out.println(line);
	    			
	    			//calculate the same based on single value
	    			hashingPowerUnit = (String) configToCheck.get("unit");
    				if(hashingPowerUnit == null){
    					hashingPowerUnit = DEFAULT_HASHING_POWER_UNIT;
    				}
    				
    			}
    		}
    	}
    	
    }
    
    
    public static String getCurrentDateStr(){
    	Date now = new Date();
    	//return df.format(now) + " " + tf.format(now);
    	return tf.format(now);
    }
    
    
}
