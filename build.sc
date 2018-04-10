import mill._
import scalalib._
import ammonite.ops._
import $file.reformat
import $file.packager

object base extends BaseModule with packager.Packager {
  def moduleDeps = Seq(models)

  def mainClass = Some("Main")

  object test extends Tests {
    def testFrameworks = Seq("utest.runner.Framework")
    def ivyDeps = Agg(ivy"com.lihaoyi::utest:0.6.3")
  }

  def ivyDeps = Agg(
    ivy"com.lihaoyi::fansi:0.2.5",
    ivy"org.scala-lang:scala-reflect:${scalaVersion()}",
    ivy"com.typesafe.akka::akka-http:10.0.13",
    ivy"de.heikoseeberger::akka-http-circe:1.20.0"
  )

  def listSources = T {
    sources().map(_.path).flatMap(ls.rec)
  }
}

object models extends BaseModule {
  def ivyDeps = Agg(
    ivy"io.circe::circe-core:0.9.3",
    ivy"io.circe::circe-generic:0.9.3",
    ivy"io.circe::circe-parser:0.9.3"
  )
}

trait BaseModule extends ScalaModule with reformat.ScalafmtSupport {
  def scalaVersion = "2.12.4"

  def scalacOptions = super.scalacOptions() ++ Seq(
    "-Ywarn-unused:_",
//    "-Xfatal-warnings"
  )

  def compile = T {
    reformat()
    super.compile()
  }
}
