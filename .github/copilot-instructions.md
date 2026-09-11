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
   cookies, OAuth tokens, or MCP session IDs.
10. Always include:
   - `Jira requirement verification`
   - normal technical review findings

For detailed Jira retrieval, requirement verification, review summary generation,
and review publishing behavior, use the `code-review` agent skill.

The consolidated review summary produced by the `code-review` agent skill
must be published as a top-level pull request comment using the available
comment publishing capability.

Showing the consolidated review summary only in the Copilot Code Review
session, automatic review summary, logs, or internal output is not sufficient.