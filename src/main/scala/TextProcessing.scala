/*
TextProcessing: transforma y analiza texto como dato: parsear fechas, contar palabras, filtrar stopwords. Lógica de dominio.
*/
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import Domain._

object TextProcessing {
  val stopwords = Set("the", "about", "above", "after", "again", "against", "all", "am", "an",
    "and", "any", "are", "aren't", "arent", "as", "at", "be", "because", "been",
    "before", "being", "below", "between", "both", "but", "by", "can't", "cant",
    "cannot", "could", "couldn't", "couldnt", "did", "didn't", "didnt", "do", "does", "doesn't",
    "doesnt", "doing", "don't", "dont", "down", "during", "each", "few", "for", "from", "further",
    "had", "hadn't", "hadnt", "has", "hasn't", "hasnt", "have", "haven't", "having", "he", "he'd",
    "he'll", "he's", "hes", "her", "here", "here's", "heres", "hers", "herself", "him",
    "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "im", "i've", "ive", "if",
    "in", "into", "is", "isn't", "isnt", "it", "it's", "its", "itself", "let's", "lets", "me",
    "more", "most", "mustn't", "mustnt", "my", "myself", "no", "nor", "not", "of", "off",
    "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves",
    "out", "over", "own", "same", "shan't", "shant", "she", "she'd", "she'll", "she's", "shes",
    "should", "shouldn't", "shouldnt", "so", "some", "such", "than", "that", "that's",
    "thats", "the", "their", "theirs", "them", "themselves", "then", "there", "there's",
    "theres", "these", "they", "they'd", "they'll", "they're", "theyre", "they've", "this",
    "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't",
    "wasnt", "we", "we'd", "we'll", "we're", "we've", "weve", "were", "weren't", "werent",
    "what", "what's", "when", "when's", "whens", "where", "where's", "which", "while", "who",
    "who's", "whom", "why", "why's", "whys", "with", "won't", "wont", "would", "wouldn't",
    "wouldnt", "you", "you'd", "youd", "you'll", "youll", "you're", "youre",
    "you've", "youve", "your", "yours", "yourself", "yourselves")

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

