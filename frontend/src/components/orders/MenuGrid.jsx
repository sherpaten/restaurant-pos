import { Plus } from "lucide-react";

export default function MenuGrid({ items, onAdd }) {
  if (items.length === 0) {
    return <div className="flex-1 flex items-center justify-center text-sm text-gray-400">No items found.</div>;
  }

  return (
    <div className="flex-1 overflow-y-auto">
      <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
        {items.map((item) => (
          <div key={item.id} className="bg-white border border-gray-200 rounded-xl p-3">
            <div className="w-full aspect-square rounded-lg bg-gray-100 mb-2 relative">
              <span className={`absolute top-1.5 left-1.5 w-3 h-3 rounded-sm border ${item.veg ? "bg-green border-green" : "bg-red border-red"}`} />
            </div>
            <div className="text-sm font-medium text-gray-800">{item.name}</div>
            <div className="flex items-center justify-between mt-1">
              <span className="text-sm text-blue font-medium">Rs. {item.price}</span>
              <button onClick={() => onAdd(item)} className="w-6 h-6 rounded-full bg-blue text-white flex items-center justify-center hover:bg-blue/90">
                <Plus size={14} />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}