package ai.economicdatasciences.supervised

import ai.economicdatasciences.supervised.KNN
import scala.io.Source
import breeze.linalg._

object KNNexample {
    def line2Data(line: String): (List[Double], String) = {
      val elements = line.split(",")
      val y = elements.last
      val x = elements.dropRight(1).map(_.toDouble).toList

      (x, y)
    }

    val data = Source.fromFile("data/ionosphere.data").getLines().map(x => line2Data(x)).toList

    val outputs = data.map(_._2).toSeq
    val inputs = DenseMatrix(data.map(_._1).toArray: _*)

    val euclideanDist = (v1: DenseVector[Double], v2: DenseVector[Double]) =>
      v1.toArray.zip(v2.toArray).map(x => scala.math.pow((x._1 - x._2), 2)).sum

    val rng = (0 to 299)
    val trainInputs = inputs(rng, ::)
    val trainOutputs = outputs.take(rng.length)

    val nn1 = new KNN(k = 4, dataX = trainInputs, dataY = trainOutputs, euclideanDist)

    // look at accuracy
    var numCorrect = 0

    val checkRng = (300 to 350)
    checkRng.foreach(expId => {
      val pred = nn1.predict(inputs(expId, ::).t)
      val target = outputs(expId)

      if(pred == target){ numCorrect += 1}
    })

   println(numCorrect.toDouble / (checkRng.length))
}
