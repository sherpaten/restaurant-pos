import { useState } from "react";
import OrderCard from "../components/kitchen/OrderCard";
import { initialOrders } from "../data/sampleOrders";

const tabs = ["All Orders", "Preparing", "Ready"];

export default function Kitchen() {
  const [orders, setOrders] = useState(initialOrders);
  const [activeTab, setActiveTab] = useState("All Orders");

  function setStatus(id, status) {
    setOrders((prev) => prev.map((o) => (o.id === id ? { ...o, status } : o)));
  }

  const filtered = orders.filter((o) => activeTab === "All Orders" || o.status === activeTab);
  const counts = {
    "All Orders": orders.length,
    Preparing: orders.filter((o) => o.status === "Preparing").length,
    Ready: orders.filter((o) => o.status === "Ready").length,
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-lg font-semibold text-gray-800">Kitchen Display System (KDS)</h1>
        <span className="text-xs text-green flex items-center gap-1">● Auto Refresh On</span>
      </div>
      <div className="flex gap-2 mb-4">
        {tabs.map((tab) => (
          <button key={tab} onClick={() => setActiveTab(tab)} className={`px-3 py-1.5 rounded-lg text-sm font-medium transition ${activeTab === tab ? "bg-blue text-white" : "bg-white text-gray-600 border border-gray-200 hover:bg-gray-50"}`}>
            {tab} {counts[tab]}
          </button>
        ))}
      </div>
      <div className="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4">
        {filtered.map((order) => (
          <OrderCard key={order.id} order={order} onSetStatus={setStatus} />
        ))}
      </div>
    </div>
  );
}