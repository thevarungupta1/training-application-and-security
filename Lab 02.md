# Lab 02: Network Tracing, Connectivity Troubleshooting, and Secure Mortgage Topology

**Audience:** Developers joining the mortgage-platform training  
**Platform:** Windows 10 (build 19041 or later) or Windows 11  
**Shell:** Windows PowerShell 5.1 or PowerShell 7  
**Depends on:** Lab 01 workstation readiness (Python, Git for Windows, browser)  
**Estimated time:** 3 to 3.5 hours, including the capstone  
**Last reviewed:** 31 August 2026

## Purpose

In this lab you will learn how a mortgage request travels across the network, how to separate network failures from application defects, and how to draft a secure deployment topology. You will:

- run a small Windows-hosted mortgage API that simulates PostgreSQL and object storage;
- trace Browser → API → database → storage;
- inspect listening ports, DNS, HTTP, TLS, routing, and blocked connectivity;
- identify trust boundaries and incomplete network architecture; and
- produce capstone artifacts for the mortgage portal, APIs, AI service, and data layer.

Do not change corporate firewall, DNS, or routing policy on a managed workstation. Every connectivity failure in this lab is simulated locally or against public internet names that your organization already permits (`example.com`, `google.com`).

## Completion criteria

The lab series is complete when you can show:

- `http://127.0.0.1:8000/health` returns `"status": "UP"` (use `127.0.0.1`, not `localhost`; see Shared setup);
- `netstat` or `Get-NetTCPConnection` shows a listener on TCP 8000;
- a request to port 9000 fails while port 8000 succeeds, and you can explain why;
- `nslookup` distinguishes a resolvable name from `NXDOMAIN`;
- `curl.exe -v` output identifies method, host, port, status, headers, and body;
- a TLS handshake against `https://example.com` shows protocol, certificate, and issuer;
- `tracert example.com` lists network hops;
- a marked trust-boundary diagram for the mortgage platform; and
- capstone deliverables: topology diagram, communication matrix, and security-zone map.

## How to use this guide

Each hands-on lab is **independent**. You may complete a single lab without finishing the others, provided you meet that lab's **Before you start** section.

Labs 2.1, 2.2, 2.3, 2.5, and 2.7 need a local API on port 8000. Complete **Shared Windows environment setup** once, then leave that API running in a dedicated PowerShell window. Labs 2.4, 2.6, 2.8, 2.9, and 2.10 do not need the local API.

Keep the API console visible. Log lines such as `Simulating PostgreSQL lookup` are part of the trace.

## Shared Windows environment setup

Complete this section when a lab says the local mortgage API must be running. Skip it if the API is already listening on port 8000.

### Tools you need

| Tool | How to verify in PowerShell | If missing |
| --- | --- | --- |
| Python 3 | `python --version` | Complete Lab 01 Python setup, or use `py -3 --version` |
| pip (via Python) | `python -m pip --version` | Reinstall Python with pip enabled |
| PyPI or internal package index | `python -m pip index versions fastapi` (or a test `pip install`) | Instructor proxy / `--index-url`; Labs 2.1–2.3, 2.5, 2.7 cannot start without packages |
| curl | `curl.exe --version` | Use `curl.exe`, not `curl` (PowerShell 5 aliases `curl` to `Invoke-WebRequest`) |
| Git for Windows (optional, for OpenSSL) | `git --version` | Lab 01 install; used only in Lab 2.6 |
| Web browser | Edge, Chrome, or Firefox | Required for Lab 2.1 and Lab 2.5 |

If `python` is not found, retry with the Windows launcher:

```powershell
py -3 --version
```

Use **one** interpreter for the whole lab. `python` and `py -3` can be different versions on the same PC. If `python --version` works, keep using `python`. Only switch to `py -3` if `python` is missing.

This setup needs **outbound HTTPS to the Python package index** (or an instructor-provided index). If `pip` cannot download packages, stop and get the organization proxy or `--index-url` from the instructor. Do not continue until `fastapi` and `uvicorn` are installed.

### Create the project

Open **PowerShell**. Create the lab folder on the Windows filesystem (not under WSL). Prefer `C:\Projects` (same root as Lab 01). If Windows denies creating that folder, use your user profile instead.

```powershell
$LabRoot = "C:\Projects\mortgage-network-lab"
try {
    New-Item -ItemType Directory -Force "$LabRoot\app" -ErrorAction Stop | Out-Null
} catch {
    $LabRoot = Join-Path $env:USERPROFILE "Projects\mortgage-network-lab"
    New-Item -ItemType Directory -Force "$LabRoot\app" | Out-Null
}
New-Item -ItemType Directory -Force "$LabRoot\storage" | Out-Null
Set-Location $LabRoot
Write-Host "Lab folder: $LabRoot"
```

If you use the profile path, substitute it everywhere this guide shows `C:\Projects\mortgage-network-lab`.

Expected layout:

```text
C:\Projects\mortgage-network-lab\
|-- app\
|   `-- main.py
|-- storage\
`-- requirements.txt
```

Create `requirements.txt`:

Use **ASCII** encoding. Windows PowerShell 5.1 `Set-Content -Encoding utf8` writes a UTF-8 BOM, and `pip` can then fail to read `requirements.txt`.

```powershell
@"
fastapi
uvicorn
"@ | Set-Content -Encoding ascii .\requirements.txt
```

Create `app\main.py`. This service is a **simulation**. It does not connect to a real PostgreSQL instance or to AWS S3. Loan records are in-memory. Uploaded documents are written under `storage\` so you can see the "object store" on disk.

```powershell
@"
from fastapi import FastAPI, HTTPException
from datetime import datetime
from pathlib import Path

app = FastAPI(title="Mortgage Document Service")

STORAGE_DIR = Path(__file__).resolve().parent.parent / "storage"
STORAGE_DIR.mkdir(exist_ok=True)

LOANS = {
    1001: {
        "loan_id": 1001,
        "borrower": "Demo Borrower",
        "status": "ACTIVE",
        "amount": 350000,
    },
    1002: {
        "loan_id": 1002,
        "borrower": "Alex Rivera",
        "status": "UNDERWRITING",
        "amount": 275000,
    },
}


