object PostHandling {
  type Subscription = (String, String)          // (subredditName, url)
  type Post = (String, String, String, String)  // (subreddit, title, selftext, formattedDate)

  def processPosts(subList: List[Subscription]): List[(String, List[Post])] = {
    subList.map { case (subredditName, url) =>
      println(s"Fetching posts from: \"$subredditName\", $url")
      val posts = FileIO.downloadFeed(url)
      posts match {
        case Some(content) =>
          val extractedPosts = FileIO.extractPosts(subredditName, content).flatten
          (url, extractedPosts) // Se ignoran los posts "rotos"
        case None =>
          println(s"Error: Failed to download feed for subreddit $subredditName.")
          (url, List())
      }
    }
  }

  // def filterPosts(xs: List[Post]): List[Post] = {
  //   xs.filter { case (_, title, selftext, _) =>
  //     selftext.trim != "" &&  // descartamos los que sólo tienen espacios y los que no tienen texto
  //     title != ""             // descartamos los que no tiene título
  //   }
  // }

  // // Se evita imprimir "Posts from" de un subreddit inválido
  // val validPosts = allPosts.filter { case (_, postList) => postList.nonEmpty }
  // val postsFiltered = validPosts.map { case (url, post_list) => (url, filterPosts(post_list)) }

  // val output = postsFiltered.map { case (url, posts) =>
  //   Formatters.formatSubscription(url, posts) }
  //   .mkString("\n")

  // println(output)
}
