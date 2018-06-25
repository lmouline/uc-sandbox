package uncertainty.propagation.tests.vallecillo;

public interface IUReal {
	double getX();
	void setX(double d);
	double getU();
	void setU(double u);
	IUReal add(IUReal r);
	IUReal minus(IUReal r);
	IUReal mult(IUReal r);
	IUReal divideBy(IUReal r);
	IUReal abs();
	IUReal neg();
	IUReal power(float s);
	IUReal sqrt();
	boolean lessThan(IUReal r);
	boolean lessEq(IUReal r);
	boolean gt(IUReal r);
	boolean ge(IUReal r);
	boolean equals(IUReal r);
	boolean distinct(IUReal r);
	int hashcode();
	String toString();
	IUReal inverse(); //inverse (reciprocal)
	IUReal floor(); //returns (i,u) with i the largest int such that (i,u)<=(x,u)
	IUReal round(); //returns (i,u) with i the closest int to x
	IUReal min(IUReal r); //if (r<this) r; else this;
	IUReal max(IUReal r);//if (r>this) r; else this;
	int toInteger(); //returns this.getX().toInteger()
	double toReal(); //returns this.getX()
	//in addition, two constructors are added
	//public IUReal(String x); //creates an IUReal from a string representing a real, with u=0.
	//public IUReal(String x, String u); //creates an IUReal from two strings representing (x,u).
}
