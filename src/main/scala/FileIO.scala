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
    try source.mkString finally source.close()
  }

  def parseFeed(json: String, subreddit: String): List[Post] = {
    // JValue (JObject) -> JValue (JArray)
    val items = parse(json) \ "data" \ "children"
    // items.children: List[JValue], cada item es un JObject {kind, data}
    items.children.map { item =>
      // item \ "data": JValue (JObject) con los campos del post
      val data = item \ "data"
      // (data \ "title"): JValue (JString) -> extract: String
      val title = (data \ "title").extract[String]
      val selftext  = (data \ "selftext").extract[String]
      val createdUtc = (data \ "created_utc").extract[Double].toLong
      // val formattedDate = TextProcessing.formatDateFromUTC(createdUtc)
      (subreddit, title, selftext, createdUtc.toString) // Post
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

