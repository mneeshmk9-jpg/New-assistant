import { describe, expect, it } from "vitest";
import { createRateLimiter } from "./rate-limit";

describe("rate limiter", () => {
  it("blocks after the limit and recovers after the window", () => {
    let t = 0;
    const allow = createRateLimiter(2, 1000, () => t);
    expect(allow("d")).toBe(true);
    expect(allow("d")).toBe(true);
    expect(allow("d")).toBe(false);
    t = 1001;
    expect(allow("d")).toBe(true);
  });
});
