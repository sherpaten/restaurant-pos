import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { apiPost } from "../services/api";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const data = await apiPost("/auth/login", { email, password });
      localStorage.setItem("token", data.accessToken);
      localStorage.setItem("refreshToken", data.refreshToken);
      localStorage.setItem("user", JSON.stringify(data.user));
      localStorage.setItem("restaurant", JSON.stringify(data.restaurant || {}));
      navigate("/");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="w-full max-w-sm bg-white border border-gray-200 rounded-xl p-6">
        <div className="flex items-center gap-2 mb-6">
          <div className="w-9 h-9 rounded bg-blue flex items-center justify-center text-white text-sm font-bold">T</div>
          <div className="text-sm font-semibold text-gray-800">Restaurant POS</div>
        </div>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="text-xs text-gray-500">Email</label>
            <input required type="email" value={email} onChange={(e) => setEmail(e.target.value)} className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue mt-1" />
          </div>
          <div>
            <label className="text-xs text-gray-500">Password</label>
            <input required type="password" value={password} onChange={(e) => setPassword(e.target.value)} className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue mt-1" />
          </div>
          {error && <div className="text-xs text-red">{error}</div>}
          <button disabled={loading} type="submit" className="w-full bg-blue text-white rounded-lg py-2.5 text-sm font-medium disabled:opacity-50">
            {loading ? "Logging in..." : "Log In"}
          </button>
        </form>
      </div>
    </div>
  );
}