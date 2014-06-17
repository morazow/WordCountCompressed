package com.morazow.wordcount

import com.twitter.scalding._

class WordCountJob(args : Args) extends Job(args) {
  TextLine( args("input") )
    .flatMap('line -> 'word) { line : String => line.split("""\s+""") }
    .groupBy('word) { _.size }
    .write( Tsv( args("output") ) )
}

object WordCountRunner {
  import org.apache.hadoop.util.ToolRunner
  import org.apache.hadoop.conf.Configuration
  import com.twitter.scalding.Tool

  def main(args: Array[String]) {
    if (args.length != 0) {
      // run on cluster, hadoop mode
      ToolRunner.run(new Configuration, new Tool, "com.morazow.wordcount.WordCountJob" +: args)
    } else {
      // run locally
      ToolRunner.run(new Configuration, new Tool,
        Array(
          "com.morazow.wordcount.WordCountJob",
          "--local" ,
          "--input" , "data/words.txt",
          "--output", "output/result.tsv")
        )
    }
  }
}
