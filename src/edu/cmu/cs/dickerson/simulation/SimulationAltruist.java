package edu.cmu.cs.dickerson.simulation;

import java.util.Arrays;
import java.util.List;

import edu.cmu.cs.dickerson.kpd.structure.VertexAltruist;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

public class SimulationAltruist extends VertexAltruist{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Continuous features
	double age_don;
	double egfr;
	double bmi_don;
	double bp_systolic;
	double weight_don;
	
	//Binary features
	boolean isAfricanAmerican;
	boolean isCigaretteUser;
	boolean isDonorMale;
	
	//HLA
	double[] HLA_A_don = new double[2];
	double[] HLA_B_don = new double[2];
	double[] HLA_DR_don = new double[2];
	
	//Lookup code for alts.csv
	static List<String> dict =  Arrays.asList(new String[]{"ABO_DON", "AFRICAN_AMERICAN", "AGE_AT_ADD_DON",
			"BMI_DON","BP_SYSTOLIC","DA1","DA2","DB1","DB2","DDR1","DDR2","EGFR","HCU","SEX_DON","WEIGHT_DON"});

	public SimulationAltruist(int ID, String[] entries) {
		super(ID);
		
		this.bloodTypeDonor = BloodType.getBloodType(entries[0]);
		this.isAfricanAmerican = entries[1].equals("1") ? true : false;
		this.age_don = Double.parseDouble(entries[2]);
		this.bmi_don = Double.parseDouble(entries[3]);
		this.bp_systolic = Double.parseDouble(entries[4]);
		this.HLA_A_don[0] = Double.parseDouble(entries[5]);
		this.HLA_A_don[1] = Double.parseDouble(entries[6]);
		this.HLA_B_don[0] = Double.parseDouble(entries[7]);
		this.HLA_B_don[1] = Double.parseDouble(entries[8]);
		this.HLA_DR_don[0] = Double.parseDouble(entries[9]);
		this.HLA_DR_don[1] = Double.parseDouble(entries[10]);
		this.egfr = Double.parseDouble(entries[11]);
		this.isCigaretteUser = entries[12].equals("1") ? true : false;
		this.isDonorMale = entries[13].equals("1") ? true : false;
		this.weight_don = Double.parseDouble(entries[14]);
	}

}
