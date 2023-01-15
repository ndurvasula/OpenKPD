package custom;

import openkpd.Simulation;
import openkpd.SimulationAltruist;
import openkpd.SimulationPair;

public class UNOSWeights implements openkpd.CustomWeights {

	@Override
	public double computeWeight(SimulationAltruist donor, SimulationPair patient) {
		double UNOSweight = 100;
		UNOSweight += 0.07*patient.getIterations()*Simulation.DAYS_PER_MATCH;
		UNOSweight += (patient.HLA_A_cand.equals(donor.HLA_A_don) &&
				patient.HLA_B_cand.equals(donor.HLA_B_don) &&
				patient.HLA_DR_cand.equals(donor.HLA_DR_don)) ? 10 : 0;
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
		return UNOSweight;
	}
	

}
