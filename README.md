# message-throttling

Throttle message delivery from multiple sources using a fixed rate

## Description

Sample application to demonstrate throttling of messages received in arbitrary rate from a REST endpoint to a fixed (enforced) rate.
Technologies used: Java, Quarkus, Redis

## Usage

You can try out this Proof-of-Concept:

0. Install mvnw (Maven wrapper) using `mvn -N io.takari:maven:0.7.7:wrapper`
1. Run the app using Quarkus `./mvnw compile quarkus:dev`
2. Run the load simulator: `./load.sh`
