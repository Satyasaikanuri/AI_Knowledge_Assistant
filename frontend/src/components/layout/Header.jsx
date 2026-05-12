import { useState, useEffect } from 'react';
import { Menu, Moon, Sun, Bell, Search, Command, Activity, Zap, ShieldCheck, Cpu, LogOut, Settings, BellOff } from 'lucide-react';
import { useAppStore } from '../../store/useAppStore';
import { useAuthStore } from '../../store/useAuthStore';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';

const Header = () => {
  const { toggleTheme, theme, toggleSidebar } = useAppStore();
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const navigate = useNavigate();
  
  const [searchQuery, setSearchQuery] = useState('');
  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  const notifications = [
    { id: 1, title: 'Neural Ingestion Complete', desc: 'Financial_Report_Q4.pdf is now live.', time: '2m ago' },
    { id: 2, title: 'API Throughput Peak', desc: 'Core throughput reached 5.2 GB/s.', time: '15m ago' }
  ];

  const handleSearch = (e) => {
    if (e.key === 'Enter') {
      navigate(`/uploads?search=${searchQuery}`);
      setSearchQuery('');
    }
  };

  return (
    <header className="h-24 glass-panel border-b border-white/5 flex items-center justify-between px-8 sm:px-12 sticky top-0 z-40 relative">
      <motion.div 
        animate={{ x: ['-100%', '100%'] }}
        transition={{ duration: 10, repeat: Infinity, ease: 'linear' }}
        className="absolute bottom-0 left-0 w-full h-[1px] bg-gradient-to-r from-transparent via-cyan-500 to-transparent opacity-30" 
      />
      
      <div className="flex items-center gap-6 flex-1">
        <motion.button 
          whileTap={{ scale: 0.9 }}
          onClick={toggleSidebar}
          className="p-3 rounded-2xl bg-white/5 border border-white/10 text-slate-400 hover:text-cyan-400 lg:hidden transition-all relative group"
        >
          <Menu size={22} className="relative z-10" />
        </motion.button>
        
        <div className="hidden md:flex items-center gap-4 px-6 py-3 bg-slate-900/50 border border-white/5 rounded-2xl w-full max-w-md group focus-within:border-cyan-500/30 transition-all shadow-2xl relative overflow-hidden">
          <Search size={18} className="text-slate-600 group-focus-within:text-cyan-400 transition-colors relative z-10" />
          <input 
            type="text" 
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={handleSearch}
            placeholder="Interrogate Knowledge Base..." 
            className="bg-transparent border-none focus:ring-0 text-[11px] font-black text-slate-200 placeholder:text-slate-700 w-full tracking-tight relative z-10 uppercase italic"
          />
          <div className="flex items-center gap-2 px-2 py-1 bg-white/5 rounded-lg border border-white/10 text-[11px] font-black text-slate-500 uppercase tracking-widest relative z-10">
            <Command size={10} /> K
          </div>
        </div>
      </div>

      <div className="flex items-center gap-6 sm:gap-10">
        <div className="hidden xl:flex items-center gap-6">
           <div className="flex flex-col items-end">
              <p className="text-[11px] font-black text-slate-500 uppercase tracking-[0.2em] mb-1">Neural Latency</p>
              <div className="flex items-center gap-2">
                 <Activity size={12} className="text-emerald-400" />
                 <span className="text-[11px] font-black text-white tracking-tighter">14ms</span>
              </div>
           </div>
           <div className="w-px h-8 bg-white/5" />
           <div className="flex flex-col items-end">
              <p className="text-[11px] font-black text-slate-500 uppercase tracking-[0.2em] mb-1">API Throughput</p>
              <div className="flex items-center gap-2">
                 <Zap size={12} className="text-cyan-400" />
                 <span className="text-[11px] font-black text-white tracking-tighter">4.2 GB/s</span>
              </div>
           </div>
        </div>

        <div className="flex items-center gap-4 px-4 py-2 bg-white/5 rounded-2xl border border-white/5 relative">
          <motion.button 
            whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }}
            className="p-2.5 rounded-xl text-slate-500 hover:text-cyan-400 hover:bg-cyan-500/10 transition-all"
            onClick={toggleTheme}
          >
            {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
          </motion.button>
          
          <div className="w-px h-5 bg-white/10" />

          <motion.button 
            whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }}
            className={`p-2.5 rounded-xl transition-all relative group ${showNotifications ? 'text-purple-400 bg-purple-500/10' : 'text-slate-500'}`}
            onClick={() => { setShowNotifications(!showNotifications); setShowProfileMenu(false); }}
          >
            <Bell size={18} />
            <span className="absolute top-2.5 right-2.5 w-1.5 h-1.5 bg-cyan-500 rounded-full animate-pulse"></span>
          </motion.button>

          <AnimatePresence>
            {showNotifications && (
              <motion.div 
                initial={{ opacity: 0, y: 10, scale: 0.95 }} animate={{ opacity: 1, y: 0, scale: 1 }} exit={{ opacity: 0, y: 10, scale: 0.95 }}
                className="absolute top-16 right-0 w-80 glass-panel rounded-3xl border border-white/10 p-4 shadow-3xl z-50 overflow-hidden"
              >
                <div className="flex items-center justify-between mb-4 border-b border-white/5 pb-4">
                  <h3 className="text-[11px] font-black text-white uppercase tracking-widest italic">Neural Influx</h3>
                  <button className="text-[10px] text-slate-500 hover:text-cyan-400 uppercase font-black tracking-tighter">Clear Hub</button>
                </div>
                <div className="space-y-3">
                  {notifications.map(n => (
                    <div key={n.id} className="p-3 rounded-2xl bg-white/5 border border-white/5 hover:border-cyan-500/20 transition-all group">
                      <p className="text-[11px] font-black text-slate-200 group-hover:text-white transition-colors">{n.title}</p>
                      <p className="text-[10px] text-slate-500 mt-1">{n.desc}</p>
                      <span className="text-[9px] text-slate-600 mt-2 block uppercase tracking-widest">{n.time}</span>
                    </div>
                  ))}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        <div className="relative">
          <motion.div 
            whileHover={{ x: 5 }}
            onClick={() => { setShowProfileMenu(!showProfileMenu); setShowNotifications(false); }}
            className="flex items-center gap-4 cursor-pointer group"
          >
            <div className="hidden sm:flex flex-col items-end">
              <span className="text-[13px] font-black text-white leading-none mb-1 tracking-tight uppercase italic">
                {user?.firstName}
              </span>
              <div className="flex items-center gap-1.5 px-2.5 py-1 bg-white/5 rounded-full border border-white/10 group-hover:border-cyan-500/30 transition-all">
                 {user?.role === 'ADMIN' ? <ShieldCheck size={10} className="text-red-400" /> : <Cpu size={10} className="text-cyan-400" />}
                 <span className="text-[11px] font-black text-slate-400 uppercase tracking-widest leading-none">
                   {user?.role === 'ADMIN' ? 'Admin Core' : 'User Node'}
                 </span>
              </div>
            </div>
            <div className="relative h-12 w-12 rounded-2xl bg-slate-900 flex items-center justify-center font-black text-white border-2 border-white/10 shadow-xl group-hover:border-cyan-500/50 transition-all overflow-hidden uppercase italic">
               {user?.firstName?.charAt(0) || 'U'}
            </div>
          </motion.div>

          <AnimatePresence>
            {showProfileMenu && (
              <motion.div 
                initial={{ opacity: 0, y: 10, scale: 0.95 }} animate={{ opacity: 1, y: 0, scale: 1 }} exit={{ opacity: 0, y: 10, scale: 0.95 }}
                className="absolute top-16 right-0 w-56 glass-panel rounded-3xl border border-white/10 p-2 shadow-3xl z-50"
              >
                {[
                  { label: 'Neural Profile', icon: <Cpu size={16} />, onClick: () => navigate('/profile') },
                  { label: 'Admin Settings', icon: <Settings size={16} />, onClick: () => navigate('/settings') },
                ].map((item, i) => (
                  <button key={i} onClick={item.onClick} className="w-full flex items-center gap-3 p-3 rounded-2xl hover:bg-white/5 text-slate-300 hover:text-white transition-all text-[11px] font-black uppercase tracking-widest">
                    {item.icon} {item.label}
                  </button>
                ))}
                <div className="h-px bg-white/5 my-2" />
                <button 
                  onClick={() => logout()}
                  className="w-full flex items-center gap-3 p-3 rounded-2xl hover:bg-red-500/10 text-red-400 transition-all text-[11px] font-black uppercase tracking-widest"
                >
                  <LogOut size={16} /> Safely Disconnect
                </button>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </header>
  );
};

export default Header;
