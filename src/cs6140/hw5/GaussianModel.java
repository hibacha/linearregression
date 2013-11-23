package cs6140.hw5;

public class GaussianModel {

	/**
	 * @param args
	 */
	double  mu;
	double  sigma;
	double  w;

	public GaussianModel(double mu, double sigma, double w) {
		super();
		this.mu = mu;
		this.sigma = sigma;
		this.w = w;
	}
	
	public double logPdf(double x){
		
		return -0.5*(Math.log(sigma)+Math.log(2*Math.PI))-((x-mu)*(x-mu))/(2*sigma);
		
	}
	
	public double weightPdf(double x){
		if(w==0){
		  return 0;
		}
		double r=Math.exp(logPdf(x))*w;
		return r;
	}
	public double weightlogPdf(double x){
		return w*logPdf(x);
	}
}
