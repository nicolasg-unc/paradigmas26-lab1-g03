import scala.io.StdIn

object Main {
  type IndexedPost = (Int, String, String, String, String, Int, String) // (index, subName, title, selftext, formattedDate, score, urlPost)
  type IndexedSub = (Int, String, String) // (index, subredditName, url)
  type SubscriptionReport = (String, Int, List[(String, Int)], List[IndexedPost])
  // (subredditName, score, frequencies, firstPosts)
  def main(args: Array[String]): Unit = {

    println("\nReddit Post Browser\n")

    val subscriptions: Option[List[PostHandling.Subscription]] = FileIO.readSubscriptions()
    val subList = subscriptions match {
      case Some(sub) => sub
      case None => //Si el .json está malformado, dañado, o no se encuentra, abortar
        println("Error: Couldn't read subcriptions.json. Please check the file and try again.")
        return
    }

    val indexedSubs = subList.zipWithIndex.map { case ((subredditName, url), index) =>
      (index+1, subredditName, url)
    }
    
    Formatters.printSubredditOptions(indexedSubs)
    subredditSelector(readIntSafe(), indexedSubs)

    @scala.annotation.tailrec
    def subredditSelector(choice: Int, indexedSubsInner: List[IndexedSub]): Unit = {
      if (choice == 0) {
        println("\nExiting program.")
      } else if (choice >= 1 && choice <= indexedSubsInner.length) {
        val (_, subredditName, url) = indexedSubsInner(choice - 1)
        val posts = PostHandling.processPosts(List((subredditName, url)))
        val postList = posts.head._2
        if (postList.nonEmpty) {
          val filteredPosts = PostHandling.filterPosts(postList)
          val indexedPosts  = filteredPosts.zipWithIndex.map { case ((subName, title, selftext, formattedDate, score, urlPost), index) =>
            (index+1, subName, title, selftext, formattedDate, score, urlPost)
          }
          Formatters.printPostOptions(indexedPosts)
          postSelector(readIntSafe(), indexedSubsInner, indexedPosts)
        } else {
          Formatters.printSubredditOptions(indexedSubsInner)
          subredditSelector(readIntSafe(), indexedSubsInner)
        }
      } else {
        println("\nInvalid choice, try again.")
        subredditSelector(readIntSafe(), indexedSubsInner)
      }
    }

    @scala.annotation.tailrec
    def postSelector(choice: Int, indexedSubsInner: List[IndexedSub], indexedPostsInner: List[IndexedPost]): Unit = {
      if (choice == 0) {
        println("\nReturning to subreddit selection.\n\n")
        Formatters.printSubredditOptions(indexedSubsInner)
        subredditSelector(readIntSafe(), indexedSubsInner)
      } else if (choice >= 1 && choice <= indexedPostsInner.length) {
        Formatters.printPost(indexedPostsInner(choice - 1))
        postSelector(readIntSafe(), indexedSubsInner, indexedPostsInner)
      } else if (choice == indexedPostsInner.length + 1) {
        val totalScore = Analytics.totalScore(indexedPostsInner)
        val frequencies = Analytics.mapFrequencies(indexedPostsInner)
        val report: SubscriptionReport = (indexedPostsInner.head._2, totalScore, frequencies, indexedPostsInner.take(5))
        println(Formatters.formatReport(report))
        print("\nEnter 0 to return to subreddit selection or another number to \nview another post: ")
        postSelector(readIntSafe(), indexedSubsInner, indexedPostsInner)
      } else {
        print("\nInvalid choice, try again.\n")
        postSelector(readIntSafe(), indexedSubsInner, indexedPostsInner)
      }
    }

    @scala.annotation.tailrec //para que no reviente con " 1"
    def readIntSafe(prompt: String = ""): Int = {
      if (prompt.nonEmpty) print(prompt)
      try {
        StdIn.readInt()
      } catch {
        case _: NumberFormatException =>
          println("Invalid input. Please enter a number.")
          readIntSafe(prompt)
      }
    }
  }
}