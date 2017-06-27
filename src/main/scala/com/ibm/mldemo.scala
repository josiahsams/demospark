package com.ibm

import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.{Pipeline, PipelineStage}
import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer, VectorAssembler}
import org.apache.spark.sql.SparkSession

/**
  * Created by joe on 6/26/17.
  */
object mldemo {

  private def buildOneHotPipeLine(colName:String):Array[PipelineStage] = {
    val stringIndexer = new StringIndexer()
      .setInputCol(s"$colName")
      .setOutputCol(s"${colName}_index")

    val oneHotEncoder = new OneHotEncoder()
      .setInputCol(s"${colName}_index")
      .setOutputCol(s"${colName}_onehotindex")

    Array(stringIndexer,oneHotEncoder)
  }

  def buildPipeLineForFeaturePreparation():Array[PipelineStage] = {

    val trafficTypePipeLineStages = buildOneHotPipeLine("TrafficType")
    val appSiteCategoryPipelineStages = buildOneHotPipeLine("AppSiteCategory")
    val positionPipelineStages = buildOneHotPipeLine("Position")
    val genderStages = buildOneHotPipeLine("Gender")
    val deviceTypeStages = buildOneHotPipeLine("DeviceType")
    val countryStages = buildOneHotPipeLine("Country")
    val creativeTypeStages = buildOneHotPipeLine("CreativeType")
    val creativeCategoryStages = buildOneHotPipeLine("CreativeCategory")
    val zipcodeStages = buildOneHotPipeLine("Zipcode")

    Array.concat(trafficTypePipeLineStages,appSiteCategoryPipelineStages,positionPipelineStages,
      genderStages,deviceTypeStages,countryStages, creativeTypeStages, creativeCategoryStages, zipcodeStages)

  }

  def buildDataPrepPipeLine():Array[PipelineStage] = {

    val pipelineStagesforFeatures = buildPipeLineForFeaturePreparation()

    val assembler = new VectorAssembler()
      .setInputCols(Array("TrafficType_onehotindex", "AppSiteCategory_onehotindex", "Position_onehotindex",
        "Gender_onehotindex","DeviceType_onehotindex", "Country_onehotindex", "CreativeType_onehotindex",
        "CreativeCategory_onehotindex",
        "Age", "BidFloor"))
      .setOutputCol("features")

    val labelIndexer = new StringIndexer()
    //specify options
    labelIndexer.setInputCol("Outcome")
    labelIndexer.setOutputCol("label")

    val pipelineStagesWithAssembler = pipelineStagesforFeatures.toList ::: List(assembler,labelIndexer)

    pipelineStagesWithAssembler.toArray

  }

  def main(args: Array[String]): Unit = {


    val ss = SparkSession.builder().appName("ml demo").master("local[*]").getOrCreate()

    Logger.getRootLogger().setLevel(Level.ERROR)

//    val inputDF = ss.read
//        .option("header", "true")
//        .option("inferSchema", "true")
//      .csv("src/main/resources/dataset_1mb.csv")

    val columns = Seq("BidId","TrafficType","PublisherId","AppSiteId","AppSiteCategory","Position","BidFloor",
      "Timestamp","Age","Gender","OS","OSVersion","Model","Manufacturer","Carrier","DeviceType","DeviceId",
      "DeviceIP","Country","Latitude","Longitude","Zipcode","GeoType","CampaignId","CreativeId",
      "CreativeType","CreativeCategory","ExchangeBid","Outcome"
    )

    val rawinputDF = ss.read
      .option("header", "false")
      .option("inferSchema", "true")
      .csv("src/main/resources/dataset_1gb.csv")

    val rawinputTestDF = ss.read
      .option("header", "false")
      .option("inferSchema", "true")
      .csv("src/main/resources/dataset_1mb.csv")


    val inputDF = rawinputDF.toDF(columns: _*).cache()
    // val testDF = rawinputTestDF.toDF(columns: _*).cache()

//    inputDF.printSchema()

    val pickColDF = inputDF.select("TrafficType", "AppSiteCategory", "Position", "BidFloor",
      "Age", "Gender", "DeviceType", "Country",
      "Zipcode", "CreativeType", "CreativeCategory", "Outcome")


    pickColDF.printSchema()

    val labelIndexer = new StringIndexer()
    //specify options
    labelIndexer.setInputCol("Outcome")
    labelIndexer.setOutputCol("label")

    val labelIndexerTransformer = labelIndexer.fit(pickColDF)
    println("labels are "+ labelIndexerTransformer.labels.toList)

    val splitDF = pickColDF.randomSplit(Array(0.8, 0.2), 42)
    val trainDF = splitDF(0)
    val testDF = splitDF(1)

   //  val trainDF = pickColDF

//    println(inputDF.count())
//    println(trainDF.count())
//    println(testDF.count())

    val pipelineStagesWithAssembler = buildDataPrepPipeLine()

    val pipeline = new Pipeline().setStages(pipelineStagesWithAssembler)

    val featurisedDF = pipeline.fit(pickColDF).transform(trainDF)

    featurisedDF.select("features","label").show(truncate = false)

    val lr = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.01)

    val model = lr.fit(featurisedDF)

    println(model.interceptVector + " "+model.coefficientMatrix)

    val featurisedDFTrain = featurisedDF
    // val featurisedDFTest = pipeline.fit(trainDF).transform(trainDF)
    val featurisedDFTest = pipeline.fit(pickColDF).transform(testDF)

    featurisedDFTest.select("features","label").show(truncate = false)

    //predict

    val testPredictions = model.transform(featurisedDFTest)
    val trainingPredictions = model.transform(featurisedDFTrain)


    val evaluator = new BinaryClassificationEvaluator()
    //Let’s now evaluate our model using Area under ROC as a metric.
    import org.apache.spark.ml.param.ParamMap
    val evaluatorParamMap = ParamMap(evaluator.metricName -> "areaUnderROC")
    val aucTraining = evaluator.evaluate(trainingPredictions, evaluatorParamMap)
    println("AUC Training: " + aucTraining)

    val aucTest = evaluator.evaluate(testPredictions, evaluatorParamMap)
    println("AUC Test: " + aucTest)




  }

}
