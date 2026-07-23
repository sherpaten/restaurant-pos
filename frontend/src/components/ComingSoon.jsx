export default function ComingSoon({ title }) {
  return (
    <div className="flex flex-col items-center justify-center h-[70vh] text-center">
      <div className="text-lg font-semibold text-gray-800 mb-1">{title}</div>
      <div className="text-sm text-gray-400">This screen hasn't been built yet.</div>
    </div>
  );
}