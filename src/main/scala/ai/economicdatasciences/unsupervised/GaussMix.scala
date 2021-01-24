package ai.economicdatasciences.unsupervised

import java.awt.{Color, Paint}
import java.util.Random

import breeze.linalg.{DenseMatrix,DenseVector,sum,*,Axis,argmax}
import breeze.plot._
import breeze.stats.distributions.MultivariateGaussian
import breeze.stats.mean

class GaussianMixture(dataPoints: DenseMatrix[Double], numClusters: Int) {
  // dimensions
  val dataDim = dataPoints.cols

  def matrixVertTile(vec: DenseMatrix[Double], repSize: Int): DenseMatrix[Double] = {
    var tiledMat = vec
    (0 to repSize).foreach(rep => {
      tiledMat = DenseMatrix.vertcat(tiledMat, vec)
    })

    tiledMat
  }

  def empiricalCov(data: DenseMatrix[Double]): DenseMatrix[Double] = {
    val empMean: DenseVector[Double] = mean(data(::, *)).t

    var cov = DenseMatrix.zeros[Double](dataDim, dataDim)

    (0 to dataPoints.rows - 1).foreach(dpId => {
      val dp = dataPoints(dpId, ::).t
      val dpMinusMu = dp - empMean.toDenseVector

      cov += dpMinusMu * dpMinusMu.t
    })

    cov.map(_ / (data.rows - 1))
  }

  def piUpdate(posteriorMat: DenseMatrix[Double]): scala.Vector[Double] = {
    (0 to numClusters - 1).foldLeft(Vector.empty[Double])((acc, clustId) => {
      // posteriors of datapoints for this cluster
      val clustPosterior = posteriorMat(::, clustId)
      val newPi = sum(clustPosterior / dataPoints.rows.toDouble)
      acc :+ newPi
    })
  }

  def eStep(clusters: Seq[MultivariateGaussian], pi: Seq[Double]): DenseMatrix[Double] = {
    val clusterProbMat = dataPoints(*, ::).map(dp => {
      val dpProbPerCluster = clusters.map(cluster => cluster.pdf(dp))
      DenseVector(dpProbPerCluster.toArray)
    })

    val priorTiled = matrixVertTile(DenseMatrix(pi), dataPoints.rows - 2)
    val unnormalizedPosterior = clusterProbMat *:* priorTiled

    unnormalizedPosterior(*, ::).map(post => {
      val normalizer = sum(post)
      post.map(_ / normalizer)
    })
  }

  def mStep(posteriorMat: DenseMatrix[Double],
    clusters: Seq[MultivariateGaussian]): (Seq[MultivariateGaussian], scala.Vector[Double]) = {
      val newMean = meanUpdate(posteriorMat)
      val newCov = covUpdate(posteriorMat, clusters)
      val newPi = piUpdate(posteriorMat)

      val newClusters = clusters.zipWithIndex.map({ case (clusterDist, idx) =>
        MultivariateGaussian(mean = newMean(idx), covariance = newCov(idx))
      })
      (newClusters, newPi)
  }

  def meanUpdate(posteriorMat: DenseMatrix[Double]): scala.Vector[DenseVector[Double]] = {
    (0 to numClusters - 1).foldLeft(Vector.empty[DenseVector[Double]])((acc, clustId) => {
      val clustPosterior = posteriorMat(::, clustId)
      val unnormalizedMu = sum(dataPoints(::, *) *:* clustPosterior, Axis._0).t
      val normalizer = sum(clustPosterior)
      val normalizedMu = unnormalizedMu.map(_ / normalizer)
      acc :+ normalizedMu.toDenseVector
    })
  }

  def covUpdate(posteriorMat: DenseMatrix[Double],
    clusters: Seq[MultivariateGaussian]): scala.Vector[DenseMatrix[Double]] = {

    (0 to numClusters - 1).foldLeft(Vector.empty[DenseMatrix[Double]])((acc, clustId) => {
      // posteriors of datapoints for this cluster
      val clustPosterior = posteriorMat(::, clustId)
      // mean for this cluster
      val mu = clusters(clustId).mean
      // (x_i - mean)(x_i-mean)^T for each datapoint i
      val unscaledCovariances = dataPoints(*, ::).map(dp => {
        (dp - mu) * (dp - mu).t
      })

      var covariance = DenseMatrix.zeros[Double](dataDim, dataDim)
      (0 to dataPoints.rows - 1).foreach(dp => {
        covariance += unscaledCovariances(dp) * clustPosterior(dp)
      })

      // normalized over the posteriors
      val normalizer = sum(clustPosterior)
      val normalizedCovariance = covariance.map(_ / normalizer)

      acc :+ normalizedCovariance
    })
  }

  def cluster() = {
    val randGen = new Random()

    // Initialized all cluster distributions.
    // All covariances set to empirical covariances.
    // means to random data points.
    val initialCov = empiricalCov(dataPoints)

    var currentClusters = (1 to numClusters).map(clust => {
      val meanId = randGen.nextInt(dataPoints.rows)
      val initialMean = dataPoints(meanId, ::).t

      MultivariateGaussian(
        mean = initialMean,
        covariance = initialCov
      )
    }).toList

    // also initialize Pi randomly
    var currentPi = {
      val unnormalizedRand = (1 to numClusters).map(clust => {
        randGen.nextInt(100)
      })

      val normalizer = unnormalizedRand.sum.toDouble
      unnormalizedRand.map(_ / normalizer)
    }

    var posteriorUpdated = DenseMatrix.zeros[Double](dataPoints.rows, dataDim)

    val f = Figure()
    f.subplot(0).xlabel = "X-coordinate"
    f.subplot(0).ylabel = "Y-coordinate"
    f.subplot(0).title = "311 Service Noise Complaints"

    val id2Color: Int => Paint = id => id match {
      case 0 => Color.YELLOW
      case 1 => Color.RED
      case 2 => Color.GREEN
      case 3 => Color.BLUE
      case 4 => Color.GRAY
      case _ => Color.BLACK
    }

    for(i <- (0 to 100)){
      val lastPi = currentPi

      posteriorUpdated = eStep(
        clusters = currentClusters,
        pi = currentPi
      )

      val (clusterUpdated, piUpdated): (Seq[MultivariateGaussian], Vector[Double]) = mStep(
        posteriorMat = posteriorUpdated,
        clusters = currentClusters
      )

      currentClusters = clusterUpdated.toList
      currentPi = piUpdated

      val piChange = currentPi.zip(lastPi).map(el => math.abs(el._1 - el._2)).sum / numClusters

      println("change in pi: " + piChange)
    }

    val argmaxPosterior = posteriorUpdated(*, ::).map { postDist =>
      argmax(postDist)
    }.toArray


    val clustersAndPoints = argmaxPosterior.zipWithIndex.map { case (clustIdx, dpIdx) =>
      (clustIdx, dataPoints(dpIdx, ::).t)
    }.groupBy(_._1)


    for (cl <- clustersAndPoints) {
      val x = cl._2.map(_._2(0))
      val y = cl._2.map(_._2(1))
      f.subplot(0) += scatter(x, y, { (_: Int) => 1000}, { (_: Int) => id2Color(cl._1)})
    }
  }
}
