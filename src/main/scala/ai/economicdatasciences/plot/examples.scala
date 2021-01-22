import com.cibo.evilplot.colors.RGB
import com.cibo.evilplot.geometry.Extent
import com.cibo.evilplot.geometry.LineStyle.DashDot
import com.cibo.evilplot.geometry.LineStyle
import com.cibo.evilplot.numeric.Point
import com.cibo.evilplot.plot._
import com.cibo.evilplot.plot.renderers.PointRenderer
import scala.util.Random
import com.cibo.evilplot.plot.aesthetics.DefaultTheme._
import scala.math.Ordering

import com.cibo.evilplot.colors._
import com.cibo.evilplot.plot._
import com.cibo.evilplot.plot.renderers.PointRenderer
import com.cibo.evilplot.geometry.Extent
import com.cibo.evilplot.numeric.Point
import com.cibo.evilplot.numeric.Point3d
import com.cibo.evilplot.displayPlot
import com.cibo.evilplot.plot.aesthetics.Theme
import com.cibo.evilplot.geometry.Drawable
import com.cibo.evilplot.plot.aesthetics.Fonts
import com.cibo.evilplot.plot.aesthetics.Colors
import com.cibo.evilplot.plot.aesthetics.Elements

import org.jfree.chart.encoders.EncoderUtil
import org.apache.commons.codec.binary.Base64

// import d3v4._
//
// import scala.scalajs.js

// import plotly._, element._, layout._, Plotly._

object CiboExamples {
  // plot to html img tag
  def ciboToHtml(plt: Drawable, divWidth: String = "33%", divHeight: String = "33%"): String = {
    val ciboImg = plt.asBufferedImage
    val imgDisplaySz=(600,600)

    val imageDataString =  Base64.encodeBase64String(EncoderUtil.encode(ciboImg, "png"))
    val imgHeight = imgDisplaySz._1
    val imgWidth = imgDisplaySz._2
    val imgTag = s"""<div 'width:$divWidth;height:$divHeight'><img height="$imgHeight" width="$imgWidth" src="data:image/png;base64,$imageDataString" /></div>"""
    imgTag
  }

  def edsTheme(): Theme = {
    // Font order Title - ? - ? - Legend Text - axis text - ? - font
    val edsFont = Fonts(45.0,30.0,15.0,28.0,30.0,21.0,"Yantramanav Light")
    // colors
    val gradient = ContinuousColoring.gradient(HTMLNamedColors.red, HTMLNamedColors.green)
    val edsColors = Colors(Clear,HSLA(0.0,0.0,12.0,1.0),HSLA(211.0,38.0,48.0,1.0),
    HSLA(211.0,38.0,48.0,1.0),HSLA(0.0,0.0,12.0,1.0),
    HSLA(0.0,0.0,12.0,1.0),HSLA(0.0,0.0,65.0,0.8),
    HSLA(0.0,0.0,12.0,1.0),HSLA(0.0,0.0,12.0,1.0),
    HSLA(0.0,0.0,12.0,1.0),HSLA(0.0,0.0,12.0,1.0),HSLA(0.0,0.0,12.0,1.0),
    HSLA(0.0,0.0,12.0,1.0),List(HEX("#3F4B8C"),
    HEX("#CF3500"), HEX("#96B3FF"),
    HEX("#007B82"), HEX("#454545"),
    HEX("#9E9590"), HSLA(317.0,27.0,59.0,1.0),
    HSLA(354.0,100.0,81.0,1.0), HSLA(22.0,26.0,49.0,1.0),
    HSLA(17.0,9.0,70.0,1.0)),gradient)
    // elements  ? - LineStyle(?, ?) - Point Size
    val edsElements = Elements(2.0,LineStyle(List(),0.0),8.0,0.25,20.0,1.0,4.0,0.0,20,0.0,0.0,0.0,0.0,5,5,5,5,5,1.0,5.0)
    // create theme
    val edsTheme = Theme(edsFont, edsColors, edsElements)
    edsTheme
  }

  implicit val defaultTheme: Theme = edsTheme()

  val qualities = Seq("Group 1", "Group 2", "Group 3", "Group 4", "Group 5", "Group 6")

  final case class CatData(x: Double, y: Double, category: String)
  val data: Seq[CatData] = Seq.fill(100) {
    CatData(Random.nextDouble(), Random.nextDouble(), qualities(Random.nextInt(6)))
  }
  val points = data.map(d => Point(d.x, d.y))

  def toPoint3d(data: Seq[CatData], categoryList: Seq[String]): Seq[Point3d[Int]] = {
    val out = data.map(x => {
      val catId = categoryList.indexOf(x.category)
      Point3d(x.x, x.y, catId)
    })
    out.toSeq
  }

  def pt3ToCat(x: Point3d[Int], categoryList: Seq[String]): String = {
    categoryList(x.z)
  }

