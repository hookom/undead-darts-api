# Deployment

## Create bucket
gsutil mb gs://undead-darts-api

## Build app
gradle build

## Copy built jar to bucket
gsutil cp build/libs/* gs://undead-darts-api/undeaddarts.jar

## Restart compute VM instance (manual)