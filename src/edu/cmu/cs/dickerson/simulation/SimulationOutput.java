package edu.cmu.cs.dickerson.simulation;

import java.io.IOException;

import edu.cmu.cs.dickerson.kpd.io.Output;
import edu.cmu.cs.dickerson.kpd.io.OutputCol;

public class SimulationOutput extends Output {
	
	public enum Col implements OutputCol {
		ID,
		MR_NUM,
		ABO_CAND,
		ABO_DON,
		AFRICAN_AMERICAN_DON,
		AGE_AT_ADD_CAND,
		AGE_AT_ADD_DON,
		BMI_DON,
		BP_SYSTOLIC_DON,
		CA1,
		CA2,
		CB1,
		CB2,
		CDR1,
		CDR2,
		CPRA_AT_MATCH_RUN,
		DA1,
		DA2,
		DB1,
		DB2,
		DDR1,
		DDR2,
		EGFR_DON,
		HCU_DON,
		SEX_CAND,
		SEX_DON,
		WEIGHT_CAND,
		WEIGHT_DON,
		ABO_MATCH,
		AFRICAN_AMERICAN_MATCH,
		AGE_AT_ADD_MATCH,
		BMI_MATCH,
		BP_SYSTOLIC_MATCH,
		MA1,
		MA2,
		MB1,
		MB2,
		MDR1,
		MDR2,
		EGFR_MATCH,
		HCU_MATCH,
		SEX_MATCH,
		WEIGHT_MATCH,
		NUM_PAIRS,
		NUM_ALTS,
		OUTCOME,
		ITERATIONS,
		LKDPI;
		
		@Override
		public int getColIdx() {
			return this.ordinal();
		}
		
	}
	
