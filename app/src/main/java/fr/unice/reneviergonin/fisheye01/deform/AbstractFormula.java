package fr.unice.reneviergonin.fisheye01.deform;

import java.util.Observable;

public abstract class AbstractFormula extends Observable {
	
	public double z ;
	public double r ;
	public double o;
	
	public abstract double deform(int x);
	
	public abstract double reverse(double x);
	
	public abstract void setParams(Double z, Double r, Double o);

	public String toString()
	{
		return this.getClass().getName();
	}
	
	public double getUnchangedX()
	{
		double b = 2*z*o-r*r+(z-o)*(z-o);
		double c = z*z*(o*o-r*r);
		
		double delta = b*b-4*c;
		
		
		return Math.sqrt((Math.sqrt(delta) - b) / 2);
	}

}
