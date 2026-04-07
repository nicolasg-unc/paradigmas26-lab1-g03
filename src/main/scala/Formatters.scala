import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Formatters {
  def formatReport(reportData: List[Main.SubscriptionReport]): String = reportData.map {
    case (url, subredditName, score, frequencies, firstPosts) =>
    val header = s"\n${"=" * 80}\nSubreddit: $subredditName\nURL: $url\n${"=" * 80}\n"
    val scoreSection = s"Total score: $score\n"
    val frequenciesSection = s"Word frequencies:\n" + buildFrequenciesString(frequencies)
    val firstPostsSection = "First 5 posts:\n" + buildPostsString(firstPosts)
    header + scoreSection + frequenciesSection + firstPostsSection
  }.mkString("\n")

  def buildFrequenciesString(frequencies: List[(String, Int)]): String = {
    frequencies.map { case (word, count) => s"\t- $word: $count\n" }.mkString
  }

  def buildPostsString(posts: List[Main.Post]): String = {
    posts.zipWithIndex.map {
      case ((_, title, _, formattedDate, _, urlPost), index) =>
      s"${index + 1}. Title: $title\n   Date: $formattedDate\n   URL: $urlPost\n"
    }.mkString("\n")
  }

  def formatDateFromUTC(utcSeconds: Long): String = {
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm")
      .withZone(ZoneId.of("UTC"))
      .format(Instant.ofEpochSecond(utcSeconds))
  }
}
