name := "LabScalaML"

sbtVersion := "1.2.8"

scalaVersion := "2.12.8"

organization := "ai.economicdatasciences"

// enablePlugins(ScalaJSPlugin)
// // This is an application with a main method
// scalaJSUseMainModuleInitializer := false

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.0.0",
  "org.apache.spark" %% "spark-sql" % "3.0.0",
  "org.apache.spark" %% "spark-mllib" % "3.0.0",
  "org.scalanlp" %% "breeze" % "1.0",
  "org.scalanlp" %% "breeze-natives" % "1.0",
  "org.scalanlp" %% "breeze-viz" % "1.0",
  "io.circe" %% "circe-core" % "0.14.0-M1",
  "io.circe" %% "circe-generic" % "0.14.0-M1",
  "io.circe" %% "circe-parser" % "0.14.0-M1",
  "com.cibo" %% "evilplot" % "0.8.0",
  "com.cibo" %% "evilplot-repl" % "0.8.0",
  "com.github.fommil.netlib" % "all" % "1.1.2" pomOnly(),
  "com.typesafe.akka" %% "akka-actor" % "2.6.11",
  // "com.github.fdietze.scala-js-d3v4" %%% "scala-js-d3v4" % "809f086",
  // "org.singlespaced" %%% "scalajs-d3_sjs0.6_2.12" % "0.3.4",
  // "org.plotly-scala" %% "plotly-render" % "0.8.1",
  // "com.cibo" %% "evilplot-jupyter-scala" % "0.8.0"
  "org.scalatest" %% "scalatest" % "3.3.0-SNAP2" % Test
)

resolvers ++= Seq(
    "Central" at "https://repo1.maven.org/maven2",
    "CIBO" at "https://dl.bintray.com/cibotech/public/" //,
    // "jitpack" at "https://jitpack.io"
  )
