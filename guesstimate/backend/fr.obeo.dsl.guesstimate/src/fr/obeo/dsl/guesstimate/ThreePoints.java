package fr.obeo.dsl.guesstimate;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Splitter;

public class ThreePoints {

	private Long avg;
	private Long min;
	private Long max;

	/**
	 * @return the avg
	 */
	public Long getAvg() {
		return avg;
	}

	/**
	 * @param avg the avg to set
	 */
	public void setAvg(Long avg) {
		this.avg = avg;
	}

	/**
	 * @return the min
	 */
	public Long getMin() {
		if (min == null) {
			return getAvg();
		}
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(Long min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public Long getMax() {
		if (max == null) {
			return this.avg;
		}
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(Long max) {
		this.max = max;
	}

	@Override
	public int hashCode() {
		return Objects.hash(avg, max, min);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThreePoints other = (ThreePoints) obj;
		return Objects.equals(avg, other.avg) && Objects.equals(max, other.max) && Objects.equals(min, other.min);
	}

	@Override
	public String toString() {
		return (avg != null ? avg + "~" : "") + (max != null ? max + "~" : "") + (min != null ? min : "");
	}

	public static ThreePoints createThreePointsFromString(String initialValue) {
		ThreePoints n = new ThreePoints();
		if (initialValue != null) {
			int i = 0;
			for (String val : Splitter.on('~').split(initialValue)) {
				try {
					Long iVal = Long.valueOf(val);
					switch (i) {
					case 0:
						n.setAvg(iVal);
						n.setMin(iVal);
						n.setMax(iVal);
						break;
					case 1:
						n.setMax(iVal);
						break;
					case 2:
						n.setMin(iVal);
						break;
					}
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(e);
				}
				i++;
			}
		} else {
			throw new IllegalArgumentException();
		}
		return n;
	}

	public static ThreePoints sum(List<ThreePoints> points) {
		ThreePoints result = createThreePointsFromString("0");
		int size = points.size();
		if (size > 1) {
			int factor = 2;
			double sum = 0;
			double worstSum = 0;
			double bestSum = 0;
			double ESum = 0;
			double sdSum = 0;
			for (ThreePoints p : points) {
				long m = p.getAvg();
				long b = p.getMax();
				long a = p.getMin();
				sum = sum + m;
				worstSum = worstSum + b;
				bestSum = bestSum + a;
				ESum = ESum + ((a + (4 * m) + b) / 6);
				double sdTask = (b - a) / 6;
				sdSum = sdSum + (sdTask * sdTask);
			}
			double sd = Math.sqrt(sdSum);
			long min = Math.round(ESum - (factor * sd));
			long max = Math.round(ESum + (factor * sd));
			long middle = Math.round(min + (max - min) / 2);

			result.setAvg(middle);
			result.setMin(min);
			result.setMax(max);
			return result;
		} else if (size == 1) {
			return points.get(0);
		}
		return result;
	}

}
