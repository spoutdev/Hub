import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
	val appName = "Hub"
	val appVersion = "1.0.0-SNAPSHOT"

	val appDependencies = Seq(
		// Add your project dependencies here
		javaCore,
		javaJdbc,
		javaEbean,
		"com.google.code.gson" % "gson" % "2.2.4",
		"mysql" % "mysql-connector-java" % "5.1.26",
		"org.bouncycastle" % "bcprov-ext-jdk15on" % "1.49",
		"org.projectlombok" % "lombok" % "0.12.0",
		"org.xerial" % "sqlite-jdbc" % "3.7.15-M1"
	)

	val main = play.Project(appName, appVersion, appDependencies).settings(
		// Add your own project settings here
	)
}
