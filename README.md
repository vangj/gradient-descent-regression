Using the gradient descent approach for multiple regression
===========================================================

This library is to show serial Java and parallelized Scala/Spark implementations of using the [gradient descent](https://en.wikipedia.org/wiki/Gradient_descent) approach for finding the intercept and weights in multiple linear regression models. At a high level, the gradient descent approach is used to find the set of parameters, in this case, the intercept and weights of the linear regression model, that minimizes some cost function. The cost function in the case of linear regression is sum of squared difference between the true and predicted values. In multiple linear regression, you have multiple parameters, so you must simulatenously try to find all such parameters. Initially, you generate random values for the parameters, and adjust these values by following the gradients (the partial derivatives) to (hopefully) the global minimum. 

A few resources that might help give you insight into how gradient descent works are as follows.

* [Gradient Descent: Intuition](https://www.youtube.com/watch?v=kWq2k1gPyBs)
* [An introduction to gradient descent and linear regression](https://spin.atomicobject.com/2014/06/24/gradient-descent-linear-regression)

A few more notes about gradient descent is that it may be slow to converge (requires many iterations). Also, the learning rate at which you adjust the parameters may influence how quickly you converge as well as whether or not you will converge at all. There is always a chance you will get stuck in a local minimum using gradient descent. 

Usage of "serial" gradient descent in Java
==========================================
Assuming you have built the project, you may type in the following command.

```
java -cp /path/to/gradient-descent-regression-assembly-0.0.1-SNAPSHOT.jar -r 0.001 -i 2000 -f /path/to/input/data/csv/file
```

If you want debugging turned on.

```
java -cp /path/to/gradient-descent-regression-assembly-0.0.1-SNAPSHOT.jar -r 0.001 -i 2000 -f /path/to/input/data/csv/file -d
```

Note the following options.

* -r specifies the learning rate
* -i specifies the iterations 
* -f specifies the CSV file
* -d specifies whether to output to the console intermediary results; this is simply a flag

Usage of parallelized gradient descent in Spark
===============================================
Assuming you have built the project and have a [Spark](http://spark.apache.org) cluster running, you may type in the following command [to submit the application](http://spark.apache.org/docs/latest/submitting-applications.html).

```
/path/to/spark-submit \
 --class com.github.vangj.gdr.spark.SparkMultipleRegression \
 --master <master-url> \
 --deploy-mode <deploy-mode> \
 /path/to/gradient-descent-regression-assembly-0.0.1-SNAPSHOT.jar \
 -i /csv/input/file/path \
 -o /result/output/path \
 -t <number-of-iterations> \
 -r <learning-rate>
```

Input file format
=================
The input file format must be CSV with column headers. Also, the last column in the CSV file is always assumed to be the value to be predicted. A simple CSV file is below. The program will try to find a line (the intercept and slope/weight) to predict Y from X (note Y is the last column).

```
X, Y
1, 1
2, 2
3, 3
4, 4
5, 5
6, 6
7, 7
8, 8
9, 9
```

R
=

[Here are some data sets you may use to test the algorithm.](http://college.cengage.com/mathematics/brase/understandable_statistics/7e/students/datasets/mlr/frames/frame.html)

Assuming you have downloaded the data and converted them to CSV format, you may type in the following code in [R](https://www.r-project.org) to benchmark the results of this library with what R produces. Note that R does not gradient-descent to find the intercept and weights, but, rather, [QR decomposition](https://en.wikipedia.org/wiki/QR_decomposition). Please modify the code as appropriate to the path of the CSV file. Also, note the model expression will be different for each data set. See this [link](http://www.statmethods.net/stats/regression.html) for more details on using R for multiple linear regression.

```
f <- "all-greens.csv"
d <- read.csv(f, header=TRUE)
m <- lm(X1 ~ ., data=d)
summary(m)
```

Building the project
====================
You will need the following tools to build the project.

* JDK v1.8
* Maven v3.3.9
* SBT v0.13.8

Assuming you have installed the tools and configured them correctly, type in the following.

```
sbt assembly
```
