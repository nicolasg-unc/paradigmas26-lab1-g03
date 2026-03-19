import scala.io.Source

object FileIO {
  /* Pure function to read subscriptions from a JSON file */
  def readSubscriptions(): List[Main.Subscription] = {
    val source = Source.fromFile("./subscriptions.json")
    val lines = source.getLines().toList
    val filtered_lines = lines.filter(line => line.contains("name") || line.contains("url"))
    source.close()
    val output = filtered_lines.grouped(2).map { line =>
      val name = line(0).split(":", 2)(1).trim.stripPrefix("\"").stripSuffix("\",")
      val url = line(1).split(":", 2)(1).trim.stripPrefix("\"").stripSuffix("\"")
      (name, url)
    }.toList
    output
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): String = {
    val source = Source.fromURL(url)
    source.mkString
  }
}
