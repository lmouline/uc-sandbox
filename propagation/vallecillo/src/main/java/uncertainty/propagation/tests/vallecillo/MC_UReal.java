package uncertainty.propagation.tests.vallecillo;

import java.util.Arrays;
import java.util.Random;

public class MC_UReal extends UReal implements Cloneable {
	
	//setters, getters and other operations (equals, distinct, max, min, etc) inherited from UReal
	
	private final int MAXLENGTH = 1000;
	private double [] sample = new double [MAXLENGTH];
	private static Random rnd = new Random();

	/**
	 * auxiliary methods
	 */
	protected double[] getSample() { //gets a copy of the sample
		return Arrays.copyOf(sample, sample.length);
	}
	
	private void fillSample(double s[], double x1, double u1, Distribution dist) {
		//fills the sample with values from the given distribution
		double d = u1 * Math.sqrt(3);   	
		DistributionGenerator g = new DistributionGenerator();
        switch (dist) {
        	case UNIFORM:
        		for (int i = 0; i < MAXLENGTH; i++) {
        			s[i] = (x1-d) + 2*d*rnd.nextDouble();
        	    };
        	    break;
        	case NORMAL:
                for (int i = 0; i < MAXLENGTH; i++) {
	    	      s[i] = x1 + rnd.nextGaussian() * u1;
	            };
		        break;
            case TRIANGULAR:
                for (int i = 0; i < MAXLENGTH; i++) {
	    	      s[i] = g.nextTriangular(x1,u1);
	            };
                break;
            case TRUNCATED:
            	double beta = 2/3; //ratio between the lengths of the top of the trapezoid and of the base
                for (int i = 0; i < MAXLENGTH; i++) {
	    	      s[i] = g.nextTruncated(x1,u1,beta);
	            };
                break;
            case USHAPED:
                for (int i = 0; i < MAXLENGTH; i++) {
   	    	      s[i] = g.nextUShaped(x1,u1);
   	            };
                break;
            // Uniform distribution by deafult
            default:
        		for (int i = 0; i < MAXLENGTH; i++) {
	    	        s[i] = (x1-d) + 2*d*rnd.nextDouble();
	            }
                break;
        }
	}
	/**
	 * Constructors
	 */
	public MC_UReal(){ //no params, sample initialised to 0
		x = 0.0;
		u = 0.0;
		Arrays.fill(sample, 0); 	
	}

	public MC_UReal(double x){ //"promotes" a real x to (x,0) 
		this.x = x;
		u = 0.0;
		Arrays.fill(sample, x); //sample initialised to x	
	}

	//Uniform distribution if no distribution given.
	public MC_UReal(double x, double u) {
		this.x = x;
		this.u = u;
		fillSample(sample,x,u,Distribution.UNIFORM);
	}

    //creates an UReal from two numbers representing (x,u), given a distribution
    public MC_UReal(double x, double u, Distribution dist) {
		this.x = x;
		this.u = u;
    	fillSample(sample,x,u,dist);
	}

	public MC_UReal(double x, double u, double s[]) { //for cloning numbers
		this.x = x;
		this.u = u;
		sample = Arrays.copyOf(s, s.length);
	}

    public MC_UReal(String x) { //creates an UReal from a string representing a real, with u=0.
    	this.x = Double.parseDouble(x);
    	this.u = 0.0;
		Arrays.fill(sample, this.x); //sample initialised to 0	
    }

    public MC_UReal(String x, String u) { 
    	//creates an UReal from two strings representing (x,u)
    	this.x = Double.parseDouble(x);
    	this.u = Double.parseDouble(u);
    	fillSample(sample,this.x,this.u,Distribution.UNIFORM);
    }

    public MC_UReal(String x, String u, Distribution dist) { 
    	//creates an UReal from two strings representing (x,u), given a distribution
    	this.x = Double.parseDouble(x);
    	this.u = Double.parseDouble(u);
    	fillSample(sample,this.x,this.u,dist);
    }
    
    /*********
     * 
     * Type Operations
     */

	public UReal add(MC_UReal r) {
		MC_UReal result = new MC_UReal();
		double sum = 0.0; 
        double dev = 0.0;
		
	    for (int i = 0; i < MAXLENGTH; i++) {
	    	result.sample[i] = this.sample[i] + r.sample[i];
            sum += result.sample[i];
            dev += result.sample[i]*result.sample[i];
	    }
	    // average
	    result.x = sum/MAXLENGTH;
	    //standard deviation
	    result.u = Math.sqrt(Math.abs(dev-(sum*sum/MAXLENGTH))/(MAXLENGTH-1));
	    
	    return result;
	}
	
	public UReal minus(MC_UReal r) {
		MC_UReal result = new MC_UReal();
		double sum = 0.0; 
        double dev = 0.0;
		
	    for (int i = 0; i < MAXLENGTH; i++) {
	    	result.sample[i] = this.sample[i] - r.sample[i];
            sum += result.sample[i];
            dev += result.sample[i]*result.sample[i];
	    }
	    // average
	    result.x = sum/MAXLENGTH;
	    //standard deviation
	    result.u = Math.sqrt(Math.abs(dev-(sum*sum/MAXLENGTH))/(MAXLENGTH-1));
	    
	    return result;
	}
	
