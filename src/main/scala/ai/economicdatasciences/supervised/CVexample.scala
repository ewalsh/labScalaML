package ai.economicdatasciences.supervised

import breeze.linalg.DenseMatrix
import breeze.linalg.DenseVector
import breeze.numerics._
import breeze.plot.{Figure, _}
import breeze.stats.mean

import scala.io.Source

import ai.economicdatasciences.supervised.LinearRegression

object CVExample extends App {
  // get data clean
  def line2Data(line: String): Array[Double] = {
    line.split("\\s+").filter(_.length > 0).map(_.toDouble)
  }

  //import data
  val data = Source.fromFile("data/boston_housing.data").getLines().map(x => line2Data(x)).toArray

  //convert to breeze matrix
  val dm = DenseMatrix(data: _*)

  //the inputs are all but the last column.  Outputs are last column
  val X = dm(::, 0 to 12)
  val y = dm(::, -1).toDenseMatrix.t

  val mseEvaluator = (pred: DenseMatrix[Double], target: DenseMatrix[Double]) =>
    mean((pred - target).map(x => pow(x, 2)))

    //create LR object with our dataset
    val lm1 = new LinearRegression(
      inputs = X,
      outputs = y
    )


    var i = 0.000001

    var errors = Vector.empty[Double]
    var paramVals = Vector.empty[Double]
    var counter = 0

    while(i < 1000) {
      val cvError = lm1.crossValidation(
        folds = 5,
        regularizationParam = i,
        evaluator = mseEvaluator
      )

      i *= 10

      counter += 1
      errors :+= cvError
      paramVals :+= i //counter.toDouble
    }

    println(errors.size)
    println(paramVals.size)

    val f = Figure()
    val p = f.subplot(0)
    p.xlabel = "log(Gamma)"
    p.ylabel = "Error"

    p += plot(log(DenseVector(paramVals.toArray)), errors.toIndexedSeq)

}