@app.get("/health")
def health():
    return {
        "status": "UP",
        "service": "mortgage-document-service",
    }


@app.get("/loans/{loan_id}")
def get_loan(loan_id: int):
    print(f"[{datetime.now()}] Request received for loan {loan_id}")
    print("Simulating PostgreSQL lookup")
    loan = LOANS.get(loan_id)
    if not loan:
        raise HTTPException(status_code=404, detail="Loan not found")
    return loan


@app.post("/loans/{loan_id}/documents/{filename}")
def store_document(loan_id: int, filename: str):
    print(f"[{datetime.now()}] Document store request for loan {loan_id}")
    if loan_id not in LOANS:
        raise HTTPException(status_code=404, detail="Loan not found")
    print("Simulating S3 object write")
    dest = STORAGE_DIR / f"loan-{loan_id}-{filename}"
    dest.write_text(
        f"simulated object for loan {loan_id}\nfilename={filename}\n",
        encoding="utf-8",
    )
    return {
        "loan_id": loan_id,
        "object_key": dest.name,
        "storage": "simulated-s3",
        "status": "STORED",
    }
"@ | Set-Content -Encoding ascii .\app\main.py
```

### Create the virtual environment and install packages

```powershell
Set-Location C:\Projects\mortgage-network-lab
python -m venv .venv
.\.venv\Scripts\python.exe -m pip install --upgrade pip
.\.venv\Scripts\python.exe -m pip install -r requirements.txt
```

If this fails with a connection or proxy error, retry with the instructor-provided proxy or index, for example:

```powershell
.\.venv\Scripts\python.exe -m pip install -r requirements.txt --proxy http://proxy.contoso.com:8080
```

Replace the proxy URL with the real training-network value. Do not invent one.

If you prefer to activate the environment in the current session:

```powershell
.\.venv\Scripts\Activate.ps1
python -m pip install -r requirements.txt
```

If PowerShell blocks `Activate.ps1`, do not change the machine-wide execution policy. Call `.\.venv\Scripts\python.exe` and `.\.venv\Scripts\uvicorn.exe` directly, as shown below.

### Start the API

Before starting, confirm port 8000 is free. If another process is already `LISTENING`, uvicorn will fail:

```powershell
netstat -ano | findstr :8000
```

If you see `LISTENING`, identify the PID (last column), confirm it is an old Python/uvicorn process, and stop it (`Stop-Process -Id <PID>`). Then start the API.

Use a **dedicated** PowerShell window and leave it open:

```powershell
Set-Location C:\Projects\mortgage-network-lab
.\.venv\Scripts\python.exe -m uvicorn app.main:app --reload --host 127.0.0.1 --port 8000
```

`--host 127.0.0.1` binds **IPv4 loopback only**. On Windows, `localhost` often resolves to IPv6 `::1` first. A browser request to `http://localhost:8000` can fail even while the API is healthy. Always use `http://127.0.0.1:8000`. Do not use `0.0.0.0` on a managed training workstation unless the instructor asks you to.

Wait until the console shows startup, then use the second window. Do not call the API before this line appears:

```text
INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
INFO:     Application startup complete.
```

Windows Defender Firewall may prompt the first time Python listens on a port. Allow access on **private** networks only, or cancel and keep the bind on `127.0.0.1` so no inbound firewall rule is required. If `--reload` later leaves the port occupied after `Ctrl+C`, use the Stop the API steps below.

### Confirm the API is up

In a **second** PowerShell window:

```powershell
curl.exe http://127.0.0.1:8000/health
```

Expected body:

```text
{"status":"UP","service":"mortgage-document-service"}
```

### Stop the API

In the API window press `Ctrl+C`. Confirm the port is free:

```powershell
netstat -ano | findstr :8000
```

No `LISTENING` row should remain. If a process is stuck, note the PID in the last column and stop only that process after confirming it is your Python/uvicorn instance:

```powershell
Get-Process -Id <PID>
Stop-Process -Id <PID>
```

Replace `<PID>` with the number from `netstat`. Do not stop unrelated processes.

---

## Lab 2.1 — Trace Browser → API → Database → Cloud Storage

**Time:** 25 minutes  
**Needs local API:** Yes

### Description

You run a small FastAPI service that represents the mortgage document service. A browser (or `curl.exe`) is the client. The service looks up a loan in a simulated PostgreSQL store and writes a simulated object into local `storage\`. The goal is to see every hop, not to build the production platform.

### Business scenario

A loan officer opens the mortgage portal and requests loan **1001**. The portal calls the document service. Underwriting also stores `income-proof.pdf` against that loan. When this path breaks in production, teams often jump to "the API is buggy." This lab shows the real chain: client → TCP → application → data store → object storage.

### Before you start

Complete **Shared Windows environment setup** and leave uvicorn running on port 8000.

### Architecture

```text
Browser or curl.exe
        |
        | HTTP  (TCP 127.0.0.1:8000)
        v
Python FastAPI  (mortgage-document-service)
        |
        +------------------+
        |                  |
        v                  v
