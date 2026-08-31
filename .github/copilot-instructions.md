# Copilot Code Review Instructions

## Purpose

When reviewing a pull request, perform all of the following:

1. A normal technical code review.
2. A Jira requirement verification.
3. Detailed diagnostics for every interaction with the configured Atlassian Rovo MCP server.

The Jira issue is the source of truth for functional requirements.

The MCP diagnostics are required because the GitHub `View session` page may not be available.

---

## Jira configuration

This repository belongs to the `Gepardec` Jira environment.

The configured MCP server is named:

`Gepardec`

When using the configured MCP server `Gepardec`, ALWAYS use the following Jira cloudId:

`edf56f73-7260-4bb3-ae3a-1450c0895e6a`

Never discover, select, infer, or use another Atlassian cloudId.

Only Jira issues from project `LAKWYC` are relevant for this repository.

Never access Jira issues from another project or another Atlassian cloud.

---

## Locate the Jira issue

Look for a Jira issue key matching:

`LAKWYC-[0-9]+`

Check the following locations:

1. Pull request title.
2. Pull request description.
3. Branch name.
4. Commit messages, when available.

Examples of valid Jira issue keys:

- `LAKWYC-1`
- `LAKWYC-20`
- `LAKWYC-201`
- `LAKWYC-12345`

If multiple Jira issue keys are found, retrieve and verify all referenced
`LAKWYC` issues that are relevant to the pull request.

If no Jira issue key matching `LAKWYC-[0-9]+` can be identified, explicitly state:

`Jira requirement verification could not be performed because no LAKWYC Jira issue was referenced.`

Do not guess or construct a Jira issue key.

---

# MCP diagnostics

MCP diagnostics are REQUIRED for every pull request review.

The diagnostics must describe every MCP-related action that is visible to the review agent.

The goal is to determine exactly at which stage access to Atlassian Rovo MCP or Jira fails.

---

## Diagnostic file

If the review environment allows writing files, create:

`/tmp/copilot-mcp-diagnostics.log`

Do NOT commit this file to the repository.

Append every MCP diagnostic event to this file in chronological order.

If the file cannot be created, record:

`Diagnostic file could not be created in this review environment.`

The inability to create the file must never stop the normal code review.

All relevant diagnostic information must ALSO be included in the pull request review itself.

---

## MCP diagnostic lifecycle

For every review, perform and record the following diagnostic stages.

The configured MCP integration has the following expected architecture:

```text
GitHub Copilot Code Review
        |
        v
MCP server: Gepardec
        |
        v
local MCP process
        |
        v
npx mcp-remote@latest
        |
        v
https://mcp.atlassian.com/v1/mcp
        |
        v
Atlassian Rovo MCP
        |
        v
Jira project LAKWYC
```

The configured MCP server uses:

- MCP server name: `Gepardec`
- MCP server type: `local`
- command: `npx`
- MCP bridge: `mcp-remote@latest`
- remote endpoint: `https://mcp.atlassian.com/v1/mcp`
- authentication mechanism: HTTP Basic authentication
- credential source: environment variable `ATLASSIAN_API_KEY`
- GitHub Agents secret source: `COPILOT_MCP_ATLASSIAN_API_KEY`

NEVER log the value of either `ATLASSIAN_API_KEY` or
`COPILOT_MCP_ATLASSIAN_API_KEY`.

For every stage below, report only information actually observable by the
review environment.

Never invent successful connections, authentication, initialization,
tool discovery, or Jira requests.

---

### Stage 1 - MCP server configuration

Determine whether the MCP server `Gepardec` is available to the Copilot
Code Review environment.

Record:

```text
[MCP-DIAG] Stage: MCP_CONFIGURATION
[MCP-DIAG] Expected MCP server: Gepardec
[MCP-DIAG] Expected MCP type: local
[MCP-DIAG] Expected command: npx
[MCP-DIAG] Expected MCP bridge: mcp-remote@latest
[MCP-DIAG] Expected remote endpoint: https://mcp.atlassian.com/v1/mcp
[MCP-DIAG] MCP server Gepardec available: YES | NO | UNKNOWN
```

If the server is unavailable:

```text
[MCP-DIAG] Failure classification: MCP_SERVER_UNAVAILABLE
```

If the server is unavailable, do NOT claim that `mcp-remote` was started
and do NOT claim that Atlassian was contacted.

---

### Stage 2 - Local MCP process startup

Determine whether GitHub started the local MCP server process for
`Gepardec`.

