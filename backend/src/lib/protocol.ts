import { z } from "zod";

export const ALLOWED_TOOLS = [
  "send_sms",
  "update_device",
  "create_reminder",
  "open_app",
  "save_memory"
] as const;

export const AssistantRequestSchema = z.object({
  text: z.string().trim().min(1).max(4000),
  history: z.array(z.object({
    role: z.enum(["user", "assistant"]),
    text: z.string().max(1500)
  }).strict()).max(10).default([])
}).strict();

const ProposedActionSchema = z.object({
  tool: z.enum(ALLOWED_TOOLS),
  args: z.record(z.string(), z.string()).default({})
}).strict();

export const AssistantOutputSchema = z.object({
  reply: z.string().trim().min(1).max(4000),
  actions: z.array(ProposedActionSchema).max(5).default([])
}).strict();

/**
 * Schema sent to Gemini. Gemini structured output handles free-form maps poorly,
 * so the model returns args as key/value pairs and we convert them to a record.
 */
export const ModelOutputSchema = z.object({
  reply: z.string().max(4000),
  actions: z.array(z.object({
    tool: z.string().max(40),
    args: z.array(z.object({ key: z.string().max(40), value: z.string().max(1000) })).max(16)
  })).max(5)
});

export function modelOutputToAssistantOutput(raw: z.infer<typeof ModelOutputSchema>): AssistantOutput {
  const actions = raw.actions.flatMap((a) => {
    const keys = a.args.map((p) => p.key);
    if (new Set(keys).size !== keys.length) return []; // duplicate argument keys: reject, never overwrite silently
    const parsed = ProposedActionSchema.safeParse({ tool: a.tool, args: Object.fromEntries(a.args.map((p) => [p.key, p.value])) });
    return parsed.success ? [parsed.data] : [];
  });
  return AssistantOutputSchema.parse({ reply: raw.reply.trim() || "ठीक है।", actions });
}

export type AssistantRequest = z.infer<typeof AssistantRequestSchema>;
export type AssistantOutput = z.infer<typeof AssistantOutputSchema>;

const TOOL_ARGS: Record<(typeof ALLOWED_TOOLS)[number], { required: string[]; optional: string[] }> = {
  send_sms: { required: ["recipient", "message"], optional: [] },
  update_device: { required: ["device_id", "state"], optional: [] },
  create_reminder: { required: ["title"], optional: ["category"] },
  open_app: { required: ["app_package"], optional: [] },
  save_memory: { required: ["fact"], optional: [] }
};

/** Defense-in-depth output filter. This server only proposes; Android owns consent and execution. */
export function filterProposedActions(actions: AssistantOutput["actions"]) {
  return actions.flatMap((action) => {
    if (!ALLOWED_TOOLS.includes(action.tool)) return [];
    const spec = TOOL_ARGS[action.tool];
    const entries = Object.entries(action.args);
    if (entries.length > 16) return [];
    if (entries.some(([key, value]) => !/^[a-zA-Z][a-zA-Z0-9_]{0,39}$/.test(key) || value.length > 1000)) return [];
    // Exact per-tool schema: unknown arguments are rejected, not ignored.
    if (entries.some(([key]) => !spec.required.includes(key) && !spec.optional.includes(key))) return [];
    if (spec.required.some((key) => !action.args[key]?.trim())) return [];
    return [{ tool: action.tool, args: action.args }];
  });
}
