---
layout: default
title: Policy Customization
nav_order: 5
---

# Policy Customization
{: .no_toc }

## Table of contents
{: .no_toc .text-delta }

1. TOC
{:toc}

---

## What is a matching policy?

OpenKPD allows users to write their own matching policies that are then dynamically compiled into simulation. Custom matching policies are written in Java. A matching policy takes as input the features of two adjacent vertices in a compatibility graph, and returns a numerical weight. That weight is then used by the integer program to match patients. The higher the weight for a given edge, the more the optimizer would benefit by including that weight in the cycle/chain packing that utlimately determines the final match for that round. See Equation 3.1 in [John's thesis](http://jpdickerson.com/pubs/dickerson16unified.pdf) for more details. 

## Code example

An example of a custom policy is given in the folder `/custom/UNOSWEIGHTS.java` in the release. Below is a copy of that policy, which implements the [point system used by UNOS in the OPTN exchange](https://optn.transplant.hrsa.gov/media/3239/20191011_kidney_kpd_priority_points.pdf)

```
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
  ```


## Defining your own policy

To define your own policy, create a new `.java` file containing a class definition. Recall that the name of the class must match the file name, and the class must be placed in a package that shares a name with the file's parent directory. In the code example above, we place the file in the folder `/custom` and name the file `UNOSWeights.java`. 

In order to implement a valid OpenKPD policy, the class definition must begin with the lines
```
package <package_name>

import openkpd.Simulation;
import openkpd.SimulationAltruist;
import openkpd.SimulationPair;

public class <class_name> implements openkpd.CustomWeights {

	@Override
	public double computeWeight(SimulationAltruist donor, SimulationPair patient) {
```

The method `computeWeight` is the pricipal one to be filled in, and retuns a `double`-valued edge weight between any given set of adjacaent vertices. As the donor of the "donating" vertex and the patient of the "receiving" vertex are the only ones necessary to compute most notions of compatibility, OpenKPD automatically casts the donating vertex to be an altruistic donor, if it is not one already. This way, users do not have to worry about whether or not the donating vertex is an altruist or a patient-donor pair. 

With the `import` statements for [`openkpd.Simulation`](https://github.com/ndurvasula/OpenKPD/blob/master/source/src/openkpd/Simulation.java), [`openkpd.SimulationAltruist`](https://github.com/ndurvasula/OpenKPD/blob/master/source/src/openkpd/SimulationAltruist.java), and [`openkpd.SimulationPair`](https://github.com/ndurvasula/OpenKPD/blob/master/source/src/openkpd/SimulationPair.java), users have access to all public variables and methods within those files. We outline these below:

### Helpful methods and variables in `openkpd.Simulation`

| Public Variable/Method | Description |
| :---: | :--- |
| `Simulation.DAYS_PER_MATCH` | Corresponds to the value `DAYS_PER_MATCH` set in the [configuration file](https://openkpd.org/docs/simulators#configuration). |
| `Simulation.DEATH` | Corresponds the value `EXPIRY` set in the [configuration file](https://openkpd.org/docs/simulators#configuration). |
| `Simulation.PATIENCE` | Corresponds to the value `PATIENCE` in the [configuration file](https://openkpd.org/docs/simulators#configuration) |
| `Simulation.RENEGE` | Corresponds to the value `RENEGE` in the [configuration file](https://openkpd.org/docs/simulators#configuration) |
| `Simulation.EXPECTED_PAIRS` | Corresponds to the value `EXPECTED_PAIRS` in the [configuration file](https://openkpd.org/docs/simulators#configuration) |
| `Simulation.EXPECTED_ALTRUISTS` | Corresponds to the value `EXPECTED_ALTRUISTS` in the [configuration file](https://openkpd.org/docs/simulators#configuration) |
| `Simulation.CHAIN_CAP` | Corresponds to the value `CHAIN_CAP` in the [configuration file](https://openkpd.org/docs/simulators#configuration) |
| `Simulation.CYCLE_CAP` | Corresponds to the value `CYCLE_CAP` in the [configuration file](https://openkpd.org/docs/simulators#configuration) |
| `Simulation.LKDPI(SimulationPair p, SimulationAltruist m)` | Computes the quality of a transplant from `SimulationALtruist` `m` to the patient of a  `SimulationPair` `p` using the [Living Kidney Donor Profile Index](https://pubmed.ncbi.nlm.nih.gov/26752290/) |


### Helpful methods and variables in `openkpd.SimulationAltruist`

We consider a `SimulationAltruist` `donor` (as in the method description for `computeWeight`).

| Public Variable/Method | Description |
| :---: | :--- |
| `donor.age_don` | Corresponds to `AGE_MATCH` in [Numerical Features](https://openkpd.org/docs/outputs#numerical-features) |
| `donor.egfr` | Corresponds to `EGFR_MATCH` in [Numerical Features](https://openkpd.org/docs/outputs#numerical-features) |
| `donor.bmi_don` | Corresponds to `BMI_MATCH` in [Numerical Features](https://openkpd.org/docs/outputs#numerical-features) |
| `donor.bp_sytolic` | Corresponds to `BP_SYSTOLIC_MATCH` in [Numerical Features](https://openkpd.org/docs/outputs#numerical-features) |
| `donor.weight_don` | Corresponds to `WEIGHT_MATCH` in [Numerical Features](https://openkpd.org/docs/outputs#numerical-features) |
| `donor.isAfricanAmerican` | Corresponds to `AFRICAN_AMERICAN_MATCH` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `donor.isCigaretteUser` | Corresponds to `HCU_MATCH` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `donor.isAfricanAmerican` | Corresponds to `AFRICAN_AMERICAN_MATCH` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `donor.isDonorMale` | Corresponds to `SEX_MATCH` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `donor.HLA_A_don` | Corresponds to `MA1` and `MA2` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `donor.HLA_B_don` | Corresponds to `MB1` and `MB2` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `donor.HLA_DR_don` | Corresponds to `MDR1` and `MDR2` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `donor.bloodTypeDonor` | Corresponds to `ABO_MATCH` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features). This is an `enum` -- see [BloodType.java](https://github.com/ndurvasula/OpenKPD/blob/master/source/src/edu/cmu/cs/dickerson/kpd/structure/types/BloodType.java) for its definition |


### Helpful methods and variables in `openkpd.SimulationPair`

We consider a `SimulationPair` `patient` (as in the method description for `computeWeight`).

| Public Variable/Method | Description |
| :---: | :--- |
| `patient.age_cand` | Corresponds to `AGE_CAND` in [Numerical Features](https://openkpd.org/docs/outputs#numerical-features) |
| `patient.weight_cand` | Corresponds to `WEIGHT_CAND` in [Numerical Features](https://openkpd.org/docs/outputs#numerical-features) |
| `patient.patientCPRA` | Corresponds to `CPRA_AT_MATCH_RUN` in [Numerical Features](https://openkpd.org/docs/outputs#numerical-features) |
| `patient.getIterations()` | Returns the number of match iterations the patient has been in the pool |
| `patient.getPairs()` | Returns the number of patient-donor pairs in the pool when this patient entered the exchange |
| `patient.getAlts()` | Returns the number of altruists in the pool when this patient entered the exchange |
| `patient.HLA_A_cand` | Corresponds to `CA1` and `CA2` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `patient.HLA_B_cand` | Corresponds to `CB1` and `CB2` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `patient.HLA_DR_cand` | Corresponds to `CDR1` and `CDR2` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features) |
| `patient.bloodTypePatient` | Corresponds to `ABO_CAND` in [Categorical and Boolean Features](https://openkpd.org/docs/outputs#categorical-and-boolean-features). This is an `enum` -- see [BloodType.java](https://github.com/ndurvasula/OpenKPD/blob/master/source/src/edu/cmu/cs/dickerson/kpd/structure/types/BloodType.java) for its definition |


## Linking OpenKPD to a custom policy

Once a custom policy has been written, it can be linked to OpenKPD by means of the [configuration file](https://openkpd.org/docs/simulators#configuration). This is done through the variables `CUSTOM_WEIGHTS_PATH` and `CUSTOM_WEIGHTS_CLASSNAME`. Given a `<package_name>` and `<class_name>`, we should set `CUSTOM_WEIGHTS_CLASSNAME` to be `<package_name>.<class_name>` and `CUSTOM_WEIGHTS_PATH` to be the path `/.../<package_name>/<class_name>.java` to the custom weight file. 