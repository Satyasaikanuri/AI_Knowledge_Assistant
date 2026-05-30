import { useState, useEffect } from 'react';
import { useAuthStore } from '../store/useAuthStore';
import { Users, UploadCloud, Activity, Trash2, ShieldAlert, Zap, Server, Database, Lock } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';
import api from '../api/api';

const AdminDashboard = () => {
  const [stats, setStats] = useState({ totalUsers: 0, totalUploads: 0, totalQueries: 0, systemHealth: 'OPERATIONAL' });
  const [users, setUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchAdminData();
  }, []);

  const fetchAdminData = async () => {
    try {
      const [statsRes, usersRes] = await Promise.all([
        api.get('/admin/stats'),
        api.get('/admin/users')
      ]);
      setStats(statsRes.data);
      setUsers(usersRes.data);
    } catch (error) {
      toast.error('System Link Failure: Admin Data Unreachable');
    } finally {
      setIsLoading(false);
    }
  };

  const deleteUser = async (id) => {
    if (!window.confirm('Initiate user termination sequence? This cannot be undone.')) return;
    try {
      await api.delete(`/admin/users/${id}`);
      setUsers(users.filter(u => u.id !== id));
      toast.success('User Identity Terminated');
    } catch (error) {
      toast.error('Termination Failed: User protected by system level lock');
    }
  };

  if (isLoading) return (
    <div className="flex flex-col items-center justify-center h-[60vh] gap-6">
       <div className="relative">
          <div className="absolute inset-0 bg-red-500 blur-xl opacity-20 animate-pulse" />
          <Activity className="animate-spin text-red-500 relative" size={48} />
       </div>
       <p className="text-xs font-bold text-slate-500 uppercase tracking-[0.3em]">Accessing Neural Admin Core...</p>
    </div>
  );

  return (
    <div className="max-w-7xl mx-auto space-y-12 selection:bg-red-500/30">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
           <motion.div 
             initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}
             className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-red-500/10 border border-red-500/20 text-red-500 text-[10px] font-bold uppercase tracking-widest mb-4"
           >
             <Lock size={12} />
             <span>Restricted Access: Level 01 Admin</span>
           </motion.div>
           <h1 className="text-4xl font-bold text-white tracking-tight flex items-center gap-4">
             System Control Center
             <ShieldAlert size={32} className="text-red-500" />
           </h1>
        </div>

        <div className="flex items-center gap-4 px-6 py-4 glass-panel rounded-3xl border-red-500/10 bg-red-500/5">
           <div className="relative">
              <div className="absolute inset-0 bg-red-500 blur-md opacity-40 animate-pulse" />
              <div className="relative p-2 bg-red-600 rounded-xl">
                 <Server size={20} className="text-white" />
              </div>
           </div>
           <div>
              <p className="text-xs font-bold text-red-400 uppercase tracking-tighter">Core Health</p>
              <p className="text-[10px] text-white font-bold uppercase tracking-[0.2em]">{stats.systemHealth}</p>
           </div>
        </div>
      </div>

      {/* Admin Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {[
          { name: 'Total Entities', value: stats.totalUsers, icon: Users, color: 'text-blue-400', glow: 'shadow-blue-500/10' },
          { name: 'Data Units', value: stats.totalUploads, icon: Database, color: 'text-emerald-400', glow: 'shadow-emerald-500/10' },
          { name: 'Neural Queries', value: stats.totalQueries, icon: Zap, color: 'text-purple-400', glow: 'shadow-purple-500/10' },
          { name: 'Throughput', value: '1.2 GB/s', icon: Activity, color: 'text-cyan-400', glow: 'shadow-cyan-500/10' },
        ].map((stat, i) => (
          <motion.div 
            key={stat.name}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.1 }}
            className={`glass-card p-6 rounded-3xl relative overflow-hidden ${stat.glow}`}
          >
            <div className="absolute top-0 right-0 p-4 opacity-5">
               <stat.icon size={60} />
            </div>
            <p className="text-[10px] font-bold text-slate-500 uppercase tracking-widest mb-4">{stat.name}</p>
            <p className={`text-4xl font-bold text-white tabular-nums ${stat.color}`}>{stat.value}</p>
            <div className="mt-4 h-1 bg-white/5 rounded-full overflow-hidden">
               <motion.div 
                 initial={{ width: 0 }} animate={{ width: '70%' }} transition={{ duration: 1, delay: 0.5 }}
                 className={`h-full bg-current ${stat.color}`} 
               />
            </div>
          </motion.div>
        ))}
      </div>

      {/* User Management Section */}
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="glass-panel rounded-[2.5rem] border-white/5 overflow-hidden shadow-2xl"
      >
        <div className="px-10 py-8 border-b border-white/5 flex items-center justify-between">
           <h2 className="text-xl font-bold text-white tracking-tight">Identity Matrix Management</h2>
           <div className="flex items-center gap-2 px-4 py-1.5 bg-white/5 rounded-full border border-white/10 text-[10px] font-bold text-slate-400 uppercase">
              <div className="w-1.5 h-1.5 bg-cyan-500 rounded-full animate-pulse" />
              Live Monitoring
           </div>
        </div>
        
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-slate-900/50">
                <th className="py-5 px-10 font-bold text-[10px] uppercase tracking-[0.2em] text-slate-500">Node ID</th>
                <th className="py-5 px-10 font-bold text-[10px] uppercase tracking-[0.2em] text-slate-500">Identity</th>
                <th className="py-5 px-10 font-bold text-[10px] uppercase tracking-[0.2em] text-slate-500">Neural Link (Email)</th>
                <th className="py-5 px-10 font-bold text-[10px] uppercase tracking-[0.2em] text-slate-500">Clearance</th>
                <th className="py-5 px-10 font-bold text-[10px] uppercase tracking-[0.2em] text-slate-500 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5">
              {users.map(user => (
                <motion.tr 
                  key={user.id} 
                  initial={{ opacity: 0 }} animate={{ opacity: 1 }}
                  className="hover:bg-white/[0.02] transition-colors group"
                >
                  <td className="py-5 px-10 text-xs font-mono text-slate-500">#{user.id}</td>
                  <td className="py-5 px-10">
                     <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-slate-700 to-slate-800 flex items-center justify-center font-bold text-xs text-white border border-white/10 group-hover:border-cyan-500/50 transition-all">
                           {user.firstName[0]}
                        </div>
                        <span className="text-sm font-bold text-white tracking-tight">{user.firstName} {user.lastName}</span>
                     </div>
                  </td>
                  <td className="py-5 px-10 text-sm text-slate-400 font-medium">{user.email}</td>
                  <td className="py-5 px-10">
                    <span className={clsx(
                      "px-3 py-1 rounded-full text-[10px] font-bold tracking-widest uppercase border",
                      user.role === 'ADMIN' 
                        ? "bg-red-500/10 text-red-400 border-red-500/20" 
                        : "bg-cyan-500/10 text-cyan-400 border-cyan-500/20"
                    )}>
                      {user.role}
                    </span>
                  </td>
                  <td className="py-5 px-10 text-right">
                    <button 
                      onClick={() => deleteUser(user.id)}
                      disabled={user.role === 'ADMIN'}
                      className="p-2 text-slate-600 hover:text-red-500 hover:bg-red-500/10 rounded-xl transition-all disabled:opacity-0 disabled:pointer-events-none"
                      title="Terminate Identity"
                    >
                      <Trash2 size={18} />
                    </button>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
        
        {users.length === 0 && (
          <div className="py-20 flex flex-col items-center justify-center text-slate-600">
             <Users size={48} className="mb-4 opacity-20" />
             <p className="text-xs font-bold uppercase tracking-widest">No Identities Found</p>
          </div>
        )}
      </motion.div>
    </div>
  );
};

export default AdminDashboard;
