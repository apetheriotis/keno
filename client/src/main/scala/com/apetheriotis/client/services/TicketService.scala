package com.apetheriotis.client.services

import com.apetheriotis.client.dao.AbstractDao
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import com.apetheriotis.client.dto.{CheckTicketRs, SubmitTicketRs}
import java.util.{UUID, Properties}
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}

/**
 * Provides services to submit tickets and retrieve tickets data
 */
class TicketService extends AbstractDao {
  val DATE_FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmm").withZoneUTC()
  val KENO_TOPIC = envConf.getString("kenoTopic")

  // Zookeeper connection properties
  val props = new Properties()
  props.put("metadata.broker.list", envConf.getString("brokers"))
  props.put("serializer.class", "kafka.serializer.StringEncoder")
  props.put("producer.type", "async")
  props.put("queue.enqueue.timeout.ms", "-1")
  props.put("batch.num.messages", "500")
  // Producer configuration
  val config = new ProducerConfig(props)
  val producer = new Producer[String, String](config)

  val rsService = new ResultsService

  /**
   * Checks for ticket's winnings if any
   * @param ticketId the id of the ticket to check
   * @return CheckTicketRs. If SubmitTicketRs.drawId=-1 indicates results are not yet ready
   **/
  def checkTicketRs(ticketId: String): CheckTicketRs = {
    val ticketData = jedis.get(ticketId).split("___")
    val numbers = ticketData(3).split(",").toList.map(x => x.toInt)
    val drawIdForTicket = DATE_FORMATTER.parseDateTime(ticketData(0))
    val latestDrawId = DrawIdHelper.latestDrawId

    // Check if results are ready...
    if (drawIdForTicket.isAfter(latestDrawId)) {
      CheckTicketRs("-1", numbers, -1)
    } else {
      val drawRs = rsService.setupLatestResults(drawIdForTicket)
      val numbersInRs = for (a <- drawRs.numbers if numbers.contains(a)) yield {
        a
      }
      CheckTicketRs(ticketData(1), numbersInRs, 100)
    }
  }

  /**
   * Submits new ticket
   * @param weight the weight
   * @param numbers the numbers in the ticket
   * @return the SubmitTicketRs
   */
  def submitTicket(weight: Int, numbers: List[Int]): SubmitTicketRs = {
    val ticketData = generateTicket(weight, numbers)
    val message = new KeyedMessage[String, String](KENO_TOPIC, ticketData._1)
    jedis.set(ticketData._2.ticketNo, ticketData._1)
    producer.send(message)
    ticketData._2
  }

  /**
   * Create the kafka message and the response
   * @param weight the weight
   * @param numbers the numbers in the ticket
   * @return the ticket in plain String and the SubmitTicketRs
   */
  private def generateTicket(weight: Int, numbers: List[Int]): (String, SubmitTicketRs) = {
    // Setup draw id
    var nowTime = new DateTime().withSecondOfMinute(59)
    val minutesToAdd = nowTime.getMinuteOfHour % 10
    if (minutesToAdd > 4) {
      nowTime = nowTime.plusMinutes(10 - minutesToAdd)
    } else {
      nowTime = nowTime.plusMinutes(5 - minutesToAdd)
    }
    val drawId = DATE_FORMATTER.print(nowTime)
    // Setup ticket id and weight
    val ticketId = UUID.randomUUID().toString
    (drawId + "___" + ticketId + "___" + weight + "___" + numbers.mkString(","),
      SubmitTicketRs(drawId, ticketId, numbers, weight))
  }


}
