name := "LabScalaML"

sbtVersion := "1.2.8"

scalaVersion := "2.12.8"

organization := "ai.economicdatasciences"



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
  "com.cibo" %% "evilplot-jupyter-scala" % "0.8.0"
)

resolvers ++= Seq(
    "Central" at "https://repo1.maven.org/maven2",
    "CIBO" at "https://dl.bintray.com/cibotech/public/"
  )
