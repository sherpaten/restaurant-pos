import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from "recharts";

const data = [
  { day: "Mon", sales: 32000 },
  { day: "Tue", sales: 41000 },
  { day: "Wed", sales: 38000 },
  { day: "Thu", sales: 55000 },
  { day: "Fri", sales: 47000 },
  { day: "Sat", sales: 62000 },
  { day: "Sun", sales: 58450 },
];

export default function SalesChart() {
  return (
    <div className="h-56">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" vertical={false} />
          <XAxis dataKey="day" tick={{ fontSize: 12, fill: "#9CA3AF" }} axisLine={false} tickLine={false} />
          <YAxis tick={{ fontSize: 12, fill: "#9CA3AF" }} axisLine={false} tickLine={false} tickFormatter={(v) => `${v / 1000}K`} />
          <Tooltip formatter={(value) => [`Rs. ${value.toLocaleString()}`, "Sales"]} />
          <Line type="monotone" dataKey="sales" stroke="#2563EB" strokeWidth={2} dot={{ r: 3 }} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}