Simulated PostgreSQL    Simulated S3
(in-memory LOANS)       (C:\Projects\mortgage-network-lab\storage)
```

### Steps

1. Confirm the service is healthy. In the browser address bar, open:

   ```text
   http://127.0.0.1:8000/health
   ```

   You should see JSON with `"status":"UP"`.

2. Request a loan that exists. Open:

   ```text
   http://127.0.0.1:8000/loans/1001
   ```

   Expected fields include `loan_id`, `borrower`, `status`, and `amount`.

3. Watch the API PowerShell window. You should see lines similar to:

   ```text
   Request received for loan 1001
   Simulating PostgreSQL lookup
   ```

   That printout is the "database hop." The browser never talks to PostgreSQL directly.

4. Repeat from PowerShell so you can script the same call later:

   ```powershell
   curl.exe http://127.0.0.1:8000/loans/1001
   ```

5. Simulate object storage. Store a document metadata object for the same loan:

   ```powershell
   curl.exe -X POST http://127.0.0.1:8000/loans/1001/documents/income-proof.pdf
   ```

   Expected JSON includes `"storage":"simulated-s3"` and `"status":"STORED"`.

6. Prove the object landed on disk:

   ```powershell
   Get-ChildItem C:\Projects\mortgage-network-lab\storage
   Get-Content C:\Projects\mortgage-network-lab\storage\loan-1001-income-proof.pdf
   ```

7. Request a loan that does not exist and confirm the application (not the network) returns an error:

   ```powershell
   curl.exe -i http://127.0.0.1:8000/loans/9999
   ```

   Expected HTTP status: `404`. The TCP connection succeeded; the application rejected the loan id.

8. Draw the successful path in your notes:

   ```text
   curl.exe / browser
        |
        | TCP 127.0.0.1:8000
        v
   FastAPI
        |
        | in-process lookup
        v
   Simulated PostgreSQL (memory)
        |
        | file write
        v
   Simulated S3 (storage\ folder)
        |
        v
   JSON response to client
   ```

### Expected result

You can point to three distinct stages in one request: network delivery to port 8000, application processing, and data/storage side effects. A 404 on an unknown loan is an application result, not a connectivity failure.

---

## Lab 2.2 — Inspect Network Connections

**Time:** 15 minutes  
**Needs local API:** Yes

### Description

You identify which process is listening on TCP 8000 and prove that the workstation can open a connection to that port. This is the first question in production when a client reports "connection refused" or "timed out."

### Business scenario

The mortgage portal cannot load loan 1001. The document-service team says "the API is running." You must verify that a listener exists on the expected port and that a local client can complete a TCP handshake, without guessing.

### Before you start

The API from Shared setup must be running. If you are unsure:

```powershell
curl.exe http://127.0.0.1:8000/health
```

If that fails, complete **Shared Windows environment setup**.

### Steps

1. List TCP connections and listeners:

   ```powershell
   netstat -ano
   ```

   Output is long. You will filter it in the next step.

2. Find port 8000:

   ```powershell
   netstat -ano | findstr :8000
   ```

   Expected: a row containing `LISTENING` and `127.0.0.1:8000` (or `0.0.0.0:8000` if the bind host was changed). The last column is the process ID (PID).

3. Optional modern equivalent:

   ```powershell
   Get-NetTCPConnection -LocalPort 8000 -ErrorAction SilentlyContinue |
       Select-Object LocalAddress, LocalPort, State, OwningProcess
   ```

   Expected `State`: `Listen`.

4. Map the PID to a process name:

   ```powershell
   netstat -ano | findstr :8000
   Get-Process -Id <PID> | Select-Object Id, ProcessName, Path
   ```

   Replace `<PID>` with the number from step 2. You should see `python` or `uvicorn`.

5. Test whether a client can connect. `Test-NetConnection` can take 20–40 seconds, especially when the port is closed. Wait for it; do not assume the shell is frozen.

   ```powershell
   Test-NetConnection -ComputerName 127.0.0.1 -Port 8000
   ```

   Expected: `TcpTestSucceeded : True`. `PingSucceeded` may be `False` on some images; ICMP is not required for this lab. The TCP result is the one that matters.

   Faster alternative if `Test-NetConnection` is too slow:

   ```powershell
   $tcp = New-Object System.Net.Sockets.TcpClient
   try {
       $ok = $tcp.ConnectAsync("127.0.0.1", 8000).Wait(2000)
       Write-Host "TcpConnectSucceeded : $ok"
   } finally {
       $tcp.Dispose()
   }
   ```

### What `LISTENING` means

`LISTENING` means an application has bound the port and is waiting for inbound TCP connections. It does not prove that HTTP, TLS, or business logic work. It only proves the network socket is open.

### Expected result

You can name the PID, the process, the port, and a successful `Test-NetConnection` result. Record these values in your notes; Labs 2.3 and 2.7 compare against them.

---

## Lab 2.3 — Simulate the Wrong Port

**Time:** 15 minutes  
**Needs local API:** Yes

### Description

You send a healthy client to the wrong port on purpose. The application code is unchanged. The failure is a network-endpoint mistake, which is a common mortgage-portal configuration error (wrong base URL, wrong load-balancer target group, stale port in a secret).

### Business scenario

A new environment variable sets the document service URL to `http://localhost:9000` after a copy-paste from an old runbook. Borrowers see "unable to load loan." Developers start debugging FastAPI handlers. You must show that the process is healthy on 8000 and that 9000 has no listener.

### Before you start

API listening on 8000 (Shared setup). Confirm:

```powershell
Test-NetConnection -ComputerName 127.0.0.1 -Port 8000
```

`TcpTestSucceeded` must be `True`. `Test-NetConnection` may take 20–40 seconds; wait for it.

### Steps

1. Call the correct health endpoint so you have a baseline:

   ```powershell
   curl.exe http://127.0.0.1:8000/health
   ```

2. Call the **wrong** port with the same path:

   ```powershell
   curl.exe http://127.0.0.1:9000/health
   ```

   Expected: connection failure. Typical messages include `Failed to connect` or `Connection refused`. This is **not** an HTTP 500 from FastAPI. No HTTP response is returned because TCP never completed.

3. Prove nothing is listening on 9000:

   ```powershell
   netstat -ano | findstr :9000
   Test-NetConnection -ComputerName 127.0.0.1 -Port 9000
   ```

   Expected: no `LISTENING` row, and `TcpTestSucceeded : False`.

4. Compare side by side:

   ```powershell
   Test-NetConnection -ComputerName 127.0.0.1 -Port 8000
   Test-NetConnection -ComputerName 127.0.0.1 -Port 9000
   ```

5. Answer these questions in your notes (write the answers; do not only discuss them):

   | Question | Answer you should reach |
   | --- | --- |
   | Is the FastAPI handler wrong? | No. The process on 8000 still returns `/health`. |
   | Did HTTP reach the application on 9000? | No. There is no listener. |
   | What should the portal URL use? | Host `127.0.0.1` (or `localhost`) and port `8000`. |

### Expected result

You can separate **wrong endpoint** from **application defect**. In operations terms: fix configuration, DNS, or load-balancer targets before opening the Python debugger.

---

## Lab 2.4 — DNS Investigation

