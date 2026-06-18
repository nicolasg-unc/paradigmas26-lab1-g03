import scala.io.Source
import org.json4s._
import org.json4s.jackson.JsonMethods._
import Domain._

object FileIO {
  implicit val formats: Formats = DefaultFormats
  // Pure function to read subscriptions from a JSON file
  def readSubscriptions(path: String): List[Subscription] = {
    // 1. Abrir el archivo
    // 2. Leer como String
    // 3. Parsear JSON
    // 4. Extraer cada (name, url) con map
    ???
  }

  // Pure function to download JSON feed from a URL
  def downloadFeed(url: String): String = {
    val source = Source.fromURL(url)
    source.mkString
  }
}

