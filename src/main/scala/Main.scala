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
      val json = FileIO.downloadFeed(url)
      val posts = FileIO.parseFeed(json, subreddit)
      ((subreddit, url), posts)
    }

    val validPosts: List[(Subscription, List[Post])] = allPosts.map { case (subscription, posts) =>
      (subscription, posts.filter(TextProcessing.isValidPost)) // posts.filter(p => TextProcessing.isValidPost(p))
    }

    /*val output = allPosts
      .map { case (url, posts) => Formatters.formatSubscription(url, posts) }
      .mkString("\n")

    println(output)
    */
  }
}

