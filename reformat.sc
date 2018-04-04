import mill._, scalalib._
import ammonite.ops._

import $ivy.`com.geirsson::scalafmt-core:1.4.0`
import $ivy.`com.geirsson::scalafmt-cli:1.4.0`
import org.scalafmt._
import org.scalafmt.cli._
import org.scalafmt.config._

trait ScalafmtSupport extends ScalaModule {

  def reformat = T {
    val files = filesToFormat(sources())
    T.ctx.log.info(s"Formatting ${files.size} Scala sources")
    files.map { path =>
      Scalafmt
        .format(read(path), config)
        .toEither
        .fold(
          err =>
            T.ctx.log.error(
              s"Failed to format file: ${path}. Error: ${err.getMessage}"),
          formatted => write.over(path, formatted)
        )
      path
    }
  }

  def config =
    try StyleCache
      .getStyleForFile(".scalafmt.conf")
      .getOrElse(ScalafmtConfig.default)
    catch { case err => ScalafmtConfig.default }

  private def filesToFormat(sources: Seq[PathRef]) = {
    for {
      pathRef <- sources if exists(pathRef.path)
      file <- ls.rec(pathRef.path) if file.isFile && file.ext == "scala"
    } yield file
  }

}
