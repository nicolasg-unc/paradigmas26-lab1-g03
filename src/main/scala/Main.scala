object Main {
  type Subscription = (String, String)          // (subredditName, url)
  type Post = (String, String, String, String)  // (subreddit, title, selftext, formattedDate)
  def main(args: Array[String]): Unit = {

    val subscriptions: List[Subscription] = FileIO.readSubscriptions()

    val allPosts: List[Post] = subscriptions.flatMap { subscription =>
      println(s"Fetching posts from: ${subscription._1} (${subscription._2})")
      val post = FileIO.downloadFeed(subscription._2)
      FileIO.extractPosts(subscription._1, post)
    }

    def filterPosts(xs: List[Post]): List[Post] = {
      xs.filter { case (_, title, selftext, _) =>
        selftext.trim != "" && // descartamos los que sólo tienen espacios y los que no tienen texto
        title != "" // descartamos los que no tiene título
      }
    }

    val postsFiltered = filterPosts(allPosts)

    // TODO: a veces algunos caracteres explotan y el formateo se ve mal
    val output = postsFiltered
      .map { case (subreddit, title, selftext, formattedDate) =>
        s"Subreddit: $subreddit\n Title: $title\n Date: $formattedDate\n Content: ${selftext.take(100)}...\n" + ("-" * 80)
      }
      .mkString("\n")

    println(output)
  }
}