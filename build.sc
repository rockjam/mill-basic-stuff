import mill._, scalalib._
import ammonite.ops._

object base extends ScalaModule {
  def scalaVersion = T { "2.12.4" }

  def listSources = T {
    sources().map(_.path).flatMap(ls.rec)
  }
}
