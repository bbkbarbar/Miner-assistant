package hu.barbar.miner_assistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import hu.barbar.miner_assistant.util.IncomeResultForCoin;
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
    	
    	getDataFor("coins.json");
        
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
    				IncomeResultForCoin incomeResult = new IncomeResultForCoin(new Date(), (String)obj.get("short name"), (String)obj.get("coin name"), (getIncomeValuePerMonthFor((String)(obj.get("short name")), 1000, hashingPowerUnit, 0, 0.15f)/1000) );
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
    					incomeValue = getIncomeValuePerMonthFor(
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
    				/*
	    			float incomePerMH = getIncomeValueAsFloatPerMonthFor((String)(configToCheck.get("short name")),1000, hashingPowerUnit, 0, 0) / 1000;
	    			
	    			float profit = (float) ((incomePerMH * (Double)configToCheck.get("total_hashing_power"))
	    							- (((Double)configToCheck.get("power_consumption"))/1000)*24*(365/12) * (Double)config.get("Energy cost per kwh"));
	    			System.out.println("Calculated profit:\t" + Float.toString(profit));/**/
    			}
    			
    		}
    	}
    	
    }
    
    
    /**
     * 
     * @param currency e.g.: eth
     * @param hashingPower in MH
     * @param powerConsumption in kWh
     * @param energyCostInUSD as a float value
     * @return an integer value as a float with the estimated income for given parameters
     */
    public static float getIncomeValuePerMonthFor(String currency, double hashingPower, String hashingPowerUnit, double powerConsumption, double energyCostInUSD){
    	/*
		Sample URL:
    		https://www.cryptocompare.com/mining/calculator/eth?HashingPower=1000&HashingUnit=MH%2Fs&PowerConsumption=550&CostPerkWh=1.56
    	 */
    	String urlBase = "https://www.cryptocompare.com/mining/calculator/";
    	String urlPart2 = "?HashingPower=";
    	String urlPart3 = "&HashingUnit=";
    	String urlPart4 = "%2Fs&PowerConsumption=";
    	String urlPart5 = "&CostPerkWh=";
    	
    	String composedUrl = urlBase + currency.toLowerCase() + urlPart2 + hashingPower + urlPart3 + hashingPowerUnit + urlPart4 + powerConsumption + urlPart5 + energyCostInUSD;
    	
    	ArrayList<String> lines = getWebContentBodyFrom(composedUrl);
    	
    	String priceStr = "";
    	for(int i=0; i<lines.size(); i++){
    		if(lines.get(i).contains("Profit per month")){
    			priceStr = lines.get(i+1);
    		}
    	}
    	
    	priceStr = (priceStr.split(" ")[1]);
     	priceStr = priceStr.replaceAll("\\,", "");
    	priceStr = priceStr.replaceAll("\\.", "");
    	float income = (Float.valueOf(priceStr))/100;
    	
    	return income;
    }
    
    
    /**
     * Get data from web with request for calculation with hashingPower of 1000 MH and 0 power consumption  <br>
     * and divide the given value with 1000 to get accurate income for 1 MH without any power cost.
     * @return the date of enquiry and the estimated income for 1 MH hashingPower without any power consumption
     */
    /*
    public static String getDataLine(){
    	return (getCurrentDateStr() + "\t" + Float.toString( getIncomeValueAsFloatPerMonthFor("eth", 1000, "MH", 0, 0.15f)/1000 ));
    }/**/
    
    
    
    public static ArrayList<String> getWebContentBodyFrom(String url){
    	System.setProperty("webdriver.chrome.driver", "c:\\Tools\\chromedriver.exe");
    	ArrayList<String> lines = null;
    	
    	try {
	    	
    		
	    	WebDriver driver = new ChromeDriver();
	    	driver.get(url);
    	
			//Thread.sleep(50);
		
	    	WebElement resultbox = driver.findElement(By.tagName("body"));
	    	
	    	String str = resultbox.getText();
	    	String[] array = str.split("\n");
	    	lines = new ArrayList<String>(Arrays.asList(array));
	    	driver.quit();
	    	
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return lines;
    }
    
    
    public static String getCurrentDateStr(){
    	Date now = new Date();
    	//return df.format(now) + " " + tf.format(now);
    	return tf.format(now);
    }
    
    
}
