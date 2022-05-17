package com.perftest.reporting

import com.perftest.mailer.PropertyController

import scala.collection.mutable.ArrayBuffer
import java.io.OutputStreamWriter
import java.net.{HttpURLConnection, URL}

/**
  * Created by sbhattacharjee on 11/12/2017.
  */

object InfluxLoader {
  var serviceUrl = PropertyController.properties("INFLUX_DB_URL")
  val dbUsername = PropertyController.properties("DB_USERNAME")
  val dbPassword = PropertyController.properties("DB_PASSWORD")
  val dbName = PropertyController.properties("DB_NAME")

  def uploadActiveUsers(): Unit ={
    var payload = ""
    for (testCaseResult <- ResultContainer.getResultContainer) {
      val timeMapKey = new ArrayBuffer[Long]()
      val timeMapValue = new ArrayBuffer[Int]()
      val startTimes = testCaseResult.startTime
      val endTimes = testCaseResult.endTime
      val sortedStartTimes = startTimes.sorted
      val sortedEndTimes = endTimes.sorted
      val startTimestamp = sortedStartTimes(0).toLong - sortedStartTimes(0).substring(11).toLong -100
      val endTimestamp = sortedEndTimes(sortedEndTimes.size - 1).toLong - sortedEndTimes(sortedEndTimes.size - 1).substring(11).toLong + 100
      var timestamp = startTimestamp
      while (timestamp <= endTimestamp) {
        timeMapKey += (timestamp)
        timeMapValue += 0
        timestamp = timestamp + 100
      }
      for (i <- 0 until timeMapKey.size) {
        for (j <- 0 until startTimes.size) {
          val startTime = startTimes(j).toLong
          val endTime = endTimes(j).toLong
          if (startTime < timeMapKey(i) && endTime > timeMapKey(i))
            timeMapValue(i) += 1
        }
      }
      for (i <- 0 until timeMapKey.size)
        payload = payload + "active_users" + ",type=" + testCaseResult.testCaseName.replace(" ","_") + " " + "activeusers" + "=" + timeMapValue(i).toString + " " + timeMapKey(i).toString + "000000" + "\n"

    }
    if (serviceUrl.endsWith("/")) {
      serviceUrl = serviceUrl.substring(0, serviceUrl.length - 1)
    }
    val obj1:URL = new URL(serviceUrl + "/write?db=" + dbName)
    val con1 = obj1.openConnection().asInstanceOf[HttpURLConnection]
    con1.setRequestMethod("POST")
    con1.setDoOutput(true)
    val wr: OutputStreamWriter = new OutputStreamWriter(con1.getOutputStream)
    wr.write(payload)
    wr.flush()
    con1.getResponseCode
  }

  def uploadRequestDist(): Unit ={
    var payload = ""
    for (testCaseResult <- ResultContainer.getResultContainer) {
      val timeMapKey = new ArrayBuffer[Long]()
      val requestCount = new ArrayBuffer[Int]()
      val startTimes = testCaseResult.startTime
      val status = testCaseResult.status
      val sortedStartTimes = startTimes.sorted
      val startTimestamp = sortedStartTimes(0).toLong - sortedStartTimes(0).substring(10).toLong -1000
      val endTimestamp = sortedStartTimes(sortedStartTimes.size - 1).toLong - sortedStartTimes(sortedStartTimes.size - 1).substring(10).toLong + 2000
      println(startTimestamp)
      println(endTimestamp)
      var timestamp = startTimestamp
      while (timestamp <= endTimestamp) {
        timeMapKey += (timestamp)
        requestCount += 0
        timestamp = timestamp + 1000
      }
      for (i <- 0 until timeMapKey.size) {
        for (j <- 0 until startTimes.size) {
          val startTime = startTimes(j).toLong
          if (startTime < timeMapKey(i)&&startTime > (timeMapKey(i)-1000)) {
            requestCount(i) += 1
          }
        }
      }
      for (i <- 0 until timeMapKey.size)
        payload = payload + "request_dist" + ",type=" + testCaseResult.testCaseName.replace(" ","_") + " " + "request_count" + "=" + requestCount(i).toString + " " + timeMapKey(i).toString + "000000" + "\n"

    }
    if (serviceUrl.endsWith("/")) {
      serviceUrl = serviceUrl.substring(0, serviceUrl.length - 1)
    }
    val obj1:URL = new URL(serviceUrl + "/write?db=" + dbName)
    val con1 = obj1.openConnection().asInstanceOf[HttpURLConnection]
    con1.setRequestMethod("POST")
    con1.setDoOutput(true)
    val wr: OutputStreamWriter = new OutputStreamWriter(con1.getOutputStream)
    wr.write(payload)
    wr.flush()
    con1.getResponseCode
  }

