package ashe;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public abstract class NumericGenome {
	
	protected NumericGenome(double[] genes) {
		this.genes = genes;
	}
	
	public double[] getGenes() {
		return genes;
	}

	public abstract void mutate(double mutationRate, double mutationStrength);

	public abstract NumericGenome crossOver(NumericGenome spouseGenome);

	public void writeToFile(String pathToFile) throws FileNotFoundException {
		PrintWriter genomeLog = new PrintWriter(pathToFile);
		for (int i = 0; i < genes.length; i++)
			genomeLog.println(genes[i]);
		genomeLog.close();
	}

	protected double[] genes;
}