**Time:** 15 minutes  
**Needs local API:** No

### Description

You resolve a public name that should succeed and a name that must fail. DNS happens before TCP. If the name does not resolve, the mortgage API is never reached, and application logs stay empty.

### Business scenario

The portal is configured with `https://api.mortgage.local/loans/1001`. After a DNS change, borrowers see a generic upload or load failure. The document-service pods are healthy. You must check name resolution first, exactly as you will in the mini exercise later.

### Before you start

Outbound DNS must be allowed from the training workstation (standard Lab 01 image). You do not need uvicorn for this lab.

### Steps

1. Resolve a known public name (same host used later for TLS and traceroute):

   ```powershell
   nslookup example.com
   ```

   Expected: a non-authoritative or authoritative answer with one or more IP addresses. Record the DNS server address shown in `Server:`. If `example.com` is blocked by policy, use another instructor-approved public name. Do not use an internal hostname for this step.

2. Optional: see what Windows itself will use for the connection:

   ```powershell
   Resolve-DnsName example.com | Select-Object Name, Type, IPAddress
   ```

3. Resolve a name that does not exist. Use the reserved `.invalid` TLD so Windows does not wait on multicast `.local` (mDNS/LLMNR), which can hang for tens of seconds:

   ```powershell
   nslookup mortgage-api-invalid.invalid
   ```

   Expected: failure such as `Non-existent domain` / `NXDOMAIN`. No usable A/AAAA record.

4. Confirm PowerShell agrees:

   ```powershell
   Resolve-DnsName mortgage-api-invalid.invalid -ErrorAction SilentlyContinue
   ```

   You should get no successful A record. If the cmdlet throws, that is the same outcome: the name does not resolve.

5. Draw the failure:

   ```text
   Mortgage portal (browser)
           |
           | needs IP for api host
           v
   DNS resolution
           X  NXDOMAIN
           |
           (request never reaches the API)
   ```

6. Write one operational rule in your notes: **empty API logs plus a client error often means the client never arrived; check DNS before code.**

### Expected result

You can distinguish "name resolved to an IP" from "NXDOMAIN." You did not need to start or stop the local API to prove this.

---

## Lab 2.5 — HTTP Inspection

**Time:** 20 minutes  
**Needs local API:** Yes

### Description

You capture the HTTP request line, headers, and response for the health endpoint, then exercise the same API through FastAPI's Swagger UI. This is how you confirm that TCP worked and that the application protocol is HTTP as expected (not TLS-only, not a proxy error page).

### Business scenario

A partner integration posts loan documents to the document service. Their support ticket includes only "it failed." You need method, URL, status code, and response body to decide whether the problem is auth, path, payload, or network.

### Before you start

API on port 8000 (Shared setup). Confirm `/health` works.

### Steps

1. Send a verbose GET. `curl.exe -v` writes request and response details to the console:

   ```powershell
   curl.exe -v http://127.0.0.1:8000/health
   ```

2. In the output, identify and write down:

   | Item | Where to look | Example |
   | --- | --- | --- |
   | Method | Request line starting with `>` | `GET` |
   | Host | `Host:` header | `127.0.0.1:8000` |
   | Port | Host header or URL | `8000` |
   | Request target | Request line | `/health` |
   | Response status | Line starting with `< HTTP/` | `200` |
   | Response headers | Lines starting with `<` | `content-type: application/json` |
   | Response body | After the headers | `{"status":"UP",...}` |

3. Inspect a business GET the same way:

   ```powershell
   curl.exe -v http://127.0.0.1:8000/loans/1001
   ```

4. Inspect a missing loan so you can recognize an application HTTP error (connection succeeded, status is 404):

   ```powershell
   curl.exe -v http://127.0.0.1:8000/loans/9999
   ```

5. Open the interactive docs in the browser:

   ```text
   http://127.0.0.1:8000/docs
   ```

   FastAPI loads Swagger UI from a public CDN. On a locked-down or offline training network the page can be **blank** even though the API is healthy. If that happens, skip the UI and use:

   ```text
   http://127.0.0.1:8000/openapi.json
   ```

   plus the `curl.exe -v` commands above. That is a complete Lab 2.5. Do not spend time debugging CSS/JavaScript when `/health` already returns 200.

6. If Swagger UI loaded, expand `GET /health`, choose **Try it out**, then **Execute**. Confirm status `200` and the JSON body.

7. If Swagger UI loaded, execute `GET /loans/{loan_id}` with `loan_id` = `1001`, then again with `9999`. Compare 200 and 404 in the UI.

8. Optional: PowerShell equivalent without `curl.exe`:

   ```powershell
   Invoke-WebRequest -Uri http://127.0.0.1:8000/health -UseBasicParsing |
       Select-Object StatusCode, StatusDescription, Content
   ```

### Expected result

You can read a verbose HTTP trace and map it to method, host, port, status, headers, and body. You can drive the same operations from Swagger UI.

---

## Lab 2.6 — TLS Handshake

**Time:** 20 minutes  
**Needs local API:** No

### Description

The local lab API is HTTP on loopback. Production mortgage traffic is HTTPS. You inspect a real TLS handshake against `example.com` using Windows-native tools. You do not disable certificate validation, and you do not target internal corporate hosts.

### Business scenario

The borrower portal loads, but the document API call fails with a certificate or TLS error after a load-balancer certificate rotation. You must know what a normal handshake looks like: TCP, then TLS, then HTTP.

### Before you start

Outbound HTTPS to `example.com` must be allowed. You do not need uvicorn. Git for Windows (Lab 01) provides `openssl.exe` if it is not already on PATH.

### Steps

1. Observe HTTPS with verbose curl (TLS plus HTTP):

   ```powershell
   curl.exe -v https://example.com
   ```

   Look for lines that mention the TLS protocol (for example `TLSv1.2` or `TLSv1.3`), the server certificate, and `SSL certificate verify ok`. Then look for `HTTP/` and a `200` or `301`/`302` status. Record:

   - TLS version  
   - that certificate verification succeeded  
   - HTTP status  

