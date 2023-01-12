package openkpd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.cmu.cs.dickerson.kpd.dynamic.arrivals.ExponentialArrivalDistribution;
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
import edu.cmu.cs.dickerson.kpd.structure.generator.PoolGenerator;

public class BatchSimulation extends Simulation {
	static int ITERATIONS; //number of iterations to collect data
	static int DAYS;
	
	String metadatastring; 
	
	public BatchSimulation(int iters, String path) {
		ITERATIONS = iters;
		PATH = path+"/"+pathString();
	}
	
	public BatchSimulation(int days) {
		DAYS = days;
		ITERATIONS = (int) Math.round(days/DAYS_PER_MATCH);
		PATH = "./"+pathString();
		metadatastring = "Running a batch simulation for "+DAYS+" days ("+ITERATIONS+" iterations), with a timeout period of "
				+TIMEOUT+" iterations.\n\n";
	}
	
	private String pathString() {
		return "/Batch Simulation "+(new Date()).toString().replace(":","-");
	}

	@Override
	String generateMetadata() {
		return metadatastring;
	}
	
	void addMetadata(String header, int pairs, int alts) throws IOException {
		File metadata = new File(root, "metadata.txt");
		metadatastring += header+": "+pairs+" total pairs, and "+alts+" total altruists.\n";
		FileWriter fw = new FileWriter(metadata);
		fw.write(generateMetadata());
		fw.close();
	}
	

	@Override
	void simulate() throws IOException {
		long startTime = System.currentTimeMillis();
		
		long rFailureSeed = System.currentTimeMillis();
		Random rFailure = new Random(rFailureSeed);
		long rEntranceSeed = System.currentTimeMillis() + 1L;
		Random rEntrance = new Random(rEntranceSeed);
		long rDepartureSeed = System.currentTimeMillis() + 2L;
		Random rDeparture = new Random(rDepartureSeed);
		
		PoolGenerator poolGen = new SimulationPoolGenerator(rEntrance);
		ExponentialArrivalDistribution m = new ExponentialArrivalDistribution(1.0 / EXPECTED_PAIRS);
		ExponentialArrivalDistribution a = new ExponentialArrivalDistribution(1.0 / EXPECTED_ALTRUISTS);
		Pool pool = new Pool(Edge.class);
		ArrayList<Cycle> matches = new ArrayList<Cycle>();
		
		poolGen.addVerticesToPool(pool, INITIAL_PAIRS, INITIAL_ALTS);
		
		for (int t = 0; t < ITERATIONS; t++) {
			if (t != 0 && t % Math.round(365/DAYS_PER_MATCH) == 0) {
				addMetadata("Year "+t/Math.round(365/DAYS_PER_MATCH)+" (iteration "+t+", currentVertexID="+((SimulationPoolGenerator) poolGen).getVertexID()+")", pool.getNumPairs(), pool.getNumAltruists());
				FileOutputStream saveFile = new FileOutputStream(root.getPath()+"/year"+t/Math.round(365/DAYS_PER_MATCH)+".pool");
				FileOutputStream saveFile2 = new FileOutputStream(root.getPath()+"/year"+t/Math.round(365/DAYS_PER_MATCH)+".poolgen");
				try {
					ObjectOutputStream sav = new ObjectOutputStream(saveFile);
					ObjectOutputStream sav2 = new ObjectOutputStream(saveFile2);
					sav.writeObject(pool);
					sav2.writeObject(poolGen);
					sav.close();
					sav2.close();
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
				saveFile.close();
				saveFile2.close();
			}
			int pairs = m.draw().intValue();
			int alts = a.draw().intValue();
			poolGen.addVerticesToPool(pool, pairs, alts);
			progress("Running Batch Simulation:",t,ITERATIONS);
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
					expire((SimulationPair) v, t);
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
							match(pool, e, t);
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
							match(pool, e, t);
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
		
		addMetadata("TERMINATED (currentVertexID="+((SimulationPoolGenerator) poolGen).getVertexID()+")"
				, pool.getNumPairs(), pool.getNumAltruists());
		
		for (VertexPair p : pool.getPairs()) {
			SimulationPair q = (SimulationPair) p;
			if (q.getIterations() > TIMEOUT) {
				timeout(q, ITERATIONS);
			}
		}
		progress("Running Batch Simulation:",ITERATIONS,ITERATIONS);
		}
		
	}
