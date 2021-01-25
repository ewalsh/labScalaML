package ai.economicdatasciences.akka

import java.util.UUID

import ai.economicdatasciences.akka.NNmaster.QueryInput
import akka.actor.{Actor, ActorLogging}
import breeze.linalg.{*, DenseVector}

object NNslave {
  case class TopK(slaveId: UUID, neighbours: Seq[(String, Double)])
}

class NNslave(id: UUID, inputPartition: Seq[DenseVector[Double]],
  outputPartition: Seq[String], k: Int,
  distanceFn: (DenseVector[Double], DenseVector[Double]) => Double
) extends Actor with ActorLogging {

  import NNslave._

  val slaveData = inputPartition

  def receive = {
    case QueryInput(input) => {
      log.info(s"slave ${id} received query")

      // comput similarity for each example
      val distances = slaveData.map(r => distanceFn(r, input))

      // get top k classes
      val topKClasses = distances.toArray.zipWithIndex.sortBy(_._1).take(k).map({
        case (dist, idx) => (outputPartition(idx), dist)
      })

      sender() ! TopK(id, topKClasses)

      log.info(s"slave ${id} finished NN search")
    }
  }
}