  val ptData = toPoint3d(data, qualities)

  val plot0 = ScatterPlot(ptData, pointRenderer = Some(PointRenderer.colorByCategory(ptData, { x: Point3d[Int] =>
    " " + qualities(x.z) + " "
  }))).standard().xLabel("X Axis").yLabel("Y Axis").padTop(25.0).topLegend().title("Test Scatter Plot").render()

  displayPlot(plot0)
  // val plot0 = ScatterPlot(ptData, pointRenderer = Some(PointRenderer.colorByCategory(ptData, { x: Point3d[Int] =>
  //   if(x.z == 1){
  //     " Good "
  //   } else {
  //     " Bad "
  //   }
  // }))).standard().xLabel("X Axis").yLabel("Y Axis").padTop(25.0).topLegend().title("Test Scatter Plot").render()
//xLabels = (0.0 to 1.0 by 0.25).map(_.toString) .xAxis(Seq("a","b","c","d")).yAxis() .frame()
//(0.0 to 1.0 by 0.25).toSeq


  // val ptData = data.map(d => {
  //   if(d.quality == "good"){
  //     Point3d(d.x, d.y, 1 )
  //   } else {
  //     Point3d(d.x, d.y, 0)
  //   }
  //
  // })

  // examples
  // val data = Seq.tabulate(100) { i =>
  //   Point(i.toDouble, scala.util.Random.nextDouble())
  // }
  // displayPlot(ScatterPlot(data).render())
  //
  // val data = Seq.tabulate(100) { i =>
  //   Point(i.toDouble, scala.util.Random.nextDouble())
  // }
  //
  // val plot = ScatterPlot(data).xAxis().yAxis().frame().xLabel("x").yLabel("y").render()
  // displayPlot(plot)

  // val test: scala.math.Ordering[MyFancyData] = scala.math.Ordering.by(_.quality)

  // ScatterPlot(data, pointRenderer = Some(PointRenderer.colorByCategory(data, { x: MyFancyData =>
  //   x.quality
  // }))).xAxis().yAxis().frame().rightLegend().render()

  // ScatterPlot(points,pointRenderer = Some(PointRenderer.colorByCategory(data.map(_.quality), { x: Point3d[String] =>
  //   x
  // }))).xAxis().yAxis().frame().rightLegend().render()
  // ScatterPlot(points,pointRenderer = Some(PointRenderer.colorByCategory(data.map(_.quality),CategoricalColoring.fromFunction(Seq(1), (x: Int) => HTMLNamedColors.green)))).xAxis().yAxis().frame().rightLegend().render()
  // ScatterPlot(data, pointRenderer = Some(PointRenderer.colorByCategory(data, { x: MyFancyData =>
  //   x.quality
  // })))
// }

// object PlotlyExamples {
//   // plotly example
//   val x = (0 to 100).map(_ * 0.1)
//   val y1 = x.map(d => 2.0 * d + util.Random.nextGaussian())
//   val y2 = x.map(math.exp)
//
//   val plot = Seq(
//     Scatter(x, y1).withName("Approx twice"),
//     Scatter(x, y2).withName("Exp")
//   )
//
//   val lay = Layout().withTitle("Curves")
//   plot.plot("plot", lay)  // attaches to div element with id 'plot'
// }

// object ScalaJSExample extends js.JSApp {
//
//   def main(): Unit = {
//     /**
//       * Adapted from http://thecodingtutorials.blogspot.ch/2012/07/introduction-to-d3.html
//       */
//     val graphHeight = 450
//
//     //The width of each bar.
//     val barWidth = 80
//
//     //The distance between each bar.
//     val barSeparation = 10
//
//     //The maximum value of the data.
//     val maxData = 50
//
//     //The actual horizontal distance from drawing one bar rectangle to drawing the next.
//     val horizontalBarDistance = barWidth + barSeparation
//
//     //The value to multiply each bar's value by to get its height.
//     val barHeightMultiplier = graphHeight / maxData;
//
//     //Color for start
//     val c = d3 //.rgb("DarkSlateBlue")
//
//     val rectXFun = (d: Int, i: Int) => i * horizontalBarDistance
//     val rectYFun = (d: Int) => graphHeight - d * barHeightMultiplier
//     val rectHeightFun = (d: Int) => d * barHeightMultiplier
//     val rectColorFun = (d: Int, i: Int) => c.brighter(i * 0.5).toString
//
//     val svg = d3.select("body").append("svg").attr("width", "100%").attr("height", "450px")
//     val sel = svg.selectAll("rect").data(js.Array(8, 22, 31, 36, 48, 17, 25))
//     sel.enter().append("rect").attr("x", rectXFun).attr("y", rectYFun).attr("width", barWidth).attr("height", rectHeightFun).style("fill", rectColorFun)
//
//
//   }

}
