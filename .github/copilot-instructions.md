# Copilot Code Review Instructions

When reviewing pull requests:

1. Perform a normal technical code review.
2. Perform Jira requirement verification using the `code-review` agent skill.
3. A Jira issue key must match `LAKWYC-[0-9]+`.
4. Jira requirements are the source of truth for functional requirements.
5. Use only Jira project `LAKWYC`.
6. Use only Atlassian cloudId:
   `edf56f73-7260-4bb3-ae3a-1450c0895e6a`
7. Use the configured MCP server `Gepardec`.
8. Jira access is read-only.
9. Never expose API keys, authorization headers, secrets, cookies, or MCP session IDs.
10. Always include:
   - `Jira requirement verification`
   - `MCP / Jira diagnostics`
   - normal technical review findings

For the detailed Jira retrieval, MCP diagnostics, requirement classification,
and review-output workflow, use the `code-review` agent skill.