The expected command is conceptually:

```text
npx mcp-remote@latest https://mcp.atlassian.com/v1/mcp ...
```

Do NOT include authentication credentials when logging the command.

If process startup information is observable, record:

```text
[MCP-DIAG] Stage: MCP_PROCESS_START
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Command: npx
[MCP-DIAG] MCP bridge: mcp-remote@latest
[MCP-DIAG] Local MCP process started: SUCCESS | FAILED
```

If startup fails, record the sanitized error:

If stdout or stderr from the MCP process is visible to the review environment,
include all relevant sanitized startup output.

In particular, report errors related to:

- `npx`
- Node.js
- package installation
- `mcp-remote`
- DNS resolution
- network access
- TLS
- connection to `mcp.atlassian.com`
- missing environment variables
- MCP protocol initialization

Example:

```text
[MCP-DIAG] Process stderr: <sanitized stderr>
```

If GitHub does not expose this information:

```text
[MCP-DIAG] Stage: MCP_PROCESS_START
[MCP-DIAG] Local MCP process startup: NOT OBSERVABLE
```

Do NOT infer successful process startup merely because the MCP server is
configured.

---

### Stage 3 - mcp-remote startup

Determine whether `mcp-remote` itself started successfully.

If observable:

```text
[MCP-DIAG] Stage: MCP_REMOTE_BRIDGE_START
[MCP-DIAG] MCP bridge: mcp-remote@latest
[MCP-DIAG] mcp-remote startup: SUCCESS | FAILED
```

If `mcp-remote` reports an error, record the sanitized error:

```text
[MCP-DIAG] Stage: MCP_REMOTE_BRIDGE_START
[MCP-DIAG] mcp-remote startup: FAILED
[MCP-DIAG] Failure classification: MCP_REMOTE_BRIDGE_FAILED
[MCP-DIAG] Error: <sanitized error>
```

If this information is unavailable:

```text
[MCP-DIAG] Stage: MCP_REMOTE_BRIDGE_START
[MCP-DIAG] mcp-remote startup: NOT OBSERVABLE
```

---

### Stage 4 - Connection to Atlassian Rovo MCP

Determine whether `mcp-remote` established a connection to:

`https://mcp.atlassian.com/v1/mcp`

If observable:

```text
[MCP-DIAG] Stage: ATLASSIAN_MCP_CONNECTION
[MCP-DIAG] Remote endpoint: https://mcp.atlassian.com/v1/mcp
[MCP-DIAG] Connection attempted: YES
[MCP-DIAG] Connection result: SUCCESS | FAILED
```

If an HTTP status code is visible, record it:

```text
[MCP-DIAG] HTTP status: 200
```

or:

```text
[MCP-DIAG] HTTP status: 401
```

or:

```text
[MCP-DIAG] HTTP status: 403
```

If the connection fails:

```text
[MCP-DIAG] Failure classification: MCP_CONNECTION_FAILED
[MCP-DIAG] Error: <sanitized error>
```

If the review environment does not expose whether Atlassian was contacted:

```text
[MCP-DIAG] Stage: ATLASSIAN_MCP_CONNECTION
[MCP-DIAG] Connection to Atlassian Rovo MCP: NOT OBSERVABLE
```

Do NOT claim that Atlassian was contacted solely because the `Gepardec`
MCP server is configured.

---

### Stage 5 - Atlassian MCP authentication

Determine whether authentication against Atlassian Rovo MCP succeeded.

The expected authentication mechanism is:

`Authorization: Basic <credential>`

NEVER log the actual Authorization header.

NEVER log the credential.

NEVER log the value of `ATLASSIAN_API_KEY`.

NEVER log the value of `COPILOT_MCP_ATLASSIAN_API_KEY`.

If authentication status is explicitly observable:

```text
[MCP-DIAG] Stage: MCP_AUTHENTICATION
[MCP-DIAG] Authentication mechanism: Basic
[MCP-DIAG] Authentication attempted: YES
[MCP-DIAG] Authentication result: SUCCESS
```

Authentication failure example:

```text
[MCP-DIAG] Stage: MCP_AUTHENTICATION
[MCP-DIAG] Authentication mechanism: Basic
[MCP-DIAG] Authentication attempted: YES
[MCP-DIAG] Authentication result: FAILED
[MCP-DIAG] Failure classification: MCP_AUTHENTICATION_FAILED
[MCP-DIAG] Error: <sanitized error>
```

If an HTTP `401` is visible, classify it as:

