import random
import uuid
import time
import logging
import requests
from locust import HttpUser, task, between, events

# Configure standard logging to output directly to standard stdout
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

REGISTERED_BANKS = []

# --- 1. LIVE TERMINAL LOGGING FOR REQUEST STATUS CODES ---
@events.request.add_listener
def on_request(request_type, name, response_time, response_length, response, context, exception, **kwargs):
    """
    Listens to every completed request and logs non-200 responses to the terminal.
    """
    if response is not None:
        status_code = response.status_code
        # Log any failing requests (4xx / 5xx) to terminal immediately
        if status_code != 200 and status_code != 201:
            logging.error(f"[HTTP {status_code}] {request_type} {name} | Response: {response.text[:120]}")
    elif exception:
        logging.error(f"[CONNECTION ERROR] {request_type} {name} | Exception: {exception}")


# --- 2. GLOBAL SETUP BEFORE LOAD TEST ---
@events.test_start.add_listener
def on_test_start(environment, **kwargs):
    """ Runs exactly ONCE synchronously before any Locust users spawn. """
    base_url = environment.host or "http://localhost:8080"
    print("Starting global setup: Logging in as manager and registering banks...", flush=True)

    setup_start_time = time.time()
    session = requests.Session()

    # Manager Login
    login_response = session.post(f"{base_url}/api/v1/auth/login", json={
        "username": "manager1",
        "password": "password1"
    })

    if login_response.status_code != 200:
        print(f"❌ Manager login failed [HTTP {login_response.status_code}]: {login_response.text}", flush=True)
        return

    print("✅ Manager login successful.", flush=True)

    # Register banks sequentially
    reg_start_time = time.time()

    for _ in range(100):
        bank_code = f"BK-{random.randint(10000, 99999)}"
        registration_payload = {
            "bankName": f"Automated Test Bank {bank_code}",
            "bankCode": bank_code
        }

        reg_response = session.post(
            f"{base_url}/api/v1/bank/register",
            json=registration_payload
        )

        if reg_response.status_code in [200, 201]:
            api_key = reg_response.json().get("apiKey")
            REGISTERED_BANKS.append({
                "bankCode": bank_code,
                "apiKey": api_key
            })
        else:
            print(f"❌ Failed to register bank {bank_code} [HTTP {reg_response.status_code}]: {reg_response.text}", flush=True)

    reg_duration = time.time() - reg_start_time
    total_setup_duration = time.time() - setup_start_time

    print(f"✅ Bank registration completed in {reg_duration:.2f}s (Average: {reg_duration / len(REGISTERED_BANKS) if REGISTERED_BANKS else 0:.3f}s per bank).", flush=True)
    print(f"✅ Total setup completed in {total_setup_duration:.2f}s. Successfully registered {len(REGISTERED_BANKS)} banks. Starting traffic...\n", flush=True)


# --- 3. WORKER SIMULATION ---
class BankTrafficSimulator(HttpUser):
    wait_time = between(0.1, 0.5)

    def on_start(self):
        if not REGISTERED_BANKS:
            print("❌ No registered banks available. Stopping worker...", flush=True)
            self.environment.runner.quit()
            return

        my_bank = random.choice(REGISTERED_BANKS)
        self.bank_code = my_bank["bankCode"]
        self.api_key = my_bank["apiKey"]

        self.client.headers.update({
            "X-API-KEY": self.api_key,
            "Content-Type": "application/json"
        })

    @task
    def send_transaction(self):
        locations = ["Houston, USA", "Glasgow, UK", "Tokyo, Japan", "Dubai, UAE", "London, UK"]

        recipient_bank = random.choice(REGISTERED_BANKS)
        while recipient_bank["bankCode"] == self.bank_code:
            recipient_bank = random.choice(REGISTERED_BANKS)

        transaction_payload = {
            "senderAccountNumber": f"BANK-ACC-{random.randint(1000, 9999)}",
            "senderBankCode": self.bank_code,
            "recipientAccountNumber": f"BANK-ACC-{random.randint(1000, 9999)}",
            "recipientBankCode": recipient_bank["bankCode"],
            "amount": round(random.uniform(10.0, 150000.0), 2),
            "senderLocation": random.choice(locations),
            "transactionType": "TRANSFER",
            "bankTrxReference": f"TX-REF-{uuid.uuid4()}"
        }

        # Corrected 'proccess' typo -> 'process'
        self.client.post("/api/v1/process/fraudDetection/transaction", json=transaction_payload)