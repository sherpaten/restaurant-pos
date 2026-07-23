import { Link, useLocation } from "react-router-dom";
import { LayoutDashboard, ClipboardList, Table2, Monitor, UtensilsCrossed, BarChart3, Users, Settings } from "lucide-react";

const navItems = [
  { icon: LayoutDashboard, label: "Dashboard", path: "/" },
  { icon: ClipboardList, label: "Orders", path: "/orders" },
  { icon: Table2, label: "Tables", path: "/tables" },
  { icon: Monitor, label: "Kitchen Display", path: "/kitchen" },
  { icon: UtensilsCrossed, label: "Menu", path: "/menu" },
  { icon: BarChart3, label: "Reports", path: "/reports" },
  { icon: Users, label: "Users", path: "/users" },
  { icon: Settings, label: "Settings", path: "/settings" },
];

export default function Sidebar() {
  const location = useLocation();

  return (
    <aside className="w-60 bg-navy text-white flex flex-col h-screen">
      <div className="px-5 py-6 flex items-center gap-2 border-b border-white/10">
        <div className="w-8 h-8 rounded bg-blue flex items-center justify-center text-sm font-bold">T</div>
        <div className="text-sm font-semibold leading-tight">Restaurant POS</div>
      </div>
      <nav className="flex-1 py-4 px-3 space-y-1">
        {navItems.map(({ icon: Icon, label, path }) => {
          const active = location.pathname === path;
          return (
            <Link key={label} to={path} className={`flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition ${active ? "bg-blue text-white" : "text-white/70 hover:bg-white/10 hover:text-white"}`}>
              <Icon size={18} />
              {label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}