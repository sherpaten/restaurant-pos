import { Wallet, ClipboardList, TrendingUp, Receipt, Table2, CheckCircle2 } from "lucide-react";
import StatCard from "../components/StatCard";
import SalesChart from "../components/SalesChart";

const stats = [
  { icon: Wallet, iconBg: "bg-blue", label: "Today's Sales", value: "Rs. xxx", change: "↑ xx%", sublabel: "vs Yesterday" },
  { icon: ClipboardList, iconBg: "bg-green", label: "Orders Today", value: "xxx", change: "↑ xx%", sublabel: "vs Yesterday" },
  { icon: TrendingUp, iconBg: "bg-purple", label: "Revenue", value: "Rs. xxx", change: "↑ xx%", sublabel: "vs Yesterday" },
  { icon: Receipt, iconBg: "bg-orange", label: "VAT Collected", value: "Rs. xxx", change: "↑ xx%", sublabel: "vs Yesterday" },
  { icon: Table2, iconBg: "bg-red", label: "Occupied Tables", value: "xx", sublabel: "Currently" },
  { icon: CheckCircle2, iconBg: "bg-green", label: "Available Tables", value: "xx", sublabel: "Ready for Guests" },
];

const recentOrders = [
  { id: "#ORD-xxxx", table: "Table xx", amount: "Rs. xxx", status: "Preparing", time: "xx min ago" },
  { id: "#ORD-xxxx", table: "Table xx", amount: "Rs. xxx", status: "Ready", time: "xx min ago" },
  { id: "#ORD-xxxx", table: "Table xx", amount: "Rs. xxx", status: "Preparing", time: "xx min ago" },
  { id: "#ORD-xxxx", table: "Take Away", amount: "Rs. xxx", status: "Completed", time: "xx min ago" },
];

const statusColors = {
  Preparing: "bg-orange/10 text-orange",
  Ready: "bg-green/10 text-green",
  Completed: "bg-gray-100 text-gray-500",
};

const popularItems = [
  { name: "Item xxx", sub: "xx Plates" },
  { name: "Item xxx", sub: "xx Plates" },
  { name: "Item xxx", sub: "xx Plates" },
  { name: "Item xxx", sub: "xx Plates" },
];

const kitchenStatus = [
  { label: "Preparing", value: "xx", color: "text-orange" },
  { label: "Ready", value: "xx", color: "text-green" },
  { label: "Waiting", value: "xx", color: "text-red" },
];

const recentActivity = [
  { name: "xxx", action: "xxx", time: "xx min ago" },
  { name: "xxx", action: "xxx", time: "xx min ago" },
  { name: "xxx", action: "xxx", time: "xx min ago" },
];

export default function Dashboard() {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
        {stats.map((s) => (
          <StatCard key={s.label} {...s} />
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="bg-white rounded-xl border border-gray-200 p-4">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-semibold text-gray-800">Sales Overview</h2>
            <span className="text-xs text-gray-400">This Week</span>
          </div>
          <SalesChart />
        </div>

        <div className="bg-white rounded-xl border border-gray-200 p-4">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-semibold text-gray-800">Recent Orders</h2>
            <a href="#" className="text-xs text-blue">View All</a>
          </div>
          <div className="space-y-3">
            {recentOrders.map((o, i) => (
              <div key={i} className="text-sm">
                <div className="flex items-center justify-between">
                  <span className="font-medium text-gray-800">{o.id}</span>
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${statusColors[o.status]}`}>{o.status}</span>
                </div>
                <div className="flex items-center justify-between text-xs text-gray-400 mt-0.5">
                  <span>{o.table} · {o.time}</span>
                  <span className="text-gray-700">{o.amount}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 p-4">
          <div className="flex items-center justify-between mb-4">
            <h2 className="font-semibold text-gray-800">Popular Items</h2>
            <a href="#" className="text-xs text-blue">View All</a>
          </div>
          <div className="space-y-3">
            {popularItems.map((item, i) => (
              <div key={i} className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-lg bg-gray-100" />
                <div>
                  <div className="text-sm font-medium text-gray-800">{item.name}</div>
                  <div className="text-xs text-gray-400">{item.sub}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <div className="bg-white rounded-xl border border-gray-200 p-4">
          <h2 className="font-semibold text-gray-800 mb-4">Kitchen Status</h2>
          <div className="grid grid-cols-3 gap-2">
            {kitchenStatus.map((k) => (
              <div key={k.label} className="text-center">
                <div className={`text-2xl font-semibold ${k.color}`}>{k.value}</div>
                <div className="text-xs text-gray-400">{k.label}</div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 p-4">
          <h2 className="font-semibold text-gray-800 mb-4">Recent Activity</h2>
          <div className="space-y-3">
            {recentActivity.map((a, i) => (
              <div key={i} className="text-xs">
                <span className="font-medium text-gray-700">{a.name}</span>
                <span className="text-gray-400"> · {a.action}</span>
                <div className="text-gray-300 mt-0.5">{a.time}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}