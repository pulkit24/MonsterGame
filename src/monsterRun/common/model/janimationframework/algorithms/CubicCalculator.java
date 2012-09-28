package monsterRun.common.model.janimationframework.algorithms;

public class CubicCalculator extends CalculatorBase {
	@Override
	public double getCalculatedTime(double maxTime, double currentTime) {
		return super.maxPercentage
				* ((currentTime = currentTime / maxTime - 1) * currentTime
						* currentTime + 1);
	}
}
