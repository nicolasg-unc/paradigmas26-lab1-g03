import scala.io.StdIn

object Main {
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
    
    print(indexedSubs.map { case (index, subredditName, url) => s"[$index] $subredditName ($url)" }.mkString("\n"))
    print("\n\nEnter the corresponding number to the subreddit you'd like to browse: ")
    subredditSelector(readIntSafe(), indexedSubs)

    @scala.annotation.tailrec
    def subredditSelector(choice: Int, indexedSubsInner: List[(Int, String, String)]): Unit = {
      if (choice == 0) {
        println("\nExiting program.")
      } else if (choice >= 1 && choice <= indexedSubsInner.length) {
        val (_, subredditName, url) = indexedSubsInner(choice - 1)

        val posts = PostHandling.processPosts(List((subredditName, url)))
        val postList = posts.head._2
        val indexedPosts  = postList.zipWithIndex.map { case ((_, title, selftext, formattedDate), index) =>
          (index+1, title, selftext, formattedDate)
        }
        if (!(indexedPosts.isEmpty)) {
          println(s"\nPosts from $subredditName:")
          println(indexedPosts.map { case (index, title, _, formattedDate) => s"[$index] $title ($formattedDate)" }.mkString("\n"))
        }
        print("\n\nEnter the corresponding number to the post you'd like to open: ")
        postSelector(readIntSafe(), indexedSubsInner, indexedPosts)
      } else {
        println("\nInvalid choice, try again.")
        subredditSelector(readIntSafe(), indexedSubsInner)
      }
    }

    @scala.annotation.tailrec
    def postSelector(choice: Int, indexedSubsInner: List[(Int, String, String)], indexedPostsInner: List[(Int, String, String, String)]): Unit = {
      if (choice == 0) {
        println("\nReturning to subreddit selection.\n\n")
        println(indexedSubsInner.map { case (index, subredditName, url) => s"$index. $subredditName ($url)" }.mkString("\n"))
        print("\nEnter the corresponding number to the subreddit you'd like to browse, or 0 to exit: ")
        subredditSelector(readIntSafe(), indexedSubsInner)
      } else if (choice >= 1 && choice <= indexedPostsInner.length) {
        val (_, title, selftext, formattedDate) = indexedPostsInner(choice - 1)
        println(s"\nTitle: $title\nDate: $formattedDate\n\n$selftext\n")
        print("Enter 0 to return to subreddit selection or another number to view another post: ")
        postSelector(readIntSafe(), indexedSubsInner, indexedPostsInner)
      } else {
        print("Invalid choice, try again.\n")
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
