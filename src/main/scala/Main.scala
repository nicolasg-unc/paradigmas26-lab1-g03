import Domain._

object Main {
  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"

    val subscriptions: List[Subscription] = FileIO.readSubscriptions("./subscriptions.json")

    val allPosts: List[(Subscription, List[Post])] = subscriptions.map { case (subreddit, url) =>
      println(s"Fetching posts from: $url")
      val json = FileIO.downloadFeed(url)
      val posts = FileIO.parseFeed(json, subreddit)
      ((subreddit, url), posts)
    }

    /*val output = allPosts
      .map { case (url, posts) => Formatters.formatSubscription(url, posts) }
      .mkString("\n")

    println(output)
    */
  }
}

