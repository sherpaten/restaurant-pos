import { useState, useEffect } from "react";
import { X } from "lucide-react";

export default function ItemFormModal({ open, onClose, onSave, categories, initialItem }) {
  const [form, setForm] = useState({ name: "", categoryId: categories[0]?.id || "", price: "", veg: true });

  useEffect(() => {
    if (initialItem) setForm(initialItem);
    else setForm({ name: "", categoryId: categories[0]?.id || "", price: "", veg: true });
  }, [initialItem, open]);

  if (!open) return null;

  function handleSubmit(e) {
    e.preventDefault();
    onSave({ ...form, price: Number(form.price) });
  }

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-xl w-full max-w-sm p-5">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-gray-800">{initialItem ? "Edit Item" : "Add Item"}</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X size={18} /></button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="text-xs text-gray-500">Item Name</label>
            <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue mt-1" />
          </div>
          <div>
            <label className="text-xs text-gray-500">Category</label>
            <select value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })} className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue mt-1">
              {categories.filter((c) => c.id !== "all").map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="text-xs text-gray-500">Price (Rs.)</label>
            <input required type="number" min="0" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-blue mt-1" />
          </div>
          <label className="flex items-center gap-2 text-sm text-gray-600">
            <input type="checkbox" checked={form.veg} onChange={(e) => setForm({ ...form, veg: e.target.checked })} />
            Vegetarian
          </label>
          <button type="submit" className="w-full bg-blue text-white rounded-lg py-2 text-sm font-medium mt-2">Save Item</button>
        </form>
      </div>
    </div>
  );
}