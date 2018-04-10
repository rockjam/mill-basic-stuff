import mill._, scalalib._
import mill.modules.Jvm
import mill.define.Task
import ammonite.ops._

trait Packager extends ScalaModule {

  def packageIt = T {
    val dest = T.ctx().dest
    val libDir = dest / 'lib
    val binDir = dest / 'bin
    val runFile = binDir / "run"

    mkdir(libDir)
    mkdir(binDir)

    val allJars = packageSelfModules() ++ runClasspath()
      .map(_.path)
      .filter(path => exists(path) && !path.isDir)
      .toSeq

    allJars.foreach { file =>
      cp.into(file, libDir)
    }

    val runnerScript = s"""exec java $$JAVA_OPTS -cp "lib/*" ${finalMainClass()} "$$@""""

    write(runFile, runnerScript)
    setExecPermissions(runFile)

    PathRef(dest)
  }

  // package root and dependent modules with meaningfull names
  def packageSelfModules = T {
    Task.traverse(moduleDeps :+ this) { module =>
      module.jar
        .zip(module.artifactName)
        .zip(scalaVersion)
        .map {
          case ((jar, name), scalaV) =>
            val namedJar = jar.path / up / s"${name}_${Lib.scalaBinaryVersion(scalaV)}.jar"
            cp(jar.path, namedJar)

            namedJar
        }
    }
  }

  def setExecPermissions(runFile: Path) = {
    import java.nio.file.Files
    import java.nio.file.attribute.PosixFilePermission

    val perms = Files.getPosixFilePermissions(runFile.toNIO)
    perms.add(PosixFilePermission.GROUP_EXECUTE)
    perms.add(PosixFilePermission.OWNER_EXECUTE)
    perms.add(PosixFilePermission.OTHERS_EXECUTE)
    Files.setPosixFilePermissions(runFile.toNIO, perms)
  }
}
