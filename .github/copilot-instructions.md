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

For every review, perform and record the following stages.

### Stage 1 - MCP configuration visibility

Determine whether the configured MCP server named `Gepardec` is visible to the review environment.

Log:

```text
[MCP-DIAG] Stage: MCP_CONFIGURATION
[MCP-DIAG] Expected MCP server: Gepardec
[MCP-DIAG] MCP server visible: YES | NO | UNKNOWN
```

If the MCP server is not available, record:

```text
[MCP-DIAG] Failure classification: MCP_SERVER_UNAVAILABLE
```

Do not claim that Atlassian was contacted if the MCP server itself is unavailable.

---

### Stage 2 - MCP connection, initialization and authentication

Determine whether the MCP connection, initialization and authentication were successful.

If the review environment exposes this information, log each observable step.

Relevant steps may include:

- connection to the configured MCP server
- MCP initialization
- MCP authentication
- MCP tool discovery
- errors returned while establishing the MCP connection

Example when the information is observable:

```text
[MCP-DIAG] Stage: MCP_CONNECTION
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Connection: SUCCESS

[MCP-DIAG] Stage: MCP_INITIALIZATION
[MCP-DIAG] Initialization: SUCCESS

[MCP-DIAG] Stage: MCP_AUTHENTICATION
[MCP-DIAG] Authentication: SUCCESS
```

Failure example:

```text
[MCP-DIAG] Stage: MCP_AUTHENTICATION
[MCP-DIAG] Authentication: FAILED
[MCP-DIAG] Failure classification: MCP_AUTHENTICATION_FAILED
[MCP-DIAG] Error: <sanitized error>
```

If authentication succeeds but initialization fails:

```text
[MCP-DIAG] Stage: MCP_INITIALIZATION
[MCP-DIAG] Initialization: FAILED
[MCP-DIAG] Failure classification: MCP_INITIALIZATION_FAILED
[MCP-DIAG] Error: <sanitized error>
```

IMPORTANT:

If the review environment does NOT expose the underlying connection,
initialization or authentication information, explicitly log:

```text
[MCP-DIAG] MCP connection status: NOT OBSERVABLE
[MCP-DIAG] MCP initialization status: NOT OBSERVABLE
[MCP-DIAG] MCP authentication status: NOT OBSERVABLE
```

Never invent or infer successful authentication.

A configured MCP server does NOT prove that authentication succeeded.

An available Jira issue key does NOT prove that the MCP server was contacted.

---

### Stage 3 - MCP tool availability

Determine whether the following tools from MCP server `Gepardec` are available:

- `getJiraIssue`
- `searchJiraIssuesUsingJql`

Record each tool separately.

Example:

```text
[MCP-DIAG] Stage: MCP_TOOL_DISCOVERY
[MCP-DIAG] Tool getJiraIssue available: YES
[MCP-DIAG] Tool searchJiraIssuesUsingJql available: YES
```

Failure example:

```text
[MCP-DIAG] Stage: MCP_TOOL_DISCOVERY
[MCP-DIAG] Tool getJiraIssue available: NO
[MCP-DIAG] Failure classification: MCP_TOOL_UNAVAILABLE
```

If the tool is unavailable, do NOT claim that a Jira request was attempted.

---

### Stage 4 - Jira issue detection

Record the Jira issue detected from the pull request.

Example:

```text
[MCP-DIAG] Stage: JIRA_ISSUE_DETECTION
[MCP-DIAG] Detected Jira issue: LAKWYC-201
[MCP-DIAG] cloudId: edf56f73-7260-4bb3-ae3a-1450c0895e6a
```

If none was detected:

```text
[MCP-DIAG] Stage: JIRA_ISSUE_DETECTION
[MCP-DIAG] Detected Jira issue: NONE
```

---

### Stage 5 - Before EVERY MCP tool call

Before EVERY invocation of an MCP tool, record the call.

For `getJiraIssue`, log:

```text
[MCP-DIAG] Stage: MCP_TOOL_CALL
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Call attempted: YES
[MCP-DIAG] cloudId: edf56f73-7260-4bb3-ae3a-1450c0895e6a
[MCP-DIAG] Jira issue: LAKWYC-201
```

For `searchJiraIssuesUsingJql`, log:

