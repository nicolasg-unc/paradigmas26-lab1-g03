object Domain {
  type Subscription = (String, String) // (subreddit, url)
  type Post = (String, String, String, String) // (subreddit, title, selftext, formattedDate)
}

