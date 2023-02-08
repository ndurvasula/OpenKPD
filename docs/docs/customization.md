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



### Helpful methods and variables in `openkpd.SimulationAltruist`

### Helpful methods and variables in `openkpd.SimulationPair`



## Linking OpenKPD to a custom policy