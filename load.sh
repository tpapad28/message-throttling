#!/bin/bash
for i in {1..100}
do
   curl -i  -XPUT localhost:8080/messages -d "Blah: $i" -H "Content-type: text/plain"
done