```text
[MCP-DIAG] HTTP status: 401
[MCP-DIAG] Failure classification: MCP_AUTHENTICATION_FAILED
```

If an HTTP `403` is visible, record:

```text
[MCP-DIAG] HTTP status: 403
[MCP-DIAG] Failure classification: MCP_ACCESS_DENIED
```

If authentication information is not exposed:

```text
[MCP-DIAG] Stage: MCP_AUTHENTICATION
[MCP-DIAG] Authentication status: NOT OBSERVABLE
```

Do NOT infer:

```text
Authentication result: SUCCESS
```

merely because `mcp-remote` started.

---

### Stage 6 - MCP protocol initialization

Determine whether the MCP protocol between GitHub Copilot and the configured
MCP server was initialized successfully.

If MCP protocol events are observable, record relevant events.

For example:

```text
[MCP-DIAG] Stage: MCP_INITIALIZATION
[MCP-DIAG] MCP initialize attempted: YES
[MCP-DIAG] MCP initialize result: SUCCESS
```

If initialization fails:

```text
[MCP-DIAG] Stage: MCP_INITIALIZATION
[MCP-DIAG] MCP initialize attempted: YES
[MCP-DIAG] MCP initialize result: FAILED
[MCP-DIAG] Failure classification: MCP_INITIALIZATION_FAILED
[MCP-DIAG] Error: <sanitized error>
```

If the actual MCP protocol exchange is not visible:

```text
[MCP-DIAG] Stage: MCP_INITIALIZATION
[MCP-DIAG] MCP initialize: NOT OBSERVABLE
```

If observable, also record:

```text
[MCP-DIAG] MCP notifications/initialized: SUCCESS | FAILED | NOT OBSERVABLE
```

NEVER log an MCP session ID.

---

### Stage 7 - MCP tool discovery

Determine whether the tools exposed by Atlassian Rovo MCP are available to
Copilot Code Review.

At minimum, check for:

- `getJiraIssue`
- `searchJiraIssuesUsingJql`

Record:

```text
[MCP-DIAG] Stage: MCP_TOOL_DISCOVERY
[MCP-DIAG] Tool getJiraIssue available: YES | NO
[MCP-DIAG] Tool searchJiraIssuesUsingJql available: YES | NO
```

If the underlying `tools/list` MCP operation is observable:

```text
[MCP-DIAG] MCP operation: tools/list
[MCP-DIAG] tools/list result: SUCCESS | FAILED
```

If `tools/list` fails:

```text
[MCP-DIAG] Failure classification: MCP_TOOL_DISCOVERY_FAILED
[MCP-DIAG] Error: <sanitized error>
```

If the tools are not available:

```text
[MCP-DIAG] Failure classification: MCP_TOOL_UNAVAILABLE
```

Do NOT attempt to retrieve Jira through an unavailable tool.

---

### Stage 8 - Jira issue detection

Detect the Jira issue key from the pull request.

Record:

```text
[MCP-DIAG] Stage: JIRA_ISSUE_DETECTION
[MCP-DIAG] Detected Jira issue: LAKWYC-201
[MCP-DIAG] cloudId: edf56f73-7260-4bb3-ae3a-1450c0895e6a
```

If no Jira issue was found:

```text
[MCP-DIAG] Stage: JIRA_ISSUE_DETECTION
[MCP-DIAG] Detected Jira issue: NONE
```

Do not construct or guess a Jira issue key.

---

### Stage 9 - Before EVERY MCP tool invocation

Before EVERY MCP tool invocation, record the intended invocation.

For `getJiraIssue`:

```text
[MCP-DIAG] Stage: MCP_TOOL_CALL
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Call attempted: YES
[MCP-DIAG] cloudId: edf56f73-7260-4bb3-ae3a-1450c0895e6a
[MCP-DIAG] Jira issue: LAKWYC-201
```

For `searchJiraIssuesUsingJql`:

```text
[MCP-DIAG] Stage: MCP_TOOL_CALL
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Tool: searchJiraIssuesUsingJql
[MCP-DIAG] Call attempted: YES
[MCP-DIAG] cloudId: edf56f73-7260-4bb3-ae3a-1450c0895e6a
[MCP-DIAG] JQL: project = LAKWYC ...
```

Record EVERY MCP invocation separately.

Do NOT combine multiple calls into a single diagnostic event.

Do NOT include secrets or authentication information in tool-call diagnostics.

---

### Stage 10 - After EVERY MCP tool invocation

