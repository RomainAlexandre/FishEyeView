package fr.unice.reneviergonin.fisheye01.deform;

public class BasicDeform extends AbstractFormula {
	
	double zmoinso ;
	double zmoinso2 ;
	double z2zmoinso2;
	double r2;
	double z2;
	double r2moinszmoinso2;
	double zzmoinso;
	
	@Override
	public double deform(int x) {
		double result;
		result = (x*x+z2)*r2moinszmoinso2+z2zmoinso2;
		result = (x*(Math.sqrt(result)+zzmoinso));
		result = result / (x*x+z2);
		return  result;
	}

	@Override
	public void setParams(Double zz, Double rr, Double oo) {
		// order of parameters :  z, r et 0
		z = (zz==null)?z:zz;
		r = (rr==null)?r:rr;
		o = (oo==null)?o:oo;
		
		zmoinso = z-o;

		r2 = r*r;
		z2 = z*z;
		
		z2zmoinso2 = z2*zmoinso2;
		zmoinso2 = zmoinso*zmoinso;
		r2moinszmoinso2 = r2-zmoinso2;
		zzmoinso = z*zmoinso;
		
		this.setChanged();
		this.notifyObservers();
	}

	@Override
	public double reverse(double x) {
		double result = (double) x;
		
		result = result*z/(Math.sqrt(r2 - result * result)+zmoinso);
		
		return result;
	}

}
