package com.apetheriotis.spark

import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel
import kafka.serializer.StringDecoder
import com.typesafe.config.ConfigFactory

/**
 * The Driver for the spark engine.
 * Spark setup, stream selection etc..happens here
 */
object KenoDriver {

  val KAFKA_WEB_APP_LOGS_TOPIC = "WebServerLogs"
  val KAFKA_KENO_TOPIC = "KenoTopic"
  val envConf = ConfigFactory.load()
  val kenoManager= new KenoTicketsManager


  def main(args: Array[String]) {

    // Setup kafka parameters
    val kafkaParams = Map[String, String](
      "zookeeper.connect" -> envConf.getString("zookeepers"),
      "group.id" -> "LogTucSpark",
      "zookeeper.connection.timeout.ms" -> "10000",
      "auto.commit.interval.ms" -> "10000",
      "auto.offset.reset" -> "largest")

    // Set logging level
    SparkLogging.setStreamingLogLevels()

    // Create context
    val ssc = new StreamingContext(envConf.getString("sparkMaster"), "Keno-Engine", Seconds(2),
      "/opt/spark/spark-0.9.0-incubating/", StreamingContext.jarOfClass(this.getClass))

    // Fix error "No FileSystem for scheme: hdfs" with the following:
    val hadoopConfig = ssc.sparkContext.hadoopConfiguration
    hadoopConfig.set("fs.hdfs.impl", classOf[org.apache.hadoop.hdfs.DistributedFileSystem].getName)
    hadoopConfig.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName)

    // Database Logs
    val kenoKafkaStreams = (1 to envConf.getInt("noSparkSlaves")).map {
      _ =>
        KafkaUtils.createStream[String, String, StringDecoder,
          StringDecoder](ssc, kafkaParams, Map(KAFKA_KENO_TOPIC -> 1),
            StorageLevel.MEMORY_ONLY_SER_2).map(_._2)
    }
    val kenoStreams = ssc.union(kenoKafkaStreams).repartition(envConf.getInt("noSparkSlaves"))
    kenoManager.processKenoTickets(kenoStreams)

    ssc.start()
    ssc.awaitTermination()
  }


}
