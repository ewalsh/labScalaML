import breeze.plot._
import breeze.linalg._
import breeze.numerics._

import scala.io.Source
import java.io.File
import java.awt.Image
import scala.collection.mutable.MutableList

import com.cibo.evilplot.displayPlot
import com.cibo.evilplot.plot._
import com.cibo.evilplot.plot.renderers.PointRenderer
import com.cibo.evilplot.plot.aesthetics.DefaultTheme._
import com.cibo.evilplot.numeric.Point
import com.cibo.evilplot.geometry.Drawable

// import plotly._, element._, layout._, Plotly._
// import org.singlespaced.d3js.Ops._
// import org.singlespaced.d3js.d3


case class CatPoint(category: String, pt: Point)

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

  // def showPlot(plot: com.cibo.evilplot.geometry.Drawable) = Image.fromRenderedImage(plot.asBufferedImage, Image.PNG)
  // read data
  val data = Source.fromFile("data/boston_housing.data").getLines().map(x => line2Data(x))
  // transform to dense matrix
  val dm = DenseMatrix(data.map(r => { row2DenseVec(r) }).toArray: _*)

  val output = dm(::,13)
  // print stats
  println(s"mean: ${breeze.stats.mean(output)}")
  println(s"median: ${breeze.stats.median(output)}")
  println(s"variance: ${breeze.stats.variance(output)}")
  println(s"maximum: ${breeze.linalg.max(output)}")

  val pdata = Seq.tabulate(100) { i =>
    val pt = Point(i.toDouble, scala.util.Random.nextDouble())
    CatPoint("test", pt)
  }
  val plot = ScatterPlot(pdata.map(_.pt)).xAxis().yAxis().frame().xLabel("x").yLabel("y").render()
  //.write(new File("plots/plot.png"))
  // display
  // displayPlot(plot)
  // Image.
  // val context
  // ScatterPlot(pdata).render(Extent(400, 400)).draw(context)
  val f = Figure()
  f.width = 800
  f.height = 800

  val columns = Array("CRIM", "ZN", "INDUS", "CHAS", "NOX", "RM", "AGE", "DIS", "RAD", "TAX", "PTRATIO", "B", "LSTAT", "MEDV")

  //val xs = for (x <- fr.colAt(0).toSeq) yield x._2
  // val ys = for (y <- fr.colAt(13).toSeq) yield y._2
  val ys = dm(::,13)  //.toArray.toSeq

  println(ys)
  f.subplot(0)
  f.subplot(0) += hist(ys,10)

  val subplots = for (i <- 0 to 3; j <- 0 to 3)
  yield { f.subplot(4, 4, i + 4 * j)  }


  for (i <- 0 to 12; j <- 0 to 3) {
    // val xs = for (x <- fr.colAt(i).toSeq) yield { x._2 }
    val xs = dm(::, i)
    val p = subplots(i)
    p += breeze.plot.plot(xs, ys, '+')
    p.xlabel = columns(i)
    p.ylabel = "PRICE"
  }

  val plots = MutableList[Seq[com.cibo.evilplot.plot.Plot]]()
  val columns1 = Array("CRIM", "ZN", "INDUS", "CHAS", "NOX", "RM", "AGE", "DIS", "RAD", "TAX", "PTRATIO", "B", "LSTAT", "MEDV","PRICE")

  for(i <- 0 to 3){
    val ys = dm(::,i)
    val plist = MutableList[com.cibo.evilplot.plot.Plot]()
    for(j <- 0 to 3){
      val xs = dm(::, j)
      val tmpPts = MutableList[Point]()
      for(pid <- 0 to (xs.length - 1)){
        tmpPts += Point(xs(pid), ys(pid))
      }
      val seqPts = tmpPts.toSeq
      plist += ScatterPlot(seqPts).xAxis().yAxis().xLabel(columns1(j)).yLabel(columns1(i))
    }
    val pseq = plist.toSeq
    plots += pseq
  }
  val plotSeq = plots.toSeq

  val ciboTest = Facets(plotSeq).standard().title("Boston Housing Data") //.topLabels(data.map { _._1 }).rightLabels(data.map { _._1 }).render()


}
