---
name: code-review
description: Perform pull request code reviews with Jira requirement verification through the configured Atlassian Rovo MCP server. Use for PR reviews referencing LAKWYC Jira issues.
---

# Code Review with Jira Requirement Verification

Use this skill for pull request reviews in this repository.

Perform:

1. Jira requirement verification.
2. Normal technical code review.

The Jira issue is the source of truth for functional requirements.

---

## Jira configuration

Use only the configured MCP server:

`atlassian-rovo-mcp`

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
If a LAKWYC Jira issue key is present, Jira requirement verification is mandatory.

Before performing requirement verification, call `getJiraIssue`
from MCP server `atlassian-rovo-mcp`.

If `atlassian-rovo-mcp` or `getJiraIssue` is not available to the review agent,
do not silently skip Jira verification.

Report exactly:

Jira requirement verification could not be completed because the configured
Atlassian MCP server or getJiraIssue tool was unavailable to Copilot Code Review.

For every relevant Jira issue:

1. Use `getJiraIssue` from MCP server `atlassian-rovo-mcp`.
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