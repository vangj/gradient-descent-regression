package com.github.vangj.gdr;

import org.junit.Assert;
import org.junit.Test;

public class MultipleRegressionTest {
  @Test
  public void testLearning() {
    final double rate = 0.01d;
    final int iterations = 2000;
    final boolean debug = false;
    double[][] data = {
      {1, 1},
      {2, 2},
      {3, 3},
      {4, 4},
      {5, 5},
      {6, 6},
      {7, 7},
      {8, 8},
      {9, 9}
    };

    MultipleRegression.Result result = MultipleRegression.learn(rate, iterations, data, debug);
//    System.out.println(result);
    Assert.assertNotNull(result);
    Assert.assertEquals(0.0001d, result.b, 0.001d);
    Assert.assertEquals(1, result.w.length);
    Assert.assertEquals(1.0d, result.w[0], 0.001d);
  }
}
