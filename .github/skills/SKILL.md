---
name: code-review
description: Perform pull request code reviews with Jira requirement verification through the configured Gepardec Atlassian Rovo MCP server. Use this skill for PR reviews that reference Jira issues matching LAKWYC-[0-9]+. Retrieve the Jira issue, compare acceptance criteria against the PR changes and tests, and report MCP/Jira diagnostics when retrieval fails.
---

# Code Review with Jira Requirement Verification

Use this skill for pull request reviews in this repository.

Perform:

1. Jira requirement verification.
2. MCP/Jira diagnostics.
3. Normal technical code review.

The Jira issue is the source of truth for functional requirements.

---

## Jira configuration

Use only the configured MCP server:

`Gepardec`

Use only this Jira cloudId:

`edf56f73-7260-4bb3-ae3a-1450c0895e6a`

Use only Jira project:

`LAKWYC`

Treat Jira access as read-only.

Never discover, infer, select, or use another Atlassian cloudId.

Never access another Jira project.

---

## Detect the Jira issue

Look for a Jira issue key matching:

`LAKWYC-[0-9]+`

Search in this order:

1. Pull request title.
2. Pull request description.
3. Branch name.
4. Commit messages when available.

Examples:

- `LAKWYC-1`
- `LAKWYC-20`
- `LAKWYC-201`

Do not guess or construct an issue key.

If no matching issue key is found, report:

`Jira requirement verification could not be performed because no LAKWYC Jira issue was referenced.`

---

# Retrieve Jira requirements

For every relevant Jira issue:

1. Use `getJiraIssue` from MCP server `Gepardec`.
2. Use cloudId `edf56f73-7260-4bb3-ae3a-1450c0895e6a`.
3. Retrieve the identified `LAKWYC-*` issue.
4. Verify that the returned issue belongs to project `LAKWYC`.
5. Read the available:
    - summary
    - description
    - acceptance criteria
    - issue type
    - relevant requirement information

Prefer `getJiraIssue`.

Use `searchJiraIssuesUsingJql` only if direct retrieval is insufficient.

Every JQL search MUST include:

`project = LAKWYC`

Never execute unrestricted JQL.

Never search another project.

---

# MCP diagnostics

When Jira retrieval is attempted, collect the most specific MCP diagnostics visible to the review environment.

Do not invent information that is not observable.

Distinguish the following stages:

1. MCP server availability.
2. Local MCP process availability.
3. `mcp-remote` availability.
4. Connection to Atlassian Rovo MCP.
5. Authentication.
6. MCP initialization.
7. Tool discovery.
8. Tool invocation.
9. Jira response.

---

## Expected MCP architecture

The configured integration is expected to be:

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

Expected configuration:

- MCP server: `Gepardec`
- type: `local`
- command: `npx`
- bridge: `mcp-remote@latest`
- remote endpoint: `https://mcp.atlassian.com/v1/mcp`
- authentication: HTTP Basic
- runtime credential variable: `ATLASSIAN_API_KEY`
- GitHub Agents secret: `COPILOT_MCP_ATLASSIAN_AUTH`

Never expose either credential value.

---

## Diagnostic stages

### Stage 1 - MCP server

Record whether `Gepardec` is available.

```text
[MCP-DIAG] Stage: MCP_SERVER
[MCP-DIAG] Server: Gepardec
[MCP-DIAG] Available: YES | NO | UNKNOWN
```

If unavailable:

```text
[MCP-DIAG] Failure classification: MCP_SERVER_UNAVAILABLE
```

---

### Stage 2 - Local MCP process

If observable, report whether GitHub successfully started the local MCP process.

```text
[MCP-DIAG] Stage: MCP_PROCESS
[MCP-DIAG] Command: npx
[MCP-DIAG] Result: SUCCESS | FAILED | NOT OBSERVABLE
```

If startup output or stderr is visible, include relevant sanitized errors related to:

- `npx`
- Node.js
- package installation
- `mcp-remote`
- DNS
- network access
- TLS
- missing environment variables
- MCP protocol initialization

Never include credentials.

---

### Stage 3 - mcp-remote

If observable:

```text
[MCP-DIAG] Stage: MCP_REMOTE
[MCP-DIAG] Bridge: mcp-remote@latest
[MCP-DIAG] Startup: SUCCESS | FAILED | NOT OBSERVABLE
```

If startup fails:

```text
[MCP-DIAG] Failure classification: MCP_REMOTE_BRIDGE_FAILED
[MCP-DIAG] Error: <sanitized error>
```

---

### Stage 4 - Atlassian connection

If observable:

```text
[MCP-DIAG] Stage: ATLASSIAN_CONNECTION
[MCP-DIAG] Endpoint: https://mcp.atlassian.com/v1/mcp
[MCP-DIAG] Result: SUCCESS | FAILED | NOT OBSERVABLE
```

If an HTTP status code is visible, include it.

Examples:

```text
[MCP-DIAG] HTTP status: 200
```

```text
[MCP-DIAG] HTTP status: 401
```

```text
[MCP-DIAG] HTTP status: 403
```

---

### Stage 5 - Authentication

Only report authentication status if directly observable.

```text
[MCP-DIAG] Stage: MCP_AUTHENTICATION
[MCP-DIAG] Mechanism: Basic
[MCP-DIAG] Result: SUCCESS | FAILED | NOT OBSERVABLE
```

Never infer successful authentication merely because the MCP server is configured or `mcp-remote` started.

Never expose:

- Authorization header
- Basic Auth value
- API token
- `ATLASSIAN_API_KEY`
- `COPILOT_MCP_ATLASSIAN_AUTH`

