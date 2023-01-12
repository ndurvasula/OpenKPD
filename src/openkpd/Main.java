package openkpd;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException, NumberFormatException, ClassNotFoundException {
		Simulation sim = null;
		
		switch(args[0]) {
		case "batch":
			if (args.length == 3) {
				sim = new BatchSimulation(Integer.parseInt(args[1]), args[2]);
			} else {
				sim = new BatchSimulation(Integer.parseInt(args[1]));
			}
			break;
		case "sample":
			if (args.length == 4) {
				sim = new SampleSimulation(Integer.parseInt(args[1]),  args[2], Integer.parseInt(args[3]));
			} else {
				sim = new SampleSimulation(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
						Integer.parseInt(args[3]), Integer.parseInt(args[4]));
			}
			break;
		default:
			break;
		}
		
		sim.run();

	}

}
