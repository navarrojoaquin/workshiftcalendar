package com.jnsapps.workshiftcalendar.model;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * @author Joaquín Navarro Salmerón
 *
 */
public class Shift implements Serializable{
	
	private static final long serialVersionUID = 3037041053381702896L;
	
	private String name;
	private String abbreviation;
	private float hours;
	private int color;
	
	public static ArrayList<Shift> parse(String shiftString){
		ArrayList<Shift> res = new ArrayList<Shift>();
		String[] shiftsText = shiftString.split(";");
		for (String shiftText : shiftsText) {
			String[] parameters = shiftText.split(":");
			Shift s = new Shift();
			s.name = parameters[0];
			s.abbreviation = parameters[1];
			s.color = Integer.parseInt(parameters[2]);
			s.hours = Float.valueOf(parameters[3]);
			res.add(s);
		}
		return res;
	}
	
	@Override
	public String toString(){
		return name + ":" + abbreviation + ":" + color + ":" + hours + ";";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public float getHours() {
		return hours;
	}

	public void setHours(float hours) {
		this.hours = hours;
	}

	public synchronized int getColor() {
		return color;
	}

	public synchronized void setColor(int color) {
		this.color = color;
	}
	
	

}