	public UReal mult(MC_UReal r) {
		MC_UReal result = new MC_UReal();
		double sum = 0.0; 
        double dev = 0.0;
		
	    for (int i = 0; i < MAXLENGTH; i++) {
	    	result.sample[i] = this.sample[i] * r.sample[i];
            sum += result.sample[i];
            dev += result.sample[i]*result.sample[i];
	    }
	    // average
	    result.x = sum/MAXLENGTH;
	    //standard deviation
	    result.u = Math.sqrt(Math.abs(dev-(sum*sum/MAXLENGTH))/(MAXLENGTH-1));
	    
	    return result;
	}
	
	public UReal divide(MC_UReal r) {
		MC_UReal result = new MC_UReal();
		double sum = 0.0; 
        double dev = 0.0;
		
	    for (int i = 0; i < MAXLENGTH; i++) {
	    	result.sample[i] = this.sample[i] / r.sample[i];
            sum += result.sample[i];
            dev += result.sample[i]*result.sample[i];
	    }
	    // average
	    result.x = sum/MAXLENGTH;
	    //standard deviation
	    result.u = Math.sqrt(Math.abs(dev-(sum*sum/MAXLENGTH))/(MAXLENGTH-1));
	    
	    return result;
	}

	public UReal abs() {
		MC_UReal result = new MC_UReal();
		double sum = 0.0; 
        double dev = 0.0;
	
	    for (int i = 0; i < MAXLENGTH; i++) {
	    	result.sample[i] = Math.abs(this.sample[i]);
            sum += result.sample[i];
            dev += result.sample[i]*result.sample[i];
	    }
	    // average
	    result.x = sum/MAXLENGTH;
	    //standard deviation
	    result.u = Math.sqrt(Math.abs(dev-(sum*sum/MAXLENGTH))/(MAXLENGTH-1));
	
		return result;
	}
	
	public UReal neg() {
		MC_UReal result = new MC_UReal();
	
	    for (int i = 0; i < MAXLENGTH; i++) {
	    	result.sample[i] = -this.sample[i];
	    }
	    result.x = -this.x;
	    result.u = this.u;
	
		return result;
	}

	public UReal power(float s) {
		MC_UReal result = new MC_UReal();
		double sum = 0.0; 
        double dev = 0.0;
	
	    for (int i = 0; i < MAXLENGTH; i++) {
	    	result.sample[i] = Math.pow(this.sample[i],s);
            sum += result.sample[i];
            dev += result.sample[i]*result.sample[i];
	    }
	    // average
	    result.x = sum/MAXLENGTH;
	    //standard deviation
	    result.u = Math.sqrt(Math.abs(dev-(sum*sum/MAXLENGTH))/(MAXLENGTH-1));
	
		return result;
	}

	public UReal sqrt() {
		MC_UReal result = new MC_UReal();
		double sum = 0.0; 
        double dev = 0.0;
	
	    for (int i = 0; i < MAXLENGTH; i++) {
	    	result.sample[i] = Math.sqrt(this.sample[i]);
            sum += result.sample[i];
            dev += result.sample[i]*result.sample[i];
	    }
	    // average
	    result.x = sum/MAXLENGTH;
	    //standard deviation
	    result.u = Math.sqrt(Math.abs(dev-(sum*sum/MAXLENGTH))/(MAXLENGTH-1));
	
		return result;
	}

	
	public UReal inv() { //inverse (reciprocal)
		return new MC_UReal(1.0).divide(this);
	}
	
	public UReal floor() { //returns (i,u) with i the largest int such that (i,u)<=(x,u)
		double [] s = getSample();
		double newX = Math.floor(this.getX());
		for (int i=0;i<s.length;i++) {
			s[i] = newX + (s[i] - this.getX()); 
		}
		return new MC_UReal(newX,this.getU(),s);
	}
	public UReal round(){ //returns (i,u) with i the closest int to x
		double [] s = getSample();
		double newX = Math.round(this.getX());
		for (int i=0;i<s.length;i++) {
			s[i] = newX + (s[i] - this.getX()); 
		}
		return new MC_UReal(newX,this.getU(),s);
	}
	public UReal min(MC_UReal r) {
		if (r.lessThan(this)) return r.clone(); 
		return this.clone();
	}
	public UReal max(MC_UReal r) {
		//if (r>this) r; else this;
		if (r.gt(this)) return r.clone(); 
		return this.clone();
	}
	
	/******
	 * Conversions
	 */
	
	public String toString() {
		return "(" + x + "," + u + ") ["+sample[0]+","+sample[1]+","+sample[2]+","+sample[3]+","+sample[4]+"]";
	}

	/******
	 * Other
	 */

	public MC_UReal clone() {
		return new MC_UReal(this.getX(),this.getU(),this.sample);
	}
}
