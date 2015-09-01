# Keno tickets producer
A scala app that produces keno tickets and sends them to a Kafka cluster. Each produced ticket contains the following information:
* drawId (draw id depends on the time the ticket is produced)
* ticketId (a random id for each ticket)
* weight (simulates the bet on the ticket from 1$-10$)
* numbers (the numbers selected in the ticket)
