import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
	val appName = "Hub"
	val appVersion = "1.0.0-SNAPSHOT"

	val appDependencies = Seq(
		javaCore,
		javaJdbc,
		javaEbean,
		"mysql" % "mysql-connector-java" % "5.1.18",
		"org.xerial" % "sqlite-jdbc" % "3.7.2",
		"securesocial" %% "securesocial" % "2.1.1"
	)

	val main = play.Project(appName, appVersion, appDependencies).settings(
		resolvers += Resolver.url("sbt-plugin-snapshots", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
	)
}
