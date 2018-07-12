#!/bin/sh

gcloud compute instances create undead-darts-api-instance --image-family debian-9 --image-project debian-cloud --machine-type f1-micro --scopes "userinfo-email,cloud-platform" --metadata-from-file startup-script=instance-startup.sh --metadata BUCKET=undead-darts-api --zone us-east1-b --tags http-server

gcloud compute firewall-rules create default-allow-http-8080 --allow tcp:8080 --source-ranges 0.0.0.0/0 --target-tags http-server --description "Allow port 8080 access to http-server"

# print details including external IP
gcloud compute instances list