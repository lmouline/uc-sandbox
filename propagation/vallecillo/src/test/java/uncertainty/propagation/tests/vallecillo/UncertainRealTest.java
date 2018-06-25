package uncertainty.propagation.tests.vallecillo;

import java.util.Calendar;

public class UncertainRealTest {
	final static int NTEST = 20;
	
	static UReal [] n = new UReal [NTEST];
	static UReal [] nU = new UReal [NTEST];
	static MC_UReal [] mnN = new MC_UReal [NTEST];
	static MC_UReal [] mnUN = new MC_UReal [NTEST];
	static MC_UReal [] mnU = new MC_UReal [NTEST];
	static MC_UReal [] mnUU = new MC_UReal [NTEST];

	static MC_UReal [] mnTra=new MC_UReal [NTEST];
	static MC_UReal [] mnTraU=new MC_UReal [NTEST];
	static MC_UReal [] mnTri=new MC_UReal [NTEST];
	static MC_UReal [] mnTriU=new MC_UReal [NTEST];
	static MC_UReal [] mnUsh=new MC_UReal [NTEST];
	static MC_UReal [] mnUshU=new MC_UReal [NTEST];


	
	static void testEquals(){
		for (int i=0;i<NTEST; i++) {
			if (!n[i].equals(nU[i])) System.out.println("Error");
			if (!n[i].equals(mnN[i])) System.out.println("Error");
			if (!n[i].equals(mnUN[i])) System.out.println("Error");
			if (!n[i].equals(mnU[i])) System.out.println("Error");
			if (!n[i].equals(mnUU[i])) System.out.println("Error");
			if (!n[i].equals(mnTra[i])) System.out.println("Error");
			if (!n[i].equals(mnTraU[i])) System.out.println("Error");
			if (!n[i].equals(mnTri[i])) System.out.println("Error");
			if (!n[i].equals(mnTriU[i])) System.out.println("Error");
			if (!n[i].equals(mnUsh[i])) System.out.println("Error");
			if (!n[i].equals(mnUshU[i])) System.out.println("Error");

			if (!nU[i].equals(mnN[i])) System.out.println("Error");
			if (!nU[i].equals(mnUN[i])) System.out.println("Error");
			if (!nU[i].equals(mnU[i])) System.out.println("Error");
			if (!nU[i].equals(mnUU[i])) System.out.println("Error");
			if (!nU[i].equals(mnTra[i])) System.out.println("Error");
			if (!nU[i].equals(mnTraU[i])) System.out.println("Error");
			if (!nU[i].equals(mnTri[i])) System.out.println("Error");
			if (!nU[i].equals(mnTriU[i])) System.out.println("Error");
			if (!nU[i].equals(mnUsh[i])) System.out.println("Error");
			if (!nU[i].equals(mnUshU[i])) System.out.println("Error");

			if (!mnN[i].equals(mnUN[i])) System.out.println("Error");
			if (!mnN[i].equals(mnU[i])) System.out.println("Error");
			if (!mnN[i].equals(mnUU[i])) System.out.println("Error");
			if (!mnN[i].equals(mnTra[i])) System.out.println("Error");
			if (!mnN[i].equals(mnTraU[i])) System.out.println("Error");
			if (!mnN[i].equals(mnTri[i])) System.out.println("Error");
			if (!mnN[i].equals(mnTriU[i])) System.out.println("Error");
			if (!mnN[i].equals(mnUsh[i])) System.out.println("Error");
			if (!mnN[i].equals(mnUshU[i])) System.out.println("Error");

			if (!mnUN[i].equals(mnU[i])) System.out.println("Error");
			if (!mnUN[i].equals(mnUU[i])) System.out.println("Error");
			if (!mnUN[i].equals(mnTra[i])) System.out.println("Error");
			if (!mnUN[i].equals(mnTraU[i])) System.out.println("Error");
			if (!mnUN[i].equals(mnTri[i])) System.out.println("Error");
			if (!mnUN[i].equals(mnTriU[i])) System.out.println("Error");
			if (!mnUN[i].equals(mnUsh[i])) System.out.println("Error");
			if (!mnUN[i].equals(mnUshU[i])) System.out.println("Error");

			if (!mnU[i].equals(mnUU[i])) System.out.println("Error");
			if (!mnU[i].equals(mnTra[i])) System.out.println("Error");
			if (!mnU[i].equals(mnTraU[i])) System.out.println("Error");
			if (!mnU[i].equals(mnTri[i])) System.out.println("Error");
			if (!mnU[i].equals(mnTriU[i])) System.out.println("Error");
			if (!mnU[i].equals(mnUsh[i])) System.out.println("Error");
			if (!mnU[i].equals(mnUshU[i])) System.out.println("Error");

			if (!mnUU[i].equals(mnTra[i])) System.out.println("Error");
			if (!mnUU[i].equals(mnTraU[i])) System.out.println("Error");
			if (!mnUU[i].equals(mnTri[i])) System.out.println("Error");
			if (!mnUU[i].equals(mnTriU[i])) System.out.println("Error");
			if (!mnUU[i].equals(mnUsh[i])) System.out.println("Error");
			if (!mnUU[i].equals(mnUshU[i])) System.out.println("Error");

			if (!mnTra[i].equals(mnTraU[i])) System.out.println("Error");
			if (!mnTra[i].equals(mnTri[i])) System.out.println("Error");
			if (!mnTra[i].equals(mnTriU[i])) System.out.println("Error");
			if (!mnTra[i].equals(mnUsh[i])) System.out.println("Error");
			if (!mnTra[i].equals(mnUshU[i])) System.out.println("Error");

			if (!mnTraU[i].equals(mnTri[i])) System.out.println("Error");
			if (!mnTraU[i].equals(mnTriU[i])) System.out.println("Error");
			if (!mnTraU[i].equals(mnUsh[i])) System.out.println("Error");
			if (!mnTraU[i].equals(mnUshU[i])) System.out.println("Error");

			if (!mnTri[i].equals(mnTriU[i])) System.out.println("Error");
			if (!mnTri[i].equals(mnUsh[i])) System.out.println("Error");
			if (!mnTri[i].equals(mnUshU[i])) System.out.println("Error");

			if (!mnTriU[i].equals(mnUsh[i])) System.out.println("Error");
			if (!mnTriU[i].equals(mnUshU[i])) System.out.println("Error");

			if (!mnUsh[i].equals(mnUshU[i])) System.out.println("Error");
		}
	}
	
