#!/bin/bash

# Script generated with AI assistance
# Purpose: Test fraud detection API with rate limiting via JWT authentication

# ==========================================
# Configuration
# ==========================================
REGISTER_URL="http://localhost:8080/api/auth/register"
LOGIN_URL="http://localhost:8080/api/auth/login"
TRANSACTION_URL="http://localhost:8080/api/v1/fraudDetect/processTransaction"
REQUEST_COUNT=100 # Change this to test larger bucket capacities

TEST_USERNAME="ci_user_$(date +%s)_$RANDOM"
TEST_PASSWORD="Alex@12345"
TEST_EMAIL="${TEST_USERNAME}@test.com"

echo "[1/4] Registering test user: ${TEST_USERNAME}..."

# ==========================================
# Step 1: Register user & Capture JSON Response
# ==========================================
REGISTER_RESPONSE=$(curl -s -i -X POST "$REGISTER_URL" \
                   -H "Content-Type: application/json" \
                   -d "{\"username\": \"${TEST_USERNAME}\", \"password\": \"${TEST_PASSWORD}\", \"email\": \"${TEST_EMAIL}\"}")

REGISTER_STATUS=$(echo "$REGISTER_RESPONSE" | grep -Fi "HTTP/" | awk '{print $2}')

if [ "$REGISTER_STATUS" != "200" ] && [ "$REGISTER_STATUS" != "201" ]; then
  echo "❌ CRITICAL: Registration failed with status ${REGISTER_STATUS:-000}"
  echo "DEBUG: Full register response:"
  echo "$REGISTER_RESPONSE"
  exit 1
fi

# ==========================================
# Step 2: Login & Capture JSON Response
# ==========================================
LOGIN_RESPONSE=$(curl -s -X POST "$LOGIN_URL" \
                   -H "Content-Type: application/json" \
                   -d "{\"username\": \"${TEST_USERNAME}\", \"password\": \"${TEST_PASSWORD}\"}")

# ==========================================
# Step 3: Extract the JWT Token from JSON using grep/sed
# ==========================================
# Extract token from JSON response without jq
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"userJwt":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
  echo "❌ CRITICAL: Failed to extract JWT Token!"
  echo "DEBUG: Full register response:"
  echo "$REGISTER_RESPONSE"
  echo "DEBUG: Full login response:"
  echo "$LOGIN_RESPONSE"
  exit 1
fi

echo "✅ Registration successful!"
echo "✅ Login successful! Token acquired."
echo "[2/4] Preparing to execute $REQUEST_COUNT concurrent transactions..."
echo "--------------------------------------------------------"

# ==========================================
# Step 4: Hammer the Protected Endpoint
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
echo "[4/4] Test complete."
echo "--------------------------------------------------------"