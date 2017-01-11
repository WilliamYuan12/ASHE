package ashe;

import agent.ActionBase;

abstract class EstimatorBase {
	
	EstimatorBase(Ashe ashe) {
		this.ashe = ashe;
	}
	
	abstract double estimate(Intel intel, double handStrength, ActionBase action) throws Exception;
	
	Ashe ashe;
}
