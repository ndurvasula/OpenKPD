package openkpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.cmu.cs.dickerson.kpd.dynamic.arrivals.ExponentialArrivalDistribution;
import edu.cmu.cs.dickerson.kpd.dynamic.arrivals.PoissonArrivalDistribution;
import edu.cmu.cs.dickerson.kpd.solver.GreedyPackingSolver;
import edu.cmu.cs.dickerson.kpd.solver.approx.CyclesSampleChainsIPPacker;
import edu.cmu.cs.dickerson.kpd.solver.exception.SolverException;
import edu.cmu.cs.dickerson.kpd.solver.solution.Solution;
import edu.cmu.cs.dickerson.kpd.structure.Cycle;
import edu.cmu.cs.dickerson.kpd.structure.Edge;
import edu.cmu.cs.dickerson.kpd.structure.Pool;
import edu.cmu.cs.dickerson.kpd.structure.VertexAltruist;
import edu.cmu.cs.dickerson.kpd.structure.VertexPair;
import edu.cmu.cs.dickerson.kpd.structure.alg.CycleGenerator;
import edu.cmu.cs.dickerson.kpd.structure.alg.FailureProbabilityUtil;

public class SampleSimulation extends Simulation {
	SimulationPair SUBJECT;
	int TRAJECTORIES;
	
	long startTime = System.currentTimeMillis();
	
	long rFailureSeed = System.currentTimeMillis();
	Random rFailure = new Random(rFailureSeed);
	long rEntranceSeed = System.currentTimeMillis() + 1L;
	Random rEntrance = new Random(rEntranceSeed);
	long rDepartureSeed = System.currentTimeMillis() + 2L;
	Random rDeparture = new Random(rDepartureSeed);
	
	SimulationPoolGenerator poolGen = new SimulationPoolGenerator(rEntrance);
	
	Pool SOURCE = null;
	SimulationPoolGenerator SOURCEGEN = null;
	
	int SAMPLE_ID;
	String stub;
	
	public SampleSimulation(int trajs, String pool, int sampleID) throws IOException, ClassNotFoundException {
		this(null, trajs, pool, sampleID);
	}
	
	public SampleSimulation(String config, int trajs, String pool, int sampleID) throws IOException, ClassNotFoundException {
		this.config(config);
		SUBJECT = (SimulationPair) poolGen.generate(1,0).getPairs().first();
		TRAJECTORIES = trajs;
		SAMPLE_ID = sampleID;
		stub = pool;
        
		FileInputStream fileIn = new FileInputStream(pool+".poolgen");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        SOURCEGEN = (SimulationPoolGenerator) in.readObject();
        in.close();
        fileIn.close();
	}
	
