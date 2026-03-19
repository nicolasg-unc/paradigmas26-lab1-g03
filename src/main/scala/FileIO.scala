import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

object FileIO {
  /* Pure function to read subscriptions from a JSON file */
  def readSubscriptions(): List[Main.Subscription] = {
    implicit val formats = org.json4s.DefaultFormats
    val source = Source.fromFile("./subscriptions.json")
    val stringSource = source.mkString
    val json = parse(stringSource).children.map { item =>
      val url = (item \ "url").extract[String]
      val name = (item \ "name").extract[String]
      (name, url)
    }.toList
    source.close()
    json
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): String = {
    val source = Source.fromURL(url)
    source.mkString
  }
}
