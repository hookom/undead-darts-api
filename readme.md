# Deployment

## GCLOUD AUTH LOCAL (for new machine or when logged out)
gcloud auth application-default login

## Create bucket (should already be there)
gsutil mb gs://undead-darts-api

## Build app
gradle build

## Copy built jar to bucket
gsutil cp build/libs/* gs://undead-darts-api/undeaddarts.jar

## Restart compute VM instance (manual)

