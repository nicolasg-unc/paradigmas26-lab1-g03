import Domain._

object Main {
  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"

    val subscriptions: List[Subscription] = FileIO.readSubscriptions("./local_subscriptions.json") match {
      case Some(subs) => subs
      case None =>
        println("Error: no se pudieron leer las suscripciones")
        return
    }

    val allPosts: List[(Subscription, List[Post])] = subscriptions.map { case (subreddit, url) =>
      println(s"Fetching posts from: $url")
      val posts = FileIO.downloadFeed(url)                            // Option[String]: None si falla la red
        .flatMap(json => FileIO.parseFeed(json, subreddit))           // Option[List[Post]]: None si falla el parsing
        .getOrElse(List.empty)                                        // List[Post]: vacía si algún paso falló
      ((subreddit, url), posts)                                       // (Subscription, List[Post])
    }

    val validPosts: List[(Subscription, List[Post])] = allPosts.map { case (subscription, posts) =>
      (subscription, posts.filter(TextProcessing.isValidPost)) // posts.filter(p => TextProcessing.isValidPost(p))
    }

    val output = validPosts.map { case (subscription, posts) =>
      val score = TextProcessing.totalScore(posts)
      val words = TextProcessing.wordFrequencies(posts)
      val top5  = posts.take(5)
      Formatters.formatSubscription(subscription, posts, score, words, top5)
    }.mkString("\n")

    println(output)
  }
}

