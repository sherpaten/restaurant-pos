const BASE_URL = "http://localhost:8080/api";

export async function apiPost(path, body) {
  const res = await fetch(`${BASE_URL}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  const json = await res.json().catch(() => null);
  if (!res.ok || json?.success === false) {
    throw new Error(json?.message || "Something went wrong. Please try again.");
  }
  return json.data; // unwraps the ApiResponse envelope — callers get the real payload directly
}