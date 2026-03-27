object Main {
  type Subscription = (String, String)          // (subredditName, url)
  type Post = (String, String, String, String)  // (subreddit, title, selftext, formattedDate)
  def main(args: Array[String]): Unit = {

    val subscriptions: List[Subscription] = FileIO.readSubscriptions()

    val allPosts: List[(String, List[Post])] = subscriptions.map { case (subredditName, url) =>
      println(s"Fetching posts from: $url")
      println(s"Fetching posts from: $subredditName $url")
      val posts = FileIO.downloadFeed(url)
      val post_list = FileIO.extractPosts(subredditName, posts)
      (url, post_list)
    }

    def filterPosts(xs: List[Post]): List[Post] = {
      xs.filter { case (_, title, selftext, _) =>
        selftext.trim != "" &&  // descartamos los que sólo tienen espacios y los que no tienen texto
        title != ""             // descartamos los que no tiene título
      }
    }

    val postsFiltered = allPosts.map { case (url, post_list) => (url, filterPosts(post_list)) }

    val output = postsFiltered.map { case (url, posts) =>
      Formatters.formatSubscription(url, posts) }
      .mkString("\n")

    println(output)
  }
}