	@Override
	public void run() {
		try {
			root = new File(PATH);
			Files.createDirectories(root.toPath());
			out = new SimulationOutput(PATH + "/sample"+SAMPLE_ID+".csv");
			
			simulate();
			
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void match(Pool pool, Edge e, int iter) throws IOException {
		SimulationPair patient = (SimulationPair) pool.getEdgeTarget(e);
		patient.setDisplay(SAMPLE_ID);
		
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
	
	@Override
	public void timeout(SimulationPair p, int iter) throws IOException {
		p.setDisplay(SAMPLE_ID);
		out.timeout(p, iter);
	}
	
	@Override
	public void expire(SimulationPair p, int iter) throws IOException {
		p.setDisplay(SAMPLE_ID);
		if (p.getIterations() > TIMEOUT) {
			timeout(p, iter);
			return;
		}
		out.expire(p, iter);
	}

	@Override
	String generateMetadata() {
		return "Running a sample simulation with a timeout period of "
				+TIMEOUT+" iterations.";
	}

	@Override
	void simulate() throws IOException {

		PoissonArrivalDistribution m = new PoissonArrivalDistribution(EXPECTED_PAIRS);
		PoissonArrivalDistribution a = new PoissonArrivalDistribution(EXPECTED_ALTRUISTS);
		
		for (int i = 0; i < TRAJECTORIES; i++) {
			
			try {
				SUBJECT.reset();
				FileInputStream fileIn = new FileInputStream(stub+".pool");
		        ObjectInputStream in = new ObjectInputStream(fileIn);
		        SOURCE = (Pool) in.readObject();
		        in.close();
		        fileIn.close();
		        
				poolGen = new SimulationPoolGenerator(SOURCEGEN);
				Pool pool = poolGen.addSubjectToPool(SUBJECT, SOURCE);
				ArrayList<Cycle> matches = new ArrayList<Cycle>();
				
				progress("Running Sample Simulation (ID: "+SAMPLE_ID+"):",i,TRAJECTORIES);
			
				for (int t = 0; t < TIMEOUT; t++) {
					
					int pairs = m.draw().intValue();
					int alts = a.draw().intValue();
					poolGen.addVerticesToPool(pool, pairs, alts);
					updateWeights(pool);
					
					FailureProbabilityUtil.setFailureProbability(pool, 
							FailureProbabilityUtil.ProbabilityDistribution.CONSTANT, rFailure);
					
					// Remove all pairs where the patient dies
					ArrayList<VertexPair> rm = new ArrayList<VertexPair>();
					for (VertexPair v : pool.getPairs()) {
						if (rDeparture.nextDouble() <= DEATH) {
							Iterator<Cycle> matchIterator = matches.iterator();
							while (matchIterator.hasNext()) {
								Cycle c = matchIterator.next();
								if (Cycle.getConstituentVertices(c, pool).contains(v)) {
									matchIterator.remove();
								}
							}
							rm.add(v);
							if (v == SUBJECT) {
								expire(SUBJECT, t);
								throw new SimulationException("Patient Expired");
							}
						}
					}
					pool.removeAllVertices(rm);
					
					// Remove all altruists that run out of patience
					Iterator<VertexAltruist> aiter = pool.getAltruists().iterator();
					ArrayList<VertexAltruist> toRemove = new ArrayList<VertexAltruist>();
					while (aiter.hasNext()) {
						VertexAltruist alt = aiter.next();
						if (rDeparture.nextDouble() <= PATIENCE) {
							toRemove.add(alt);
						}
					}
					pool.removeAllVertices(toRemove);
					
					// Remove edges in matchings
					Iterator<Cycle> iter = matches.iterator();
					while (iter.hasNext()) {
						Cycle ci = iter.next();
						boolean fail = false;
						for (Edge e : ci.getEdges()) {
							if (rFailure.nextDouble() <= e.getFailureProbability()) {
								iter.remove();
								fail = true;
								break;
							}
						}
						if (fail) {
							continue;
						}
						// All edges in the Cycle remain, so we have a match!
						else {
							// We matched a chain, now we have to make the last
							// donor a bridge donor with some probability
							if (Cycle.isAChain(ci, pool)) {
								ArrayList<VertexPair> trm = new ArrayList<VertexPair>();
								ArrayList<Edge> le = new ArrayList<Edge>(); 		
								for(Edge e : ci.getEdges()){
									le.add(e);
								}
								Collections.reverse(le);
								le.remove(le.size()-1);
								for(Edge e : le){
									if (pool.getEdgeTarget(e) == SUBJECT) {
										match(pool, e, t);
										throw new SimulationException("Matched in a Chain");
									}
									trm.add((VertexPair)pool.getEdgeTarget(e));	
									// The bridge donor reneged, we stop the chain here
									if (rDeparture.nextDouble() <= RENEGE) {
										break;
									} 
								}
								pool.removeAllVertices(trm);
		
							}
							else{
								for (Edge e : ci.getEdges()) {
									if (pool.getEdgeTarget(e) == SUBJECT) {
										match(pool, e, t);
										throw new SimulationException("Matched in a Cycle");
									}
								}
								// Remove all vertices in the match from the pool
								pool.removeAllVertices(Cycle.getConstituentVertices(ci, pool));
							}
							// Remove this match from our current set of matchings
							iter.remove();
						}
					}
						
						try {
							// Solution optSolIP = optIPS.solve();
							GreedyPackingSolver s = new GreedyPackingSolver(pool);
							List<Cycle> reducedCycles = (new CycleGenerator(pool)).generateCyclesAndChains(CYCLE_CAP, 0, true);
							Solution sol = s.solve(1, new CyclesSampleChainsIPPacker(pool, reducedCycles, 100, CHAIN_CAP, true),
									Double.MAX_VALUE);
							for (Cycle c : sol.getMatching()) {
								matches.add(c);
							}
		
						} catch (SolverException e) {
							e.printStackTrace();
							System.exit(-1);
						}
						
						for (VertexPair p : pool.getPairs()) {
							((SimulationPair) p).increment();
						}
		
					}
				long endTime = System.currentTimeMillis();
				
				timeout(SUBJECT, TIMEOUT);
				throw new SimulationException("TIMEOUT");
			}
			
			catch (SimulationException e) {
				
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		progress("Running Sample Simulation (ID: "+SAMPLE_ID+"):",TRAJECTORIES,TRAJECTORIES);
	}
		
}

class SimulationException extends Exception {

	public SimulationException(String errorMessage) {
		super(errorMessage);
	}
	
}
