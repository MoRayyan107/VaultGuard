#!/bin/bash

# Script generated with AI assistance
# Purpose: Test rate limiting with concurrent registration and login spam (800 requests)

# ==========================================
# Configuration
# ==========================================
LOGIN_URL="http://localhost:8080/api/auth/login"
REGISTER_URL="http://localhost:8080/api/auth/register"
REQUEST_COUNT=800  # 800 spam requests

echo "=========================================="
echo "Starting Auth Spam Test (800 requests)"
echo "=========================================="
echo ""

# Counter for successful and rate-limited requests
SUCCESS_COUNT=0
RATE_LIMITED_COUNT=0
ERROR_COUNT=0

# ==========================================
# Test 1: Register Spam (400 requests)
# ==========================================
echo "[1/2] Running REGISTER spam (400 requests)..."
for ((i=1; i<=400; i++)); do
  # Generate unique username and email for each request
  USERNAME="testuser_$RANDOM"
  EMAIL="user_$RANDOM@test.com"
  
  RESPONSE=$(curl -s -i -X POST "$REGISTER_URL" \
    -H "Content-Type: application/json" \
    -d "{
      \"username\": \"$USERNAME\",
      \"password\": \"Test@123456\",
      \"email\": \"$EMAIL\"
    }")

  # Extract status
  STATUS=$(echo "$RESPONSE" | grep -Fi "HTTP/" | awk '{print $2}')
  
  # Extract rate limit header
  IP_REMAINING=$(echo "$RESPONSE" | grep -i "X-IP-Rate-Limit-Remaining:" | tr -d '\r' | awk '{print $2}')
  
  if [ "$STATUS" = "429" ]; then
    RETRY_AFTER=$(echo "$RESPONSE" | grep -o '"retryAfterSeconds":"[^"]*' | cut -d'"' -f4)
    echo "Register $i | Status: 429 | Retry After: ${RETRY_AFTER:-N/A}"
    ((RATE_LIMITED_COUNT++))
  elif [ "$STATUS" = "200" ] || [ "$STATUS" = "201" ]; then
    echo "Register $i | Status: ${STATUS:-000} | IP Remaining: ${IP_REMAINING:-N/A} ✅"
    ((SUCCESS_COUNT++))
  else
    echo "Register $i | Status: ${STATUS:-000} | ERROR"
    ((ERROR_COUNT++))
  fi
done

echo ""
echo "=========================================="
echo "[2/2] Running LOGIN spam (400 requests)..."
for ((i=1; i<=400; i++)); do
  # Use consistent test user for login
  RESPONSE=$(curl -s -i -X POST "$LOGIN_URL" \
    -H "Content-Type: application/json" \
    -d '{
      "username": "alex_analyst",
      "password": "alex@123"
    }')

  # Extract status
  STATUS=$(echo "$RESPONSE" | grep -Fi "HTTP/" | awk '{print $2}')
  
  # Extract rate limit header
  IP_REMAINING=$(echo "$RESPONSE" | grep -i "X-IP-Rate-Limit-Remaining:" | tr -d '\r' | awk '{print $2}')
  
  if [ "$STATUS" = "429" ]; then
    RETRY_AFTER=$(echo "$RESPONSE" | grep -o '"retryAfterSeconds":"[^"]*' | cut -d'"' -f4)
    echo "Login $i | Status: 429 | Retry After: ${RETRY_AFTER:-N/A}"
    ((RATE_LIMITED_COUNT++))
  elif [ "$STATUS" = "200" ] || [ "$STATUS" = "201" ]; then
    echo "Login $i | Status: ${STATUS:-000} | IP Remaining: ${IP_REMAINING:-N/A} ✅"
    ((SUCCESS_COUNT++))
  else
    echo "Login $i | Status: ${STATUS:-000} | ERROR"
    ((ERROR_COUNT++))
  fi
done

echo ""
echo "=========================================="
echo "Test Results"
echo "=========================================="
echo "✅ Successful: $SUCCESS_COUNT"
echo "❌ Rate Limited (429): $RATE_LIMITED_COUNT"
echo "⚠️  Errors: $ERROR_COUNT"
echo "=========================================="
