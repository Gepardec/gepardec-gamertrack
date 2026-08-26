# Copilot Code Review Instructions

## Purpose

When reviewing a pull request, perform both:

1. a normal technical code review
2. a Jira requirement verification

The Jira issue is the source of truth for the functional requirements.

---

## Jira configuration

This repository belongs to the `Gepardec` Jira environment.

When using the configured MCP server `Gepardec`, ALWAYS use the following
Jira cloudId:

`edf56f73-7260-4bb3-ae3a-1450c0895e6a`

Never discover, select, infer, or use another Atlassian cloudId.

Only Jira issues from project `LAKWYC` are relevant for this repository.

Never access Jira issues from another project or another Atlassian cloud.

---

## Locate the Jira issue

Look for a Jira issue key matching:

`LAKWYC-[0-9]+`

Check the following locations:

1. pull request title
2. pull request description
3. branch name
4. commit messages, when available

Examples of valid Jira issue keys:

- `LAKWYC-1`
- `LAKWYC-20`
- `LAKWYC-201`
- `LAKWYC-12345`

If multiple Jira issue keys are found, retrieve and verify all referenced
`LAKWYC` issues that are relevant to the pull request.

If no Jira issue key matching `LAKWYC-[0-9]+` can be identified, explicitly
state:

`Jira requirement verification could not be performed because no LAKWYC Jira issue was referenced.`

Do not guess or construct a Jira issue key.

---

## Retrieve the Jira issue

For every identified Jira issue:

1. Use the configured Atlassian MCP server.
2. Use the `Gepardec` cloudId defined above.
3. Retrieve the issue using `getJiraIssue`.
4. Verify that the returned issue key starts with `LAKWYC-`.

Read the available:

- summary
- description
- acceptance criteria
- issue type
- relevant requirement information
- relevant linked issues, when their content is required to understand
  the acceptance criteria

Do not use another Atlassian cloudId.

Do not search another Jira project.

If the Jira issue cannot be retrieved, explicitly state that Jira
requirement verification could not be completed.

---

## Jira search

Only use `searchJiraIssuesUsingJql` when retrieving the issue directly
with `getJiraIssue` is insufficient.

Every JQL query MUST be restricted to the LAKWYC project.

Every JQL query MUST contain:

`project = LAKWYC`

Never execute an unrestricted Jira search.

Never search another Jira project.

Always use the `Gepardec` cloudId defined above when executing Jira searches.

---

## Source of truth

The retrieved Jira issue is the source of truth for functional requirements.

Do not treat the following as authoritative requirements when Jira is
available:

- pull request description
- commit messages
- comments in the source code
- assumptions based on the implementation

These sources may provide additional context but must not override Jira
requirements.

---

## Requirement verification

Compare the implementation in the pull request against every acceptance
criterion found in the Jira issue.

For each acceptance criterion, assign exactly one of the following states:

### PASS

Use PASS only when there is clear evidence in the code, tests, or
configuration that the acceptance criterion is fulfilled.

### PARTIAL

Use PARTIAL when only part of the acceptance criterion is implemented
or the available evidence is incomplete.

### FAIL

Use FAIL when required functionality is missing or the implementation
contradicts the acceptance criterion.

### NOT VERIFIABLE

Use NOT VERIFIABLE when the acceptance criterion cannot reasonably be
verified from the repository, pull request, tests, or configuration.

Examples include requirements depending on:

- external systems
- manual testing
- production configuration
- business processes outside the repository
- information unavailable to the reviewer

Never mark an acceptance criterion as PASS solely because the pull request
description or developer states that it has been implemented.

---

## Evidence

For every PASS, PARTIAL, or FAIL result, provide concrete evidence whenever
possible.

Reference:

- file
- class
- method or function
- relevant changed code
- configuration
- unit test
- integration test

Prefer evidence from the actual pull request changes.

---

## Tests

For every requirement, check whether appropriate automated tests exist.

Consider:

- unit tests
- integration tests
- negative test cases
- boundary conditions
- error cases

Do not classify a requirement as PASS solely because a test exists.

Verify that the implementation itself supports the requirement.

---

## Technical code review

In addition to Jira requirement verification, perform a normal code review.

Check for:

- functional correctness
- potential bugs
- error handling
- security issues
- maintainability
- unnecessary complexity
- architecture violations
- regression risks
- missing tests
- incorrect tests
- concurrency issues when relevant
- resource handling when relevant

Focus review comments on actionable findings.

Avoid comments that are purely stylistic unless they violate an established
project convention.

---

## Review output

Include a section named:

`Jira requirement verification`

Clearly state which Jira issue was used.

Example:

### Jira requirement verification

Jira issue: `LAKWYC-201`

| Acceptance criterion | Status | Evidence |
|---|---|---|
| AC1 | PASS | Implemented in `PaymentValidator` |
| AC2 | PARTIAL | Validation exists but negative case is not tested |
| AC3 | FAIL | Required audit logging could not be found |
| AC4 | NOT VERIFIABLE | Depends on external production configuration |

After the requirement verification, provide the normal technical code
review findings.

---

## Safety constraints

Never use an Atlassian cloudId other than the configured `Gepardec` cloudId.

Never access Jira projects other than `LAKWYC`.

Never execute unrestricted JQL queries.

Never modify Jira issues.

Never create Jira issues.

Never transition Jira issues.

Never add Jira comments.

The Jira integration must be treated as read-only.