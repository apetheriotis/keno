# Keno
A simple project that demonstrates the use of Spark Streaming with Kafka to implement a [keno-like gambling game](http://www.kenoonline.org) (simplified version). We simulate a draw every 5 minutes and after spark streaming calculates the results of each draw, it sends them to a redis instance and then they are exposed by the rest API (built with spray). 

Use the web UI to submit tickets for the next draw and use the provided ticket id to check the results after draw has completed!

Note that:
* In order to run the full workflow you need to have a working Spark and Kafka setup.
* you need to change .properties file values accordingly.
