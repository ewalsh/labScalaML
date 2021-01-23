package ai.economicdatasciences.supervised

import breeze.linalg._
import breeze.numerics.log
import breeze.stats._
import breeze.stats.distributions.Gaussian

class NaiveBayes(dataX: DenseMatrix[Double], dataY: Seq[String]) {

  val classes = dataY.distinct

  val likelihoods = {
    //map classes to row indexes of their examples
    val classIdx = dataY.zipWithIndex.groupBy(_._1).mapValues(_.map(_._2))
    // for each class create normal dist for each feature
    classIdx.mapValues(idx => {
      val classData = dataX(idx, ::).toDenseMatrix
      classData(::, *).map(col => {
        val empMean = mean(col)
        val empSd = stddev(col)

        // standard deviation cannont be zero
        val trueV = empSd match {
          case 0.0 => 0.00001
          case _ => empSd
        }

        new Gaussian(mu = empMean, sigma = trueV)
      }).t.toArray
    })
  }

  val priors = {
    val numExamples = dataY.length
    dataY.groupBy(identity).mapValues(x => x.size/numExamples.toDouble)
  }

  def predict(x: DenseVector[Double]): String = {
    // compute posteriors for each class
    val posteriors = classes.map(cl => {
      val prior = priors(cl)
      val likelihoodDists = likelihoods(cl)

      val logLikelihoods = likelihoodDists.zip(x.toArray).map( { case (dist, value) => log(dist.pdf(value)) })

      val posterior = logLikelihoods.sum + log(prior)

      (cl, posterior)
    })
    posteriors.sortBy(-_._2).head._1
  }
}
