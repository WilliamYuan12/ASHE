package ashe;

public class ACPCNoLimitHeadsUp {

	public static void main(String[] args) {
		try {
			Ashe ashe = new Ashe(1);
			ashe.ACPCMatch(args);
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}

}
