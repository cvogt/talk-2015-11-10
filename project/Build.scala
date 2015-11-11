import sbt._
import Keys._

object stagedBuild extends Build {
  lazy val macros = Project(
    id="macros",
    base=file("macros"),
    settings = Seq(
      scalaVersion := "2.11.7",
      libraryDependencies ++= monocle.all
    )
  )

  lazy val main = Project(
    id="main",
    base=file("."),
    settings = Seq(
      scalaVersion := "2.11.7",
      libraryDependencies ++= Seq(
        "org.scalaz" %% "scalaz-core" % "7.1.5"
      )
    )
  ).dependsOn( macros )
}

object monocle{
  val version = "1.2.0-M1"
  val org = "com.github.julien-truffaut"
  val core = org %% "monocle-core" % version
  val generic = org %% "monocle-generic" % version
  val macro_ = org %% "monocle-macro" % version
  val all = Seq(core, generic, macro_)
}
