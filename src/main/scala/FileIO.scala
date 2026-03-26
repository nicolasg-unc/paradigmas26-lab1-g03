import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object FileIO {
  /* Pure function to read subscriptions from a JSON file */
  def readSubscriptions(): List[Main.Subscription] = {
    implicit val formats = org.json4s.DefaultFormats
    val source = Source.fromFile("./subscriptions.json")
    val content = source.mkString
    val json = parse(content).children.map { item =>
      val url = (item \ "url").extract[String]
      val name = (item \ "name").extract[String]
      (name, url)
    }.toList
    source.close()
    json
  }

  def extractPosts(subreddit: String, jsonContent: String): List[Main.Post] = {
    implicit val formats = org.json4s.DefaultFormats

    (parse(jsonContent) \ "data" \ "children").children.map { child =>
      val title = (child \ "data" \ "title").extract[String]
      val selftext = (child \ "data" \ "selftext").extract[String]
      val createdUtc = (child \ "data" \ "created_utc").extract[Double].toLong
      val date = TextProcessing.formatDateFromUTC(createdUtc)
      (subreddit, title, selftext, date)
    }
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): String = {
    val source = Source.fromURL(url)
    source.mkString
  }
}