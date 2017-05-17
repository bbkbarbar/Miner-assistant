package hu.barbar.miner_assistant.util;

import java.util.Date;

public class ProfitForConfig extends IncomeResultForCoin {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5132733965983475902L;

	private double energyConsumptionInWh;
	
	private double powerCostPerKWhInUSD;
	
	private double incomePerMonth;
	
	private static final double AVERAGE_HOURS_IN_A_MONTH = 730;
	
	private static final double KWH_IN_WH = 1000;
	
	
	public ProfitForConfig(String coinShortName, double incomePerMonth, double energyConsumptionInWh, double powerCostPerKWhInUSD) {
		super(new Date(), coinShortName, coinShortName, incomePerMonth);
		this.energyConsumptionInWh = energyConsumptionInWh;
		this.powerCostPerKWhInUSD = powerCostPerKWhInUSD;
		this.incomePerMonth = incomePerMonth;
	}


	public double getEnergyConsumptionInWh() {
		return energyConsumptionInWh;
	}


	public double getPowerCostPerKWhInUSD() {
		return powerCostPerKWhInUSD;
	}


	public double getIncomePerMonth() {
		return incomePerMonth;
	}


	public double getCostPerMonthInUSD() {
		return ((energyConsumptionInWh/KWH_IN_WH) * AVERAGE_HOURS_IN_A_MONTH) * powerCostPerKWhInUSD;
	}
	
	
	public double getProfitPerMonthInUSD(){
		return getIncomePerMonth() - getCostPerMonthInUSD();
	}
	

}