2. Locate OpenSSL from Git for Windows if `openssl` is not on PATH:

   ```powershell
   openssl version
   Get-Command openssl -ErrorAction SilentlyContinue
   Test-Path "C:\Program Files\Git\usr\bin\openssl.exe"
   ```

   If `openssl version` works, use `openssl` in the next command. Otherwise call Git's binary:

   ```powershell
   & "C:\Program Files\Git\usr\bin\openssl.exe" version
   ```

3. Inspect the certificate and cipher. OpenSSL waits for keyboard input. Pipe `Q` so the command exits instead of appearing hung:

   ```powershell
   cmd /c "echo Q | openssl s_client -connect example.com:443 -servername example.com"
   ```

   Or:

   ```powershell
   cmd /c "echo Q | ""C:\Program Files\Git\usr\bin\openssl.exe"" s_client -connect example.com:443 -servername example.com"
   ```

   Git's OpenSSL does **not** use the Windows certificate store. A `Verify return code: 20 (unable to get local issuer certificate)` is common and is **not** a failed lab if `curl.exe -v` already showed Schannel verification. Record Protocol, subject, issuer, and cipher from the OpenSSL dump anyway.

   In the output, locate:

   | Item | Typical heading or field |
   | --- | --- |
   | TLS version | `Protocol` or `TLSv1.x` |
   | Server certificate | `-----BEGIN CERTIFICATE-----` block and subject |
   | Issuer | `issuer=` |
   | Cipher | `Cipher` or `Cipher is` |

4. If OpenSSL is not installed and you cannot add it, use .NET on Windows to print protocol and issuer (read-only inspection):

   ```powershell
   $hostName = "example.com"
   $tcp = New-Object System.Net.Sockets.TcpClient($hostName, 443)
   $ssl = New-Object System.Net.Security.SslStream($tcp.GetStream(), $false, { param($s, $c, $ch, $e) $true })
   $ssl.AuthenticateAsClient($hostName)
   [PSCustomObject]@{
       Protocol     = $ssl.SslProtocol
       Cipher       = $ssl.CipherAlgorithm
       CertSubject  = $ssl.RemoteCertificate.Subject
       CertIssuer   = $ssl.RemoteCertificate.Issuer
       NotAfter     = ([System.Security.Cryptography.X509Certificates.X509Certificate2]$ssl.RemoteCertificate).NotAfter
   }
   $ssl.Dispose()
   $tcp.Close()
   ```

   The callback `{ ... $true }` is only for this inspection script so you can still print the certificate when a local trust store is incomplete. Do not copy that pattern into application code. Production services must validate certificates.

5. Draw the order:

   ```text
   Client
     |
     TCP handshake  (port 443)
     |
     TLS handshake  (certificates, cipher, protocol)
     |
     Encrypted HTTP request
     |
   Server
   ```

   HTTP in Lab 2.5 skipped the TLS box. Production mortgage APIs must not.

### Expected result

You can name TLS version, certificate subject, issuer, and cipher (or .NET equivalents) for `example.com`, and you can explain why TCP success is not enough if TLS fails.

---

## Lab 2.7 — Simulate Firewall and Connectivity Failure

**Time:** 20 minutes  
**Needs local API:** Yes (for the working comparison only)

### Description

You do not change Windows Firewall or corporate rules. You compare a port that is listening (8000) with a port that is closed (9999). The closed port behaves like a firewall drop or a missing security-group rule: the client cannot complete TCP.

### Business scenario

After a network change, the mortgage portal can resolve `api.mortgage.local` but document upload still fails. DNS is fine. The runbook next step is "is the port open from this source?" You practice that test safely on localhost.

### Before you start

API on 8000 for the success case. Port 9999 must **not** be used by another training process. If something already listens on 9999, pick another high port (for example 9998) and use it consistently.

### Steps

1. Verify the known-good service:

   ```powershell
   Test-NetConnection -ComputerName 127.0.0.1 -Port 8000
   curl.exe http://127.0.0.1:8000/health
   ```

   Expected: `TcpTestSucceeded : True` and HTTP 200.

2. Test a port with no listener (simulated block / missing allow rule). Allow 20–40 seconds for `Test-NetConnection` to finish:

   ```powershell
   Test-NetConnection -ComputerName 127.0.0.1 -Port 9999
   ```

   Expected: `TcpTestSucceeded : False`. On localhost this is usually immediate "connection refused." Across a real firewall you might instead see a timeout. Both mean the client did not establish TCP to an application.

3. Confirm with netstat:

   ```powershell
   netstat -ano | findstr :9999
   ```

   Expected: no `LISTENING` row.

4. Optional: see how `curl.exe` reports the same failure:

   ```powershell
   curl.exe -v --connect-timeout 5 http://127.0.0.1:9999/health
   ```

5. Copy this troubleshooting sequence into your notes. Use it in the mini exercise and on the job:

   ```text
   API call failed
        |
        v
   Can DNS resolve the host?
        |
        v
   Can the destination IP be reached (routing)?
        |
        v
   Is the required port open from this source?
        |
        v
   Does TLS succeed (for HTTPS)?
        |
        v
   Did HTTP reach the application?
        |
        v
   What status code and body did the application return?
   ```

   Stop at the first failed step. Do not debug Python while DNS or TCP is failing.

### Expected result

You have a side-by-side True/False TCP test and a written order of investigation. You did not modify Windows Firewall.

---

## Lab 2.8 — Trace Route

**Time:** 10 minutes  
**Needs local API:** No

### Description

You list the routers (hops) between your Windows workstation and a public host. This introduces routing without configuring enterprise routers. Some hops may show `Request timed out` if a router does not reply to traceroute probes; later hops can still succeed.

### Business scenario

A partner bank's API is slow or unreachable from the office network but works from a cloud jump host. Traceroute shows where packets stop or where latency jumps. You practice the Windows command so you can capture that evidence.

### Before you start

Outbound ICMP/UDP traceroute to `example.com` should be allowed. If your organization blocks traceroute, record that the command is blocked and continue with the explanation. You do not need uvicorn.

### Steps

1. Run the Windows traceroute:

   ```powershell
   tracert example.com
   ```

   Do not use `traceroute`; that name is for Linux/macOS.

