# AI-Assisted Code Review

Setup for code reviews with the JetBrains AI Assistant (chat, Claude Agent or Junie),
including Jira access and validation of ticket requirements.

## Files

| File | Used by | Purpose |
|---|---|---|
| `.aiassistant/rules/coding-conventions.md` | AI Assistant (always), Claude Agent, Junie | Coding conventions – single source of truth |
| `.aiassistant/rules/code-review.md` | AI Assistant (`@rule:code-review`), Claude Agent, Junie | Review procedure incl. Jira requirement check |
| `.aiignore` | AI Assistant | Files that are never sent to a model |
| `CLAUDE.md` | Claude Agent | Pointer to the rules |
| `.junie/guidelines.md` | Junie | Pointer to the rules |

The Atlassian MCP server is configured **only in the IntelliJ settings**, not in a project file
(there is intentionally no `.mcp.json` in this repository).
None of these files contain credentials. Jira authentication uses OAuth in the browser.

## One-time setup

1. **Atlassian MCP in IntelliJ:**
   *Settings → Tools → AI Assistant → Model Context Protocol (MCP) → Add → As JSON*,
   paste the content of `docs/ai/mcp.example.json` (requires Node.js for `npx`).
2. On the first Jira call a browser window opens – log in with your Atlassian account and grant access.

## Running a review

Check out the feature branch (e.g. `bugfix/LAKWYC-10`) and ask in the AI chat:

- AI Assistant chat: `@rule:code-review Review this branch`
- Claude Agent / Junie: `Review this branch according to the code-review rule`

The Jira key is taken from the branch name or commit messages; you can also name it explicitly
(`@rule:code-review Review against LAKWYC-10`).
