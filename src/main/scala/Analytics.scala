object Analytics {
  def totalScore(xs: List[Main.Post]): Int = {
    xs.foldLeft(0)((acc, post) => acc + post._5) // post._5 == score
  }
}
