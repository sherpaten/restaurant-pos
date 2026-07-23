import { Minus, Plus, Users } from "lucide-react";

const VAT_RATE = 0.13;

export default function OrderCart({ cart, onUpdateQty, table }) {
  const subtotal = cart.reduce((sum, line) => sum + line.price * line.qty, 0);
  const vat = subtotal * VAT_RATE;
  const total = subtotal + vat;

  return (
    <div className="w-80 shrink-0 bg-white border border-gray-200 rounded-xl flex flex-col">
      <div className="px-4 py-3 bg-blue text-white rounded-t-xl flex items-center justify-between">
        <span className="font-medium text-sm">{table}</span>
        <span className="flex items-center gap-1 text-xs"><Users size={14} /> xx Guests</span>
      </div>
      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {cart.length === 0 ? (
          <div className="text-sm text-gray-400 text-center py-8">No items added yet.</div>
        ) : (
          cart.map((line) => (
            <div key={line.id} className="flex items-center justify-between text-sm">
              <div className="flex-1 min-w-0 text-gray-800 truncate">{line.name}</div>
              <div className="flex items-center gap-2 mx-2">
                <button onClick={() => onUpdateQty(line.id, -1)} className="w-5 h-5 rounded border border-gray-300 flex items-center justify-center text-gray-500 hover:bg-gray-100"><Minus size={12} /></button>
                <span className="w-4 text-center">{line.qty}</span>
                <button onClick={() => onUpdateQty(line.id, 1)} className="w-5 h-5 rounded border border-gray-300 flex items-center justify-center text-gray-500 hover:bg-gray-100"><Plus size={12} /></button>
              </div>
              <div className="w-16 text-right text-gray-700">Rs. {line.price * line.qty}</div>
            </div>
          ))
        )}
        <input type="text" placeholder="Add note / special instruction" className="w-full text-xs border border-gray-200 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-blue mt-2" />
      </div>
      <div className="p-4 border-t border-gray-100 space-y-1.5 text-sm">
        <div className="flex justify-between text-gray-500"><span>Subtotal</span><span>Rs. {subtotal.toFixed(2)}</span></div>
        <div className="flex justify-between text-gray-500"><span>VAT (13%)</span><span>Rs. {vat.toFixed(2)}</span></div>
        <div className="flex justify-between font-semibold text-gray-800 text-base pt-1"><span>Total</span><span>Rs. {total.toFixed(2)}</span></div>
      </div>
      <div className="p-4 pt-0 space-y-2">
        <button className="w-full bg-green text-white rounded-lg py-2.5 text-sm font-medium hover:bg-green/90">Checkout Rs. {total.toFixed(2)}</button>
        <div className="grid grid-cols-2 gap-2">
          <button className="border border-gray-200 rounded-lg py-2 text-xs text-gray-600 hover:bg-gray-50">Hold Order</button>
          <button className="border border-gray-200 rounded-lg py-2 text-xs text-gray-600 hover:bg-gray-50">Save Order</button>
        </div>
      </div>
    </div>
  );
}