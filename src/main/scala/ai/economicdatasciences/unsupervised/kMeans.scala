package ai.economicdatasciences.unsupervised

import breeze.linalg.DenseVector
import scala.util.Random

case class Cluster(mean: DenseVector[Double], assignedDataPoints: Seq[DenseVector[Double]])

object KMeans {

  def initClusters(data: Seq[DenseVector[Double]], numClusters: Int): Seq[Cluster] = {
    val dataDim = data.head.length
    val randomizedData = Random.shuffle(data)
    val groupSize = math.ceil(data.size / numClusters.toDouble).toInt

    randomizedData.grouped(groupSize).map(grp => {
      Cluster(mean = DenseVector.zeros[Double](dataDim), assignedDataPoints = grp)
    }).toSeq
  }

  def computeMean(data: Seq[DenseVector[Double]]): DenseVector[Double] = {
    val dataDim = data.head.length
    val meanArr = data.foldLeft(Array.fill[Double](dataDim)(0.0))((acc, dataPt) => {
      (acc, dataPt.toArray).zipped.map(_ + _)
    }).map(_ / data.size)

    DenseVector(meanArr)
  }

  def assignDataPoints(clusterMeans: Seq[DenseVector[Double]],
    dataPoints: Seq[DenseVector[Double]],
    distance: (DenseVector[Double], DenseVector[Double]) => Double
  ): Seq[Cluster] = {
    // setup
    val dataDim = dataPoints.head.length
    var initialClusters = Map.empty[DenseVector[Double], Set[DenseVector[Double]]]
    clusterMeans.foreach(m => initialClusters += (m -> Set.empty[DenseVector[Double]]))

    // add nearest mean
    val clusters = dataPoints.foldLeft(initialClusters)((acc, dpt) => {
      val nearestMean = clusterMeans.foldLeft((Double.MaxValue, DenseVector.zeros[Double](dataDim)))((acc, mean) => {
        val meanDist = distance(dpt, mean)
        if(meanDist < acc._1){
          (meanDist, mean)
        } else {
          acc
        }
      })._2
      acc + (nearestMean -> (acc(nearestMean) + dpt))
    })

    clusters.toSeq.map(cl => Cluster(cl._1, cl._2.toSeq))
  }


  def cluster(data: Seq[DenseVector[Double]], numClusters: Int,
    distanceFunc: (DenseVector[Double], DenseVector[Double]) => Double): Seq[Cluster] = {
      // size check
      assert(data.size > 0)

      var clusters = initClusters(data, numClusters)

      var oldClusterMeans = clusters.map(_.mean)
      var newClusterMeans = oldClusterMeans.map(m => m.map(_ + 1.0))

      var iterations = 0

      while(oldClusterMeans != newClusterMeans) {
        oldClusterMeans = newClusterMeans
        newClusterMeans = clusters.map(c => {
          computeMean(c.assignedDataPoints)
        })
        clusters = assignDataPoints(newClusterMeans, data, distanceFunc)

        iterations += 1
        println(s"iteration ${iterations}")
      }

      clusters
    }
}
