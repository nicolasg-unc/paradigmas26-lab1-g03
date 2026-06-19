/*
TextProcessing: transforma y analiza texto como dato: parsear fechas, contar palabras, filtrar stopwords. Lógica de dominio.
*/
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TextProcessing {
  def formatDateFromUTC(utcSeconds: Long): String = {
    DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm")
      .withZone(ZoneId.of("UTC"))
      .format(Instant.ofEpochSecond(utcSeconds))
  }

  def isValidPost(post: Post): Boolean = { // (subreddit, title, selftext, formattedDate)
    post._2.trim.nonEmpty && // título no vacío ni solo espacios
      post._3.trim.nonEmpty  // selftext no vacío ni solo espacios
  }
}

