import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import Domain._

object FileIO {
  implicit val formats: Formats = DefaultFormats
  // Pure function to read subscriptions from a JSON file
  def readSubscriptions(path: String): List[Subscription] = {
    val source = Source.fromFile(path)
    // String
    val content = try source.mkString finally source.close()
    // JValue (JArray) -> children: List[JValue]
    parse(content).children.map { item =>
      // item: JValue (JObject), \ navega el campo, extract[String] lo convierte
      val name = (item \ "name").extract[String]
      val url  = (item \ "url").extract[String]
      (name, url) // Subscription
    }
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): String = {
    val source = Source.fromURL(url)
    source.mkString
  }
}

