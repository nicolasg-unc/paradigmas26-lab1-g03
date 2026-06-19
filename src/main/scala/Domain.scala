object Domain {
  type Subscription = (String, String) // (subreddit, url)
  type Post = (String, String, String, String, Int, String) // (subreddit, title, selftext, formattedDate, score, url)
}

