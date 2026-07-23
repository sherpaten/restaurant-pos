import { useState } from "react";
import { Search } from "lucide-react";
import CategoryList from "../components/orders/CategoryList";
import MenuGrid from "../components/orders/MenuGrid";
import OrderCart from "../components/orders/OrderCart";
import { menuItems } from "../data/sampleMenu";
import { useSearchParams } from "react-router-dom";

export default function Orders() {
  const [searchParams] = useSearchParams();
  const tableLabel = searchParams.get("table") || "Table xx";
  const [activeCategory, setActiveCategory] = useState("all");
  const [search, setSearch] = useState("");
  const [cart, setCart] = useState([]);

  const filteredItems = menuItems.filter((item) => {
    const matchesCategory = activeCategory === "all" || item.categoryId === activeCategory;
    const matchesSearch = item.name.toLowerCase().includes(search.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  function addToCart(item) {
    setCart((prev) => {
      const existing = prev.find((line) => line.id === item.id);
      if (existing) return prev.map((line) => (line.id === item.id ? { ...line, qty: line.qty + 1 } : line));
      return [...prev, { ...item, qty: 1 }];
    });
  }

  function updateQty(id, delta) {
    setCart((prev) => prev.map((line) => (line.id === id ? { ...line, qty: line.qty + delta } : line)).filter((line) => line.qty > 0));
  }

  return (
    <div className="flex gap-6 h-full">
      <div className="flex-1 flex flex-col min-w-0">
        <div className="mb-4">
          <h1 className="text-lg font-semibold text-gray-800 mb-3">Current Orders (POS)</h1>
          <div className="relative">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
            <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search menu items..." className="w-full bg-white border border-gray-200 rounded-lg pl-9 pr-4 py-2 text-sm outline-none focus:ring-2 focus:ring-blue" />
          </div>
        </div>
        <div className="flex gap-4 flex-1 min-h-0">
          <CategoryList active={activeCategory} onSelect={setActiveCategory} />
          <MenuGrid items={filteredItems} onAdd={addToCart} />
        </div>
      </div>
<OrderCart cart={cart} onUpdateQty={updateQty} table={tableLabel} />    </div>
  );
}