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
}