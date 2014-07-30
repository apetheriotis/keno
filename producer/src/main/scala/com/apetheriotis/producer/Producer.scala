package com.apetheriotis.producer

import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime
import java.util.{UUID, Properties}
import kafka.producer.{KeyedMessage, ProducerConfig, Producer}
import com.typesafe.config.ConfigFactory
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

object Producer {

  val envConf = ConfigFactory.load()
  val DATE_FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmm").withZoneUTC()
  val KENO_TOPIC = envConf.getString("kenoTopic")

  def main(args: Array[String]) {

    // Get parameters from input
    if (args.length != 1) {
      System.err.println("Usage: timeToSleep")
      System.exit(1)
    }
    val Array(timeToSleep) = args

    // Zookeeper connection properties
    val props = new Properties()
    props.put("metadata.broker.list", envConf.getString("brokers"))
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("producer.type", "async");
    props.put("queue.enqueue.timeout.ms", "-1");
    props.put("batch.num.messages", "500");

    // Producer configuration
    val config = new ProducerConfig(props)
    val producer = new Producer[String, String](config)

    println(generateTicket())

    // Start sending
    var startTime = 0L
    var numberOfTickets = 0L
    while (true) {
      val webLogs = generateTickets
      producer.send(webLogs: _*)
      numberOfTickets = numberOfTickets + webLogs.size
      if (System.currentTimeMillis() - startTime > 1000) {
        startTime = System.currentTimeMillis()
        println(numberOfTickets + " tickets/s")
        numberOfTickets = 0

      }
      if (timeToSleep.toLong != 0) Thread.sleep(timeToSleep.toLong)
    }
  }


  /**
   * Generates a list of Keno Tickets
   * @return an Array of Keno Tickets
   */
  def generateTickets(): Seq[KeyedMessage[String, String]] = {
    val producerDataList = new ArrayBuffer[KeyedMessage[String, String]]
    for (i <- 0 until 500) {
      producerDataList.append(new KeyedMessage[String, String](KENO_TOPIC, generateTicket))
    }
    producerDataList
  }

  /**
   * Generate a random Keno ticket
   * @return the ticket in plain String
   */
  def generateTicket(): String = {

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
    val weight = 1 + Random.nextInt(10)
    // Setup random numbers
    val numbersInTicket = 1 + Random.nextInt(12)
    var numbers = new ListBuffer[Int]()
    for (index <- 1 to numbersInTicket) numbers += 1 + Random.nextInt(80)

    drawId + "___" + ticketId + "___" + weight + "___" + numbers.mkString(",")
  }


}