2. Optional timeout cap if the command runs too long:

   ```powershell
   tracert -d -h 15 example.com
   ```

   `-d` skips reverse DNS lookups. `-h 15` limits hops.

3. In the output, identify:

   - hop number  
   - round-trip times (three probes)  
   - hostname or IP of that hop, or `Request timed out`  

4. Write one sentence in your notes: packets can cross many networks before they reach the mortgage API; a failure can be in a hop you do not own.

### Expected result

You have a hop list for `example.com` (or a documented corporate block) and you can explain why traceroute is evidence for routing, not for HTTP status codes.

---

## Lab 2.9 — Analyze Secure Network Architecture

**Time:** 20 minutes  
**Needs local API:** No  
**Type:** Architecture review (diagram and written findings)

### Description

You review a first-draft mortgage architecture and list what is missing. There is no server to start. The deliverable is a short written critique plus an improved zone sketch.

### Business scenario

A vendor proposes this topology for the mortgage platform "to go live next quarter." Security architecture asks you to review it before procurement. Your job is to find exposure, missing zones, and missing controls—not to implement AWS in this lab.

### Given architecture

```text
                    INTERNET
                       |
                       |
                      WAF
                       |
                       v
                Load Balancer
                       |
              -------------------
              |                 |
             API-1             API-2
              |                 |
              --------+----------
                      |
                   Database
                      |
                     S3
```

### Steps

1. Copy the diagram into your notes (or a whiteboard photo).

2. Mark what is unspecified. At minimum call out:

   - no DNS / Route 53 (or equivalent) in front of the WAF  
   - no public vs private subnet labels  
   - no statement that the database is private  
   - APIs appear adjacent to the internet path with no private application subnet  
   - S3 access path and permissions are not shown  
   - no monitoring, no identity, no encryption-in-transit labels  
   - no availability-zone redundancy story beyond two API boxes  

3. Answer: **What is wrong or incomplete?** Write at least five findings. Example directions (use your own wording):

   - Database must not be reachable from the internet.  
   - Application instances belong in private subnets; only the load balancer should sit on a public path.  
   - Object storage needs a locked-down access pattern (no public bucket, least-privilege identity).  
   - WAF without a defined public entry (DNS) and without logging is incomplete.  
   - Two APIs sharing one database still need backup, Multi-AZ, and a data subnet boundary.  

4. Draw the improved sketch:

   ```text
   Public subnet
        |
   Load balancer (and WAF / DNS at the edge)
        |
   Private application subnet
        |
   Mortgage APIs
        |
   Private data subnet
        |
   PostgreSQL

   Document service --> S3 with tightly scoped identity and private access
   ```

5. Note one sentence on S3: use least-privilege credentials or a role, encrypt objects, and avoid public ACLs. You are not creating an AWS account in this lab.

### Expected result

A written list of gaps and a second diagram that introduces public edge, private app, and private data layers.

---

## Lab 2.10 — Identify Trust Boundaries

**Time:** 20 minutes  
**Needs local API:** No  
**Type:** Architecture review

### Description

You mark trust boundaries on the mortgage request path and answer four control questions at each boundary. Trust boundaries are where the network, identity, or data sensitivity changes—not merely where a box is drawn.

### Business scenario

Internal audit asks: "Who can reach underwriting data, and how do we know?" You cannot answer with a single VPC diagram. You need labeled boundaries and controls for authentication, allowed protocols, and monitoring.

### Given path

```text
Internet
   |
   v
Mortgage Portal
   |
   v
Mortgage API
   |
   +-------> AI Service
   |
   +-------> PostgreSQL
   |
   +-------> S3
```

### Steps

1. Redraw the path with these boundaries:

   ```text
   [UNTRUSTED]
   Internet
       |
   ==== TRUST BOUNDARY #1 ====
       |
   Edge / WAF / public portal entry
       |
   ==== TRUST BOUNDARY #2 ====
       |
   Application services (portal BFF, mortgage API, document API)
       |
   ==== TRUST BOUNDARY #3 ====
       |
   Sensitive data and high-risk processors
   (PostgreSQL, S3, AI service with loan content)
   ```

2. For **each** of the three boundaries, write answers to all four questions:

   | Question | What a complete answer includes |
   | --- | --- |
   | Who can cross it? | Named actors: anonymous borrowers, authenticated staff, platform services, admins |
   | How are they authenticated? | TLS, WAF, portal login, service identity, IAM/role, mTLS as applicable |
   | What communication is permitted? | Ports, protocols, directions (for example internet → 443 only) |
   | How is the activity monitored? | WAF logs, access logs, application traces, data-plane audit |

3. Mark the AI service carefully. It processes borrower content. Treat it as crossing into a higher-sensitivity zone even if it is "just another microservice."

4. Optional prompt for discussion: an engineer says "it is inside the VPC, so it is trusted." Write why Lab 2.10 and the capstone Zero Trust step reject that assumption.

### Expected result

Three numbered boundaries, each with four written answers. This sheet is reused in the capstone security-zone activity.

---

## Mini exercise — Mortgage document upload failure

**Time:** 25 minutes  
**Needs local API:** No (tabletop troubleshooting)  
**Type:** Guided incident, Windows commands as you would run them in a real environment

### Description

You walk a failed borrower upload using the same order you wrote in Lab 2.7. Several steps are **scripted results** (you will not have a real `api.mortgage.local` in DNS). Run the commands that are valid on your workstation, and treat the stated results as the incident data.

### Business scenario

A borrower attempts to upload `income-proof.pdf`. The UI shows: **Document upload failed.**

```text
Browser
   |
api.mortgage.local
   |
WAF
   |
Document API
   |
S3
```

### Step 1 — DNS

On a real incident you would run:

```powershell
nslookup api.mortgage.local
```

**Incident result (given):** `NXDOMAIN` / non-existent domain.

**Problem 1:** The name does not resolve. The Document API is never contacted. Application logs on the API will not show the upload.

**Remediation (given, after DNS is corrected):**

```text
api.mortgage.local  -->  10.0.2.25
```

Retry the upload. It still fails. Continue; DNS is no longer the blocker.

To practice the **success** shape of DNS on your machine, resolve a real name and contrast it with the NXDOMAIN you already saw in Lab 2.4:

