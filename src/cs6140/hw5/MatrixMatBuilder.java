package cs6140.hw5;

public class MatrixMatBuilder {

	/**
	 * @param args
	 */
	public static final String SUB="minus";
	public static final String ADD="plus";
	public static final String MUL="times";
	public static String ArrayScanOp(String op,String var1, String var2){
		return "bsxfun(@"+op+","+var1+","+var2+")";
	}
    
	public static String getCol(String mat, int index){
		return mat+"(:,"+index+")";
	}
	
	public static String tranpose(String mat){
		return mat+"'";
	}
}
