import { useState } from "react";
import { Plus, Pencil, Trash2 } from "lucide-react";
import ItemFormModal from "../components/menu/ItemFormModal";
import { categories as initialCategories, menuItems as initialItems } from "../data/sampleMenu";

export default function Menu() {
  const [categories] = useState(initialCategories.filter((c) => c.id !== "all"));
  const [items, setItems] = useState(initialItems);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);

  function openAdd() {
    setEditingItem(null);
    setModalOpen(true);
  }

  function openEdit(item) {
    setEditingItem(item);
    setModalOpen(true);
  }

  function handleSave(form) {
    if (editingItem) {
      setItems((prev) => prev.map((i) => (i.id === editingItem.id ? { ...form, id: editingItem.id } : i)));
    } else {
      setItems((prev) => [...prev, { ...form, id: Date.now() }]);
    }
    setModalOpen(false);
  }

  function handleDelete(id) {
    setItems((prev) => prev.filter((i) => i.id !== id));
  }

  function categoryName(id) {
    return categories.find((c) => c.id === id)?.name || id;
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-lg font-semibold text-gray-800">Menu Management</h1>
        <button onClick={openAdd} className="flex items-center gap-1.5 bg-blue text-white text-sm font-medium px-3 py-2 rounded-lg hover:bg-blue/90">
          <Plus size={16} /> Add Item
        </button>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-xs text-gray-400 border-b border-gray-100">
              <th className="px-4 py-3 font-medium">Item</th>
              <th className="px-4 py-3 font-medium">Category</th>
              <th className="px-4 py-3 font-medium">Price</th>
              <th className="px-4 py-3 font-medium">Type</th>
              <th className="px-4 py-3 font-medium text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id} className="border-b border-gray-50 last:border-0">
                <td className="px-4 py-3 text-gray-800">{item.name}</td>
                <td className="px-4 py-3 text-gray-500">{categoryName(item.categoryId)}</td>
                <td className="px-4 py-3 text-gray-700">Rs. {item.price}</td>
                <td className="px-4 py-3">
                  <span className={`w-3 h-3 rounded-sm border inline-block ${item.veg ? "bg-green border-green" : "bg-red border-red"}`} />
                </td>
                <td className="px-4 py-3 text-right space-x-2">
                  <button onClick={() => openEdit(item)} className="text-gray-400 hover:text-blue"><Pencil size={15} className="inline" /></button>
                  <button onClick={() => handleDelete(item.id)} className="text-gray-400 hover:text-red"><Trash2 size={15} className="inline" /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <ItemFormModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSave={handleSave}
        categories={categories}
        initialItem={editingItem}
      />
    </div>
  );
}