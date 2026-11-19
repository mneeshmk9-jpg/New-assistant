import { createHash, timingSafeEqual } from "node:crypto";

const TOKEN_PATTERN = /^jv_[A-Za-z0-9_-]{43}$/;

export function isAuthorizedDeviceToken(token: string | null | undefined, expectedHash = process.env.JARVIS_DEVICE_TOKEN_SHA256): boolean {
  if (!token || !TOKEN_PATTERN.test(token) || !expectedHash || !/^[a-fA-F0-9]{64}$/.test(expectedHash)) return false;
  const actual = createHash("sha256").update(token, "utf8").digest();
  const expected = Buffer.from(expectedHash, "hex");
  return actual.length === expected.length && timingSafeEqual(actual, expected);
}

export function readBearerToken(header: string | null): string | null {
  if (!header) return null;
  const match = /^Bearer ([^\s]+)$/i.exec(header.trim());
  return match?.[1] ?? null;
}
