/*
Formatters: convierte datos ya procesados en strings para mostrar: armar el informe, los headers, el layout de salida. Lógica de presentación.
*/
import Domain._

object Formatters {

  // Pure function to format posts from a subscription
/*  def formatSubscription(url: String, posts: String): String = {
    val header = s"\n${"=" * 80}\nPosts from: $url \n${"=" * 80}"
    val formattedPosts = posts.take(80)
    header + "\n" + formattedPosts
  }*/

  def formatSubscription(subscription: Subscription, posts: List[Post], score: Int, words: List[(String, Int)], top5: List[Post]): String = {
    val (subreddit, _) = subscription
    val header           = s"\n${"=" * 80}\nSubreddit: $subreddit\n${"=" * 80}\n"
    val scoreSection     = s"Total score: $score\n"
    val wordsSection     = "Palabras más frecuentes:\n" + formatWords(words.take(10))
    val postsSection     = "Top 5 posts:\n" + formatPosts(top5)
    header + scoreSection + wordsSection + postsSection
  }

  def formatWords(words: List[(String, Int)]): String =
    words.map { case (word, count) => s"\t- $word: $count\n" }.mkString

  def formatPosts(posts: List[Post]): String =
    posts.zipWithIndex.map { case (post, i) =>
      val (_, title, _, date, _, url) = post
      s"${i + 1}. $title\n   Fecha: $date\n   URL: $url\n"
    }.mkString("\n")
}

