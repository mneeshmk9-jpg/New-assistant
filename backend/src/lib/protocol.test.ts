import { describe, expect, it } from "vitest";
import { filterProposedActions, modelOutputToAssistantOutput } from "./protocol";
import { isAuthorizedDeviceToken, readBearerToken } from "./device-auth";
import { createHash } from "node:crypto";

describe("server proposal boundary", () => {
  it("allows only the five Android-supported tools", () => {
    const allowed = ["send_sms", "update_device", "create_reminder", "open_app", "save_memory"] as const;
    for (const tool of allowed) {
      const args = tool === "send_sms" ? { recipient: "Asha", message: "Hi" }
        : tool === "update_device" ? { device_id: "flashlight", state: "on" }
        : tool === "create_reminder" ? { title: "Call back" }
        : tool === "open_app" ? { app_package: "com.example.app" }
        : { fact: "Prefers Hindi" };
      expect(filterProposedActions([{ tool, args }])).toHaveLength(1);
    }
  });

  it("drops update_policy, delete_data and incomplete proposals", () => {
    const hostile = [
      { tool: "update_policy", args: { policy: "off" } },
      { tool: "delete_data", args: { scope: "all" } },
      { tool: "send_sms", args: { message: "No recipient" } }
    ] as never;
    expect(filterProposedActions(hostile)).toEqual([]);
  });

  it("drops update_device without device_id (Android cannot execute it)", () => {
    expect(filterProposedActions([{ tool: "update_device", args: { state: "on" } }])).toEqual([]);
  });

  it("converts model key/value args and drops disallowed tools", () => {
    const out = modelOutputToAssistantOutput({
      reply: "ok",
      actions: [
        { tool: "create_reminder", args: [{ key: "title", value: "Call" }] },
        { tool: "delete_data", args: [{ key: "scope", value: "all" }] }
      ]
    });
    expect(out.actions).toEqual([{ tool: "create_reminder", args: { title: "Call" } }]);
  });

  it("rejects unknown arguments but allows optional category", () => {
    expect(filterProposedActions([{ tool: "create_reminder", args: { title: "A", category: "Work" } }])).toHaveLength(1);
    expect(filterProposedActions([{ tool: "create_reminder", args: { title: "A", extra: "x" } }])).toEqual([]);
    expect(filterProposedActions([{ tool: "open_app", args: { app_package: "a.b", app_name: "x" } }])).toEqual([]);
  });

  it("drops actions with duplicate argument keys", () => {
    const out = modelOutputToAssistantOutput({
      reply: "ok",
      actions: [{ tool: "save_memory", args: [{ key: "fact", value: "a" }, { key: "fact", value: "b" }] }]
    });
    expect(out.actions).toEqual([]);
  });

  it("rejects missing or malformed device token hashes", () => {
    expect(isAuthorizedDeviceToken("jv_" + "a".repeat(43), "bad-hash")).toBe(false);
    expect(isAuthorizedDeviceToken(null, "a".repeat(64))).toBe(false);
  });

  it("validates a correctly hashed device token", () => {
    const token = "jv_" + "A".repeat(43);
    const digest = createHash("sha256").update(token).digest("hex");
    expect(isAuthorizedDeviceToken(token, digest)).toBe(true);
    expect(isAuthorizedDeviceToken(token + "x", digest)).toBe(false);
  });

  it("parses only a bearer authorization header", () => {
    expect(readBearerToken("Bearer jv_abc")).toBe("jv_abc");
    expect(readBearerToken("Basic abc")).toBeNull();
  });
});
