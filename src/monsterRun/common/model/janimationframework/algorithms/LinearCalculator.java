package monsterRun.common.model.janimationframework.algorithms;

public class LinearCalculator extends CalculatorBase {
	@Override
	public double getCalculatedTime(double maxTime, double currentTime) {
		return (currentTime / maxTime) * super.maxPercentage;
	}
}