After EVERY MCP tool invocation, record its result.

Successful `getJiraIssue` example:

```text
[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: SUCCESS
[MCP-DIAG] Jira response received: YES
[MCP-DIAG] Returned issue key: LAKWYC-201
[MCP-DIAG] Failure classification: NONE
```

Failed invocation example:

```text
[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: FAILED
[MCP-DIAG] Jira response received: NO
[MCP-DIAG] Failure classification: MCP_TOOL_CALL_FAILED
[MCP-DIAG] Error: <sanitized error>
```

If the tool reports an authentication failure:

```text
[MCP-DIAG] Result: FAILED
[MCP-DIAG] Failure classification: MCP_AUTHENTICATION_FAILED
[MCP-DIAG] Error: <sanitized error>
```

If Jira denies access:

```text
[MCP-DIAG] Result: FAILED
[MCP-DIAG] Failure classification: JIRA_ACCESS_DENIED
[MCP-DIAG] Error: <sanitized error>
```

If the Jira issue does not exist:

```text
[MCP-DIAG] Result: FAILED
[MCP-DIAG] Failure classification: JIRA_ISSUE_NOT_FOUND
```

If a valid Jira issue is returned:

```text
[MCP-DIAG] Result: SUCCESS
[MCP-DIAG] Jira response received: YES
[MCP-DIAG] Returned issue key: LAKWYC-201
```

---

### Stage 11 - Jira response validation

When a Jira issue is successfully returned, validate the response.

Record:

```text
[MCP-DIAG] Stage: JIRA_RESPONSE_VALIDATION
[MCP-DIAG] Jira response received: YES
[MCP-DIAG] Returned issue key: LAKWYC-201
[MCP-DIAG] Expected project: LAKWYC
[MCP-DIAG] Jira project validation: SUCCESS
```

Verify that the returned issue key starts with:

`LAKWYC-`

If it does not:

```text
[MCP-DIAG] Stage: JIRA_RESPONSE_VALIDATION
[MCP-DIAG] Jira project validation: FAILED
[MCP-DIAG] Failure classification: JIRA_RESPONSE_INVALID
```

Do not use a returned Jira issue from another project.

---

### Stage 12 - Final MCP diagnostic summary

At the end of the review, summarize the complete observable MCP chain.

Always include:

```text
[MCP-DIAG] ===== MCP DIAGNOSTIC SUMMARY =====
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] MCP type: local
[MCP-DIAG] Local MCP process: SUCCESS | FAILED | NOT OBSERVABLE
[MCP-DIAG] mcp-remote startup: SUCCESS | FAILED | NOT OBSERVABLE
[MCP-DIAG] Atlassian endpoint: https://mcp.atlassian.com/v1/mcp
[MCP-DIAG] Atlassian connection: SUCCESS | FAILED | NOT OBSERVABLE
[MCP-DIAG] Authentication: SUCCESS | FAILED | NOT OBSERVABLE
[MCP-DIAG] MCP initialization: SUCCESS | FAILED | NOT OBSERVABLE
[MCP-DIAG] getJiraIssue available: YES | NO
[MCP-DIAG] searchJiraIssuesUsingJql available: YES | NO
[MCP-DIAG] Jira request attempted: YES | NO
[MCP-DIAG] Jira response received: YES | NO
[MCP-DIAG] Failure classification: <classification>
[MCP-DIAG] ==================================
```

Use the most specific failure classification supported by the observable
information.

---

## MCP failure classifications

Use exactly one of the following values where possible:

- `NONE`
- `MCP_SERVER_UNAVAILABLE`
- `MCP_PROCESS_START_FAILED`
- `MCP_REMOTE_BRIDGE_FAILED`
- `MCP_CONNECTION_FAILED`
- `MCP_INITIALIZATION_FAILED`
- `MCP_AUTHENTICATION_FAILED`
- `MCP_ACCESS_DENIED`
- `MCP_TOOL_DISCOVERY_FAILED`
- `MCP_TOOL_UNAVAILABLE`
- `MCP_TOOL_CALL_FAILED`
- `JIRA_ACCESS_DENIED`
- `JIRA_ISSUE_NOT_FOUND`
- `JIRA_RESPONSE_INVALID`
- `UNKNOWN_MCP_FAILURE`

Always choose the earliest and most specific observable failure in the
MCP/Jira chain.

For example:

