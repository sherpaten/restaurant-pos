import { Search, Bell, ChevronDown } from "lucide-react";

export default function Header() {
  return (
    <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-6">
      <div className="flex items-center gap-2 text-sm text-gray-500">
        <span className="font-medium text-gray-800"> Restaurant POS</span>
        <span>· Location</span>
      </div>
      <div className="flex-1 max-w-md mx-8 relative">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <input
          type="text"
          placeholder="Search anything..."
          className="w-full bg-gray-100 rounded-lg pl-9 pr-4 py-2 text-sm outline-none focus:ring-2 focus:ring-blue"
        />
      </div>
      <div className="flex items-center gap-5">
        <button className="relative text-gray-500 hover:text-gray-800">
          <Bell size={20} />
          <span className="absolute -top-1 -right-1 w-4 h-4 bg-red text-white text-[10px] rounded-full flex items-center justify-center">2</span>
        </button>
        <div className="flex items-center gap-2 cursor-pointer">
          <div className="w-9 h-9 rounded-full bg-purple text-white flex items-center justify-center text-sm font-semibold">S</div>
          <div className="text-sm">
           <div className="font-medium text-gray-800 leading-tight">User Name</div>
            <div className="text-xs text-gray-500">Role</div>
          </div>
          <ChevronDown size={16} className="text-gray-400" />
        </div>
      </div>
    </header>
  );
}