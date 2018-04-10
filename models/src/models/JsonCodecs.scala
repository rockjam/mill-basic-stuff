package models

import io.circe.{Decoder, Encoder}

trait JsonCodecs {
  import io.circe.generic.semiauto._

  implicit val ipEncoder: Decoder[Ip] = deriveDecoder

  implicit val userAgentEncoder: Decoder[UserAgent] = deriveDecoder

  implicit val fullInfoEncoder: Encoder[FullInfo] = deriveEncoder

}
