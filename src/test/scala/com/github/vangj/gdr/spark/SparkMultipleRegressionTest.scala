package com.github.vangj.gdr.spark

import com.github.vangj.gdr.spark.SparkMultipleRegression.{Result, Options}
import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.rdd.RDD
import org.scalatest.{Matchers, FlatSpec}

import scala.collection.mutable.ArrayBuffer

class SparkMultipleRegressionTest extends FlatSpec with Matchers with SharedSparkContext {

  "vectors" should "multiply correctly" in {
    val w = List(1.0d, 2.0d, 3.0d)
    val x = List(1.0d, 2.0d, 3.0d)
    val b = 1.0d

    val observed = SparkMultipleRegression.predict(b, w, x)
    val expected = 1.0d + 4.0d + 9.0d + 1.0d

    observed should equal(expected +- 0.0001d)
  }

  "initializing random vector" should "generate the correct length" in {
    SparkMultipleRegression.init(10).length should be(10)
    SparkMultipleRegression.init(5).length should be(5)
  }

  "results" should "add correctly" in {
    var result = new Result(0.0, List(0, 0), 0, 0)
    for (i <- 0 until 10) {
      result += new Result(1, List(1, 1), 1, 1)
    }

    result.b should equal(10)
    result.w.length should equal (2)
    result.w(0) should equal(10)
    result.w(1) should equal(10)
    result.e should equal(10)
    result.t should equal(10)
  }

  "regression" should "find weights" in {
    val options = new Options(0.01d, 2000, "input", "output")
    val arr = new ArrayBuffer[List[Double]]()
    for (i <- 1 until 10) {
      arr += List(i, i)
    }

    val data: RDD[List[Double]] = sc.parallelize(arr)

    val result = SparkMultipleRegression.learn(0.7276532767062343d, List(0.5136790759391296d), data, options)
    println(s"FINAL: ${result.b} ~ ${result.w.mkString(",")} ~ ${result.e} ~ ${result.t}")
    result.b should equal(0.0 +- 0.001d)
    result.w.length should be(1)
    result.w(0) should equal (1.0 +- 0.001d)
    result.t should be(9)
  }
}
