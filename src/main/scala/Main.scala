import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.io.Source


object Main {
  type Subscription = (String, String) // (name, url)
  type Post = (String, String, String) // (title, selftext, author)

  def readSubscriptions(path: String): List[Option[Subscription]] = {
    val source = Source.fromFile(path)
    val jsonString = source.mkString
    implicit val formats: Formats = DefaultFormats

    val json = parse(jsonString)
    val subscriptions = json.extract[List[Map[String, String]]].map { subscriptionMap =>
      try {
        val name = subscriptionMap("name")
        val before = subscriptionMap("before")
        val count = subscriptionMap("count")
        val urlOriginal = subscriptionMap("url")
        val url = s"${urlOriginal}?count=${count}&before=${before}"
        Some((name, url))
      } catch {
        case _: Exception => None
      } finally {
        source.close()
      }
    }
    subscriptions
  }


  def readPosts(url: String): List[Option[Post]] = {
    try {
      val source = Source.fromURL(url)
      val jsonContent = source.mkString
      implicit val formats: Formats = DefaultFormats

      val json = parse(jsonContent)
      val children = (json \ "data" \ "children").extract[List[JValue]]

      children.map { child =>
        val data = child \ "data"
        val title = (data \ "title").extract[String]
        val selftext = (data \ "selftext").extract[String]
        val author = (data \ "author").extract[String]
        Some((title, selftext, author))
      }
    } catch {
      case _: Exception => List(None)
    }
  }

  val keyWords = List("LLM", "LLMs", "AI", "ChatGPT", "Copilot", "Claude", "ML", "Gemini", "agent", "agentic").map (_.toLowerCase)

  //Contar las palabras del título y del cuerpo que están en la siguiente lista sin tener en cuenta mayúsculas.
  def countWords(post: Post): Int = {
    val whereToCount = (post._1 + " " + post._2).toLowerCase.split(" ").toList
    val filteredWords = whereToCount.filter {word => keyWords.contains(word)}
    filteredWords.length
  }

  def printPost(post: Post): Unit = {
    val content = post._2
    val truncatedContent = if (content.length > 80) content.take(80) else content
    val wordCount = countWords(post)
    println(s"\t${post._1} by **${post._3}**")
    println(s"\tContenido: ${truncatedContent}")
    println(s"\tPalabras censuradas: ${wordCount}")
    println("\t-----------------------")
  }

  def printSubscription(posts: (String, List[Post])): Unit = {
    val url = posts._1
    println(s"Posts from: $url")
    posts._2.map(printPost)
  }

  // Main function to run
  def main(args: Array[String]): Unit = {
    val header = s"Reddit Post Parser\n${"=" * 40}"
    println("=======================")
    println("EJ1: LEER SUSCRIPCIONES")
    val subscriptions: List[Option[Subscription]] = readSubscriptions("subscriptions.json")

    // Print subscriptions read - We can use imperative for I/O
    for (subscription <- subscriptions) {
      subscription match {
        case Some(s: Subscription) => println(s._2)
        case None => println("Error: Could not load suscriptions.")
      }
    }

    println("=======================")
    println("")
    println("=======================")
    println("EJ2: DESCARGAR POSTS")

    val myAllPosts: List[(String, List[Option[Post]])] = subscriptions.map {
      sub => sub match {
        case Some(s) => (s._2, readPosts(s._2))
        case None => ("", List())
      }
    }.filter {case (_, postList) => !postList.isEmpty}

    // Descargar y parsear los posts
    val allPosts: List[(String, List[Post])] = myAllPosts.map {
      case (url, postList) =>
        val extractedPosts = postList.flatten
        (url, extractedPosts)
    }

    println("=======================")
    println("")
    println("=======================")
    println("EJ3: IMPRIMIR POSTS Y CONTEO DE PALABRAS CENSURADAS")

    // Print final results
    allPosts.map(printSubscription)
    println("=======================")
  }
}
