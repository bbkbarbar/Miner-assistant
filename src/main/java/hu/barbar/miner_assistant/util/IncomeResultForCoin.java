package hu.barbar.miner_assistant.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IncomeResultForCoin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4419902498083384587L;


	public static final SimpleDateFormat tf = new SimpleDateFormat("YYYY.MM.dd HH:mm:ss");
	
	
	private Date dateOfCheck = null;
	
	private String coinShortName = null;
	
	private String coinName = null;
	
	private double incomePerUnitPerMonth = -1;

	
	public IncomeResultForCoin(Date dateOfCheck, String coinShortName, String coinName, double incomePerUnitPerMonth) {
		super();
		this.dateOfCheck = dateOfCheck;
		this.coinShortName = coinShortName;
		this.coinName = coinName;
		this.incomePerUnitPerMonth = incomePerUnitPerMonth;
	}
	
	public IncomeResultForCoin(String coinShortName, String coinName, float incomePerUnitPerMonth) {
		super();
		this.dateOfCheck = new Date();
		this.coinShortName = coinShortName;
		this.coinName = coinName;
		this.incomePerUnitPerMonth = incomePerUnitPerMonth;
	}
	
	public IncomeResultForCoin(String coinShortName, float incomePerUnitPerMonth) {
		super();
		this.dateOfCheck = new Date();
		this.coinShortName = coinShortName;
		this.coinName = coinShortName;
		this.incomePerUnitPerMonth = incomePerUnitPerMonth;
	}


	public Date getDateOfCheck() {
		return dateOfCheck;
	}


	public String getCoinShortName() {
		return coinShortName;
	}


	public String getCoinName() {
		return coinName;
	}


	public double getIncomePerUnitPerMonth() {
		return incomePerUnitPerMonth;
	}
	
	
	public String getLine(String separator){
		return tf.format(this.dateOfCheck) + separator + this.coinName + separator + Double.toString(this.incomePerUnitPerMonth) + separator + "$ / month / MH";
	}
	
	
	public float calculateProfit(double hashingPower, double energyConsumptionInWh, double energyPricePerKWh){
		return (float)((this.getIncomePerUnitPerMonth() * hashingPower)
				- ((energyConsumptionInWh)/1000)*24*(365/12) * energyPricePerKWh);
	}
	
}
