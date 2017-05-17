package hu.barbar.miner_assistant;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import hu.barbar.miner_assistant.util.IncomeResultForCoin;
import hu.barbar.miner_assistant.util.ProfitForConfig;

public class ProfitabilityChecker {

	/**
     * 
     * @param currency e.g.: eth
     * @param hashingPower in MH
     * @param hashingPowerUnit e.g.: "MH"
     * @param powerConsumption in kWh
     * @param energyCostInUSD as a float value
     * @return an integer value as a float with the estimated income for given parameters
     */
    public static float getIncomeValuePerMonthFor(String coinShortName, double hashingPower, String hashingPowerUnit, double powerConsumption, double energyCostInUSD){
    	/*
		Sample URL:
    		https://www.cryptocompare.com/mining/calculator/eth?HashingPower=1000&HashingUnit=MH%2Fs&PowerConsumption=550&CostPerkWh=1.56
    	 */
    	String urlBase = "https://www.cryptocompare.com/mining/calculator/";
    	String urlPart2 = "?HashingPower=";
    	String urlPart3 = "&HashingUnit=";
    	String urlPart4 = "%2Fs&PowerConsumption=";
    	String urlPart5 = "&CostPerkWh=";
    	
    	String composedUrl = urlBase + coinShortName.toLowerCase() + urlPart2 + hashingPower + urlPart3 + hashingPowerUnit + urlPart4 + powerConsumption + urlPart5 + energyCostInUSD;
    	
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

    
    public static IncomeResultForCoin getIncomeValueFor(String currency, String coinShortName, double hashingPower, String hashingPowerUnit){
    	return new IncomeResultForCoin(
    			new Date(),
    			coinShortName,
    			currency,
    			getIncomeValuePerMonthFor(coinShortName, hashingPower, hashingPowerUnit, 0, 0)
		); 
    }
    
    public static IncomeResultForCoin getIncomeValueFor(String coinShortName, double hashingPower, String hashingPowerUnit){
    	return getIncomeValueFor(coinShortName, coinShortName, hashingPower, hashingPowerUnit);
    }
    
    
    public static ProfitForConfig getProfitForConfig(String coinShortName, double hashingPower, String hashingPowerUnit, double powerConsumption, double energyCostInUSD){
    	double factor = 1;
    	
    	if(hashingPower < 10){
    		factor = 100;
    	}else
    	if(hashingPower < 100){
    		factor = 10;
    	}/**/
    	
    	double income = getIncomeValuePerMonthFor(coinShortName, (hashingPower*factor), hashingPowerUnit, 0, 0) / factor;
    	
    	return new ProfitForConfig(
    			coinShortName, 
    			income, 
    			powerConsumption, 
    			energyCostInUSD
    	);
    	//TODO check this
    }
    
    
    private static ArrayList<String> getWebContentBodyFrom(String url){
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
	
}