	static void testAdd() {
		for (int i=0;i<NTEST; i++) {
			double a =(double)i/10.0;
			for (int j=0;j<NTEST;j++) {
				double b =(double)j/10.0;
				if (!(new UReal(a+b,0.0)).equals(n[i].add(n[j]))) System.out.println("ERROR");
				if (!(new UReal(a+b,0.01)).equals(n[i].add(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.NORMAL)).equals(n[i].add(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.NORMAL)).equals(n[i].add(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.UNIFORM)).equals(n[i].add(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.UNIFORM)).equals(n[i].add(n[j]))) System.out.println("ERROR");

				if (!(new UReal(a+b,0.0)).equals(nU[i].add(n[j]))) System.out.println("ERROR");
				if (!(new UReal(a+b,0.01)).equals(nU[i].add(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.NORMAL)).equals(nU[i].add(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.NORMAL)).equals(nU[i].add(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.UNIFORM)).equals(nU[i].add(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.UNIFORM)).equals(nU[i].add(n[j]))) System.out.println("ERROR");

				if (!(new UReal(a+b,0.0)).equals(mnN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a+b,0.01)).equals(mnN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.NORMAL)).equals(mnN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.NORMAL)).equals(mnN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.UNIFORM)).equals(mnN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.UNIFORM)).equals(mnN[i].add(nU[j]))) System.out.println("ERROR");

				if (!(new UReal(a+b,0.0)).equals(mnU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a+b,0.01)).equals(mnU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.NORMAL)).equals(mnU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.NORMAL)).equals(mnU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.UNIFORM)).equals(mnU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.UNIFORM)).equals(mnU[i].add(nU[j]))) System.out.println("ERROR");

				if (!(new UReal(a+b,0.0)).equals(mnUN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a+b,0.01)).equals(mnUN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.NORMAL)).equals(mnUN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.NORMAL)).equals(mnUN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.UNIFORM)).equals(mnUN[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.UNIFORM)).equals(mnUN[i].add(nU[j]))) System.out.println("ERROR");

				if (!(new UReal(a+b,0.0)).equals(mnUU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a+b,0.01)).equals(mnUU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.NORMAL)).equals(mnUU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.NORMAL)).equals(mnUU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.0,Distribution.UNIFORM)).equals(mnUU[i].add(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a+b,0.01,Distribution.UNIFORM)).equals(mnUU[i].add(nU[j]))) System.out.println("ERROR");


			}
		}
	}

	static void testMult() {
		for (int i=0;i<NTEST; i++) {
			double a =(double)i/10.0;
			for (int j=0;j<NTEST;j++) {
				double b =(double)j/10.0;
				if (!(new UReal(a*b,0.0)).equals(n[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new UReal(a*b,0.01)).equals(n[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.NORMAL)).equals(n[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.NORMAL)).equals(n[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.UNIFORM)).equals(n[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.UNIFORM)).equals(n[i].mult(n[j]))) System.out.println("ERROR");

				if (!(new UReal(a*b,0.0)).equals(nU[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new UReal(a*b,0.01)).equals(nU[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.NORMAL)).equals(nU[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.NORMAL)).equals(nU[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.UNIFORM)).equals(nU[i].mult(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.UNIFORM)).equals(nU[i].mult(n[j]))) System.out.println("ERROR");

				if (!(new UReal(a*b,0.0)).equals(mnN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a*b,0.01)).equals(mnN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.NORMAL)).equals(mnN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.NORMAL)).equals(mnN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.UNIFORM)).equals(mnN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.UNIFORM)).equals(mnN[i].mult(nU[j]))) System.out.println("ERROR");

				if (!(new UReal(a*b,0.0)).equals(mnU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a*b,0.01)).equals(mnU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.NORMAL)).equals(mnU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.NORMAL)).equals(mnU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.UNIFORM)).equals(mnU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.UNIFORM)).equals(mnU[i].mult(nU[j]))) System.out.println("ERROR");

				if (!(new UReal(a*b,0.0)).equals(mnUN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a*b,0.01)).equals(mnUN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.NORMAL)).equals(mnUN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.NORMAL)).equals(mnUN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.UNIFORM)).equals(mnUN[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.UNIFORM)).equals(mnUN[i].mult(nU[j]))) System.out.println("ERROR");

				if (!(new UReal(a*b,0.0)).equals(mnUU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a*b,0.01)).equals(mnUU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.NORMAL)).equals(mnUU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.NORMAL)).equals(mnUU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.0,Distribution.UNIFORM)).equals(mnUU[i].mult(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a*b,0.01,Distribution.UNIFORM)).equals(mnUU[i].mult(nU[j]))) System.out.println("ERROR");

			}
		}
	}

	static void testDiv() {
		for (int i=1;i<NTEST; i++) {
			double a =(double)i/10.0;
			for (int j=1;j<NTEST;j++) {
				double b =(double)j/10.0;
				if (!(new UReal(a/b,0.0)).equals(n[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new UReal(a/b,0.01)).equals(n[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.NORMAL)).equals(n[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.NORMAL)).equals(n[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.UNIFORM)).equals(n[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.UNIFORM)).equals(n[i].divideBy(n[j]))) System.out.println("ERROR");

				if (!(new UReal(a/b,0.0)).equals(nU[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new UReal(a/b,0.01)).equals(nU[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.NORMAL)).equals(nU[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.NORMAL)).equals(nU[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.UNIFORM)).equals(nU[i].divideBy(n[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.UNIFORM)).equals(nU[i].divideBy(n[j]))) System.out.println("ERROR");

				if (!(new UReal(a/b,0.0)).equals(mnN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a/b,0.01)).equals(mnN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.NORMAL)).equals(mnN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.NORMAL)).equals(mnN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.UNIFORM)).equals(mnN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.UNIFORM)).equals(mnN[i].divideBy(nU[j]))) System.out.println("ERROR");

				if (!(new UReal(a/b,0.0)).equals(mnU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a/b,0.01)).equals(mnU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.NORMAL)).equals(mnU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.NORMAL)).equals(mnU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.UNIFORM)).equals(mnU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.UNIFORM)).equals(mnU[i].divideBy(nU[j]))) System.out.println("ERROR");

				if (!(new UReal(a/b,0.0)).equals(mnUN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a/b,0.01)).equals(mnUN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.NORMAL)).equals(mnUN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.NORMAL)).equals(mnUN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.UNIFORM)).equals(mnUN[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.UNIFORM)).equals(mnUN[i].divideBy(nU[j]))) System.out.println("ERROR");

				if (!(new UReal(a/b,0.0)).equals(mnUU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new UReal(a/b,0.01)).equals(mnUU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.NORMAL)).equals(mnUU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.NORMAL)).equals(mnUU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.0,Distribution.UNIFORM)).equals(mnUU[i].divideBy(nU[j]))) System.out.println("ERROR");
				if (!(new MC_UReal(a/b,0.01,Distribution.UNIFORM)).equals(mnUU[i].divideBy(nU[j]))) System.out.println("ERROR");

			}
		}
	}

	static void testLess() {
		for (int i=0;i<NTEST; i++) {
			for (int j=i+1;j<NTEST;j++) {
				if (!n[i].lessThan(n[j])) System.out.println("Error");
				if (!n[i].lessThan(nU[j])) System.out.println("Error");
				if (!n[i].lessThan(mnN[j])) System.out.println("Error");
				if (!n[i].lessThan(mnUN[j])) System.out.println("Error");
				if (!n[i].lessThan(mnU[j])) System.out.println("Error");
				if (!n[i].lessThan(mnUU[j])) System.out.println("Error");

				if (!nU[i].lessThan(mnN[j])) System.out.println("Error");
				if (!nU[i].lessThan(mnN[j])) System.out.println("Error");
				if (!nU[i].lessThan(mnUN[j])) System.out.println("Error");
				if (!nU[i].lessThan(mnU[j])) System.out.println("Error");
				if (!nU[i].lessThan(mnUU[j])) System.out.println("Error");

				if (!mnN[i].lessThan(mnUN[j])) System.out.println("Error");
				if (!mnN[i].lessThan(mnUN[j])) System.out.println("Error");
				if (!mnN[i].lessThan(mnU[j])) System.out.println("Error");
				if (!mnN[i].lessThan(mnUU[j])) System.out.println("Error");

				if (!mnUN[i].lessThan(mnU[j])) System.out.println("Error");
				if (!mnUN[i].lessThan(mnU[j])) System.out.println("Error");
				if (!mnUN[i].lessThan(mnUU[j])) System.out.println("Error");

				if (!mnU[i].lessThan(mnUU[j])) System.out.println("Error");
				if (!mnU[i].lessThan(mnUU[j])) System.out.println("Error");

				if (!mnUU[i].lessThan(mnUU[j])) System.out.println("Error");

			}
		}
	}

	static public void testPower(){
		
		for (int i=1;i<NTEST; i++) {
			double a =(double)i/10.0; 
			for (int j=1; j<NTEST;j++) {
				float s = ((float)j)/10; 
				UReal x1 = new UReal(Math.pow(a,s),0.0);
				UReal x2 = new UReal(Math.pow(a,s),0.01);
				MC_UReal x3 = new MC_UReal(Math.pow(a,s),0.0,Distribution.NORMAL);
				MC_UReal x4 = new MC_UReal(Math.pow(a,s),0.01,Distribution.NORMAL);
				MC_UReal x5 = new MC_UReal(Math.pow(a,s),0.0,Distribution.UNIFORM);
				MC_UReal x6 = new MC_UReal(Math.pow(a,s),0.01,Distribution.UNIFORM);
				
				if (!(x1).equals(n[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|1|"+x1+"->"+n[i].power(s));;;}
				if (!(x2).equals(n[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|2|"+x2+"->"+n[i].power(s));;;}
				if (!(x3).equals(n[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|3|"+x3+"->"+n[i].power(s));;;}
				if (!(x4).equals(n[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|4|"+x4+"->"+n[i].power(s));;;}
				if (!(x5).equals(n[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|5|"+x5+"->"+n[i].power(s));;;}
				if (!(x6).equals(n[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|6|"+x6+"->"+n[i].power(s));;;}

				if (!(x1).equals(nU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|10|"+x1+"->"+nU[i].power(s));;;}
				if (!(x2).equals(nU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|11|"+x2+"->"+nU[i].power(s));;;}
				if (!(x3).equals(nU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|12|"+x3+"->"+nU[i].power(s));;;}
				if (!(x4).equals(nU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|13|"+x4+"->"+nU[i].power(s));;;}
				if (!(x5).equals(nU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|14|"+x5+"->"+nU[i].power(s));;;}
				if (!(x6).equals(nU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|15|"+x6+"->"+nU[i].power(s));;;}

				if (!(x1).equals(mnN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|20|"+x1+"->"+mnN[i].power(s));;;}
				if (!(x2).equals(mnN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|21|"+x2+"->"+mnN[i].power(s));;;}
				if (!(x3).equals(mnN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|22|"+x3+"->"+mnN[i].power(s));;;}
				if (!(x4).equals(mnN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|23|"+x4+"->"+mnN[i].power(s));;;}
				if (!(x5).equals(mnN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|24|"+x5+"->"+mnN[i].power(s));;;}
				if (!(x6).equals(mnN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|25|"+x6+"->"+mnN[i].power(s));;;}

				if (!(x1).equals(mnUN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|30|"+x1+"->"+mnUN[i].power(s));;;}
				if (!(x2).equals(mnUN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|31|"+x2+"->"+mnUN[i].power(s));;;}
				if (!(x3).equals(mnUN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|32|"+x3+"->"+mnUN[i].power(s));;;}
				if (!(x4).equals(mnUN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|33|"+x4+"->"+mnUN[i].power(s));;;}
				if (!(x5).equals(mnUN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|34|"+x5+"->"+mnUN[i].power(s));;;}
				if (!(x6).equals(mnUN[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|35|"+x6+"->"+mnUN[i].power(s));;;}

				if (!(x1).equals(mnU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|40|"+x1+"->"+mnU[i].power(s));;;}
				if (!(x2).equals(mnU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|41|"+x2+"->"+mnU[i].power(s));;;}
				if (!(x3).equals(mnU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|42|"+x3+"->"+mnU[i].power(s));;;}
				if (!(x4).equals(mnU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|43|"+x4+"->"+mnU[i].power(s));;;}
				if (!(x5).equals(mnU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|44|"+x5+"->"+mnU[i].power(s));;;}
				if (!(x6).equals(mnU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|45|"+x6+"->"+mnU[i].power(s));;;}

				if (!(x1).equals(mnUU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|50|"+x1+"->"+mnUU[i].power(s));;;}
				if (!(x2).equals(mnUU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|51|"+x2+"->"+mnUU[i].power(s));;;}
				if (!(x3).equals(mnUU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|52|"+x3+"->"+mnUU[i].power(s));;;}
				if (!(x4).equals(mnUU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|53|"+x4+"->"+mnUU[i].power(s));;;}
				if (!(x5).equals(mnUU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|54|"+x5+"->"+mnUU[i].power(s));;;}
				if (!(x6).equals(mnUU[i].power(s))) {System.out.println("ERROR ("+i+"|"+s+"|55|"+x6+"->"+mnUU[i].power(s));;;}
			}
		}
	}
	
static public void testFloor(){
		
	for (int i=1;i<NTEST; i++) {
		double a =1+(double)i*0.49/NTEST; 
		double b = a + 0.5;
		for (int j=1; j<NTEST;j++) {

			UReal x1 = new UReal(a,0.01);
			UReal x2 = new UReal(b,0.001);
			MC_UReal x3 = new MC_UReal(a,0.01,Distribution.NORMAL);
			MC_UReal x4 = new MC_UReal(a,0.001,Distribution.NORMAL);
			MC_UReal x5 = new MC_UReal(b,0.01,Distribution.UNIFORM);
			MC_UReal x6 = new MC_UReal(b,0.001,Distribution.UNIFORM);
	
	if (!x1.floor().equals(x2.floor())) System.out.println("Error");
	if (!x1.floor().equals(x3.floor())) System.out.println("Error");
	if (!x1.floor().equals(x4.floor())) System.out.println("Error");
	if (!x1.floor().equals(x5.floor())) System.out.println("Error");
	if (!x1.floor().equals(x6.floor())) System.out.println("Error");
	if (!x2.floor().equals(x1.floor())) System.out.println("Error");
	if (!x2.floor().equals(x3.floor())) System.out.println("Error");
	if (!x2.floor().equals(x4.floor())) System.out.println("Error");
	if (!x2.floor().equals(x5.floor())) System.out.println("Error");
	if (!x2.floor().equals(x6.floor())) System.out.println("Error");
	if (!x3.floor().equals(x4.floor())) System.out.println("Error");
	if (!x3.floor().equals(x5.floor())) System.out.println("Error");
	if (!x3.floor().equals(x6.floor())) System.out.println("Error");
	if (!x4.floor().equals(x5.floor())) System.out.println("Error");
	if (!x4.floor().equals(x6.floor())) System.out.println("Error");
	if (!x5.floor().equals(x6.floor())) System.out.println("Error");


	if (x1.round().equals(x2.round())) System.out.println("Error");
	if (!x1.round().equals(x3.round())) System.out.println("Error");
	if (!x1.round().equals(x4.round())) System.out.println("Error");
	if (x1.round().equals(x5.round())) System.out.println("Error");
	if (x1.round().equals(x6.round())) System.out.println("Error");

	if (x2.round().equals(x3.round())) System.out.println("Error");
	if (x2.round().equals(x4.round())) System.out.println("Error");
	if (!x2.round().equals(x5.round())) System.out.println("Error");
	if (!x2.round().equals(x6.round())) System.out.println("Error");

	if (!x3.round().equals(x4.round())) System.out.println("Error");
	if (x3.round().equals(x5.round())) System.out.println("Error");
	if (x3.round().equals(x6.round())) System.out.println("Error");

	if (x4.round().equals(x5.round())) System.out.println("Error");
	if (x4.round().equals(x6.round())) System.out.println("Error");

	if (!x5.round().equals(x6.round())) System.out.println("Error");
		}
	}
}

	public static void main(String[] args) {
	
	
		System.out.println("Loading sample...");
		long millisStart = Calendar.getInstance().get(Calendar.MILLISECOND);
		long millisEnd;
		float total;
		
		
		for (int i=0; i<NTEST; i++) {
			n[i]=new UReal((double)i/10,0.0);
			nU[i]=new UReal((double)i/10,0.01);
			mnN[i]=new MC_UReal((double)i/10,0.0,Distribution.NORMAL);
			mnUN[i]=new MC_UReal((double)i/10,0.01,Distribution.NORMAL);
			mnU[i]=new MC_UReal((double)i/10,0.0,Distribution.UNIFORM);
			mnUU[i]=new MC_UReal((double)i/10,0.01,Distribution.UNIFORM);
			mnTra[i]=new MC_UReal((double)i/10,0.0,Distribution.TRUNCATED);
			mnTraU[i]=new MC_UReal((double)i/10,0.0,Distribution.TRUNCATED);
			mnTri[i]=new MC_UReal((double)i/10,0.0,Distribution.TRIANGULAR);
			mnTriU[i]=new MC_UReal((double)i/10,0.0,Distribution.TRIANGULAR);
			mnUsh[i]=new MC_UReal((double)i/10,0.0,Distribution.USHAPED);
			mnUshU[i]=new MC_UReal((double)i/10,0.0,Distribution.USHAPED);
		}		
		System.out.println("EQUALS");
		testEquals();
		millisEnd = Calendar.getInstance().get(Calendar.MILLISECOND);
		total = (millisEnd - millisStart);
		System.out.println("Total time: "+ total + " ms");
		System.out.println("ADD");
		testAdd();
		millisEnd = Calendar.getInstance().get(Calendar.MILLISECOND);
		total = (millisEnd - millisStart);
		System.out.println("Total time: "+ total + " ms");
		System.out.println("LESS");
		testLess();
		millisEnd = Calendar.getInstance().get(Calendar.MILLISECOND);
		total = (millisEnd - millisStart);
		System.out.println("Total time: "+ total + " ms");
		System.out.println("MULT");
		testMult();
		millisEnd = Calendar.getInstance().get(Calendar.MILLISECOND);
		total = (millisEnd - millisStart);
		System.out.println("Total time: "+ total + " ms");
		System.out.println("POWER");
		testPower();
		millisEnd = Calendar.getInstance().get(Calendar.MILLISECOND);
		total = (millisEnd - millisStart);
		System.out.println("Total time: "+ total + " ms");
		System.out.println("DIVIDE");
		testDiv();
		millisEnd = Calendar.getInstance().get(Calendar.MILLISECOND);
		total = (millisEnd - millisStart);
		System.out.println("Total time: "+ total + " ms");
		System.out.println("FLOOR");
		
		testFloor();
		
		millisEnd = Calendar.getInstance().get(Calendar.MILLISECOND);
		total = (millisEnd - millisStart);
		System.out.println("Total time: "+ total + " ms");
		
		System.out.println("DONE");
	}

}
