package openkpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Properties;

import edu.cmu.cs.dickerson.kpd.structure.Edge;
import edu.cmu.cs.dickerson.kpd.structure.Pool;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

/**
 * 
 * @author Naveen Durvasula
 *
 * This class provides the scaffolding for simulations of the kidney exchange. It contains the precomputed/default
 * values for all matching-related constants.  
 */
public abstract class Simulation {
	
	
	
	//File path for storing generated data
	String PATH = ".";
	static int TIMEOUT = 600;
	
	// Matching-related constants
	// Probabilities generated based on a match frequency of 1 match per week
	static double DAYS_PER_MATCH = 5.803886925795053;
	static double DEATH = 0.00220192718495970;
	static double PATIENCE = 0;
	static double RENEGE = .5;
	static double EXPECTED_PAIRS = 4.7715827338129495;
	static double EXPECTED_ALTRUISTS = 0.1420863309352518;
	static int CHAIN_CAP = 4;
	static int CYCLE_CAP = 3;
	
	
	static final boolean DEBUG = false;
	
	SimulationOutput out;
	File root;
	
	abstract String generateMetadata();
	
	abstract void simulate() throws IOException;
	
	public Properties config(String config) throws IOException {
		if(config == null){
			return null;
		}
		Properties props = new Properties();
		props.load(new FileInputStream(config));
		DAYS_PER_MATCH = Double.parseDouble(props.getProperty("DAYS_PER_MATCH", String.valueOf(DAYS_PER_MATCH)));
		DEATH = Double.parseDouble(props.getProperty("EXPIRY", String.valueOf(DEATH)));
		PATIENCE = Double.parseDouble(props.getProperty("PATIENCE", String.valueOf(PATIENCE)));
		RENEGE = Double.parseDouble(props.getProperty("RENEGE", String.valueOf(RENEGE)));
		EXPECTED_PAIRS = Double.parseDouble(props.getProperty("EXPECTED_PAIRS", String.valueOf(EXPECTED_PAIRS)));
		EXPECTED_ALTRUISTS = Double.parseDouble(props.getProperty("EXPECTED_ALTRUISTS", String.valueOf(EXPECTED_ALTRUISTS)));
		CHAIN_CAP = Integer.parseInt(props.getProperty("CHAIN_CAP", String.valueOf(CHAIN_CAP)));
		CYCLE_CAP = Integer.parseInt(props.getProperty("CYCLE_CAP", String.valueOf(CHAIN_CAP)));
		TIMEOUT = Integer.parseInt(props.getProperty("TIMEOUT", String.valueOf(TIMEOUT)));
		PATH = props.getProperty("PATH", String.valueOf(PATH));
		
		return props;
	}
	