  def uploadResponseDist(): Unit ={
    var payload = ""
    for (testCaseResult <- ResultContainer.getResultContainer) {
      val timeMapKey = new ArrayBuffer[Long]()
      val responseCount = new ArrayBuffer[Int]()
      val responseCountOK = new ArrayBuffer[Int]()
      val responseCountKO = new ArrayBuffer[Int]()
      val endTimes = testCaseResult.endTime
      val status = testCaseResult.status
      val sortedEndTimes = endTimes.sorted
      val startTimestamp = sortedEndTimes(0).toLong - sortedEndTimes(0).substring(10).toLong -1000
      val endTimestamp = sortedEndTimes(sortedEndTimes.size - 1).toLong - sortedEndTimes(sortedEndTimes.size - 1).substring(10).toLong + 2000
      var timestamp = startTimestamp
      while (timestamp <= endTimestamp) {
        timeMapKey += (timestamp)
        responseCount += 0
        responseCountOK += 0
        responseCountKO += 0
        timestamp = timestamp + 1000
      }
      for (i <- 0 until timeMapKey.size) {
        for (j <- 0 until endTimes.size) {
          val endTime = endTimes(j).toLong
          if (endTime < timeMapKey(i)&&endTime > (timeMapKey(i)-1000)) {
            responseCount(i) += 1
            if(status(j).equals("OK"))
              responseCountOK(i) += 1
            else
              responseCountKO(i) += 1
          }
        }
      }
      for (i <- 0 until timeMapKey.size) {
        payload = payload + "response_dist" + ",type=" + testCaseResult.testCaseName.replace(" ", "_") + " " + "response_count" + "=" + responseCount(i).toString + " " + timeMapKey(i).toString + "000000" + "\n"
        payload = payload + "response_dist" + ",type=" + testCaseResult.testCaseName.replace(" ", "_")+"_OK" + " " + "response_count" + "=" + responseCountOK(i).toString + " " + timeMapKey(i).toString + "000000" + "\n"
        payload = payload + "response_dist" + ",type=" + testCaseResult.testCaseName.replace(" ", "_")+"_KO" + " " + "response_count" + "=" + responseCountKO(i).toString + " " + timeMapKey(i).toString + "000000" + "\n"
      }

    }
    if (serviceUrl.endsWith("/")) {
      serviceUrl = serviceUrl.substring(0, serviceUrl.length - 1)
    }
    val obj1:URL = new URL(serviceUrl + "/write?db=" + dbName)
    val con1 = obj1.openConnection().asInstanceOf[HttpURLConnection]
    con1.setRequestMethod("POST")
    con1.setDoOutput(true)
    val wr: OutputStreamWriter = new OutputStreamWriter(con1.getOutputStream)
    wr.write(payload)
    wr.flush()
    con1.getResponseCode
  }


  def uploadResponseTimeDist(): Unit = {
    var payload = ""
    for (testCaseResult <- ResultContainer.getResultContainer) {
      var timeIntervalMap = new ArrayBuffer[Int]()
      for (i <- 1 to 20) {
        timeIntervalMap += (i * 1000)
      }
      timeIntervalMap += 21000
      var numberOfUserMap = new ArrayBuffer[Int]()
      var timeStampMap = new ArrayBuffer[Long]()
      var startTime = 1514764800000L
      for (i <- 0 until timeIntervalMap.size) {
        numberOfUserMap += 0
        startTime += 1000
        timeStampMap += startTime
      }
      println(timeIntervalMap.size)
      val startTimes = testCaseResult.startTime
      val endTimes = testCaseResult.endTime
      for (i <- 0 until startTimes.size) {
        val responseTime = endTimes(i).toLong - startTimes(i).toLong
        val responseTimeInSecond = (responseTime / 1000).toInt
        numberOfUserMap(responseTimeInSecond) += 1
      }
      for (i <- 0 until timeIntervalMap.size) {
        println(timeIntervalMap(i) + "     " + numberOfUserMap(i) + "     " + timeStampMap(i))
      }

      for (i <- 0 until timeIntervalMap.size) {
        payload = payload + "response_time" + ",API=" + testCaseResult.testCaseName.replace(" ", "_") + " " + "responsetime=" + timeIntervalMap(i)/1000 + " " + (timeStampMap(i)+500).toString + "000000" + "\n"
        payload = payload + "response_time" + ",API=" + testCaseResult.testCaseName.replace(" ", "_") + " " + "numberofuser=" + numberOfUserMap(i) + " " + timeStampMap(i).toString + "000000" + "\n"
      }
    }
    if (serviceUrl.endsWith("/")) {
      serviceUrl = serviceUrl.substring(0, serviceUrl.length - 1)
    }
    val obj1: URL = new URL(serviceUrl + "/write?db=" + dbName)
    val con1 = obj1.openConnection().asInstanceOf[HttpURLConnection]
    con1.setRequestMethod("POST")
    con1.setDoOutput(true)
    val wr: OutputStreamWriter = new OutputStreamWriter(con1.getOutputStream)
    wr.write(payload)
    wr.flush()
    println(con1.getResponseMessage)
  }
}
