import mill._
import scalalib._
import ammonite.ops._
import $file.reformat

object base extends ScalaModule with reformat.ScalafmtSupport {
  def scalaVersion = T { "2.12.4" }

  def ivyDeps = Agg(
    ivy"com.lihaoyi::fansi:0.2.5",
    ivy"org.scala-lang:scala-reflect:${scalaVersion()}"
  )

  def scalacOptions = super.scalacOptions() ++ Seq(
    "-Ywarn-unused:_",
//    "-Xfatal-warnings"
  )

  def compile = T {
    reformat()
    super.compile()
  }

  def listSources = T {
    sources().map(_.path).flatMap(ls.rec)
  }
}