	public void run() {
		try {
			root = new File(PATH);
			Files.createDirectories(root.toPath());
			out = new SimulationOutput(PATH + "/data.csv");
			
			simulate();
			
			File metadata = new File(root, "metadata.txt");
			FileWriter fw = new FileWriter(metadata);
			fw.write(generateMetadata());
			fw.close();
			
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void match(Pool pool, Edge e, int iter) throws IOException {
		SimulationPair patient = (SimulationPair) pool.getEdgeTarget(e);
		
		if (patient.getIterations() > TIMEOUT) {
			timeout(patient, iter);
			return;
		}
		
		if(pool.getEdgeSource(e) instanceof SimulationPair) {
			SimulationPair matched = (SimulationPair) pool.getEdgeSource(e);
			out.match(patient, matched, iter);
		} else {
			SimulationAltruist matched = (SimulationAltruist) pool.getEdgeSource(e);
			out.match(patient, matched, iter);
		}	
	}
	
	public void timeout(SimulationPair p, int iter) throws IOException {
		out.timeout(p, iter);
	}
	
	public void expire(SimulationPair p, int iter) throws IOException {
		if (p.getIterations() > TIMEOUT) {
			timeout(p, iter);
			return;
		}
		out.expire(p, iter);
	}
	
	public static void progress(String header, int iters, int total) {
		if (DEBUG) {
			final int EQS = 20;
			int reps = (int) Math.round(((double) iters)/total*EQS);
			System.out.print(header + " ["+String.join("", Collections.nCopies(reps, "="))
			+String.join("", Collections.nCopies(EQS-reps, " "))+"] "+iters +"/"+total+"\r");
		}
	}
	
	//Assumes blood type compatibility
	public static double LKDPI(SimulationPair p, SimulationAltruist m){
		double ret = -11.30;
		if(m.age_don > 50)
			ret += 1.85*(m.age_don-50);
		ret -= 0.381*m.egfr;
		double bmi = m.bmi_don;
		if(m.isAfricanAmerican)
			bmi += 22.34;
		if(m.isCigaretteUser)
			bmi += 14.33;
		ret += 1.17*bmi;
		double sbp = m.bp_systolic;
		if(m.isDonorMale && p.isPatientMale)
			sbp -= 21.68;
		sbp -= 10.61;
		ret += .44*sbp;
		ret += 8.57*((p.HLA_B_cand[0] != m.HLA_B_don[0] ? 1 : 0) + (p.HLA_B_cand[1] != m.HLA_B_don[1] ? 1 : 0));
		ret += 8.26*((p.HLA_DR_cand[0] != m.HLA_DR_don[0] ? 1 : 0) + (p.HLA_DR_cand[1] != m.HLA_DR_don[1] ? 1 : 0));
		ret -= 50.87*Math.min(m.weight_don/p.weight_cand,0.9);
		return ret;
	}
	
	public static double LKDPI(SimulationPair p, SimulationPair m){
		double ret = -11.30;
		if(m.age_don > 50)
			ret += 1.85*(m.age_don-50);
		ret -= 0.381*m.egfr;
		double bmi = m.bmi_don;
		if(m.isAfricanAmerican)
			bmi += 22.34;
		if(m.isCigaretteUser)
			bmi += 14.33;
		ret += 1.17*bmi;
		double sbp = m.bp_systolic;
		if(m.isDonorMale && p.isPatientMale)
			sbp -= 21.68;
		sbp -= 10.61;
		ret += .44*sbp;
		ret += 8.57*((p.HLA_B_cand[0] != m.HLA_B_don[0] ? 1 : 0) + (p.HLA_B_cand[1] != m.HLA_B_don[1] ? 1 : 0));
		ret += 8.26*((p.HLA_DR_cand[0] != m.HLA_DR_don[0] ? 1 : 0) + (p.HLA_DR_cand[1] != m.HLA_DR_don[1] ? 1 : 0));
		ret -= 50.87*Math.min(m.weight_don/p.weight_cand,0.9);
		return ret;
	}
	
	//Update edge weights given a match frequency of one day
	public static void updateWeights(Pool pool) {
		for (Edge e : pool.getNonDummyEdgeSet()) {
			SimulationPair patient = (SimulationPair) pool.getEdgeTarget(e);
			double UNOSweight = 100;
			UNOSweight += 0.07*patient.getIterations()*DAYS_PER_MATCH;
			if(pool.getEdgeSource(e) instanceof SimulationPair) {
				SimulationPair donor = (SimulationPair) pool.getEdgeSource(e);
				UNOSweight += (patient.HLA_A_cand.equals(donor.HLA_A_don) &&
						patient.HLA_B_cand.equals(donor.HLA_B_don) &&
						patient.HLA_DR_cand.equals(donor.HLA_DR_don)) ? 10 : 0;
			} else {
				SimulationAltruist donor = (SimulationAltruist) pool.getEdgeSource(e);
				UNOSweight += (patient.HLA_A_cand.equals(donor.HLA_A_don) &&
						patient.HLA_B_cand.equals(donor.HLA_B_don) &&
						patient.HLA_DR_cand.equals(donor.HLA_DR_don)) ? 10 : 0;
			}
			UNOSweight += patient.age_cand < 18 ? 100 : 0;
			switch (patient.getBloodTypePatient()) {
			case O:
				UNOSweight += 100;
				break;
			case B:
				UNOSweight += 50;
				break;
			case A:
				UNOSweight += 25;
				break;
			default:
				break;
			}
			switch (patient.getBloodTypeDonor()) {
			case AB:
				UNOSweight += 500;
				break;
			case A:
				UNOSweight += 250;
				break;
			case B:
				UNOSweight += 100;
				break;
			default:
				break;
			}
			if (patient.getPatientCPRA() == 100) {
				UNOSweight += 2000;
			} else if (patient.getPatientCPRA() == 99) {
				UNOSweight += 1500;
			} else if (patient.getPatientCPRA() == 98) {
				UNOSweight += 1250;
			} else if (patient.getPatientCPRA() == 97) {
				UNOSweight += 900;
			} else if (patient.getPatientCPRA() == 96) {
				UNOSweight += 700;
			} else if (patient.getPatientCPRA() == 95) {
				UNOSweight += 500;
			} else if (patient.getPatientCPRA() >= 90) {
				UNOSweight += 300;
			} else if (patient.getPatientCPRA() >= 85) {
				UNOSweight += 200;
			} else if (patient.getPatientCPRA() >= 80) {
				UNOSweight += 125;
			} else if (patient.getPatientCPRA() >= 75) {
				UNOSweight += 75;
			} else if (patient.getPatientCPRA() >= 70) {
				UNOSweight += 50;
			} else if (patient.getPatientCPRA() >= 60) {
				UNOSweight += 25;
			} else if (patient.getPatientCPRA() >= 50) {
				UNOSweight += 20;
			} else if (patient.getPatientCPRA() >= 40) {
				UNOSweight += 15;
			} else if (patient.getPatientCPRA() >= 30) {
				UNOSweight += 10;
			} else if (patient.getPatientCPRA() >= 20) {
				UNOSweight += 5;
			}
			
			pool.setEdgeWeight(e, UNOSweight);
		}
	}
	

}
