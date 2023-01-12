package openkpd;

import java.util.Arrays;
import java.util.List;

import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

public class SimulationPair extends VertexPair {
	
	/**
	 * 
	 */
	//Number of iterations in the pool
	private int iterations = 0;
	private int num_pairs; //number of pairs at instantiation
	private int num_alts; // number of altruists at instantiation
	
	public int DISPLAY_ID;
	
	
	//Extra features for UNOS policy
	//Already included is patient CPRA and donor/patient blood types
	
	//Continuous features
	double age_don;
	double age_cand;
	double egfr;
	double bmi_don;
	double bp_systolic;
	double weight_don;
	double weight_cand;
	
	//Binary features
	boolean isAfricanAmerican;
	boolean isCigaretteUser;
	boolean isDonorMale;
	boolean isPatientMale;
	
	//HLA
	double[] HLA_A_don = new double[2];
	double[] HLA_B_don = new double[2];
	double[] HLA_DR_don = new double[2];
	
	double[] HLA_A_cand = new double[2];
	double[] HLA_B_cand = new double[2];
	double[] HLA_DR_cand = new double[2];
	
	//Lookup code for pairs.csv
	static List<String> dict =  Arrays.asList(new String[]{"ABO_CAND","ABO_DON","AFRICAN_AMERICAN","AGE_AT_ADD_CAND","AGE_AT_ADD_DON",
	                 "BMI_DON","BP_SYSTOLIC","CA1","CA2","CB1","CB2","CDR1","CDR2","CPRA_AT_MATCH_RUN",
	                 "DA1","DA2","DB1","DB2","DDR1","DDR2","EGFR","HCU","SEX_CAND","SEX_DON",
	                 "WEIGHT_CAND","WEIGHT_DON"});
	
	public SimulationPair(int ID, String[] entries) {
		super(ID);
		DISPLAY_ID = ID;
		this.isWifePatient = false;
		
		this.bloodTypePatient = BloodType.getBloodType(entries[0]);
		this.bloodTypeDonor = BloodType.getBloodType(entries[1]);
		this.isAfricanAmerican = entries[2].equals("1") ? true : false;
		this.age_cand = Double.parseDouble(entries[3]);
		this.age_don = Double.parseDouble(entries[4]);
		this.bmi_don = Double.parseDouble(entries[5]);
		this.bp_systolic = Double.parseDouble(entries[6]);
		this.HLA_A_cand[0] = Double.parseDouble(entries[7]);
		this.HLA_A_cand[1] = Double.parseDouble(entries[8]);
		this.HLA_B_cand[0] = Double.parseDouble(entries[9]);
		this.HLA_B_cand[1] = Double.parseDouble(entries[10]);
		this.HLA_DR_cand[0] = Double.parseDouble(entries[11]);
		this.HLA_DR_cand[1] = Double.parseDouble(entries[12]);
		this.patientCPRA = Double.parseDouble(entries[13]);
		this.HLA_A_don[0] = Double.parseDouble(entries[14]);
		this.HLA_A_don[1] = Double.parseDouble(entries[15]);
		this.HLA_B_don[0] = Double.parseDouble(entries[16]);
		this.HLA_B_don[1] = Double.parseDouble(entries[17]);
		this.HLA_DR_don[0] = Double.parseDouble(entries[18]);
		this.HLA_DR_don[1] = Double.parseDouble(entries[19]);
		this.egfr = Double.parseDouble(entries[20]);
		this.isCigaretteUser = entries[21].equals("1") ? true : false;
		this.isPatientMale = entries[22].equals("1") ? true : false;
		this.isDonorMale = entries[23].equals("1") ? true : false;
		this.weight_cand = Double.parseDouble(entries[24]);
		this.weight_don = Double.parseDouble(entries[25]);
		
	}
	
	public void setCompatible(boolean ic) {
		isCompatible = ic;
	}
	
	public int getIterations() {
		return iterations;
	}
	
	public void increment() {
		iterations++;
	}
	
	public void reset() {
		iterations = 0;
	}
	
	public void set(int np, int na) {
		num_pairs = np;
		num_alts = na;
	}
	
	public int getPairs() {
		return num_pairs;
	}
	
	public int getAlts() {
		return num_alts;
	}
	
	public void setDisplay(int id) {
		DISPLAY_ID = id;
	}

}
