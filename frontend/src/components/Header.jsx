import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Search, Bell, ChevronDown, LogOut } from "lucide-react";

export default function Header() {
  const [open, setOpen] = useState(false);
  const [user, setUser] = useState(null);
  const menuRef = useRef(null);
  const navigate = useNavigate();

 useEffect(() => {
    const stored = localStorage.getItem("user");
    if (stored) {
      const parsed = JSON.parse(stored);
      setUser({ ...parsed, name: `${parsed.firstName || ""} ${parsed.lastName || ""}`.trim() });
    }
    function handleClickOutside(e) {
      if (menuRef.current && !menuRef.current.contains(e.target)) setOpen(false);
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  function handleLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("user");
    navigate("/login");
  }

  return (
    <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-6">
      <div className="flex items-center gap-2 text-sm text-gray-500">
        <span className="font-medium text-gray-800">Restaurant Name</span>
        <span>· Location</span>
      </div>
      <div className="flex-1 max-w-md mx-8 relative">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input type="text" placeholder="Search anything..." className="w-full bg-gray-100 rounded-lg pl-9 pr-4 py-2 text-sm outline-none focus:ring-2 focus:ring-blue" />
      </div>
      <div className="flex items-center gap-5">
        <button className="relative text-gray-500 hover:text-gray-800">
          <Bell size={20} />
          <span className="absolute -top-1 -right-1 w-4 h-4 bg-red text-white text-[10px] rounded-full flex items-center justify-center">2</span>
        </button>
        <div className="relative" ref={menuRef}>
          <div className="flex items-center gap-2 cursor-pointer" onClick={() => setOpen((v) => !v)}>
            <div className="w-9 h-9 rounded-full bg-purple text-white flex items-center justify-center text-sm font-semibold">
              {(user?.name || "U").charAt(0).toUpperCase()}
            </div>
            <div className="text-sm">
              <div className="font-medium text-gray-800 leading-tight">{user?.name || "User Name"}</div>
              <div className="text-xs text-gray-500">{user?.role || "Role"}</div>
            </div>
            <ChevronDown size={16} className="text-gray-400" />
          </div>
          {open && (
            <div className="absolute right-0 top-12 bg-white border border-gray-200 rounded-lg shadow-lg w-40 py-1 z-10">
              <button onClick={handleLogout} className="w-full flex items-center gap-2 px-3 py-2 text-sm text-gray-600 hover:bg-gray-50">
                <LogOut size={15} /> Log Out
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}