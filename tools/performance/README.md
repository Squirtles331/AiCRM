# CRM performance stage gate

This harness exercises the CRM HTTP API. It is intentionally separate from Maven verification: it requires a dedicated pre-production environment holding production-shaped data and it writes follow-up records through the normal audit and Outbox path.

## Acceptance profile

- Docker Desktop (or the target Docker engine) must expose at least 4 CPU cores and 8 GiB memory.
- The test database must contain 10 isolated tenants and at least 1,000,000 active CRM sales records in total. Distribute the records across lead, customer, opportunity, quotation, contract and sales-order datasets according to the release data plan; do not create delivery, payment, invoice or ticket tables.
- Pass a JSON data profile from the approved data-load process. The launcher requires `tenantCount: 10`, `salesRecordCount >= 1000000` and a non-empty `source`, copies the profile into the result directory, and records its SHA-256. [`data-profile.example.json`](data-profile.example.json) defines the required shape but is not itself evidence.
- The supplied token must belong to the chosen tenant, have `lead:read` and `lead:follow-up`, and be able to read the dedicated private lead.
- `FollowUpLeadId` must be a disposable, private, non-terminal lead owned by the token user. The gate adds one normal CRM follow-up per command sample.
- Run only against an isolated pre-production environment. The harness never connects to PostgreSQL directly and must not be pointed at production.

The gate uses 20 virtual users for two minutes against `GET /api/v1/leads/private`, then runs 100 sequential real follow-up commands against the dedicated lead. k6 fails the run when the common-list P95 reaches 300 ms or the business-command P95 reaches 500 ms, or when any HTTP check fails.

## Run

From the repository root in PowerShell:

```powershell
.\tools\performance\run-stage-gate.ps1 `
  -BaseUrl 'http://host.docker.internal:8080' `
  -BearerToken '<short-lived-pre-production-token>' `
  -FollowUpLeadId '900000000001' `
  -DataProfilePath '.\approved-data-profile.json'
```

For a CRM instance exposed on the same Windows host, use `http://localhost:8080`; the launcher rewrites only the container-side address to `host.docker.internal`. For a remote environment, pass its HTTPS base URL directly. The result directory contains `environment.json` (no token), the verified `data-profile.json`, and k6 `summary.json`, which are the release-review evidence.

Adjust load only through the launcher's `ListVus`, `ListDuration` and `CommandIterations` parameters. A changed profile is not evidence for the stated 4C8G/10-tenant/million-record release gate.
