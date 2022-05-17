package com.perftest.frameworkCore

import com.perftest.caseClasses.Step
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.action.async.ws.WsCloseBuilder

/**
  * Created by sbhattacharjee on 11/20/2017.
  */
class WSCloseRequest (step : Step) {

  private var requestName = step.title
  private var request = ws(requestName)
  private var wsRequest : WsCloseBuilder = null

  wsRequest = request.close

  def getWsRequest(): WsCloseBuilder = {
    wsRequest
  }


}
