import sbt._

object Dependencies {
  val junit = "junit" % "junit" % "4.12" % Test
  val junitInterface = "com.novocode" % "junit-interface" % "0.8" % "test->default"
  val args4j = "args4j" % "args4j" % "2.33"
  val specs = "org.specs2" % "specs2_2.10" % "2.4" % Test
  val scalatest = "org.scalatest" % "scalatest_2.10" % "2.2.6" % Test
  val spark = "org.apache.spark" % "spark-core_2.10" % "1.6.0" % "provided"
  val sparktesting = "com.holdenkarau" %% "spark-testing-base" % "1.6.0_0.3.0" % Test
  val csvparser = "com.opencsv" % "opencsv" % "3.8"
  val argot = "org.clapper" %% "argot" % "1.0.3"
}
