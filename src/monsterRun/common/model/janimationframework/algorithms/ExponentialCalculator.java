package monsterRun.common.model.janimationframework.algorithms;

public class ExponentialCalculator extends CalculatorBase {
	@Override
	public double getCalculatedTime(double maxTime, double currentTime) {
		return (currentTime == maxTime) ? super.maxPercentage
				: super.maxPercentage * 1.001
						* (-Math.pow(2, -10 * currentTime / maxTime) + 1);
	}
}