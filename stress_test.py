import requests
import threading

URL = "http://localhost:8080/api/v1/purchase"
PAYLOAD = {
    "productId": 1,
    "quantity": 1
}

def make_request(id):
    try:
        response = requests.post(URL, json=PAYLOAD)
        print(f"Request {id}: Status {response.status_code} - {response.text}")
    except Exception as e:
        print(f"Request {id}: Failed - {e}")

threads = []
for i in range(50):
    t = threading.Thread(target=make_request, args=(i,))
    threads.append(t)
    t.start()

for t in threads:
    t.join()