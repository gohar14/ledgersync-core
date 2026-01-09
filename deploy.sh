#!/bin/bash

# 0. Set the production database credentials
export DB_URL="jdbc:postgresql://ep-divine-cake-a1skme87-pooler.ap-southeast-1.aws.neon.tech:5432/neondb?sslmode=require"
export DB_USER="neondb_owner"
export DB_PASSWORD="npg_Gri1QqPafyn0"

# 1. Build the JAR locally on your Mac Mini M4
echo "Building LedgerSync JAR..."
./mvnw clean package -DskipTests

# 2. Check if the build actually created a JAR
if [ ! -f target/*.jar ]; then
    echo "Error: No JAR file found in target directory. Build failed?"
    exit 1
fi

# 3. Submit the build to Google Cloud
echo "Uploading to Google Cloud Build..."
gcloud builds submit --tag asia-southeast1-docker.pkg.dev/ledgersync-production/ledgersync-repo/ledgersync-app:latest .

# 4. Deploy to Cloud Run
echo "Deploying to Cloud Run..."
gcloud run deploy ledgersync-core \
  --memory 2Gi \
  --timeout 300 \
  --image asia-southeast1-docker.pkg.dev/ledgersync-production/ledgersync-repo/ledgersync-app:latest \
  --region asia-southeast1 \
  --platform managed \
  --allow-unauthenticated \
  --set-env-vars="DB_URL=$DB_URL,DB_USER=$DB_USER,DB_PASSWORD=$DB_PASSWORD,SPRING_PROFILES_ACTIVE=prod"
