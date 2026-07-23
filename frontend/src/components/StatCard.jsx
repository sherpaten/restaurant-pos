export default function StatCard({ icon: Icon, iconBg, label, value, change, sublabel }) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-4">
      <div className={`w-9 h-9 rounded-lg flex items-center justify-center mb-3 ${iconBg}`}>
        <Icon size={18} className="text-white" />
      </div>
      <div className="text-xs text-gray-500 mb-1">{label}</div>
      <div className="text-xl font-semibold text-gray-800">{value}</div>
      <div className="flex items-center gap-1 mt-1 text-xs">
        {change && <span className="text-green font-medium">{change}</span>}
        <span className="text-gray-400">{sublabel}</span>
      </div>
    </div>
  );
}