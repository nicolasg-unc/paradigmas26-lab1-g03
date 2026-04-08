object PostHandling {
    // (subredditName, urlSub)
    type Subscription = (String, String)
    // (subreddit, title, selftext, formattedDate, score, urlPost)
    type Post = (String, String, String, String, Int, String)

    def processPosts(subList: List[Subscription]): List[(String, List[Post])] = {
        subList.map { case (subredditName, url) =>
            println(s"\nFetching posts from: \"$subredditName\", $url")
            val posts = FileIO.downloadFeed(url)
            posts match {
                case Some(content) =>
                val extractedPosts = FileIO.extractPosts(subredditName, content).flatten
                (url, extractedPosts) // Se ignoran los posts "rotos"
                case None =>
                println(s"Error: Failed to download feed for subreddit $subredditName.\n")
                (url, List())
            }
        }
    }

    def filterPosts(xs: List[Post]): List[Post] = {
        val (validPosts, discardedPosts) = xs.partition { case (_, title, selftext, _, _, _) =>
        selftext.trim != "" &&  // descartamos los que sólo tienen espacios y los que no tienen texto
        title != ""             // descartamos los que no tiene título
        }
        if (discardedPosts.nonEmpty) {
        println(s"${discardedPosts.length} posts were discarded due to empty title or content.\n")
        }
        validPosts
    }
}