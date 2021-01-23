package ai.economicdatasciences.supervised

import breeze.linalg._

class KNN(k: Int, dataX: DenseMatrix[Double], dataY: Seq[String], distanceFn: (DenseVector[Double], DenseVector[Double]) => Double) {

    // make predictions
    def predict(x: DenseVector[Double]): String = {
      // comput similarity for each
      val distances = dataX(*, ::).map(r => distanceFn(r, x))
      // get top k
      val topK = distances.toArray.zipWithIndex.sortBy(_._1).take(k).map(x => dataY(x._2))
      // find most frequent
      topK.groupBy(identity).mapValues(_.size).maxBy(_._2)._1

    }
  }
