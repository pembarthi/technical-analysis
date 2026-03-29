I'll search the workspace for references to `mcp.json` and language-server-related keywords and review results.I'll run a semantic search for "mcp.json" and language-server-related keywords and review results.I'll search the workspace for any existing `mcp.json` files and related config to ensure the plan's checks and edits match the user's environment. I'll run a file search for "mcp.json" and for occurrences of "mcp" in config files.## Plan: Troubleshoot mcp.json language server registration

TL;DR — Verify the `mcp.json` file and the server JAR are valid and runnable, then try alternate `mcp.json` variants (full java path, args array, forward slashes, TCP transport). Run PowerShell checks to validate jar runs and to reproduce server start, inspect IDE logs, and iterate until IntelliJ recognizes the tool.

### Steps
1. Validate `mcp.json` syntax and required fields in `C:\Users\pemba\AppData\Local\github-copilot\intellij\mcp.json`.  
2. Verify the JAR file path exists and is runnable with `java -jar` from PowerShell.  
3. Check IntelliJ/Copilot logs and the IDE working directory for registration errors.  
4. Try safe `mcp.json` variants: full `java.exe` command, `args` array, forward-slash paths, and a TCP mode fallback.  
5. Restart IntelliJ (and optionally the Copilot/IDE plugin) and confirm the tool appears; iterate variants if needed.

### Checks to run (what and how)
- Validate JSON syntax:
  - PowerShell: attempt to parse the file: `Get-Content 'C:\Users\pemba\AppData\Local\github-copilot\intellij\mcp.json' -Raw | ConvertFrom-Json` (wrap in try/catch to capture parse errors).
- Verify file paths:
  - Test that the server JAR exists: `Test-Path 'C:\path\to\server.jar'`.
  - If `mcp.json` references a directory in `cwd`, verify that path: `Test-Path 'C:\path\to\cwd'`.
- Test jar is executable:
  - Run `java -jar` to start server and observe output: `& java -jar 'C:\path\to\server.jar'` (watch for "MCP Server running on STDIO..." or similar).
  - If using a specific Java installation, run the full `java.exe` path: `& 'C:\Program Files\Java\jdk-17\bin\java.exe' -jar 'C:\path\to\server.jar'`.
- Validate MCP schema fields:
  - Confirm `mcp.json` includes a name/id and a command entry; ensure `type` matches the server transport (`stdio` or `tcp`), and `args` is an array if present.
  - If `type` is `tcp`, ensure `host` and `port` are provided and server supports TCP.