```text
MCP server unavailable
    -> MCP_SERVER_UNAVAILABLE

Gepardec exists but local process fails
    -> MCP_PROCESS_START_FAILED

mcp-remote starts but cannot connect to Atlassian
    -> MCP_CONNECTION_FAILED

Atlassian returns authentication failure
    -> MCP_AUTHENTICATION_FAILED

Authentication works but tools cannot be discovered
    -> MCP_TOOL_DISCOVERY_FAILED

getJiraIssue exists but invocation fails
    -> MCP_TOOL_CALL_FAILED

getJiraIssue succeeds but Jira denies access
    -> JIRA_ACCESS_DENIED
```

---

## Important diagnostic accuracy rules

Do not infer information that is not actually observable.

In particular:

- MCP server configured does NOT mean MCP server started.
- MCP server started does NOT mean `mcp-remote` connected to Atlassian.
- `mcp-remote` started does NOT mean authentication succeeded.
- Authentication success does NOT mean Jira access succeeded.
- Tool availability does NOT mean the tool was invoked.
- Tool invocation does NOT mean Jira returned a response.
- Jira response does NOT automatically mean the correct project was accessed.

If a stage cannot be observed, record:

`NOT OBSERVABLE`

instead of guessing.

The goal of the diagnostics is to identify the exact last observable
successful stage and the first observable failing stage.

## Security requirements for diagnostics

NEVER log or expose:

- Authorization headers
- API tokens
- Basic Auth values
- Bearer tokens
- GitHub secrets
- MCP session IDs
- cookies
- OAuth tokens
- environment variables containing credentials

NEVER print the value of:

`COPILOT_MCP_ATLASSIAN_AUTH`

Replace sensitive values with:

`[REDACTED]`

The following values MAY be logged:

- MCP server name
- MCP tool name
- Jira cloudId
- Jira issue key
- JQL query, if it contains no sensitive information
- HTTP status code, if observable
- sanitized error messages

---

# Retrieve the Jira issue

For every identified Jira issue:

1. Use the `getJiraIssue` tool from MCP server `Gepardec`.
2. Use the cloudId:

   `edf56f73-7260-4bb3-ae3a-1450c0895e6a`

3. Retrieve the identified Jira issue.
4. Verify that the returned issue key starts with `LAKWYC-`.

Before calling `getJiraIssue`, write the corresponding MCP diagnostic entry.

After calling `getJiraIssue`, write the corresponding MCP result entry.

Read the available:

- summary
- description
- acceptance criteria
- issue type
- relevant requirement information
- relevant linked issues when required to understand the acceptance criteria

Do not use another Atlassian cloudId.

Do not search another Jira project.

If Jira cannot be retrieved, do not invent Jira contents or acceptance criteria.

---

## Jira search

Only use `searchJiraIssuesUsingJql` if retrieving the issue directly with `getJiraIssue` is insufficient.

Every JQL query MUST contain:

`project = LAKWYC`

Every Jira search MUST use the cloudId:

`edf56f73-7260-4bb3-ae3a-1450c0895e6a`

Never execute an unrestricted Jira query.

Never search another Jira project.

Log the invocation and result of EVERY `searchJiraIssuesUsingJql` call using the MCP diagnostic format.

---

# Source of truth

The retrieved Jira issue is the source of truth for functional requirements.

Do not treat the following as authoritative requirements when Jira is available:

- pull request description
- commit messages
- source code comments
- assumptions based on the implementation

These sources may provide additional context but must not override Jira requirements.

---

# Requirement verification

Compare the implementation in the pull request against every acceptance criterion found in Jira.

For each acceptance criterion, assign exactly one of the following states:

## PASS

Use `PASS` only when there is clear evidence in code, tests, or configuration that the acceptance criterion is fulfilled.

## PARTIAL

Use `PARTIAL` when only part of the acceptance criterion is implemented or evidence is incomplete.

## FAIL

Use `FAIL` when required functionality is missing or contradicts the Jira requirement.

## NOT VERIFIABLE

Use `NOT VERIFIABLE` when the criterion cannot reasonably be verified from repository code, tests, or configuration.

Never mark an acceptance criterion as `PASS` solely because the pull request description claims it is implemented.

---

# Evidence

For every `PASS`, `PARTIAL`, or `FAIL`, provide concrete evidence whenever possible.

Reference:

- file
- class
- method
- function
- changed code
- configuration
- unit test
- integration test

Prefer evidence from the pull request changes.

---

# Tests

For every requirement, check whether appropriate automated tests exist.

Consider:

- unit tests
- integration tests
- negative tests
- boundary conditions
- error cases

