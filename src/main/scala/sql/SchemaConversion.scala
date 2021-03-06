package sql

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

import org.apache.spark.sql._

import scala.collection.immutable

case class CC(i: Integer)



object SchemaConversion {
  val conf = new SparkConf().setAppName("SchemaConversion").setMaster("local[4]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  def toSchemaRDD(o: RDD[CC]) : SchemaRDD = {
    val schema = StructType(
      Seq(StructField("i", IntegerType, true)))
    val rows = o.map(cc => Row(cc.i))
    sqlContext.applySchema(rows, schema)
  }

  def main (args: Array[String]) {


    val nums = 1 to 100
    val data = sc.parallelize(nums.map(i => CC(i)), 4)
    val sdata = toSchemaRDD(data)

    sdata.registerTempTable("mytable")

    val results = sqlContext.sql("SELECT COUNT(i) FROM mytable WHERE i > 50")

    results.foreach(println)



  }
}
