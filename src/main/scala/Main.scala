object Main {
  type Subscription = (String, String)
  type Post = (String, String, String, String)
  def main(args: Array[String]): Unit = {

    val subscriptions: List[Subscription] = FileIO.readSubscriptions()

    val allPosts: List[Subscription] = subscriptions.map { subscription =>
      println(s"Fetching posts from: ${subscription._2}")
      val posts = FileIO.downloadFeed(subscription._2)
      (subscription._2, posts)
    }

    val output = allPosts
      .map { case (url, posts) => Formatters.formatSubscription(url, posts) }
      .mkString("\n")

    println(output)
  }
}