```text
[MCP-DIAG] Stage: MCP_TOOL_CALL
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Tool: searchJiraIssuesUsingJql
[MCP-DIAG] Call attempted: YES
[MCP-DIAG] cloudId: edf56f73-7260-4bb3-ae3a-1450c0895e6a
[MCP-DIAG] JQL: project = LAKWYC ...
```

Record every separate MCP tool call.

Do not combine multiple calls into one diagnostic entry.

---

### Stage 6 - After EVERY MCP tool call

After EVERY invocation of an MCP tool, record its result.

Success example:

```text
[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: SUCCESS
[MCP-DIAG] Jira response received: YES
[MCP-DIAG] Returned issue key: LAKWYC-201
[MCP-DIAG] Failure classification: NONE
```

Failure example:

```text
[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] MCP server: Gepardec
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: FAILED
[MCP-DIAG] Jira response received: NO
[MCP-DIAG] Failure classification: MCP_TOOL_CALL_FAILED
[MCP-DIAG] Error: <sanitized error>
```

If an authentication-related error is visible:

```text
[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: FAILED
[MCP-DIAG] Failure classification: MCP_AUTHENTICATION_FAILED
[MCP-DIAG] Error: Unauthorized
```

If Jira denies access:

```text
[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: FAILED
[MCP-DIAG] Failure classification: JIRA_ACCESS_DENIED
```

If the Jira issue does not exist:

```text
[MCP-DIAG] Stage: MCP_TOOL_RESULT
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: FAILED
[MCP-DIAG] Failure classification: JIRA_ISSUE_NOT_FOUND
```

---

## Failure classifications

Use exactly one of the following values where possible:

- `NONE`
- `MCP_SERVER_UNAVAILABLE`
- `MCP_CONNECTION_FAILED`
- `MCP_INITIALIZATION_FAILED`
- `MCP_AUTHENTICATION_FAILED`
- `MCP_TOOL_UNAVAILABLE`
- `MCP_TOOL_CALL_FAILED`
- `JIRA_ACCESS_DENIED`
- `JIRA_ISSUE_NOT_FOUND`
- `JIRA_RESPONSE_INVALID`
- `UNKNOWN_MCP_FAILURE`

Always use the most specific classification supported by information actually visible to the review environment.

---

## Low-level MCP transport diagnostics

Log low-level MCP information ONLY when that information is actually exposed to the review agent.

Relevant information includes:

- connection attempt to the MCP server
- MCP `initialize`
- MCP `notifications/initialized`
- MCP `tools/list`
- MCP `tools/call`
- HTTP status codes
- MCP protocol errors
- authentication errors
- connection errors
- tool invocation errors

For example, if observable:

```text
[MCP-DIAG] Transport: initialize
[MCP-DIAG] Result: SUCCESS

[MCP-DIAG] Transport: tools/list
[MCP-DIAG] Result: SUCCESS

[MCP-DIAG] Transport: tools/call
[MCP-DIAG] Tool: getJiraIssue
[MCP-DIAG] Result: SUCCESS
```

If an HTTP status is observable:

```text
[MCP-DIAG] HTTP request: POST https://mcp.atlassian.com/v1/mcp
[MCP-DIAG] HTTP status: 200
```

If authentication fails and the HTTP status is observable:

```text
[MCP-DIAG] HTTP request: POST https://mcp.atlassian.com/v1/mcp
[MCP-DIAG] HTTP status: 401
[MCP-DIAG] Failure classification: MCP_AUTHENTICATION_FAILED
```

IMPORTANT:

Do NOT invent low-level MCP transport activity.

Do NOT claim that any of the following occurred unless the review environment explicitly exposes that information:

- HTTP request to `mcp.atlassian.com`
- HTTP response status
- MCP `initialize`
- MCP `notifications/initialized`
- MCP `tools/list`
- MCP session creation
- Basic authentication handshake
- Authorization header transmission
- TLS connection
- OAuth authentication

If these details are not visible, explicitly write:

```text
[MCP-DIAG] Low-level MCP transport details: NOT OBSERVABLE
```

Do not infer:

```text
Authentication: SUCCESS
```

just because `getJiraIssue` exists.

Do not infer:

```text
MCP initialization: FAILED
```

just because Jira could not be retrieved.

Always distinguish:

1. MCP server configuration
2. MCP connection
3. MCP initialization
4. MCP authentication
5. MCP tool availability
6. MCP tool invocation
7. Jira response

---

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