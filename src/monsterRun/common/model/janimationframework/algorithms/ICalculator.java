package monsterRun.common.model.janimationframework.algorithms;

/**
 * 
 * The algorithm interface that must be implemented to add a new interpolation
 * algorithm
 */
public interface ICalculator {
	public double getCalculatedTime(double maxTime, double currentTime);
}
