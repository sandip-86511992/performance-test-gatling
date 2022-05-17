package com.perftest

import com.perftest.mailer.PropertyController
import com.perftest.metrics.{MetricsContainer, MetricsLoader, MetricsProcessor, ResultWritter}
import com.perftest.reporting.{InfluxLoader, ReportGenerator, ResultContainer}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by sbhattacharjee on 11/11/2017.
  */
object Test {
  def updateData(args: Array[String]): Unit = {
    println(PropertyController.properties("INFLUX_DB_URL"))
   /* ReportGenerator.updateResults()
   /* InfluxLoader.uploadActiveUsers()
    InfluxLoader.uploadRequestDist()
    InfluxLoader.uploadResponseDist()*/
   // ReportGenerator.generateReport()
    MetricsLoader.loadMetrics()
    MetricsProcessor.processMetrics()
    ResultWritter.writeResult()*/
 /*   println(MetricsContainer.getMetricsContainer()(0).endpoint)
    println(MetricsContainer.getMetricsContainer()(0).responseTime)
    println(MetricsContainer.getMetricsContainer()(0).responsePercentage)
    println(MetricsContainer.getMetricsContainer()(0).passPercentage)
    println(MetricsContainer.getMetricsContainer()(1).endpoint)
    println(MetricsContainer.getMetricsContainer()(1).responseTime)
    println(MetricsContainer.getMetricsContainer()(1).responsePercentage)
    println(MetricsContainer.getMetricsContainer()(1).passPercentage)*/
    ReportGenerator.updateResults()
    InfluxLoader.uploadActiveUsers()
    InfluxLoader.uploadRequestDist()
    InfluxLoader.uploadResponseDist()
    InfluxLoader.uploadResponseTimeDist()
  }

}
