package ashe;

interface Statistician {
	static StrengthEvaluator evaluator = new StrengthEvaluator(AsheParams.HSDBPath);
}
