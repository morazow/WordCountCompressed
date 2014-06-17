package com.morazow.wordcount

import com.twitter.scalding.{Job, Args, TextLine}

class WordCountJob(args : Args) extends Job(args) {

  override def config: Map[AnyRef,AnyRef] = {
    super.config ++ Map (
      "mapred.output.compress" -> "true",
      "mapred.output.compress.type" -> "BLOCK",
      "mapred.output.compress.codec" -> "org.apache.hadoop.io.compress.GzipCodec",
      "mapreduce.output.fileoutputformat.compress" -> "true",
      "mapreduce.output.fileoutputformat.compress.codec" -> "org.apache.hadoop.io.compress.GzipCodec",
      "mapreduce.output.fileoutputformat.compress.type" -> "BLOCK"
      )
  }

  TextLine( args("input") )
    .flatMap('line -> 'word) { line : String => line.split("""\s+""") }
    .groupBy('word) { _.size }
    .write(CompressedTsv( args("output") ) )
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
