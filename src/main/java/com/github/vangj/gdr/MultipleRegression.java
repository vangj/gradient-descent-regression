package com.github.vangj.gdr;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.Random;

import static com.github.vangj.gdr.util.FileUtil.read;

/**
 * Multiple regression using gradient-descent approach.
 */
public class MultipleRegression {
  /**
   * Object to store intercept and weights.
   */
  public static class Result {
    /**
     * The intercept.
     */
    public double b;

    /**
     * The weights.
     */
    public double[] w;

    /**
     * The error associated with the intercept and weights.
     */
    public double e;

    public Result(double b, double[] w, double e) {
      this.b = b;
      this.w = w;
      this.e = e;
    }

    @Override
    public String toString() {
      return (new StringBuilder())
        .append(b)
        .append(',')
        .append(getString(w))
        .append(',')
        .append(e)
        .toString();
    }
  }

  /**
   * Random number generator.
   */
  private static final Random random = new Random(37L);

  @Option(name = "-r", usage = "learning rate", required = true)
  private double rate = 0.001d;

  @Option(name = "-i", usage = "number of iterations", required = true)
  private int iterations = 2000;

  @Option(name = "-d", usage = "debug progress?", required = false)
  private boolean debug = false;

  @Option(name = "-f", usage = "path to input file", required = true)
  private String filePath;

  public static void main(String[] args) throws Exception {
    MultipleRegression mr = new MultipleRegression();
    CmdLineParser parser = new CmdLineParser(mr);

    try {
      parser.parseArgument(args);
      mr.learn();
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      System.err.println("java -cp path/to/gradient-descent-regression-assembly-0.0.1-SNAPSHOT.jar com.github.vangj.gdr.MultipleRegression [options...] arguments...");
      parser.printUsage(System.err);
      System.err.println();
    }
  }

  /**
   * Starts the learning.
   * @throws IOException Thrown if input file cannot be loaded.
   */
  public void learn() throws IOException {
    Result r = learn(rate, iterations, read(filePath), debug);

    System.out.println(r.b + ", " + getString(r.w) + ", " + r.e);
  }

  /**
   * Learns the linear regression model.
   * @param rate Learning rates.
   * @param iterations Maximum number of iterations.
   * @param data Data.
   * @return Result.
   */
  public static Result learn(final double rate, final int iterations, final double[][] data, final boolean debug) {
    final int numberOfWeights = data[0].length - 1;

    double b = random.nextDouble();
    double[] w = initialize(numberOfWeights);
    double e = 0.0d;

//    System.out.println(b + ", " + getString(w) + ", " + e);

    for(int i=0; i < iterations; i++) {
      Result r = step(b, w, data, rate);

      if(isInvalid(r.b) || isInvalid(r.w)) {
        break;
      }

      b = r.b;
      w = r.w;
      e = r.e;

      if(debug) {
        System.out.println(b + ", " + getString(w) + ", " + e);
      }
    }

    return new Result(b, w, e);
  }

  /**
   * Checks if the specified number is invalid.
   * @param number Number.
   * @return Boolean.
   */
  public static boolean isInvalid(double number) {
    if(Double.isInfinite(number) || Double.isInfinite(number)) {
      return true;
    }
    return false;
  }

  /**
   * Checks if any number in the array is invalid.
   * @param numbers Array of numbers.
   * @return Boolean.
   */
  public static boolean isInvalid(double[] numbers) {
    for(double number : numbers) {
      if(isInvalid(number)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Performs weight adjustment on the model.
   * @param b Intercept.
   * @param w Weights.
   * @param data Data.
   * @param rate Learning rate.
   * @return Result containing updated intercept and weights.
   */
  public static Result step(double b, double[] w, double[][] data, double rate) {
    double b_gradient = 0d;
    double[] w_gradient = new double[w.length];
    double totalError = 0d;
    final double N = (double)data.length;
    final int y = data[0].length-1;

    for(int i=0; i < data.length; i++) {
      double diff = (data[i][y]- predict(b, w, data[i]));
      totalError += Math.pow(diff, 2.0d);
      b_gradient += diff;

      for(int j=0; j < data[i].length-1; j++) {
        w_gradient[j] += diff * data[i][j];
      }
    }

    totalError = totalError / N;
    double b_delta = b - (rate * -(2.0d/N) * b_gradient);
    double[] w_delta = new double[w.length];
    for(int i=0; i < w.length; i++) {
      w_delta[i] = w[i] - (rate * -(2.0d/N) * w_gradient[i]);
    }

    return new Result(b_delta, w_delta, totalError);
  }

  /**
   * Makes a prediction on the given input. e.g. w'x + b.
   * @param b Intercept.
   * @param w Weights.
   * @param x Inputs.
   * @return Prediction.
   */
  public static double predict(final double b, final double[] w, final double[] x) {
    double sum = 0.0d;
    for(int i=0; i < w.length; i++) {
      sum += (w[i] * x[i]);
    }
    sum += b;
    return sum;
  }

  /**
   * Gets a string representation of a double[].
   * @param numbers double[].
   * @return String.
   */
  public static String getString(final double[] numbers) {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    for(int i=0; i < numbers.length; i++) {
      sb.append(numbers[i]);
      if(i < numbers.length-1) {
        sb.append(", ");
      }
    }
    sb.append(']');
    return sb.toString();
  }

  /**
   * Creates a double[] of the specified length, and where each
   * element in the array is a random number.
   * @param length Length of array.
   * @return double[].
   */
  public static double[] initialize(final int length) {
    double[] output = new double[length];
    for(int i=0; i < length; i++) {
      output[i] = random.nextDouble();
    }
    return output;
  }


}
