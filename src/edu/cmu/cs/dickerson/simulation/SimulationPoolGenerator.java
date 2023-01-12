package edu.cmu.cs.dickerson.simulation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.*;

import edu.cmu.cs.dickerson.kpd.structure.Edge;
import edu.cmu.cs.dickerson.kpd.structure.Pool;
import edu.cmu.cs.dickerson.kpd.structure.Vertex;
import edu.cmu.cs.dickerson.kpd.structure.VertexAltruist;
import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.generator.PoolGenerator;
import edu.cmu.cs.dickerson.kpd.structure.types.BloodType;

public class SimulationPoolGenerator extends PoolGenerator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//From pairs.csv
	protected String PAIRS = "./pairs.csv";
	protected int DONOR_PATIENT_COUNT = 2576;
	
	//From alts.csv
	protected String ALTS = "./alts.csv";
	protected int ALTRUIST_COUNT = 75;

	
	// Current unused vertex ID for optimization graphs
	private int currentVertexID;

	public SimulationPoolGenerator(Random random) {
		super(random);
		this.currentVertexID = 0;
	}
	
	public SimulationPoolGenerator(SimulationPoolGenerator source) {
		super(new Random());
		this.currentVertexID = source.getVertexID();
	}
	
	//If loading a pool through serialization, set.get the unused ID from another generator
	public int getVertexID() {
		return this.currentVertexID;
	}
	
	public void setVertexID(int id) {
		this.currentVertexID = id;
	}

	/**
	 * Random roll to see if a patient and donor are crossmatch compatible
	 * @param pr_PraIncompatibility probability of a PRA-based incompatibility
	 * @return true is simulated positive crossmatch, false otherwise
	 */
	private boolean isPositiveCrossmatch(double pr_PraIncompatibility) {
		return random.nextDouble() <= pr_PraIncompatibility;
	}	


	/**
	 * Randomly rolls a patient-donor pair (possibly compatible or incompatible)
	 * @param ID unique identifier for the vertex
	 * @return a patient-donor pair KPDSimulationPair
	 */
	SimulationPair generatePair(int ID) {

		int rowNum = random.nextInt(DONOR_PATIENT_COUNT - 1)+1;
		
		SimulationPair gen = null;
		
		String line;
		try (Stream<String> lines = Files.lines(Paths.get(PAIRS))) {
			line = lines.skip(rowNum).findFirst().get();
			String[] raw = line.split(",");
			String[] entries = new String[raw.length - 1];
			System.arraycopy(raw, 1, entries, 0, entries.length);
			gen = new SimulationPair(ID, entries);
		}
		catch(IOException e){
			System.out.println(e);
		}
		
		gen.setCompatible(isCompatible(gen,gen));

		return gen;
	}

	/**
	 * Random rolls an altruistic donor (donor with no attached patient)
	 * @param ID unique identifier for the vertex
	 * @return altruistic donor vertex KPDSimulationAltruist
	 */
	private SimulationAltruist generateAltruist(int ID) {

		int rowNum = random.nextInt(ALTRUIST_COUNT - 1)+1;
		
		SimulationAltruist gen = null;
		
		String line;
		try (Stream<String> lines = Files.lines(Paths.get(ALTS))) {
			line = lines.skip(rowNum).findFirst().get();
			String[] raw = line.split(",");
			String[] entries = new String[raw.length - 1];
			System.arraycopy(raw, 1, entries, 0, entries.length);
			gen = new SimulationAltruist(ID, entries);
		}
		catch(IOException e){
			System.out.println(e);
		}

		return gen;
	}


	public boolean isCompatible(SimulationPair donor, SimulationPair patient) { 
		boolean compatible = donor.getBloodTypeDonor().canGiveTo(patient.getBloodTypePatient())    // Donor must be blood type compatible with patient
				&& !isPositiveCrossmatch(patient.getPatientCPRA()/100.0);   // Crossmatch must be negative
		return compatible;
	}
	
	public boolean isCompatible(SimulationAltruist alt, SimulationPair patient) { 
		boolean compatible = alt.getBloodTypeDonor().canGiveTo(patient.getBloodTypePatient())    // Donor must be blood type compatible with patient
				&& !isPositiveCrossmatch(patient.getPatientCPRA()/100.0);   // Crossmatch must be negative
		return compatible;
	}
	
	
	
	@Override
	public Pool generate(int numPairs, int numAltruists) {

		assert(numPairs > 0);
		assert(numAltruists >= 0);

		// Keep track of the three types of vertices we can generate: 
		// altruist-no_donor, patient-compatible_donor, patient-incompatible_donor
		List<SimulationPair> incompatiblePairs = new ArrayList<SimulationPair>();
		List<SimulationPair> compatiblePairs = new ArrayList<SimulationPair>();
		List<SimulationAltruist> altruists = new ArrayList<SimulationAltruist>();

		// Generate enough incompatible and compatible patient-donor pair vertices
		while(incompatiblePairs.size() < numPairs) {

			SimulationPair v = generatePair(currentVertexID++);
			if(v.isCompatible()) {
				compatiblePairs.add(v);  // we don't do anything with these
				currentVertexID--;       // throw away compatible pair; reuse the ID
			} else {
				incompatiblePairs.add(v);
			}
		}

		// Generate altruistic donor vertices
		while(altruists.size() < numAltruists) {
			SimulationAltruist altruist = generateAltruist(currentVertexID++);
			altruists.add(altruist);
		}

		
		

		// Only add the incompatible pairs to the pool
		Pool pool = new Pool(Edge.class);
		for(SimulationPair pair : incompatiblePairs) {
			pool.addPair(pair);	
		}


		// Add altruists to the pool
		for(SimulationAltruist altruist : altruists) {
			pool.addAltruist(altruist);
		}


		// Add edges between compatible donors and other patients
		for(SimulationPair donorPair : incompatiblePairs) {
			for(SimulationPair patientPair : incompatiblePairs) {

				if(donorPair.equals(patientPair)) { continue; }

				if(isCompatible(donorPair, patientPair)) {
					Edge e = pool.addEdge(donorPair, patientPair);
					pool.setEdgeWeight(e, 1.0);
				}
			}
		}




		for(SimulationAltruist alt : altruists) {
			for(SimulationPair patientPair : incompatiblePairs) {

				// Add edges from a donor to a compatible patient elsewhere
				if(isCompatible(alt, patientPair)) {
					Edge e = pool.addEdge(alt, patientPair);
					pool.setEdgeWeight(e, 1.0);
				}
				
				// Add dummy edges from a non-altruist donor to each of the altruists
				Edge dummy = pool.addEdge(patientPair, alt);
				pool.setEdgeWeight(dummy, 0.0);
			}
		}

		return pool;
	}

	
	@Override
	public Set<Vertex> addVerticesToPool(Pool pool, int numPairs, int numAltruists) {
		
		// Generate new vertices
		Pool more = this.generate(numPairs, numAltruists);
		int totalPairs = pool.getNumPairs() + numPairs - 1;
		int totalAlts = pool.getNumAltruists() + numAltruists;
		
		// Add edges from/to the new vertices
		for(VertexPair v : more.getPairs()) {
			((SimulationPair) v).set(totalPairs, totalAlts);
			pool.addPair(v); 
			}
		for(VertexPair vN : more.getPairs()) {
			for(VertexPair vO : pool.getPairs()) {
				if(vN.equals(vO)) { continue; }  // Don't add self-edges
				
				// Donate from new vertex to other vertex
				if(isCompatible((SimulationPair) vN, (SimulationPair) vO) && !pool.containsEdge(vN, vO)) {
					pool.setEdgeWeight(pool.addEdge(vN, vO), 1.0);
				}
				// Donate from other vertex to new vertex
				if(isCompatible((SimulationPair) vO, (SimulationPair) vN)&& !pool.containsEdge(vO, vN)) {
					pool.setEdgeWeight(pool.addEdge(vO, vN), 1.0);
				}
			}
			
			// Adds edges from old altruists to new vertices
			for(VertexAltruist altO : pool.getAltruists()) {
				if(isCompatible((SimulationAltruist) altO, (SimulationPair) vN)) {
					pool.setEdgeWeight(pool.addEdge(altO, vN), 1.0);
				}
				// Add dummy edges from a non-altruist donor to each of the altruists
				pool.setEdgeWeight(pool.addEdge(vN, altO), 0.0);
			}
		}
		
		
		// Add edges from/to the new altruists from all (old+new) vertices
		for(VertexAltruist a : more.getAltruists()) { pool.addAltruist(a); }
		for(VertexAltruist altN : more.getAltruists()) {
			// No edges between altruists
			for(VertexPair v : pool.getPairs()) {
				if(isCompatible((SimulationAltruist) altN, (SimulationPair) v)) {
					pool.setEdgeWeight(pool.addEdge(altN, v), 1.0);
				}
				
				// Add dummy edges from a non-altruist donor to each of the altruists
				pool.setEdgeWeight(pool.addEdge(v, altN), 0.0);
			}
		}
		
		// Return only the new vertices that were generated
		return more.vertexSet();
	}
	
	public Pool addSubjectToPool(SimulationPair subject, Pool pool) {
		
		// Generate new vertices
		subject.set(pool.getPairs().size(), pool.getAltruists().size());
		subject.setID(this.currentVertexID++);
		pool.addPair(subject);
		
		//Add edges from initial pool to new subject
		for(VertexPair vO : pool.getPairs()) {
			if(subject.equals(vO)) { continue; }  // Don't add self-edges
			
			// Donate from new vertex to other vertex
			if(isCompatible((SimulationPair) subject, (SimulationPair) vO) && !pool.containsEdge(subject, vO)) {
				pool.setEdgeWeight(pool.addEdge(subject, vO), 1.0);
			}
			// Donate from other vertex to new vertex
			if(isCompatible((SimulationPair) vO, subject)&& !pool.containsEdge(vO, subject)) {
				pool.setEdgeWeight(pool.addEdge(vO, subject), 1.0);
			}
		}
		
		// Adds edges from old altruists to new subject
		for(VertexAltruist altO : pool.getAltruists()) {
			if(isCompatible((SimulationAltruist) altO, (SimulationPair) subject)) {
				pool.setEdgeWeight(pool.addEdge(altO, subject), 1.0);
			}
			// Add dummy edges from a non-altruist donor to each of the altruists
			pool.setEdgeWeight(pool.addEdge(subject, altO), 0.0);
		}
		
		
		return pool;
	}
	

}
