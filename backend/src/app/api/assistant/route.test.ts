import { createHash } from "node:crypto";
import { beforeEach, describe, expect, it, vi } from "vitest";

vi.mock("@/lib/llm-router", () => ({ routeAssistantRequest: vi.fn() }));

import { routeAssistantRequest } from "@/lib/llm-router";
import { POST } from "./route";

const TOKEN = "jv_" + "B".repeat(43);
const mockedRouter = vi.mocked(routeAssistantRequest);

function makeRequest(body: unknown, token: string | null = TOKEN) {
  return new Request("http://localhost/api/assistant", {
    method: "POST",
    headers: { "content-type": "application/json", ...(token ? { authorization: `Bearer ${token}` } : {}) },
    body: JSON.stringify(body)
  });
}

async function readEvents(res: Response) {
  const text = await res.text();
  return text.split("\n\n").filter(Boolean).map((chunk) => JSON.parse(chunk.replace(/^data: /, "")));
}

describe("POST /api/assistant", () => {
  beforeEach(() => {
    process.env.JARVIS_DEVICE_TOKEN_SHA256 = createHash("sha256").update(TOKEN).digest("hex");
    mockedRouter.mockReset();
  });

  it("rejects requests without a valid device token", async () => {
    expect((await POST(makeRequest({ text: "hi" }, null))).status).toBe(401);
    expect((await POST(makeRequest({ text: "hi" }, "jv_" + "C".repeat(43)))).status).toBe(401);
  });

  it("rejects invalid bodies", async () => {
    expect((await POST(makeRequest({ text: "" }))).status).toBe(400);
    expect((await POST(makeRequest({ text: "hi", extra: 1 }))).status).toBe(400);
  });

  it("streams text_delta, plan, action (consent required) and a final done", async () => {
    mockedRouter.mockResolvedValue({ reply: "Theek hai", actions: [{ tool: "create_reminder", args: { title: "Call" } }] });
    const events = await readEvents(await POST(makeRequest({ text: "reminder lagao" })));
    expect(events.map((e) => e.type)).toEqual(["text_delta", "plan", "action", "done"]);
    const action = events.find((e) => e.type === "action");
    expect(action.requiresConsent).toBe(true);
    expect(action.allowParallel).toBe(false);
    expect(action.tool).toBe("create_reminder");
  });

  it("never emits disallowed tools even if the router returns them", async () => {
    mockedRouter.mockResolvedValue({
      reply: "ok",
      actions: [{ tool: "delete_data", args: { scope: "all" } }, { tool: "update_policy", args: { p: "x" } }]
    } as never);
    const events = await readEvents(await POST(makeRequest({ text: "delete everything" })));
    expect(events.map((e) => e.type)).toEqual(["text_delta", "done"]);
  });

  it("ends with a terminal error event when the model fails", async () => {
    mockedRouter.mockRejectedValue(new Error("upstream boom"));
    const events = await readEvents(await POST(makeRequest({ text: "hi" })));
    expect(events).toEqual([{ type: "error", code: "INTERNAL", message: "Assistant request failed" }]);
  });
});
