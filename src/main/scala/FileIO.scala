import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._

object FileIO {
  /* Pure function to read subscriptions from a JSON file */
  def readSubscriptions(): Option[List[PostHandling.Subscription]] = {
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

  def extractPosts(subreddit: String, jsonContent: String): List[Option[PostHandling.Post]] = {
    implicit val formats = org.json4s.DefaultFormats

    val output = (parse(jsonContent) \ "data" \ "children").children.map { child =>
      try {
        val title = (child \ "data" \ "title").extract[String]
        val selftext = (child \ "data" \ "selftext").extract[String]
        val createdUtc = (child \ "data" \ "created_utc").extract[Double].toLong
        val date = Formatters.formatDateFromUTC(createdUtc)
        val score = (child \ "data" \ "score").extract[Int]
        var urlPost = (child \ "data" \ "url").extract[String]
        Some((subreddit, title, selftext, date, score, urlPost))
      } catch {
        case _: Exception =>
          None
      }
    }
    val brokenPosts = output.count(_.isEmpty)
    if (brokenPosts > 0) {
      println(s"Warning: $brokenPosts posts were skipped due\n to missing or malformed data.")
    }
    output
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): Option[String] = {
    try {
      val source = Source.fromURL(url)
      val content = source.mkString
      source.close()
      if (content.trim.startsWith("{\"kind\":")) Some(content)
      else None // las strings de contenido válido para subreddits comienzan con {"kind":. Si no, descartar.
    } catch {
      case _: Exception => None
    }
  }
}