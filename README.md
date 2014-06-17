# Scalding Compressed Tsv

This small word count example shows how to output compressed results from Scalding Tsv, other taps (Csv, TemplateTsv, etc) can be implemented similarly. 

## Running Locally
```
> sbt run

> head output/result.tsv
```

In local mode there is no compression.

## Running on Hadoop
First create 'fat jar' then run on hadoop environment.
```
> sbt assembly
```
It will create fat jar with name **wcc-1.0.jar** under **target/scala-2.10/wcc-1.0.jar**.

Run on hadoop cluster giving proper input/output paths,
```
> hadoop/yarn jar wcc-1.0.jar WordCountRunner --hdfs --input /hdfs/input/path/to/file --output /hdfs/output/path/
```

## More Info

To enable compression we need to change single line in Scalding,
```scala
trait DelimitedScheme extends SchemedSource {
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
      HadoopSchemeInstance(new CHTextDelimited(fields, null, skipHeader, writeHeader, separator, strict, quote, types, safe))
    }
}
```

This line, 
```scala 
HadoopSchemeInstance(new CHTextDelimited(fields, null, skipHeader, writeHeader, separator, strict, quote, types, safe))
```
instead of `null` for second parameter change to Cascading `TextLine.Compress.DEFAULT`,
```scala
HadoopSchemeInstance(new CHTextDelimited(fields, CHTextLine.Compress.DEFAULT, skipHeader, writeHeader, separator, strict, quote, types, safe))
```
