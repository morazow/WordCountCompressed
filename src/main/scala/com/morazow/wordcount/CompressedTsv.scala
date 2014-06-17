package com.morazow.wordcount

import com.twitter.scalding.{SchemedSource, FixedPathSource}

import cascading.tap.SinkMode
import cascading.tuple.Fields
import cascading.scheme.Scheme
import cascading.scheme.local.{TextDelimited => CLTextDelimited}
import cascading.scheme.hadoop.{TextLine => CHTextLine, TextDelimited => CHTextDelimited}
import org.apache.hadoop.mapred.{OutputCollector, RecordReader, JobConf}


/**
 * This is exactly same as DelimetedScheme from Scalding
 * only modify 'hdfsScheme'
 */
trait CompressedDelimitedScheme extends SchemedSource {
  //override these as needed:
  val fields = Fields.ALL
  //This is passed directly to cascading where null is interpretted as string
  val types : Array[Class[_]] = null
  val separator = "\t"
  val skipHeader = false
  val writeHeader = false
  val quote : String = null

  // Whether to throw an exception or not if the number of fields does not match an expected number.
  // If set to false, missing fields will be set to null.
  val strict = true

  // Whether to throw an exception if a field cannot be coerced to the right type.
  // If set to false, then fields that cannot be coerced will be set to null.
  val safe = true

  //These should not be changed:
  override def localScheme = new CLTextDelimited(fields, skipHeader, writeHeader, separator, strict, quote, types, safe)

  override def hdfsScheme = {
    /**
     * Cascading TextDelimited parameters
     *
     *     fields - of type Fields
     *     sinkCompression - of type Compress
     *     skipHeader - of type boolean
     *     delimiter - of type String
     *     strict - of type boolean
     *     quote - of type String
     *     types - of type Class[]
     *     safe - of type boolean
     *
     * where 'sinkCompression' is TextLine.Compress.{DEFAULT, DISABLE, ENABLE}
     *
     * Use 'ENABLE' always compress the result, 'DEFAULT' only compresses when config set true.
     */

    val tmp = new CHTextDelimited(fields, CHTextLine.Compress.DEFAULT, skipHeader, writeHeader, separator, strict, quote, types, safe)
    tmp.asInstanceOf[Scheme[JobConf, RecordReader[_, _], OutputCollector[_, _], _, _]]

    // old Scalding code
    // HadoopSchemeInstance(new CHTextDelimited(fields, null, skipHeader, writeHeader, separator, strict, quote, types, safe))
  }
}

/**
 * Compressed Tsv
 */
case class CompressedTsv(p : String,
  override val fields : Fields = Fields.ALL,
  override val skipHeader : Boolean = false,
  override val writeHeader: Boolean = false,
  override val sinkMode: SinkMode = SinkMode.REPLACE) extends FixedPathSource(p) with CompressedDelimitedScheme
