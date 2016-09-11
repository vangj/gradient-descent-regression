package com.github.vangj.gdr.spark

import org.apache.log4j.LogManager
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.clapper.argot.ArgotParser

import scala.collection.mutable
import scala.util.Random

/**
  * Using gradient descent to learn the weights of a multiple regression model in Spark.
  */
object SparkMultipleRegression {
  @transient lazy val logger = LogManager.getLogger(SparkMultipleRegression.getClass)

  /**
    * Program options.
    * @param rate Learning rate.
    * @param iterations Number of iterations.
    * @param input Path of input data in CSV format.
    * @param output Path of output data.
    */
  case class Options(rate: Double, iterations: Int, input: String, output: String)

  /**
    * Stores the results.
    * @param b Intercept.
    * @param w Weights.
    * @param e Error associated with the weights.
    * @param t Total number of records.
    */
  case class Result(b: Double, w: List[Double], e: Double, t: Double) {
    def add(that: Result): Result = {
      val b = this.b + that.b
      val e = this.e + that.e
      val t = this.t + that.t
      val w = new mutable.MutableList[Double]()

      for (i <- 0 until this.w.length) {
        w += (this.w(i) + that.w(i))
      }
      new Result(b, w.toList, e, t)
    }
    def +(that:Result): Result = add(that)
  }

  def main(args: Array[String]): Unit = {
    import org.clapper.argot.ArgotConverters._
    val parser = new ArgotParser("MultipleRegression", preUsage = Some("v0.0.1"))

    val rate = parser.option[Double](List("r"), "r", "learning rate")
    val iterations = parser.option[Int](List("t"), "t", "number of iterations")
    val input = parser.option[String](List("i"), "i", "input file")
    val output = parser.option[String](List("o"), "o", "output file")

    try {
      parser.parse(args)

//      logger.info(
//        s"""
//           | rate = ${rate.value.get},
//           | iterations = ${iterations.value.get}
//           | input = ${input.value.get}
//           | output = ${output.value.get}
//         """.stripMargin.trim)

      val conf = new SparkConf()
      conf.setAppName(s"gradient descent for regression ${input.value.get} to ${output.value.get}")

      val sc = new SparkContext(conf)

      var rawData = sc.textFile(input.value.get)
      val headers = rawData.first()
      rawData = rawData.filter(line => !line.equals(headers))

      val rdd = rawData
        .map(line => {
          line.split(",").map(_.toDouble).toList
        }).cache()

      val options = new Options(rate.value.get, iterations.value.get, input.value.get, output.value.get)

      val result = learn(rdd, options)
      sc.parallelize(List(result))
        .map(_.toString)
        .saveAsTextFile(options.output)
    } catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
  }

  /**
    * Learns the weights of a multiple regression model using gradient descent algorithm.
    * @param rdd Data.
    * @param options Options. e.g. learning rate, iterations
    * @return Weights of a multiple regression model.
    */
  def learn(rdd: RDD[List[Double]], options: Options): Result = {
    val numWeights = rdd.take(1)(0).length - 1
    learn(Random.nextDouble(), init(numWeights), rdd, options)
  }

  /**
    * Learns the weights of a multiple regression model using gradient descent algorithm.
    * @param b Initial intercept.
    * @param w Initial weights.
    * @param rdd Data.
    * @param options Options. e.g. learning rate, iterations
    * @return Weights of a multiple regression model.
    */
  def learn(b: Double, w: List[Double], rdd: RDD[List[Double]], options: Options): Result = {
    val rate = options.rate
    val y = w.length
    val numWeights = w.length
    var solution = new Result(b, w, 0, 0)

    for (i <- 0 until options.iterations) {
      val result =
        rdd.map(point => {
          val p = predict(solution.b, solution.w, point)
          val diff = point(y) - p

          val error = Math.pow(diff, 2.0d)
          val b_gradient = diff

          val w_gradients = new mutable.MutableList[Double]()
          for (j <- 0 until numWeights) {
            val w_gradient = diff * point(j)
            w_gradients += w_gradient
          }

          new Result(b_gradient, w_gradients.toList, error, 1)
        }).reduce(_ + _)

      val N = result.t
      val e = result.e / N
      val b = solution.b - (rate * -(2.0d/N) * result.b)
      val w = new mutable.MutableList[Double]

      for (j <- 0 until result.w.length) {
        w += (solution.w(j) - (rate * -(2.0d/N) * result.w(j)))
      }

//      logger.info(s"b = ${b} ~ w = ${w.mkString(",")} ~ e = ${e} ~ N = ${N}")

      solution = new Result(b, w.toList, e, N)
    }

    solution
  }

  /**
    * Computes y = b + w'x
    * @param b Intercept.
    * @param w Weights.
    * @param x Inputs.
    * @return The prediction.
    */
  def predict(b: Double, w: List[Double], x: List[Double]): Double = {
    var y = 0.0d
    for(i <- 0 until w.length) {
      y += (w(i) * x(i))
    }
    y += b
    return y
  }

  /**
    * Initializes a list of random doubles.
    * @param length Number of random doubles (length of the array).
    * @return List[Double].
    */
  def init(length: Int): List[Double] = {
    return List.fill(length)(Random.nextDouble())
  }
}
