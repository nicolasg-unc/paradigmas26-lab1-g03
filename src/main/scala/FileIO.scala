import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object FileIO {
  /* Pure function to read subscriptions from a JSON file */
  def readSubscriptions(): Option[List[Main.Subscription]] = {
    try {
      implicit val formats = org.json4s.DefaultFormats
      val source = Source.fromFile("./subscriptions.json")
      val content = source.mkString
      val json = parse(content).children.map {item =>
        val url = (item \ "url").extract[String]
        val name = (item \ "name").extract[String]
        (name, url)
      }.toList
      source.close()
      Some(json)
    } catch {
      case _: Exception => None
    }
  }

  def extractPosts(subreddit: String, jsonContent: String): List[Option[Main.Post]] = {
    implicit val formats = org.json4s.DefaultFormats
    var brokenPosts = 0

    val output = (parse(jsonContent) \ "data" \ "children").children.map { child =>
      try {
        val title = (child \ "data" \ "title").extract[String]
        val selftext = (child \ "data" \ "selftext").extract[String]
        val createdUtc = (child \ "data" \ "created_utc").extract[Double].toLong
        val date = Formatters.formatDateFromUTC(createdUtc)
        Some((subreddit, title, selftext, date))
      } catch {
        case _: Exception =>
          brokenPosts += 1
          None
      }
    }
    if (brokenPosts > 0) println(s"Warning: $brokenPosts posts were skipped due to missing fields or invalid JSON structure.")
    output
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): Option[String] = {
    try {
      val source = Source.fromURL(url)
      val content = source.mkString
      source.close()
      if (content.trim.startsWith("{\"kind\":")) Some(content)
      else None
    } catch {
      case _: Exception => None
    }
  }
}
