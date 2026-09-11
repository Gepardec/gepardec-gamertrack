# Copilot Code Review Instructions

When reviewing pull requests:

1. Perform a normal technical code review.
2. Use the `code-review` agent skill for Jira requirement verification.
3. Jira issue keys match `LAKWYC-[0-9]+`.
4. Jira is the source of truth for functional requirements.
5. Use only Jira project `LAKWYC`.
6. Use only Atlassian cloudId:
   `edf56f73-7260-4bb3-ae3a-1450c0895e6a`
7. Use the configured MCP server `atlassian-rovo-mcp`.
8. Jira access is read-only.
9. Never expose credentials, API keys, authorization headers, secrets,
   cookies, or MCP session IDs.
10. Always include:
- `Jira requirement verification`
- normal technical review findings

For detailed Jira retrieval and requirement verification, use the
`code-review` agent skill.

## PR publication requirement

For pull request reviews, the review result must not remain only in the session log.

After generating the review content, always publish it to the pull request as a PR comment or PR review.

Mandatory completion flow:
1. Generate review findings.
2. Publish findings to the PR.
3. Mark the task complete only after successful publication.

If PR publication fails for technical reasons, explicitly report that failure in the final response.

Quality gate: if no PR comment/review was posted, treat the review task as failed.
