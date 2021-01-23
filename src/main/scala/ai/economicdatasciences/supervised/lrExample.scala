package ai.economicdatasciences.supervised

import ai.economicdatasciences.explore.Exploration.line2Data
import ai.economicdatasciences.supervised.LinearRegression
import ai.economicdatasciences.supervised.LinearRegression._
import scala.io.Source

import breeze.linalg._

object LrExample extends App {
  // get data
  val data = Source.fromFile("data/boston_housing.data").getLines().map(x => line2Data(x)).toArray
  // convert to dense matrix
  val dm = DenseMatrix(data: _*)
  // get X and y
  val X = dm(::, 0 to 12)
  val y = dm(::, -1).toDenseMatrix.t

  // create lr
  val lm1 = new LinearRegression(inputs = X, outputs = y, basisFn = None)

  // train lm
  val betas = lm1.train()

  val testRng = (0 to 30)
  val testX = X(testRng, ::)
  val testY = y(testRng, ::)

  // make predictions
  val pred = lm1.predict(betas, testX)

  val mseEval = (pred: DenseMatrix[Double], target: DenseMatrix[Double]) =>
    breeze.stats.mean((pred - target).map(x => scala.math.pow(x, 2)))

  val mse = lm1.evaluate(
    weights = betas,
    inputs = testX,
    targets = testY,
    evaluator = mseEval
  )

  println(mse)
}
