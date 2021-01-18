import breeze.plot._
import breeze.linalg._
import breeze.numerics._

import scala.io.Source

import com.cibo.evilplot.plot._
import com.cibo.evilplot.plot.aesthetics.DefaultTheme._
import com.cibo.evilplot.numeric.Point

object Exploration extends App {
  // read lines
  def line2Data(line: String): List[Double] = {

    line
      .split("\\s+")
      .filter(_.length > 0)
      .map(_.toDouble)
      .toList
  }
  // transform to dense vector
  def row2DenseVec(row: List[Double]): DenseVector[Double] = DenseVector(row: _*)
  // read data
  val data = Source.fromFile("/opt/zeppelin-0.9.0-preview2-bin-all/data/boston_housing.data").getLines().map(x => line2Data(x))
  // transform to dense matrix
  val dm = DenseMatrix(data.map(r => { row2DenseVec(r) }).toArray: _*)

  val output = dm(::,13)
  // print stats
  println(s"mean: ${breeze.stats.mean(output)}")
  println(s"median: ${breeze.stats.median(output)}")
  println(s"variance: ${breeze.stats.variance(output)}")
  println(s"maximum: ${breeze.linalg.max(output)}")

  val pdata = Seq.tabulate(100) { i =>
    Point(i.toDouble, scala.util.Random.nextDouble())
  }
  val plot = ScatterPlot(pdata)
  //   .xAxis()
  //   .yAxis()
  //   .frame()
  //   .xLabel("x")
  //   .yLabel("y")
  //   .render()
}
