import { Users } from "lucide-react";

export default function TableCard({ table, onClick }) {
  const available = table.status === "Available";
  return (
    <button
      onClick={() => onClick(table)}
      className={`rounded-xl border-2 p-4 text-left transition hover:shadow-md ${available ? "bg-white border-green/30 hover:border-green" : "bg-white border-red/30 hover:border-red"}`}
    >
      <div className="flex items-center justify-between mb-3">
        <span className="font-semibold text-gray-800">{table.number}</span>
        <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${available ? "bg-green/10 text-green" : "bg-red/10 text-red"}`}>
          {table.status}
        </span>
      </div>
      <div className="flex items-center gap-1 text-xs text-gray-400">
        <Users size={12} /> {table.seats} Seats
      </div>
    </button>
  );
}