A test alone is not sufficient evidence that a requirement is implemented correctly.

---

# Technical code review

Also perform a normal technical review.

Check:

- functional correctness
- potential bugs
- error handling
- security
- maintainability
- unnecessary complexity
- architecture violations
- regression risks
- missing tests
- incorrect tests
- concurrency issues when relevant
- resource handling when relevant

Focus on actionable findings.

Avoid purely stylistic comments unless they violate established project conventions.

---

# Required review output

The pull request review MUST contain these sections:

1. `Jira requirement verification`
2. `MCP / Jira diagnostics`
3. normal technical code review findings

---

## Jira requirement verification

Clearly state the Jira issue used.

Example:

### Jira requirement verification

Jira issue: `LAKWYC-201`

| Acceptance criterion | Status | Evidence |
|---|---|---|
| AC1 | PASS | Implemented in `PaymentValidator` |
| AC2 | PARTIAL | Validation exists but negative case is not tested |
| AC3 | FAIL | Required audit logging could not be found |
| AC4 | NOT VERIFIABLE | Depends on external production configuration |

If Jira retrieval failed, write:

`Jira requirement verification could not be completed.`

Do NOT invent acceptance criteria.

---

## MCP / Jira diagnostics

ALWAYS include a diagnostic summary.

Example success:

| Check | Result |
|---|---|
| Expected MCP server | `Gepardec` |
| MCP server visible | YES |
| MCP connection | NOT OBSERVABLE |
| MCP initialization | NOT OBSERVABLE |
| MCP authentication | NOT OBSERVABLE |
| `getJiraIssue` available | YES |
| `searchJiraIssuesUsingJql` available | YES |
| Jira issue detected | `LAKWYC-201` |
| cloudId | `edf56f73-7260-4bb3-ae3a-1450c0895e6a` |
| Jira request attempted | YES |
| Jira response received | YES |
| Tool result | SUCCESS |
| Failure classification | `NONE` |
| Low-level transport details | NOT OBSERVABLE |
| Diagnostic file | `/tmp/copilot-mcp-diagnostics.log` |

Example failure:

| Check | Result |
|---|---|
| Expected MCP server | `Gepardec` |
| MCP server visible | UNKNOWN |
| MCP connection | NOT OBSERVABLE |
| MCP initialization | NOT OBSERVABLE |
| MCP authentication | NOT OBSERVABLE |
| `getJiraIssue` available | NO |
| Jira issue detected | `LAKWYC-201` |
| cloudId | `edf56f73-7260-4bb3-ae3a-1450c0895e6a` |
| Jira request attempted | NO |
| Jira response received | NO |
| Failure classification | `MCP_TOOL_UNAVAILABLE` |
| Error | `getJiraIssue is not available in this review environment` |
| Low-level transport details | NOT OBSERVABLE |

After the table, include all observable MCP diagnostic events in chronological order.

Example:

```text
[MCP-DIAG] Stage: MCP_CONFIGURATION
[MCP-DIAG] Expected MCP server: Gepardec
[MCP-DIAG] MCP server visible: YES

[MCP-DIAG] Stage: MCP_INITIALIZATION
[MCP-DIAG] MCP initialization status: NOT OBSERVABLE
[MCP-DIAG] MCP authentication status: NOT OBSERVABLE

[MCP-DIAG] Stage: MCP_TOOL_DISCOVERY
[MCP-DIAG] Tool getJiraIssue available: YES

[MCP-DIAG] Stage: JIRA_ISSUE_DETECTION
[MCP-DIAG] Detected Jira issue: LAKWYC-201
[MCP-DIAG] cloudId: edf56f73-7260-4bb3-ae3a-1450c0895e6a

[MCP-DIAG] Stage: MCP_TOOL_CALL
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Call attempted: YES
[MCP-DIAG] Jira issue: LAKWYC-201

[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: SUCCESS
[MCP-DIAG] Jira response received: YES
[MCP-DIAG] Failure classification: NONE
```

---

# Safety constraints

Never use an Atlassian cloudId other than:

`edf56f73-7260-4bb3-ae3a-1450c0895e6a`

Never access Jira projects other than `LAKWYC`.

Never execute unrestricted JQL queries.

Never modify Jira issues.

Never create Jira issues.

Never transition Jira issues.

Never add Jira comments.

Treat Jira access as read-only.

Never expose credentials, authorization headers, API tokens, GitHub secrets, cookies, OAuth tokens, or MCP session identifiers.