	/**
	 * Add an edge of a given match to the CSV output
	 * @param p The receiving pair
	 * @param m The donating pair
	 * @throws IOException
	 */
	public void match(SimulationPair p, SimulationPair m, int iter) throws IOException {
		this.set(Col.ID, p.DISPLAY_ID);
		this.set(Col.MR_NUM, iter);
		this.set(Col.ABO_CAND, p.getBloodTypePatient());
		this.set(Col.ABO_DON, p.getBloodTypeDonor());
		this.set(Col.AFRICAN_AMERICAN_DON, p.isAfricanAmerican);
		this.set(Col.AGE_AT_ADD_CAND, p.age_cand);
		this.set(Col.AGE_AT_ADD_DON, p.age_don);
		this.set(Col.BMI_DON, p.bmi_don);
		this.set(Col.BP_SYSTOLIC_DON, p.bp_systolic);
		this.set(Col.CA1, p.HLA_A_cand[0]);
		this.set(Col.CA2, p.HLA_A_cand[1]);
		this.set(Col.CB1, p.HLA_B_cand[0]);
		this.set(Col.CB2, p.HLA_B_cand[1]);
		this.set(Col.CDR1, p.HLA_DR_cand[0]);
		this.set(Col.CDR2, p.HLA_DR_cand[1]);
		this.set(Col.CPRA_AT_MATCH_RUN, p.getPatientCPRA());
		this.set(Col.DA1, p.HLA_A_don[0]);
		this.set(Col.DA2, p.HLA_A_don[1]);
		this.set(Col.DB1, p.HLA_B_don[0]);
		this.set(Col.DB2, p.HLA_B_don[1]);
		this.set(Col.DDR1, p.HLA_DR_don[0]);
		this.set(Col.DDR2, p.HLA_DR_don[1]);
		this.set(Col.EGFR_DON, p.egfr);
		this.set(Col.HCU_DON, p.isCigaretteUser);
		this.set(Col.SEX_CAND, p.isPatientMale);
		this.set(Col.SEX_DON, p.isDonorMale);
		this.set(Col.WEIGHT_CAND, p.weight_cand);
		this.set(Col.WEIGHT_DON, p.weight_don);
		
		this.set(Col.ABO_MATCH, m.getBloodTypeDonor());
		this.set(Col.AFRICAN_AMERICAN_MATCH, m.isAfricanAmerican);
		this.set(Col.AGE_AT_ADD_MATCH, m.age_don);
		this.set(Col.BMI_MATCH, m.bmi_don);
		this.set(Col.BP_SYSTOLIC_MATCH, m.bp_systolic);
		this.set(Col.MA1, m.HLA_A_don[0]);
		this.set(Col.MA2, m.HLA_A_don[1]);
		this.set(Col.MB1, m.HLA_B_don[0]);
		this.set(Col.MB2, m.HLA_B_don[1]);
		this.set(Col.MDR1, m.HLA_DR_don[0]);
		this.set(Col.MDR2, m.HLA_DR_don[1]);
		this.set(Col.EGFR_MATCH, m.egfr);
		this.set(Col.HCU_MATCH, m.isCigaretteUser);
		this.set(Col.SEX_MATCH, m.isDonorMale);
		this.set(Col.WEIGHT_MATCH, m.weight_don);
		
		this.set(Col.NUM_PAIRS, p.getPairs());
		this.set(Col.NUM_ALTS, p.getAlts());
		this.set(Col.ITERATIONS, p.getIterations());
		this.set(Col.OUTCOME, "MATCHED");
		
		this.set(Col.LKDPI, Simulation.LKDPI(p,m));
		
		this.record();
	}
	public void match(SimulationPair p, SimulationAltruist m, int iter) throws IOException {
		this.set(Col.ID, p.DISPLAY_ID);
		this.set(Col.MR_NUM, iter);
		this.set(Col.ABO_CAND, p.getBloodTypePatient());
		this.set(Col.ABO_DON, p.getBloodTypeDonor());
		this.set(Col.AFRICAN_AMERICAN_DON, p.isAfricanAmerican);
		this.set(Col.AGE_AT_ADD_CAND, p.age_cand);
		this.set(Col.AGE_AT_ADD_DON, p.age_don);
		this.set(Col.BMI_DON, p.bmi_don);
		this.set(Col.BP_SYSTOLIC_DON, p.bp_systolic);
		this.set(Col.CA1, p.HLA_A_cand[0]);
		this.set(Col.CA2, p.HLA_A_cand[1]);
		this.set(Col.CB1, p.HLA_B_cand[0]);
		this.set(Col.CB2, p.HLA_B_cand[1]);
		this.set(Col.CDR1, p.HLA_DR_cand[0]);
		this.set(Col.CDR2, p.HLA_DR_cand[1]);
		this.set(Col.CPRA_AT_MATCH_RUN, p.getPatientCPRA());
		this.set(Col.DA1, p.HLA_A_don[0]);
		this.set(Col.DA2, p.HLA_A_don[1]);
		this.set(Col.DB1, p.HLA_B_don[0]);
		this.set(Col.DB2, p.HLA_B_don[1]);
		this.set(Col.DDR1, p.HLA_DR_don[0]);
		this.set(Col.DDR2, p.HLA_DR_don[1]);
		this.set(Col.EGFR_DON, p.egfr);
		this.set(Col.HCU_DON, p.isCigaretteUser);
		this.set(Col.SEX_CAND, p.isPatientMale);
		this.set(Col.SEX_DON, p.isDonorMale);
		this.set(Col.WEIGHT_CAND, p.weight_cand);
		this.set(Col.WEIGHT_DON, p.weight_don);
		
		this.set(Col.ABO_MATCH, m.getBloodTypeDonor());
		this.set(Col.AFRICAN_AMERICAN_MATCH, m.isAfricanAmerican);
		this.set(Col.AGE_AT_ADD_MATCH, m.age_don);
		this.set(Col.BMI_MATCH, m.bmi_don);
		this.set(Col.BP_SYSTOLIC_MATCH, m.bp_systolic);
		this.set(Col.MA1, m.HLA_A_don[0]);
		this.set(Col.MA2, m.HLA_A_don[1]);
		this.set(Col.MB1, m.HLA_B_don[0]);
		this.set(Col.MB2, m.HLA_B_don[1]);
		this.set(Col.MDR1, m.HLA_DR_don[0]);
		this.set(Col.MDR2, m.HLA_DR_don[1]);
		this.set(Col.EGFR_MATCH, m.egfr);
		this.set(Col.HCU_MATCH, m.isCigaretteUser);
		this.set(Col.SEX_MATCH, m.isDonorMale);
		this.set(Col.WEIGHT_MATCH, p.weight_don);
		
		this.set(Col.NUM_PAIRS, p.getPairs());
		this.set(Col.NUM_ALTS, p.getAlts());
		this.set(Col.ITERATIONS, p.getIterations());
		this.set(Col.OUTCOME, "MATCHED");
		
		this.set(Col.LKDPI, Simulation.LKDPI(p,m));
		
		this.record();
	}
	
