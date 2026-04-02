import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Formatters {
  def formatReport(reportData: List[Main.SubscriptionReport]): String = reportData.map {
    case (url, subredditName, score, frequencies, firstPosts) =>
    val header = s"\n${"=" * 80}\nSubreddit: $subredditName\n${"=" * 80}\n"
    val scoreSection = s"Total score: $score\n"
    val frequenciesSection = s"Word frequencies:\n" + buildFrequenciesString(frequencies)
    val firstPostsSection = "First 5 posts:\n" + buildPostsString(url, firstPosts)
    header + scoreSection + frequenciesSection + firstPostsSection
  }.mkString("\n")

  def buildFrequenciesString(frequencies: Map[String, Int]): String = {
    frequencies.map { case (word, count) => s"\t- $word: $count\n" }.mkString
  }

  def buildPostsString(url: String, posts: List[Main.Post]): String = {
    posts.zipWithIndex.map {
      case ((_, title, _, formattedDate, _), index) =>
      s"${index + 1}. Title: $title\n   Date: $formattedDate\n   URL: $url\n"
    }.mkString("\n")
  }

  // TODO: borrar esto
  // Pure function to format posts from a subscription
  def formatSubscription(url: String, posts: List[Main.Post]): String = {
    val header = s"\n${"=" * 80}\nPosts from: $url \n${"=" * 80}"

    val formattedPosts = posts.map {
      case (subreddit, title, selftext, formattedDate, score) =>
      s"Subreddit: $subreddit\nTitle: $title\nDate: $formattedDate\nScore: $score\nContent: ${selftext.take(80)}...\n${"-"*80}"
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
