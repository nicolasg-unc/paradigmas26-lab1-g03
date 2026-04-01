object Main {
  type Subscription = (String, String)          // (subredditName, url)
  type Post = (String, String, String, String, Int)  // (subreddit, title, selftext, formattedDate, score)
  def main(args: Array[String]): Unit = {

    val subscriptions: Option[List[Subscription]] = FileIO.readSubscriptions()
    val subList = subscriptions match {
      case Some(sub) => sub
      case None => //Si el .json está malformado, dañado, o no se encuentra, abortar
        println("Error: Couldn't read subcriptions.json. Please check the file and try again.")
        return
    }

    val allPosts: List[(String, List[Post])] = subList.map { case (subredditName, url) =>
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

    def filterPosts(xs: List[Post]): List[Post] = {
      xs.filter { case (_, title, selftext, _, _) =>
        selftext.trim != "" &&  // descartamos los que sólo tienen espacios y los que no tienen texto
        title != ""             // descartamos los que no tiene título
      }
    }

    // Se evita imprimir "Posts from" de un subreddit inválido
    val validPosts = allPosts.filter { case (_, postList) => postList.nonEmpty }
    val postsFiltered = validPosts.map { case (url, post_list) => (url, filterPosts(post_list)) }

    // Para cada suscripción (url, posts), calcula las estadísticas necesarias para el informe:
    // subredditName, score total, frecuencias de palabras y top 5 posts.
    val subscriptionStats: List[
      (String, String, Int, Map[String, Int], List[Post])
    ] = postsFiltered.map {
        case (url, posts) =>
        val subredditName = posts.head._1
        val score = Analytics.totalScore(posts)
        val frequencies = Map("Scala" -> 3, "Reddit" -> 1) // TODO: implementar función wordFrequencies (Ej. 5)
        val top = List(posts.head) // TODO: implementar función topPosts (Ej. 6)
        (url, subredditName, score, frequencies, top)
    }

    val output = postsFiltered.map { case (url, posts) =>
      Formatters.formatSubscription(url, posts) }
      .mkString("\n")

    println(output)
  }
}
