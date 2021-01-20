import com.cibo.evilplot.numeric.Point
import com.cibo.evilplot.plot._
import com.cibo.evilplot.plot.renderers.PointRenderer
import com.cibo.evilplot.plot.aesthetics.DefaultTheme._
import com.cibo.evilplot.plot.aesthetics.Theme
import scala.util.Random
import com.cibo.evilplot.numeric.{Bounds, Datum2d, Point, Point3d}

object ScatterPlot {

  def apply[X <: Datum2d[X]](   // Point extends Datum2d[Point]
    data: Seq[X],
    pointRenderer: Option[PointRenderer[X]] = None,
    xBoundBuffer: Option[Double] = None,
    yBoundBuffer: Option[Double] = None
  )(implicit defaulttheme: Theme): Plot = ???
}
