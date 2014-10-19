import sbt._

object ApplicationBuild extends Build {

  val appName         = "domain-finder"
  val appVersion      = "0.1-SNAPSHOT"

  val appDependencies = Seq(
    "com.google.inject" % "guice" % "3.0",
    "com.typesafe.akka" %% "akka-actor" % "2.2.4",
    "com.typesafe.akka" %% "akka-slf4j" % "2.2.4",
    "javax.inject" % "javax.inject" % "1",
    "org.reactivemongo" %% "reactivemongo" % "0.10.0",
    "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",

    "org.mockito" % "mockito-core" % "1.9.5" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

}
