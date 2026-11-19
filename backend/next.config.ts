import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  poweredByHeader: false,
  // The API route uses Node crypto and Genkit's Google provider.
  serverExternalPackages: ["@genkit-ai/google-genai"]
};

export default nextConfig;
