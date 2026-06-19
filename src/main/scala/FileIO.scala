import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import Domain._

object FileIO {
  implicit val formats: Formats = DefaultFormats
  // Pure function to read subscriptions from a JSON file
  def readSubscriptions(path: String): Option[List[Subscription]] = {
    try {
      val source = Source.fromFile(path)
      val content = try source.mkString finally source.close() // String, cierra siempre
           // JValue (JArray) -> children: List[JValue]
      Some(parse(content).children.flatMap { item => // flatMap aplana los None y Some(x) -> x
        // item: JValue (JObject), \ navega el campo, extractOpt[String] lo convierte
        val name = (item \ "name").extractOpt[String] // Option[String]
        val url  = (item \ "url").extractOpt[String]  // Option[String]
        (name, url) match {
          case (Some(n), Some(u)) => Some((n, u)) // Subscription válida
          case _ =>
            println(s"Error: suscripción con campos faltantes, se descarta")
            None
        }
      })
    } catch {
      case e: Exception =>
        println(s"Error al leer el archivo de suscripciones: ${e.getMessage}")
        None
    }
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): Option[String] = {
    try {
      val source = Source.fromURL(url)
      Some(try source.mkString finally source.close()) // String, cierra siempre
    } catch {
      case e: Exception => // fallo de red o URL inválida
        println(s"Error al descargar $url: ${e.getMessage}")
        None
    }
  }

  def parseFeed(json: String, subreddit: String): Option[List[Post]] = {
    try {
      // JValue (JObject) -> JValue (JArray)
      val items = parse(json) \ "data" \ "children"
      // items.children: List[JValue], cada item es un JObject {kind, data}
      Some(items.children.flatMap { item =>
        // item \ "data": JValue (JObject) con los campos del post
        val data = item \ "data"
        // extractOpt devuelve Option[String], si falta el campo el post se descarta
        val title      = (data \ "title").extractOpt[String]
        val selftext   = (data \ "selftext").extractOpt[String]
        val createdUtc = (data \ "created_utc").extractOpt[Double].map(_.toLong)
        val score      = (data \ "score").extractOpt[Int]
        (title, selftext, createdUtc, score) match {
          case (Some(t), Some(s), Some(d), Some(sc)) =>
            Some((subreddit, t, s, TextProcessing.formatDateFromUTC(d), sc)) // Post válido
          case _ => // campos faltantes, se descarta
            println(s"Error: post con campos faltantes, se descarta")
            None
        }
      })
    } catch {
      case e: Exception => // JSON mal formado
        println(s"Error al parsear el feed de $subreddit: ${e.getMessage}")
        None
    }
  }
}

/*
{
  "data": {
    "children": [
      {
        "kind": "t3",
        "data": {
          "title": "...",
          "selftext": "...",
          "created_utc": 1234567890.0,
          "score": 42,
          "url": "..."
        }
      }
    ]
  }
}
*/

