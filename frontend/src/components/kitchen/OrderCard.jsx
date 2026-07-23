import { Clock } from "lucide-react";

function urgencyBorder(minutesAgo) {
  if (minutesAgo >= 8) return "border-red";
  if (minutesAgo >= 4) return "border-orange";
  return "border-green";
}

export default function OrderCard({ order, onSetStatus }) {
  return (
    <div className={`bg-navy rounded-xl border-2 ${urgencyBorder(order.minutesAgo)} p-4 text-white flex flex-col`}>
      <div className="flex items-center justify-between mb-1">
        <span className="font-semibold">{order.id}</span>
        <span className="flex items-center gap-1 text-xs text-white/50">
          <Clock size={12} /> {order.minutesAgo} min ago
        </span>
      </div>
      <div className="text-sm text-white/70 mb-3">{order.table}</div>
      <div className="flex-1 space-y-1 mb-3">
        {order.items.map((item, i) => (
          <div key={i} className="text-sm">{item.qty}x {item.name}</div>
        ))}
      </div>
      {order.note && <div className="text-xs text-orange mb-3">Note: {order.note}</div>}
      <div className="grid grid-cols-2 gap-2 mt-auto">
        <button onClick={() => onSetStatus(order.id, "Preparing")} className={`py-1.5 rounded-lg text-xs font-medium transition ${order.status === "Preparing" ? "bg-orange text-white" : "bg-white/10 text-white/60 hover:bg-white/20"}`}>
          Preparing
        </button>
        <button onClick={() => onSetStatus(order.id, "Ready")} className={`py-1.5 rounded-lg text-xs font-medium transition ${order.status === "Ready" ? "bg-green text-white" : "bg-white/10 text-white/60 hover:bg-white/20"}`}>
          Ready
        </button>
      </div>
    </div>
  );
}