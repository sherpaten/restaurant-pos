import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import ProtectedRoute from "./components/ProtectedRoute";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Orders from "./pages/Orders";
import Kitchen from "./pages/Kitchen";
import Tables from "./pages/Tables";
import Menu from "./pages/Menu";
import ComingSoon from "./components/ComingSoon";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/orders" element={<Orders />} />
          <Route path="/tables" element={<Tables />} />
          <Route path="/kitchen" element={<Kitchen />} />
          <Route path="/menu" element={<Menu />} />
          <Route path="/reports" element={<ComingSoon title="Reports" />} />
          <Route path="/users" element={<ComingSoon title="Users" />} />
          <Route path="/settings" element={<ComingSoon title="Settings" />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}