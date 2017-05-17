package hu.barbar.miner_assistant.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;

import hu.barbar.miner_assistant.ProfitabilityChecker;

public class ConfigParameters {

	public static final String DEFAULT_HASHING_POWER_UNIT = "MH";
	
	//TODO create a set() method for this default value
	protected static double DEFAULT_ENERGY_COST_PER_KWH_IN_USD = 0.155; 
	
	
	private String coinName = null;
	
	private String coinShortName = null;
	
	private double hashingPower = -1;
	
	private String hashingPowerUnit = DEFAULT_HASHING_POWER_UNIT;
	
	private double powerConsumption = -1;
	
	private double energyPrice = -1;
	
	private ProfitForConfig profitResult = null;

	
	//TODO create a constructor what can handle JSON object as input (what contains the all field);
	
	
	public ConfigParameters(String name, String coinShortName, double hashingPower, String hashingPowerUnit,
			double powerConsumption, double energyPrice, ProfitForConfig profitResult) {
		super();
		this.coinName = name;
		this.coinShortName = coinShortName;
		this.hashingPower = hashingPower;
		this.hashingPowerUnit = hashingPowerUnit;
		this.powerConsumption = powerConsumption;
		this.energyPrice = energyPrice;
		this.profitResult = profitResult;
	}
	
	public ConfigParameters(JSONObject json) {
		super();
		if( ((String)json.get("config name")) != null){
			this.coinName = (String)json.get("config name");
		}else{
			if( ((String)json.get("coin name")) != null){
				this.coinName = (String)json.get("coin name");
			}else{
				this.coinName = null;
			}
		}
		this.coinShortName = ((String)json.get("short name"));
		if(this.coinName == null){
			this.coinName = this.coinShortName;
		}
		
		
		
		this.hashingPower = (Double)json.get("total_hashing_power");
		this.hashingPowerUnit = ((String)json.get("unit"));;
		this.powerConsumption = (Double)json.get("power_consumption");;
		this.energyPrice = DEFAULT_ENERGY_COST_PER_KWH_IN_USD;
		this.profitResult = null;
	}
	
	
	public ConfigParameters(String coinShortName, double hashingPower, String hashingPowerUnit,
			double powerConsumption, double energyPrice, ProfitForConfig profitResult) {
		super();
		this.coinName = coinShortName;
		this.coinShortName = coinShortName;
		this.hashingPower = hashingPower;
		this.hashingPowerUnit = hashingPowerUnit;
		this.powerConsumption = powerConsumption;
		this.energyPrice = energyPrice;
		this.profitResult = profitResult;
	}
	
	public ConfigParameters(String coinShortName, double hashingPower, String hashingPowerUnit,
			double powerConsumption, double energyPrice) {
		super();
		this.coinName = coinShortName;
		this.coinShortName = coinShortName;
		this.hashingPower = hashingPower;
		this.hashingPowerUnit = hashingPowerUnit;
		this.powerConsumption = powerConsumption;
		this.energyPrice = energyPrice;
		this.profitResult = null;
	}
	
	
	public boolean checkInputJson(JSONObject json){
		
		if(((String)json.get("short name")) == null){
			return false;
		}
		if(((String)json.get("total_hashing_power")) == null){
			return false;
		}
		if(((String)json.get("unit")) == null){
			return false;
		}
		if(((String)json.get("power_consumption")) == null){
			return false;
		}
		
		return true;
	}
	
	
	
	public void checkProfitability(){
		this.profitResult = ProfitabilityChecker.getProfitForConfig(this.coinShortName, this.hashingPower, this.hashingPowerUnit, this.powerConsumption, this.energyPrice);
	}
	
	
	/**
	 * Check profit value is not checked after the given date (or it was not checked ever)
	 * @param since
	 * @return true if profit value has been checked (or re-checked) now
	 * <br> or false if it has been checked earlier, but after the specified date.
	 */
	public boolean checkProfitabilityIfNotCheckedSince(Date since){
		if(profitResult == null){
			checkProfitability();
			return true;
		}
		if(this.profitResult.getDateOfCheck().before(since)){
			checkProfitability();
			return true;
		}else{
			return false;
		}
	}
	
	
	//TODO
	public String getLine(){
		return null;
	}
	
	public String toString(){
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM.dd. HH:mm:ss");
		String inputPart = this.coinName + "\t" + this.hashingPower + " " + this.hashingPowerUnit + "\t" + this.powerConsumption + "Wh @ " + this.energyPrice + "$/KWh";
		if(this.hasProfitCalculated()){
			return inputPart + "\tProfit: " + this.profitResult.getProfitPerMonthInUSD() + "\tIncome: " + this.profitResult.getIncomePerMonth() + "\tCosts: " + this.profitResult.getCostPerMonthInUSD() + "\t (Checked @ " + sdf.format(this.profitResult.getDateOfCheck()) + ")";
		}else{
			return inputPart + "\tNot checked yet.";
		}
	}
	

	
	public boolean hasProfitCalculated(){
		return this.profitResult != null;
	}
	
	public Date getDateOfProfitCalculation(){
		if(this.profitResult == null){
			return null;
		}
		return this.profitResult.getDateOfCheck();
	}


	public double getIncomePerMonth() {
		if(profitResult != null)
			return profitResult.getIncomePerMonth();
		else
			return -1;
	}


	public double getCostPerMonthInUSD() {
		if(profitResult != null)
			return profitResult.getCostPerMonthInUSD();
		else
			return -1;
	}


	//////////////////////////
	
	
	public String getCoinName() {
		return coinName;
	}


	public String getCoinShortName() {
		return coinShortName;
	}


	public double getHashingPower() {
		return hashingPower;
	}


	public String getHashingPowerUnit() {
		return hashingPowerUnit;
	}


	public double getPowerConsumption() {
		return powerConsumption;
	}


	public double getEnergyPrice() {
		return energyPrice;
	}


	public ProfitForConfig getProfitResult() {
		return profitResult;
	}
	
	
}
