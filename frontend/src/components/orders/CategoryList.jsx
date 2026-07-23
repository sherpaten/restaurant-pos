import { LayoutGrid, Pizza, Sandwich, Soup, CupSoda, IceCream, Cookie, Coffee } from "lucide-react";
import { categories } from "../../data/sampleMenu";

const icons = {
  all: LayoutGrid,
  pizza: Pizza,
  burger: Sandwich,
  momo: Soup,
  drinks: CupSoda,
  dessert: IceCream,
  snacks: Cookie,
  coffee: Coffee,
};

export default function CategoryList({ active, onSelect }) {
  return (
    <div className="w-36 shrink-0 space-y-1 overflow-y-auto">
      {categories.map(({ id, name }) => {
        const Icon = icons[id];
        return (
          <button key={id} onClick={() => onSelect(id)} className={`w-full flex items-center gap-2 px-3 py-2 rounded-lg text-sm text-left transition ${active === id ? "bg-blue text-white" : "text-gray-600 hover:bg-gray-100"}`}>
            <Icon size={16} />
            {name}
          </button>
        );
      })}
    </div>
  );
}