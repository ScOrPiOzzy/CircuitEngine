package com.cas.circuit.util;

import java.text.NumberFormat;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {

	public static final NumberFormat showFormat, shortFormat, noCommaFormat;
	public static final double pi = 3.14159265358979323846;
	public static final String muString = "u";
	public static final String ohmString = "ohm";

	static {
		showFormat = NumberFormat.getInstance();
		showFormat.setMaximumFractionDigits(2);
		shortFormat = NumberFormat.getInstance();
		shortFormat.setMaximumFractionDigits(1);
		noCommaFormat = NumberFormat.getInstance();
		noCommaFormat.setMaximumFractionDigits(10);
		noCommaFormat.setGroupingUsed(false);
	}

	public static String getVoltageDText(double v) {
		return getUnitText(Math.abs(v), "V");
	}

	public static String getVoltageText(double v) {
		return getUnitText(v, "V");
	}

	public static String getUnitText(double v, String u) {
		double va = Math.abs(v);
		if (va < 1e-14) {
			return "0 " + u;
		}
		if (va < 1e-9) {
			return showFormat.format(v * 1e12) + " p" + u;
		}
		if (va < 1e-6) {
			return showFormat.format(v * 1e9) + " n" + u;
		}
		if (va < 1e-3) {
			return showFormat.format(v * 1e6) + " " + muString + u;
		}
		if (va < 1) {
			return showFormat.format(v * 1e3) + " m" + u;
		}
		if (va < 1e3) {
			return showFormat.format(v) + " " + u;
		}
		if (va < 1e6) {
			return showFormat.format(v * 1e-3) + " k" + u;
		}
		if (va < 1e9) {
			return showFormat.format(v * 1e-6) + " M" + u;
		}
		return showFormat.format(v * 1e-9) + " G" + u;
	}

	public static String getShortUnitText(double v, String u) {
		double va = Math.abs(v);
		if (va < 1e-13) {
			return null;
		}
		if (va < 1e-9) {
			return shortFormat.format(v * 1e12) + "p" + u;
		}
		if (va < 1e-6) {
			return shortFormat.format(v * 1e9) + "n" + u;
		}
		if (va < 1e-3) {
			return shortFormat.format(v * 1e6) + muString + u;
		}
		if (va < 1) {
			return shortFormat.format(v * 1e3) + "m" + u;
		}
		if (va < 1e3) {
			return shortFormat.format(v) + u;
		}
		if (va < 1e6) {
			return shortFormat.format(v * 1e-3) + "k" + u;
		}
		if (va < 1e9) {
			return shortFormat.format(v * 1e-6) + "M" + u;
		}
		return shortFormat.format(v * 1e-9) + "G" + u;
	}

	public static String getCurrentText(double i) {
		return getUnitText(i, "A");
	}

	public static String getCurrentDText(double i) {
		return getUnitText(Math.abs(i), "A");
	}

	// factors a matrix into upper and lower triangular matrices by
	// gaussian elimination. On entry, a[0..n-1][0..n-1] is the
	// matrix to be factored. ipvt[] returns an integer vector of pivot
	// indices, used in the lu_solve() routine.
//	LU分解(线性代数)
//	所谓LU factorization就是把矩阵分解为一个下三角矩阵 L 和一个上三角矩阵 U 的乘积
	public static boolean lu_factor(double a[][], int n, int ipvt[]) {
		double scaleFactors[];
		int i, j, k;

		scaleFactors = new double[n];

		// divide each row by its largest element, keeping track of the scaling factors
		for (i = 0; i != n; i++) {
			double largest = 0;
			for (j = 0; j != n; j++) {
				double x = Math.abs(a[i][j]);
				if (x > largest) {
					largest = x;
				}
			}
			// if all zeros, it's a singular matrix
			if (largest == 0) {
				return false;
			}
			scaleFactors[i] = 1.0 / largest;
		}

		// use Crout's method; loop through the columns
		for (j = 0; j != n; j++) {

			// calculate upper triangular elements for this column
			for (i = 0; i != j; i++) {
				double q = a[i][j];
				for (k = 0; k != i; k++) {
					q -= a[i][k] * a[k][j];
				}
				a[i][j] = q;
			}

			// calculate lower triangular elements for this column
			double largest = 0;
			int largestRow = -1;
			for (i = j; i != n; i++) {
				double q = a[i][j];
				for (k = 0; k != j; k++) {
					q -= a[i][k] * a[k][j];
				}
				a[i][j] = q;
				double x = Math.abs(q);
				if (x >= largest) {
					largest = x;
					largestRow = i;
				}
			}

			// pivoting
			if (j != largestRow) {
				double x;
				for (k = 0; k != n; k++) {
					x = a[largestRow][k];
					a[largestRow][k] = a[j][k];
					a[j][k] = x;
				}
				scaleFactors[largestRow] = scaleFactors[j];
			}

			// keep track of row interchanges
			ipvt[j] = largestRow;

			// avoid zeros
			if (a[j][j] == 0.0) {
				log.info("avoided zero row:{} col:{}", j, j);
				for (int l = 0; l < a.length; l++) {
					System.out.println(Arrays.toString(a[l]));
				}
				a[j][j] = 1e-18;
			}

			if (j != n - 1) {
				double mult = 1.0 / a[j][j];
				for (i = j + 1; i != n; i++) {
					a[i][j] *= mult;
				}
			}
		}
		return true;
	}

	// Solves the set of n linear equations using a LU factorization
	// previously performed by lu_factor. On input, b[0..n-1] is the right
	// hand side of the equations, and on output, contains the solution.
	public static void lu_solve(double a[][], int n, int ipvt[], double b[]) {
		int i;

		// find first nonzero b element
		for (i = 0; i != n; i++) {
			int row = ipvt[i];

			double swap = b[row];
			b[row] = b[i];
			b[i] = swap;
			if (swap != 0) {
				break;
			}
		}

		int bi = i++;
		for (; i < n; i++) {
			int row = ipvt[i];
			int j;
			double tot = b[row];

			b[row] = b[i];
			// forward substitution using the lower triangular matrix
			for (j = bi; j < i; j++) {
				tot -= a[i][j] * b[j];
			}
			b[i] = tot;
		}
		for (i = n - 1; i >= 0; i--) {
			double tot = b[i];
			// back-substitution using the upper triangular matrix
			for (int j = i + 1; j != n; j++) {
				tot -= a[i][j] * b[j];
			}
			b[i] = tot / a[i][i];
		}
	}

	public static int distanceSq(int x1, int y1, int x2, int y2) {
		x2 -= x1;
		y2 -= y1;
		return x2 * x2 + y2 * y2;
	}

	public static int sign(int x) {
		return (x < 0) ? -1 : (x == 0) ? 0 : 1;
	}

}