---

### Stage 6 - MCP initialization

If observable:

```text
[MCP-DIAG] Stage: MCP_INITIALIZATION
[MCP-DIAG] initialize: SUCCESS | FAILED | NOT OBSERVABLE
```

If `notifications/initialized` is observable, report it separately.

Never log MCP session IDs.

---

### Stage 7 - Tool discovery

Check whether these tools are available:

- `getJiraIssue`
- `searchJiraIssuesUsingJql`

```text
[MCP-DIAG] Stage: MCP_TOOL_DISCOVERY
[MCP-DIAG] getJiraIssue available: YES | NO
[MCP-DIAG] searchJiraIssuesUsingJql available: YES | NO
```

If the tools are unavailable:

```text
[MCP-DIAG] Failure classification: MCP_TOOL_UNAVAILABLE
```

---

### Stage 8 - Before every MCP tool call

Before each call, record the non-sensitive parameters.

Example:

```text
[MCP-DIAG] Stage: MCP_TOOL_CALL
[MCP-DIAG] Server: Gepardec
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] cloudId: edf56f73-7260-4bb3-ae3a-1450c0895e6a
[MCP-DIAG] Jira issue: LAKWYC-201
[MCP-DIAG] Attempted: YES
```

For JQL:

```text
[MCP-DIAG] Stage: MCP_TOOL_CALL
[MCP-DIAG] Server: Gepardec
[MCP-DIAG] Tool: searchJiraIssuesUsingJql
[MCP-DIAG] cloudId: edf56f73-7260-4bb3-ae3a-1450c0895e6a
[MCP-DIAG] JQL: project = LAKWYC ...
[MCP-DIAG] Attempted: YES
```

Record each call separately.

---

### Stage 9 - After every MCP tool call

Record success or failure.

Success:

```text
[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: SUCCESS
[MCP-DIAG] Jira response received: YES
[MCP-DIAG] Returned issue: LAKWYC-201
[MCP-DIAG] Failure classification: NONE
```

Failure:

```text
[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: FAILED
[MCP-DIAG] Jira response received: NO
[MCP-DIAG] Failure classification: MCP_TOOL_CALL_FAILED
[MCP-DIAG] Error: <sanitized error>
```

Use a more specific classification when supported by the observed error.

---

## Failure classifications

Use the most specific observable classification:

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

If a stage cannot be observed, report:

`NOT OBSERVABLE`

Do not guess.

---

# Requirement verification

After retrieving Jira, compare every acceptance criterion with:

- changed code
- tests
- configuration
- relevant repository context

Classify each criterion as exactly one of:

- `PASS`
- `PARTIAL`
- `FAIL`
- `NOT VERIFIABLE`

Use `PASS` only when repository evidence clearly supports the criterion.

Use `PARTIAL` when implementation or evidence is incomplete.

Use `FAIL` when required functionality is missing or contradicts Jira.

Use `NOT VERIFIABLE` when the criterion depends on information outside the repository, such as:

- external systems
- manual tests
- production configuration
- business processes unavailable to the reviewer

Never mark a requirement `PASS` solely because the PR description says it was implemented.

Do not invent requirements if Jira retrieval failed.

---

# Evidence

For `PASS`, `PARTIAL`, and `FAIL`, cite concrete evidence when possible:

- file
- class
- method
- function
- changed code
- configuration
- unit test
- integration test

Prefer evidence from the pull request changes.

Also check for:

- missing tests
- incorrect tests
- negative cases
- boundary conditions
- error cases

---

# Technical review

Perform a normal technical review in addition to Jira verification.

Check for:

- functional correctness
- bugs
- error handling
- security
- maintainability
- unnecessary complexity
- architecture violations
- regression risks
- missing or incorrect tests
- concurrency issues when relevant
- resource handling when relevant

Focus on actionable findings.

Avoid purely stylistic comments unless they violate established project conventions.

---

# Required review output

Always include:

## Jira requirement verification

State the Jira issue used.

Example:

```text
Jira issue: LAKWYC-201
```

Use a table:

| Acceptance criterion | Status | Evidence |
|---|---|---|
| AC1 | PASS | Implemented in `PaymentValidator` |
| AC2 | PARTIAL | Validation exists but negative case is not tested |
| AC3 | FAIL | Required audit logging could not be found |
| AC4 | NOT VERIFIABLE | Depends on external configuration |

If Jira retrieval failed:

`Jira requirement verification could not be completed.`

Do not invent acceptance criteria.

---

## MCP / Jira diagnostics

Always include a concise diagnostic summary.

Example:

| Check | Result |
|---|---|
| MCP server | `Gepardec` |
| MCP server available | YES |
| Local MCP process | NOT OBSERVABLE |
| `mcp-remote` | NOT OBSERVABLE |
| Atlassian connection | NOT OBSERVABLE |
| Authentication | NOT OBSERVABLE |
| `getJiraIssue` available | YES |
| Jira issue | `LAKWYC-201` |
| Jira request attempted | YES |
| Jira response received | YES |
| Failure classification | `NONE` |

Then include the relevant chronological `[MCP-DIAG]` entries.

Do not include secrets.

---

# Security constraints

Never access another Atlassian cloudId.

Never access Jira projects other than `LAKWYC`.

Never execute unrestricted JQL.

Never modify Jira issues.

Never create Jira issues.

Never transition Jira issues.

Never add Jira comments.

Never expose:

- API tokens
- authorization headers
- Basic Auth values
- GitHub secrets
- cookies
- OAuth tokens
- MCP session IDs

Treat all Jira access as read-only.