- Read IntelliJ logs:
  - Open "Help | Show Log in Explorer" or inspect `%USERPROFILE%\.IntelliJIdea<ver>\system\log\idea.log` and Copilot plugin logs under `%LOCALAPPDATA%\github-copilot\intellij\` for errors about launching or parsing `mcp.json`.
- Check environment/permissions:
  - Ensure the user account can execute the JAR and read the `mcp.json` file.

### Likely root causes and fixes
1. Path escaping / invalid path:
   - Cause: Backslashes in JSON double-escaped or wrong path.  
   - Fix: Use `args` array (separate `java` and `-jar` and JAR path) or forward slashes, or use full `java.exe` path in `command`.
2. Current working directory (cwd) mismatch:
   - Cause: IDE launches the command from a different cwd, causing relative paths to fail.  
   - Fix: Add a `cwd` field with absolute path or make the `command` use absolute jar path.
3. Wrong transport type (`stdio` vs `tcp`):
   - Cause: `mcp.json` says `stdio` but server listens on TCP (or vice versa).  
   - Fix: Change `type` to `tcp` and include host/port, or update server to use STDIO.
4. JSON schema/field mismatch:
   - Cause: `mcp.json` missing required fields or field names differ from MCP schema.  
   - Fix: Ensure `name`/`id`, `command`, `type` and `args`/`env` follow spec. Compare with official MCP example and the server's expected registration keys.
5. Quoting/escaping issues with spaces:
   - Cause: Path contains spaces (e.g. "Program Files") and is not quoted properly when the `command` is a single string.  
   - Fix: Use `command` = full path to `java.exe` and put remaining parts in `args` array to avoid quoting problems.
6. Permissions/antivirus blocking:
   - Cause: Anti-malware blocks the JAR from executing.  
   - Fix: Temporarily disable or create an allow rule for the JAR or java process.

### Exact `mcp.json` content variants to try
Note: replace `C:\path\to\server.jar` with the absolute path to your JAR.

Variant A — Windows-safe using `command` = `java` and `args` array (recommended)
`{ "name":"stock-mcp-server", "command":"java", "args":["-jar","C:\\path\\to\\server.jar"], "type":"stdio", "cwd":null, "env":{} }`

Variant B — Windows full java.exe absolute path (avoids PATH issues)
`{ "name":"stock-mcp-server", "command":"C:\\Program Files\\Java\\jdk-17\\bin\\java.exe", "args":["-jar","C:\\path\\to\\server.jar"], "type":"stdio", "cwd":"C:\\path\\to", "env":{} }`

Variant C — Forward-slash paths (sometimes avoids escaping problems)
`{ "name":"stock-mcp-server", "command":"java", "args":["-jar","C:/path/to/server.jar"], "type":"stdio", "cwd":"C:/path/to", "env":{} }`

Variant D — TCP transport fallback (if server supports TCP)
`{ "name":"stock-mcp-server", "command":"java", "args":["-jar","C:\\path\\to\\server.jar","--tcp","--port","5007"], "type":"tcp", "host":"127.0.0.1", "port":5007, "cwd":"C:\\path\\to", "env":{} }`

Variant E — Provide JAVA_HOME in `env` if PATH is wrong
`{ "name":"stock-mcp-server", "command":"%JAVA_HOME%\\bin\\java.exe", "args":["-jar","C:\\path\\to\\server.jar"], "type":"stdio", "env":{"JAVA_HOME":"C:\\Program Files\\Java\\jdk-17"} }`

Variant F — Minimal test entry (start-only) to isolate registration
`{ "name":"stock-mcp-server-test", "command":"java", "args":["-jar","C:\\path\\to\\server.jar","--version"], "type":"stdio" }`

How to iterate:
- Start with Variant A. If IntelliJ still doesn't register the tool, try Variant B (absolute java path).  
- If server logs show it expects TCP, switch to Variant D and ensure server listens on the specified port.  
- If path quoting/escape issues persist, try Variant C (forward slashes) or Variant E (set env).

### PowerShell commands to validate and reproduce server start
- Validate JSON (will throw on parse error):
  - try { Get-Content 'C:\\Users\\pemba\\AppData\\Local\\github-copilot\\intellij\\mcp.json' -Raw | ConvertFrom-Json; Write-Host 'JSON ok' } catch { Write-Host 'JSON error:' $_.Exception.Message }
- Check path exists:
  - Test-Path 'C:\\path\\to\\server.jar' ; if (-not $?) { Write-Host 'JAR not found' }
- Run JAR interactively (watch stderr/stdout):
  - & java -jar 'C:\\path\\to\\server.jar'
  - If java not in PATH: & 'C:\\Program Files\\Java\\jdk-17\\bin\\java.exe' -jar 'C:\\path\\to\\server.jar'
- Run JAR and background it (Start-Process):
  - Start-Process -FilePath java -ArgumentList '-jar','C:\\path\\to\\server.jar' -NoNewWindow -PassThru
- Observe expected STDIO message:
  - Look for "MCP Server running on STDIO..." on the console output or in the process output stream.
- Simulate TCP connection (if TCP variant used):
  - Test-NetConnection -ComputerName 127.0.0.1 -Port 5007
- Show IntelliJ/Copilot logs (open folder in Explorer):
  - explorer.exe "$env:USERPROFILE\.IntelliJIdea*\system\log"
  - explorer.exe "$env:LOCALAPPDATA\github-copilot\intellij"

### Short checklist to confirm success
1. `mcp.json` parses without errors (PowerShell `ConvertFrom-Json`) — Done/Fail.  
2. JAR path exists (`Test-Path`) and `java -jar` starts the server and shows expected startup message — Done/Fail.  
3. IntelliJ logs show successful registration or no parsing/launch error for the `mcp.json` entry — Done/Fail.  
4. The tool appears in the IDE UI where language tools are listed (or tool is callable) — Done/Fail.  
5. If using TCP, `Test-NetConnection` confirms port is listening and IntelliJ connects — Done/Fail.

### Further Considerations
1. If you still hit issues, paste the current `mcp.json` (sanitize secrets) and the exact startup output from `java -jar` for more targeted fixes.  
2. Option: run the JAR under `cmd.exe` or an elevated PowerShell if you suspect permission problems; choose safer environment based on policy.  
3. If the MCP spec is strict about field names, compare `mcp.json` to the server project's example `McpJsonMapper`/schema in the codebase (look at `io.modelcontextprotocol.json.McpJsonMapper`) for exact keys to use.