```powershell
nslookup example.com
nslookup mortgage-api-invalid.invalid
```

### Step 2 — Connectivity

Do **not** use `Test-NetConnection 127.0.0.1 -Port 443` as a stand-in for the document API. Local port 443 may already be in use (IIS, VPN, developer tools) and would give a false “success.”

On a real incident, after DNS works, you would test the API name:

```powershell
Test-NetConnection -ComputerName api.mortgage.local -Port 443
```

You cannot complete that command successfully in this classroom (the name is not in your DNS). Treat the following as **incident data**, not as something to reproduce on your PC.

**Incident result (given):** `TcpTestSucceeded : False`.

DNS is fixed; TCP to 443 is not. Next: firewall / security group / NSG policy, not Python.

**Incident policy (given):**

| Field | Current | Required |
| --- | --- | --- |
| Source | Portal network | Portal network |
| Destination | Document API | Document API |
| Port | 443 | 443 |
| Action | DENY | ALLOW |

Required rule (logical, not to be implemented on your PC):

```text
Portal --> Document API
TCP 443
ALLOW
```

Do not add Windows Firewall rules to "simulate" this allow. On a managed workstation that is out of scope.

### Step 3 — Confirm the rest of the path (given after remediation)

After the approved firewall change, the checklist becomes:

```text
DNS              ✓
Routing          ✓
Firewall         ✓
TCP              ✓
TLS              ✓
HTTPS            ✓
Document API     ✓
S3               ✓
```

### Lesson

An API failure does not automatically mean there is a code defect. This incident was DNS, then a denied TCP 443 rule. The upload handler may have been correct the entire time.

### Mini-exercise deliverable

Write a short incident timeline (five to eight lines) that a change-advisory board could read: symptom, DNS finding, TCP finding, policy fix, verification order.

---

## Capstone — Mortgage deployment topology

**Time:** 45–60 minutes  
**Needs local API:** No  
**Type:** Design activities (diagrams and a communication matrix)

Session 3 of the curriculum requires you to progress the mortgage capstone by drafting deployment topology for the portal, APIs, AI service, and database layer. Build the artifacts in order. Do not skip the communication matrix; it is the operational version of your diagram.

### Business scenario

The bank approved a cloud-hosted mortgage platform. You must show where each component lives, what is public, what is private, which flows are allowed, and how defense in depth and Zero Trust apply to document upload. This is a paper architecture on Windows (diagram tool of your choice: Whiteboard, PowerPoint, draw.io, or paper). You are not required to create AWS accounts or click-deploy infrastructure.

### Activity 1 — Identify components

List every component you will place on the diagram:

- Mortgage Portal  
- Mortgage API  
- Document Service  
- AI Service  
- PostgreSQL  
- Object storage (S3 or equivalent)  
- Monitoring (for example CloudWatch / security monitoring)  
- Edge: DNS, WAF, load balancer  

Add any extra box your instructor requires (identity provider, secrets manager). If you add it, it must also appear in the communication matrix.

### Activity 2 — Define exposure

Classify each component:

**Public (internet-facing or internet-reachable by design)**

- Mortgage Portal (browser traffic)  
- Public load balancer (and WAF in front of it)  

**Private (not advertised to the internet)**

- Mortgage APIs  
- Document Service  
- AI Service  
- PostgreSQL  

Rule you must state explicitly: **do not expose the database publicly.**

### Activity 3 — Proposed topology (AWS-shaped, cloud-agnostic intent)

Draw the following. You may label equivalents if the course uses another cloud.

```text
                         INTERNET
                            |
                            v
                       Route 53 (DNS)
                            |
                            v
                         AWS WAF
                            |
                            v
                  Application Load Balancer
                            |
               =========================
                       VPC
               =========================

                    Public subnets
                         |
                         v
                       ALB
                         |
              -------------------------
                   Trust boundary
              -------------------------
                         |
                         v
                  Private app subnets
                  /       |        \
                 /        |         \
                v         v          v
         Mortgage API  Document    AI Service
                       Service
                |         |
                |         |
                +----+----+
                     |
              -----------------
                Data boundary
              -----------------
                     |
               Private DB subnet
                     |
                     v
                PostgreSQL / RDS

Document Service
       |
       v
      S3 (private access pattern)

All services
       |
       v
CloudWatch / security monitoring
```

Check your drawing against this list:

- DNS in front of WAF  
- WAF in front of ALB  
- ALB in public subnets  
- APIs and AI in private app subnets  
- PostgreSQL in a private data subnet  
- S3 not drawn as a public website bucket  
- monitoring attached to all services  

### Activity 4 — Communication matrix

Create this table in Excel, Word, or Markdown. Fill **Allow?** using Yes/No. Add a **Notes** column if you need a justification.

| Source | Destination | Port | Allow? |
| --- | --- | --- | --- |
| Internet | ALB | 443 | Yes |
| Internet | Mortgage API directly | Application port (for example 8080) | No |
| Internet | PostgreSQL | 5432 | No |
| Internet | AI Service directly | Any | No |
| ALB | Mortgage API | Application port | Yes |
| Mortgage API | PostgreSQL | 5432 | Yes |
| Document Service | S3 | 443 (HTTPS) | Yes |
| Mortgage API | AI Service | Application / HTTPS port | Yes |
| Internet | Document Service directly | Any | No |
| AI Service | PostgreSQL | 5432 | Instructor decision; default **No** unless a documented query path exists |

This table is the start of an enterprise network-flow matrix. Any new arrow on your diagram must get a new row.

### Activity 5 — Security zones

Draw five zones and a **red line** between each pair. Those lines are trust boundaries (reuse Lab 2.10 questions).

```text
ZONE 0  Untrusted internet

        ↓  (red line)

ZONE 1  Edge security
        Route 53, WAF, load balancer

        ↓  (red line)

ZONE 2  Application
        Mortgage API, Document Service

        ↓  (red line)

ZONE 3  AI
        AI Service

        ↓  (red line)

ZONE 4  Data
        PostgreSQL, S3
```

Write one sentence per red line: what identity is required to cross.

### Activity 6 — Defense in depth for document upload

