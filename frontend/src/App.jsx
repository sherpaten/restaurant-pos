import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import Dashboard from "./pages/Dashboard";
import Orders from "./pages/Orders";
import ComingSoon from "./components/ComingSoon";
import Kitchen from "./pages/Kitchen";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/tables" element={<ComingSoon title="Tables" />} />
          <Route path="/menu" element={<ComingSoon title="Menu" />} />
          <Route path="/kitchen" element={<Kitchen />} />
          <Route path="/reports" element={<ComingSoon title="Reports" />} />
          <Route path="/users" element={<ComingSoon title="Users" />} />
          <Route path="/settings" element={<ComingSoon title="Settings" />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}