	/**
	 * Adds a patient expiry to the CSV output
	 * @param p The expired pair
	 * @throws IOException
	 */
	public void expire(SimulationPair p, int iter) throws IOException {
		this.set(Col.ID, p.DISPLAY_ID);
		this.set(Col.MR_NUM, iter);
		this.set(Col.ABO_CAND, p.getBloodTypePatient());
		this.set(Col.ABO_DON, p.getBloodTypeDonor());
		this.set(Col.AFRICAN_AMERICAN_DON, p.isAfricanAmerican);
		this.set(Col.AGE_AT_ADD_CAND, p.age_cand);
		this.set(Col.AGE_AT_ADD_DON, p.age_don);
		this.set(Col.BMI_DON, p.bmi_don);
		this.set(Col.BP_SYSTOLIC_DON, p.bp_systolic);
		this.set(Col.CA1, p.HLA_A_cand[0]);
		this.set(Col.CA2, p.HLA_A_cand[1]);
		this.set(Col.CB1, p.HLA_B_cand[0]);
		this.set(Col.CB2, p.HLA_B_cand[1]);
		this.set(Col.CDR1, p.HLA_DR_cand[0]);
		this.set(Col.CDR2, p.HLA_DR_cand[1]);
		this.set(Col.CPRA_AT_MATCH_RUN, p.getPatientCPRA());
		this.set(Col.DA1, p.HLA_A_don[0]);
		this.set(Col.DA2, p.HLA_A_don[1]);
		this.set(Col.DB1, p.HLA_B_don[0]);
		this.set(Col.DB2, p.HLA_B_don[1]);
		this.set(Col.DDR1, p.HLA_DR_don[0]);
		this.set(Col.DDR2, p.HLA_DR_don[1]);
		this.set(Col.EGFR_DON, p.egfr);
		this.set(Col.HCU_DON, p.isCigaretteUser);
		this.set(Col.SEX_CAND, p.isPatientMale);
		this.set(Col.SEX_DON, p.isDonorMale);
		this.set(Col.WEIGHT_CAND, p.weight_cand);
		this.set(Col.WEIGHT_DON, p.weight_don);
		this.set(Col.ITERATIONS, p.getIterations());
		this.set(Col.OUTCOME, "EXPIRED");
		this.set(Col.NUM_PAIRS, p.getPairs());
		this.set(Col.NUM_ALTS, p.getAlts());
		this.record();
	}
	
	public void timeout(SimulationPair p, int iter) throws IOException {
		this.set(Col.ID, p.DISPLAY_ID);
		this.set(Col.MR_NUM, iter);
		this.set(Col.ABO_CAND, p.getBloodTypePatient());
		this.set(Col.ABO_DON, p.getBloodTypeDonor());
		this.set(Col.AFRICAN_AMERICAN_DON, p.isAfricanAmerican);
		this.set(Col.AGE_AT_ADD_CAND, p.age_cand);
		this.set(Col.AGE_AT_ADD_DON, p.age_don);
		this.set(Col.BMI_DON, p.bmi_don);
		this.set(Col.BP_SYSTOLIC_DON, p.bp_systolic);
		this.set(Col.CA1, p.HLA_A_cand[0]);
		this.set(Col.CA2, p.HLA_A_cand[1]);
		this.set(Col.CB1, p.HLA_B_cand[0]);
		this.set(Col.CB2, p.HLA_B_cand[1]);
		this.set(Col.CDR1, p.HLA_DR_cand[0]);
		this.set(Col.CDR2, p.HLA_DR_cand[1]);
		this.set(Col.CPRA_AT_MATCH_RUN, p.getPatientCPRA());
		this.set(Col.DA1, p.HLA_A_don[0]);
		this.set(Col.DA2, p.HLA_A_don[1]);
		this.set(Col.DB1, p.HLA_B_don[0]);
		this.set(Col.DB2, p.HLA_B_don[1]);
		this.set(Col.DDR1, p.HLA_DR_don[0]);
		this.set(Col.DDR2, p.HLA_DR_don[1]);
		this.set(Col.EGFR_DON, p.egfr);
		this.set(Col.HCU_DON, p.isCigaretteUser);
		this.set(Col.SEX_CAND, p.isPatientMale);
		this.set(Col.SEX_DON, p.isDonorMale);
		this.set(Col.WEIGHT_CAND, p.weight_cand);
		this.set(Col.WEIGHT_DON, p.weight_don);
		this.set(Col.ITERATIONS, p.getIterations());
		this.set(Col.OUTCOME, "TIMEOUT");
		this.set(Col.NUM_PAIRS, p.getPairs());
		this.set(Col.NUM_ALTS, p.getAlts());
		this.record();
	}
	
	static String[] header() {
		String[] header = new String[Col.values().length];
		
		for (int i = 0; i < Col.values().length; i++) {
			header[i] = Col.values()[i].name();
		}
		
		return header;
	}

	public SimulationOutput(String path) throws IOException {
		super(path, header());
	}

}
