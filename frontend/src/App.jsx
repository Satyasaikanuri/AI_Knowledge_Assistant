import { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { ErrorBoundary } from 'react-error-boundary';
import { useAppStore } from './store/useAppStore';
import { useAuthStore } from './store/useAuthStore';

// Layouts
import MainLayout from './layouts/MainLayout';
import AuthLayout from './layouts/AuthLayout';

// Pages
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Chat from './pages/Chat';
import Uploads from './pages/Uploads';
import AdminDashboard from './pages/AdminDashboard';

const ProtectedRoute = ({ children }) => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return children;
};

const AdminRoute = ({ children }) => {
  const { isAuthenticated, user } = useAuthStore();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (user?.role !== 'ADMIN') return <Navigate to="/dashboard" replace />;
  return children;
};

const PublicRoute = ({ children }) => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  if (isAuthenticated) return <Navigate to="/dashboard" replace />;
  return children;
};

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-[#030712] p-4">
      <div className="glass-panel p-8 rounded-3xl shadow-xl max-w-md w-full text-center">
        <h2 className="text-2xl font-bold text-red-500 mb-4 tracking-tight">System Override Required</h2>
        <p className="text-slate-400 mb-6 font-mono text-sm">{error.message}</p>
        <button 
          onClick={resetErrorBoundary} 
          className="w-full py-3 bg-red-500/10 border border-red-500/20 text-red-500 rounded-xl hover:bg-red-500/20 transition-all"
        >
          Reset Engine
        </button>
      </div>
    </div>
  );
}

function App() {
  const theme = useAppStore((state) => state.theme);

  useEffect(() => {
    // Force dark mode for premium feel if user hasn't set anything
    document.documentElement.classList.add('dark');
  }, []);

  return (
    <ErrorBoundary FallbackComponent={ErrorFallback}>
      <Toaster position="top-right" />
      <Router>
        <Routes>
          {/* Landing Page */}
          <Route path="/" element={<Home />} />

          {/* Auth Routes */}
          <Route element={<AuthLayout />}>
            <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
            <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />
          </Route>

          {/* Application Routes */}
          <Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/chat" element={<Chat />} />
            <Route path="/uploads" element={<Uploads />} />
            <Route path="/admin" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
          </Route>

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </ErrorBoundary>
  );
}

export default App;
