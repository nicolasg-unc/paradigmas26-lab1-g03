object Analytics {
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

  def totalScore(xs: List[Main.IndexedPost]): Int = {
    xs.foldLeft(0)((acc, post) => acc + post._6) // post._6 == score
  }

  def filterWords (xs: List[Main.IndexedPost]): List[String] = {
    xs.flatMap (post =>
      post._4.split(" ").toList
      .map(_.replaceAll("[a-z]+;", ""))
      .map(_.replaceAll("[^a-zA-Z0-9_/]", ""))
      .filterNot(_.isEmpty)
      .filter { word =>
        word.charAt(0).isUpper && !stopwords.contains(word.toLowerCase) 
      }
    )
  }

  def mapFrequencies (xs: List[Main.IndexedPost]): List[(String, Int)] = {
    val words = filterWords(xs)
    words.groupBy(identity).mapValues(_.length).toList.sortBy(-_._2)
  }
}
