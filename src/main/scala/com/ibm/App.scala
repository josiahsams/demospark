package com.ibm

import org.apache.spark.sql.SparkSession
import org.apache.log4j.Logger
import org.apache.log4j.Level


/**
 * Hello world!
 *
 */
object App {
  case class Student(name: String, dept: String, age: Long)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    val spark = SparkSession.builder().appName("testProg").master("local").getOrCreate()

    import spark.implicits._

    val dataRDD = spark.range(1, 100, 1, 10)
    val tra = dataRDD.mapPartitions(x=> {
      x.map(_ * 2 )
    })

    // tra.head(10).foreach(println)

    println(tra.head(10).count((_) => true))

    val dataDF = spark.read.json("src/main/resources/data.json")
    dataDF.createOrReplaceTempView("table1")

    dataDF.sqlContext.sql("select * from table1 where dept == 'CS'").show()

    val dataDS = dataDF.as[Student]

    dataDS.printSchema()

    import org.apache.spark.sql.functions._

    dataDS.select('age).withColumn("BirthYear", lit(2017) - $"age").show()
    dataDS.select('age).withColumn("roundedAge", round($"age", -1)).show()

    dataDS.groupBy('dept).avg().show()

    dataDS.describe().show()

    dataDS.filter($"dept" === "CS").show()
  }
}
