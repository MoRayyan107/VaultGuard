#!/bin/bash

# Script generated with AI assistance
# Purpose: Test fraud detection API with rate limiting via JWT authentication

# ==========================================
# Configuration
# ==========================================
LOGIN_URL="http://localhost:8080/api/auth/login"
TRANSACTION_URL="http://localhost:8080/api/v1/fraudDetect/processTransaction"
REQUEST_COUNT=50 # Change this to test larger bucket capacities

echo "[1/3] Logging in as alex_analyst..."

# ==========================================
# Step 1: Login & Capture JSON Response
# ==========================================
LOGIN_RESPONSE=$(curl -s -X POST "$LOGIN_URL" \
                   -H "Content-Type: application/json" \
                   -d '{"username": "alex_analyst", "password": "alex@123"}')

# ==========================================
# Step 2: Extract the JWT Token from JSON using grep/sed
# ==========================================
# Extract token from JSON response without jq
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"userJwt":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "❌ CRITICAL: Failed to extract JWT Token!"
  echo "DEBUG: Full login response:"
  echo "$LOGIN_RESPONSE"
  exit 1
fi

echo "✅ Login Successful! Token acquired."
echo "[2/3] Preparing to execute $REQUEST_COUNT concurrent transactions..."
echo "--------------------------------------------------------"

# ==========================================
# Step 3: Hammer the Protected Endpoint
# ==========================================
for ((i=1; i<=REQUEST_COUNT; i++)); do
  # Fire the request and capture the full response including headers
  RESPONSE=$(curl -s -i -X POST "$TRANSACTION_URL" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d '{
      "senderAccountNumber": "ACC-002",
      "recipientAccountNumber": "ACC-002",
      "amount": 100000,
      "transactionType": "TRANSFER",
      "senderLocation": "erhrtreth"
    }')

  # Extract the HTTP Status code (e.g., 200, 429)
  STATUS=$(echo "$RESPONSE" | grep -Fi "HTTP/" | awk '{print $2}')

  # Extract both rate limit headers
  IP_REMAINING=$(echo "$RESPONSE" | grep -i "X-IP-Rate-Limit-Remaining:" | tr -d '\r' | awk '{print $2}')
  USER_REMAINING=$(echo "$RESPONSE" | grep -i "X-User-Rate-Limit-Remaining:" | tr -d '\r' | awk '{print $2}')

  # If rate-limited, extract retry time
  if [ "$USER_REMAINING" = "N/A" ] || [ "$STATUS" = "429" ]; then
    RETRY_AFTER=$(echo "$RESPONSE" | grep -o '"retryAfterSeconds":"[^"]*' | cut -d'"' -f4)
    echo "Request $i | Status: ${STATUS:-000} | Rate Limited - Retry After: ${RETRY_AFTER:-N/A}"
  else
    # Print the result to the console cleanly
    echo "Request $i | Status: ${STATUS:-000} | IP Remaining: ${IP_REMAINING:-N/A} | User Remaining: ${USER_REMAINING:-N/A}"
  fi

done

echo "--------------------------------------------------------"
echo "[3/3] Test complete."
echo "--------------------------------------------------------"