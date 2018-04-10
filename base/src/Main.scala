import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import models._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.{duration, ExecutionContext, Future}

object Main extends App with JsonCodecs with FailFastCirceSupport {
  implicit val system = ActorSystem("simple-server")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  private def getIp() =
    for {
      resp <- Http().singleRequest(HttpRequest(uri = "http://httpbin.org/ip"))
      ip <- Unmarshal(resp).to[Ip]
    } yield ip.origin

  private def getUserAgent() =
    for {
      resp <- Http().singleRequest(
        HttpRequest(uri = "http://httpbin.org/user-agent"))
      userAgent <- Unmarshal(resp).to[UserAgent]
    } yield userAgent.`user-agent`

  private val routes =
    path("about-me") {
      get {
        val result = for {
          origin <- getIp()
          userAgent <- getUserAgent()
        } yield FullInfo(origin, userAgent)

        complete(result)
      }
    } ~
      (path("hello") & parameter("who".?)) { who =>
        get {
          complete(s"hello ${who.getOrElse("Mr. Nobody")}")
        }
      }

  val binding = Http().bindAndHandle(routes, "localhost", 9000)

  println(fansi.Color.Green("Started server at localhost:9000"))

  Await.result(binding, Duration.Inf)
}