Map the borrower upload to layered controls. Annotate your topology with these stages (boxes or a numbered legend):

```text
Borrower
   ↓
HTTPS
   ↓
WAF
   ↓
Authentication
   ↓
Authorization
   ↓
Document validation
   ↓
Private application service
   ↓
Least-privilege access to storage
   ↓
Encrypted S3 (or equivalent)
   ↓
Audit logging
   ↓
Security monitoring
```

Defense in depth means more than one control must fail before `income-proof.pdf` is stored incorrectly or stolen. Label at least two controls that would still protect the bank if WAF were misconfigured.

### Activity 7 — Zero Trust for service-to-service calls

For **every** arrow between services on your diagram (API → PostgreSQL, API → AI, Document Service → S3), write the same six checks. Do not assume `internal = trusted`.

```text
Verify identity
      ↓
Verify authorization
      ↓
Apply least privilege
      ↓
Use secure communication (TLS)
      ↓
Log activity
      ↓
Monitor abnormal behavior
```

Deliverable: a six-row checklist copied once per service-to-service flow, with the actual identity mechanism named (for example "task role," "managed identity," "mTLS service account"). Placeholder names are acceptable if the course has not chosen a vendor yet; blank rows are not.

### Capstone completion checklist

You are done when you can hand an instructor:

1. Component list with public/private classification  
2. Topology diagram matching Activity 3  
3. Communication matrix with no internet path to PostgreSQL or to the AI service  
4. Zone map with red trust-boundary lines  
5. Document-upload defense-in-depth chain  
6. Zero Trust checklist for each internal flow  

---

## Appendix A — Windows command quick reference

| Goal | Command |
| --- | --- |
| HTTP GET | `curl.exe http://127.0.0.1:8000/health` |
| Verbose HTTP | `curl.exe -v http://127.0.0.1:8000/health` |
| Listeners on a port | `netstat -ano \| findstr :8000` |
| TCP test | `Test-NetConnection -ComputerName 127.0.0.1 -Port 8000` |
| DNS | `nslookup example.com` |
| Traceroute | `tracert example.com` |
| HTTPS / TLS (curl) | `curl.exe -v https://example.com` |
| Start API | `.\.venv\Scripts\python.exe -m uvicorn app.main:app --reload --host 127.0.0.1 --port 8000` |

Use `curl.exe` in Windows PowerShell 5.1. The alias `curl` invokes `Invoke-WebRequest` and does not accept the same switches.

## Appendix B — Common Windows failures

| Symptom | Likely cause | What to do |
| --- | --- | --- |
| `python` not recognized | PATH / launcher | Use `py -3` or complete Lab 01 |
| `python` and `py -3` disagree | Two interpreters installed | Use only the one that created `.venv` |
| Access denied creating `C:\Projects` | No write to `C:\` | Use `$env:USERPROFILE\Projects\mortgage-network-lab` |
| `pip` connection / SSL / proxy error | No route to PyPI | Instructor proxy or `--index-url`; do not skip package install |
| `ERROR: Invalid requirement` on pip install | UTF-8 BOM in `requirements.txt` | Recreate the file with `Set-Content -Encoding ascii` |
| `Activate.ps1` cannot be loaded | Execution policy | Call `.\.venv\Scripts\python.exe` directly |
| `[Errno 10048] address already in use` | Port 8000 occupied | `netstat -ano \| findstr :8000` then stop the confirmed PID |
| Browser cannot open `http://localhost:8000` | IPv6 `::1` vs bind on `127.0.0.1` | Use `http://127.0.0.1:8000` only |
| `/docs` is a blank page | Swagger UI CDN blocked | Use `curl.exe` and `/openapi.json` |
| `curl: unrecognized option` | `curl` alias | Use `curl.exe` |
| Port 8000 already in use after Ctrl+C | Leftover uvicorn reload child | `netstat -ano \| findstr :8000` then `Stop-Process -Id <PID>` after confirming the process |
| Browser cannot open 127.0.0.1 | API not started or crashed | Wait for `Application startup complete`; check the uvicorn traceback |
| `openssl` not found | Not on PATH | Use `C:\Program Files\Git\usr\bin\openssl.exe` or the Lab 2.6 .NET script |
| OpenSSL `Verify return code: 20` | Git OpenSSL ignores Windows CAs | Acceptable if `curl.exe -v https://example.com` verifies; record cipher/subject anyway |
| OpenSSL appears hung | Waiting for stdin | Use `cmd /c "echo Q | openssl s_client ..."` |
| `Test-NetConnection` slow | DNS + ICMP + TCP timeout | Wait 20–40 seconds; or use the TcpClient snippet in Lab 2.2 |
| Firewall popup | First bind to a public interface | Bind `127.0.0.1` as documented; do not open 8000 to the network |
| Labs 2.4 / 2.6 / 2.8 fail | Outbound DNS/HTTPS/traceroute blocked | Record the corporate block; those labs need internet to public names |

## Appendix C — Independence map

| Lab | Local API required | Network used | Deliverable |
| --- | --- | --- | --- |
| 2.1 Trace path | Yes | Loopback HTTP | Working health, loan GET, simulated S3 file |
| 2.2 Inspect connections | Yes | Loopback TCP | PID, LISTENING, Test-NetConnection True |
| 2.3 Wrong port | Yes | Loopback 8000 vs 9000 | Written distinction: config vs code |
| 2.4 DNS | No | Corporate DNS | NXDOMAIN vs successful lookup |
| 2.5 HTTP inspection | Yes | Loopback HTTP | Annotated curl -v + Swagger try-out |
| 2.6 TLS | No | Internet HTTPS to example.com | Protocol, cert, issuer, cipher |
| 2.7 Firewall simulation | Yes (comparison) | Loopback 8000 vs 9999 | Troubleshooting sequence in notes |
| 2.8 Trace route | No | Internet traceroute | Hop list |
| 2.9 Architecture review | No | None | Written gaps + improved sketch |
| 2.10 Trust boundaries | No | None | Four questions × three boundaries |
| Mini exercise | No | Tabletop | Incident timeline |
| Capstone | No | None | Six instructor artifacts |
