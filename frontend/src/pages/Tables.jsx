import { useState } from "react";
import { useNavigate } from "react-router-dom";
import TableCard from "../components/tables/TableCard";
import { initialTables } from "../data/sampleTables";

export default function Tables() {
  const [tables] = useState(initialTables);
  const navigate = useNavigate();

  const available = tables.filter((t) => t.status === "Available").length;
  const occupied = tables.filter((t) => t.status === "Occupied").length;

  function handleSelect(table) {
    if (table.status === "Available") {
      navigate(`/orders?table=${encodeURIComponent(table.number)}`);
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-lg font-semibold text-gray-800">Tables</h1>
        <div className="flex items-center gap-4 text-xs">
          <span className="flex items-center gap-1 text-green"><span className="w-2 h-2 rounded-full bg-green" /> {available} Available</span>
          <span className="flex items-center gap-1 text-red"><span className="w-2 h-2 rounded-full bg-red" /> {occupied} Occupied</span>
        </div>
      </div>
      <div className="grid grid-cols-2 md:grid-cols-4 xl:grid-cols-5 gap-4">
        {tables.map((table) => (
          <TableCard key={table.id} table={table} onClick={handleSelect} />
        ))}
      </div>
    </div>
  );
}