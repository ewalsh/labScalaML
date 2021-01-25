package ai.economicdatasciences.spark

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.linalg.Vectors
// import org.apache.spark.ml.regression.LinearRegression //WithSGD
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.mllib.regression.StreamingLinearRegressionWithSGD
import org.apache.spark.mllib.regression.LabeledPoint
import ai.economicdatasciences.explore.Exploration.line2Data

import scala.io.Source

object SparkLM extends App {
  // get data
  val data = Source.fromFile("data/boston_housing.data").getLines().map({ l =>
    val formattedRow = line2Data(l).toArray

    val input = formattedRow.dropRight(1)
    val output = formattedRow.last

    LabeledPoint(output, Vectors.dense(input))
  }).toArray

  val conf = new SparkConf().setAppName("Spark LM").setMaster("local[*]")

  // val sc = new SparkContext(conf)
  val ssc = new StreamingContext(conf, Seconds(1))

  // parallelize the data
  // val dataRDD = sc.parallelize(data)
  val streamData = ssc.textFileStream("data/boston_housing.data").map({ l =>
    val formattedRow = line2Data(l).toArray

    val input = formattedRow.dropRight(1)
    val output = formattedRow.last

    LabeledPoint(output, Vectors.dense(input))
  }).cache()

  // model parameters
  val numIterations = 100
  val stepSize = 0.000001

  // val mod = new LinearRegression().setMaxIter(numIterations)
  //
  // val lm1 = mod.fit(dataRDD)

  // val mod = LinearRegressionWithSGD.train(dataRDD, numIterations, stepSize)
  //
  val numFeatures = 12
  val mod = new StreamingLinearRegressionWithSGD().setInitialWeights(Vectors.zeros(numFeatures))
  // // make predictions from the model
  // val predsAndActual = dataRDD.map( example => {
  //   val pred = model.predict(example.features)
  //
  //   (example.label, pred)
  // })
  //
  // val mse = predsAndActual.map({
  //   case (actual, pred) => math.pow((actual - pred), 2)
  // })
  // val trainingData = ssc.textFileStream(args(0)).map(LabeledPoint.parse).cache()
  // val testData = ssc.textFileStream(args(1)).map(LabeledPoint.parse)

  // val numFeatures = 12
  // val model = new StreamingLinearRegressionWithSGD()
  //   .setInitialWeights(Vectors.zeros(numFeatures))
  //
  mod.trainOn(streamData)
  mod.predictOnValues(streamData.map(lp => (lp.label, lp.features))).print()

  ssc.start()
  ssc.awaitTermination()

  // println(s"training error: ${mse}")

}
