import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Formatters {

  // Pure function to format posts from a subscription
  def formatSubscription(url: String, posts: List[PostHandling.Post]): String = {
    val header = s"\n${"=" * 80}\nPosts from: $url \n${"=" * 80}"

    val formattedPosts = posts.map {
      case (subreddit, title, selftext, formattedDate) =>
      s"Subreddit: $subreddit\nTitle: $title\nDate: $formattedDate\nContent: ${selftext.take(80)}...\n${"-"*80}"
      }.mkString("\n")

      s"$header\n$formattedPosts"
  }
  
  def formatDateFromUTC(utcSeconds: Long): String = {
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm")
      .withZone(ZoneId.of("UTC"))
      .format(Instant.ofEpochSecond(utcSeconds))
  }
}
