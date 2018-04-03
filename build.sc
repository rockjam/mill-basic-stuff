import mill._, scalalib._
import ammonite.ops._

object base extends ScalaModule {
  def scalaVersion = T { "2.12.4" }

  def ivyDeps = Agg(
    ivy"com.lihaoyi::fansi:0.2.5",
    ivy"org.scala-lang:scala-reflect:${scalaVersion()}"
  )
  
  def listSources = T {
    sources().map(_.path).flatMap(ls.rec)
  }
}
