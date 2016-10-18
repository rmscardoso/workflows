name := "workflows"

version := "1.0"

lazy val `workflows` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  evolutions,
  jdbc,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "com.h2database" % "h2" % "1.4.192"
)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  