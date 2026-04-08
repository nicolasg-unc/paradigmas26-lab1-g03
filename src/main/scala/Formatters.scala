import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Formatters {
  def formatReport(reportData: Main.SubscriptionReport): String = {
    val (subredditName, score, frequencies, firstPosts) = reportData
    val header = s"\n${"=" * 80}\nSubreddit: $subredditName\n${"=" * 80}\n"
    val scoreSection = s"Total score: $score\n"
    val frequenciesSection = s"Word frequencies:\n" + buildFrequenciesString(frequencies)
    val firstPostsSection = "First 5 posts:\n" + buildPostsString(firstPosts)
    header + scoreSection + frequenciesSection + firstPostsSection
  }

  def buildFrequenciesString(frequencies: List[(String, Int)]): String = {
    frequencies.map { case (word, count) => s"\t- $word: $count\n" }.mkString
  }

  def buildPostsString(posts: List[Main.IndexedPost]): String = {
    posts.zipWithIndex.map {
      case ((_, _, title, _, formattedDate, _, urlPost), index) =>
      s"${index + 1}. Title: $title\n   Date: $formattedDate\n   URL: $urlPost\n"
    }.mkString("\n")
  }

  def formatDateFromUTC(utcSeconds: Long): String = {
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm")
      .withZone(ZoneId.of("UTC"))
      .format(Instant.ofEpochSecond(utcSeconds))
  }

  def printSubredditOptions(subs: List[Main.IndexedSub]): Unit = {
    println(subs.map { case (index, subredditName, url) => s"$index. $subredditName ($url)" }.mkString("\n"))
    print("\nEnter the corresponding number to the subreddit you'd like to \nbrowse, or 0 to exit: ")
  }

  def printPostOptions(posts: List[Main.IndexedPost]): Unit = {
    println(s"\nPosts from ${posts.head._2}:")
    println(posts.map { case (index, _, title, _, formattedDate, _, _) => s"[$index] $title ($formattedDate)" }.mkString("\n"))
    println(s"\n[${(posts.last._1)+1}] View subreddit statistics\n")
    print("\nEnter the corresponding number to the post you'd like to \nopen or 0 to return to subreddit selection: ")
  }

  def printPost(post: Main.IndexedPost): Unit = {
    val (_, subName, title, selftext, formattedDate, _, _) = post
    println(s"\nTitle: $title\nDate: $formattedDate\n\n$selftext\n")
    print("\nEnter 0 to return to subreddit selection or another number to \nview another post: ")
  }
}