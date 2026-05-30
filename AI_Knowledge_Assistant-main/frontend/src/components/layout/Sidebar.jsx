import { NavLink } from 'react-router-dom';
import { LayoutDashboard, MessageSquare, UploadCloud, FileText, Settings, LogOut, ShieldAlert, Bot, Zap, Database, BrainCircuit, ShieldCheck } from 'lucide-react';
import { useAuthStore } from '../../store/useAuthStore';
import { useAppStore } from '../../store/useAppStore';
import { motion, AnimatePresence } from 'framer-motion';
import clsx from 'clsx';

const Sidebar = () => {
  const { logout, user } = useAuthStore();
  const { sidebarOpen, setSidebarOpen } = useAppStore();

  const navItems = [
    { name: 'Dashboard', icon: LayoutDashboard, path: '/dashboard', color: 'text-cyan-400' },
    { name: 'Knowledge Hub', icon: UploadCloud, path: '/uploads', color: 'text-purple-400' },
    { name: 'Neural Chat', icon: MessageSquare, path: '/chat', color: 'text-pink-400' },
  ];

  if (user?.role === 'ADMIN') {
    navItems.push({ name: 'Admin Core', icon: ShieldAlert, path: '/admin', color: 'text-red-400' });
  }

  return (
    <>
      {/* Mobile Backdrop Overlay */}
      <AnimatePresence>
        {sidebarOpen && (
          <motion.div 
            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="fixed inset-0 z-[60] bg-black/80 backdrop-blur-md lg:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        )}
      </AnimatePresence>

      <aside className={clsx(
        "fixed lg:static inset-y-0 left-0 z-[70] w-72 glass-panel border-r border-white/5 transform transition-all duration-500 ease-in-out lg:translate-x-0 flex flex-col shadow-2xl overflow-hidden",
        sidebarOpen ? "translate-x-0" : "-translate-x-full"
      )}>
        {/* Futuristic Brand Header */}
        <div className="h-24 flex items-center px-8 mb-4 relative overflow-hidden">
          <div className="absolute top-0 right-0 p-4 opacity-5 pointer-events-none">
             <Zap size={80} className="text-cyan-400" />
          </div>
          
          <div className="flex items-center gap-4 relative z-10">
            <div className="relative group">
              <div className="absolute inset-0 bg-cyan-500 blur-xl opacity-20 group-hover:opacity-60 transition-opacity" />
              <div className="relative p-3 bg-gradient-to-br from-cyan-500 via-purple-600 to-pink-500 rounded-[1.2rem] shadow-xl shadow-cyan-500/20">
                <BrainCircuit className="w-6 h-6 text-white" />
              </div>
            </div>
            <div className="flex flex-col">
              <span className="font-black text-xl leading-tight tracking-tighter text-white uppercase italic">Neural AI</span>
              <div className="flex items-center gap-1.5 mt-0.5">
                 <div className="w-1.5 h-1.5 rounded-full bg-cyan-500 animate-pulse" />
                 <span className="text-[9px] uppercase tracking-[0.3em] text-cyan-400 font-black">RAG Core Active</span>
              </div>
            </div>
          </div>
        </div>

        {/* Navigation Matrix */}
        <nav className="flex-1 overflow-y-auto py-6 px-6 space-y-3 custom-scrollbar relative z-10">
          <p className="text-[9px] font-black text-slate-500 uppercase tracking-[0.4em] mb-6 px-4">Interrogation Menu</p>
          {navItems.map((item, i) => (
            <NavLink
              key={item.name} to={item.path} onClick={() => setSidebarOpen(false)}
              className={({ isActive }) => clsx(
                "group relative flex items-center gap-4 px-5 py-4 rounded-2xl font-bold transition-all duration-300",
                isActive 
                  ? "bg-white/10 text-white shadow-xl border border-white/10" 
                  : "text-slate-500 hover:text-slate-200 hover:bg-white/5"
              )}
            >
              {({ isActive }) => (
                <>
                  <item.icon size={20} className={clsx("transition-all duration-500 group-hover:scale-125", isActive ? item.color : "text-slate-600")} />
                  <span className="text-sm tracking-tight">{item.name}</span>
                  {isActive && (
                    <motion.div layoutId="sidebar-glow" className={clsx("absolute inset-0 rounded-2xl blur-md opacity-20 pointer-events-none", item.color.replace('text', 'bg'))} />
                  )}
                  {isActive && (
                    <motion.div layoutId="sidebar-active-indicator" className={clsx("absolute right-4 w-1 h-1 rounded-full", item.color.replace('text', 'bg'))} />
                  )}
                </>
              )}
            </NavLink>
          ))}
        </nav>

        {/* Superuser Section */}
        <div className="p-6 mt-auto relative z-10">
          <div className="p-6 glass-card rounded-[2.2rem] border-white/5 bg-gradient-to-br from-white/5 to-transparent">
            <div className="flex items-center gap-4 mb-5">
              <div className="relative group">
                 <div className="absolute inset-0 bg-purple-500 blur-lg opacity-20 group-hover:opacity-40 transition-opacity" />
                 <div className="w-12 h-12 rounded-2xl bg-slate-800 flex items-center justify-center font-black text-white shadow-2xl border border-white/10 relative">
                   {user?.firstName?.[0] || 'U'}
                 </div>
              </div>
              <div className="flex flex-col min-w-0">
                <span className="text-sm font-black text-white truncate tracking-tight uppercase italic">{user?.firstName}</span>
                <span className="text-[10px] font-bold text-slate-500 truncate uppercase tracking-tighter">Level 01 User</span>
              </div>
            </div>
            <button 
              onClick={() => logout()}
              className="group flex items-center justify-center gap-3 px-4 py-3.5 rounded-2xl text-[10px] font-black text-red-500 hover:text-white hover:bg-red-500 transition-all w-full border border-red-500/20 uppercase tracking-widest overflow-hidden relative"
            >
              <LogOut size={14} className="relative z-10" />
              <span className="relative z-10">Establish Disconnect</span>
              <div className="absolute inset-0 bg-red-500 -translate-x-full group-hover:translate-x-0 transition-transform duration-500" />
            </button>
          </div>
          <div className="mt-6 flex items-center justify-center gap-4 opacity-30 grayscale hover:grayscale-0 hover:opacity-100 transition-all duration-500">
             <div className="flex items-center gap-1.5 text-[8px] font-black text-slate-500 uppercase tracking-widest">
                <ShieldCheck size={10} /> Secure Link
             </div>
             <div className="w-1 h-1 bg-slate-700 rounded-full" />
             <p className="text-[8px] text-slate-500 font-black uppercase tracking-widest">Core v1.4</p>
          </div>
        </div>
      </aside>

      {/* Futuristic Bottom Navigation (Mobile Only) */}
      <div className="lg:hidden fixed bottom-0 left-0 right-0 z-[60] px-4 pb-4">
         <div className="glass-panel border-t border-white/10 rounded-[2rem] p-2 flex items-center justify-around shadow-2xl shadow-cyan-500/20 backdrop-blur-3xl">
            {navItems.map((item) => (
              <NavLink
                key={item.name} to={item.path}
                className={({ isActive }) => clsx(
                  "flex flex-col items-center gap-1.5 p-3 rounded-2xl transition-all duration-300",
                  isActive ? "bg-white/10 text-white" : "text-slate-500"
                )}
              >
                {({ isActive }) => (
                  <>
                    <item.icon size={20} className={clsx(isActive ? item.color : "text-slate-600")} />
                    <span className="text-[9px] font-black uppercase tracking-widest">{item.name.split(' ')[0]}</span>
                  </>
                )}
              </NavLink>
            ))}
         </div>
      </div>
    </>
  );
};

export default Sidebar;
