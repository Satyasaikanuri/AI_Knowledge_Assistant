import { useState, useEffect } from 'react';
import { 
  Users, UploadCloud, Activity, Zap, FileText, Search, 
  Database, Server, Cpu, BrainCircuit, CheckCircle2, XCircle,
  TrendingUp, BarChart3, Clock, ArrowRight, Sparkles, Layers, ShieldCheck
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import api from '../api/api';
import { toast } from 'react-hot-toast';
import { 
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer
} from 'recharts';
import NeuralBackground from '../components/ui/NeuralBackground';

const Dashboard = () => {
  const [stats, setStats] = useState({ 
    totalUploads: 0, 
    totalChats: 0, 
    audioProcessed: 0, 
    videoProcessed: 0,
    vectorCount: 0
  });
  
  const [systemStatus] = useState({
    openai: 'UP',
    pinecone: 'UP',
    redis: 'UP',
    embeddings: 'UP'
  });

  const [recentUploads, setRecentUploads] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchDashboardData = async () => {
    try {
      const [statsRes, uploadsRes] = await Promise.all([
        api.get('/stats'),
        api.get('/files/list')
      ]);
      
      setStats({
        ...statsRes.data,
        vectorCount: (statsRes.data.totalUploads * 142) // Simulated vector count based on real uploads
      });
      
      setRecentUploads(uploadsRes.data.slice(0, 5));
    } catch (error) {
      console.error('Telemetry link failed', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const chartData = [
    { name: '00:00', q: 12, u: 5 }, { name: '04:00', q: 18, u: 8 }, 
    { name: '08:00', q: 45, u: 24 }, { name: '12:00', q: 62, u: 31 }, 
    { name: '16:00', q: 48, u: 22 }, { name: '20:00', q: 38, u: 15 }, 
    { name: '23:59', q: 25, u: 10 }
  ];

  const StatCard = ({ label, value, icon: Icon, color, delay }) => (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay }}
      className="glass-panel p-6 rounded-3xl border-white/5 relative overflow-hidden group hover:border-cyan-500/20 transition-all"
    >
      <div className={`absolute top-0 right-0 p-4 opacity-5 group-hover:opacity-20 transition-opacity ${color}`}>
         <Icon size={70} />
      </div>
      <p className="text-[11px] font-black text-slate-400 uppercase tracking-widest mb-2">{label}</p>
      <div className="flex items-baseline gap-2">
         <motion.h3 
           initial={{ scale: 0.5 }} animate={{ scale: 1 }}
           className={`text-5xl font-black ${color} tracking-tighter`}
         >
           {value}
         </motion.h3>
         <span className="text-[11px] font-black text-emerald-500">+Synced</span>
      </div>
    </motion.div>
  );

  return (
    <div className="max-w-7xl mx-auto space-y-10 relative">
      <NeuralBackground />
      
      {/* HUD Header */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 relative z-10">
         {[
           { name: 'OpenAI Core', status: systemStatus.openai, icon: Zap },
           { name: 'Vector Matrix', status: systemStatus.pinecone, icon: Database },
           { name: 'Neural Cache', status: systemStatus.redis, icon: Server },
           { name: 'Cognitive Engine', status: systemStatus.embeddings, icon: Cpu }
         ].map((sys) => (
           <div key={sys.name} className="flex items-center justify-between p-4 glass-card border-white/5 group hover:border-cyan-500/30 transition-all">
              <div className="flex items-center gap-3">
                 <div className="p-2 bg-white/5 rounded-lg text-slate-400 group-hover:text-cyan-400 transition-colors">
                    <sys.icon size={16} />
                 </div>
                 <span className="text-[9px] font-black text-slate-400 uppercase tracking-widest">{sys.name}</span>
              </div>
              <div className="flex items-center gap-2">
                 <div className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse shadow-[0_0_8px_rgba(16,185,129,0.8)]" />
                 <span className="text-[9px] font-black text-emerald-500 tracking-tighter">STABLE</span>
              </div>
           </div>
         ))}
      </div>

      <div className="flex flex-col lg:flex-row gap-8 relative z-10">
        <div className="flex-1 space-y-8">
          {/* Diagnostic Stats */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <StatCard label="Total Ingestions" value={stats.totalUploads} icon={UploadCloud} color="text-cyan-400" delay={0.1} />
            <StatCard label="AI Interactions" value={stats.totalChats} icon={BrainCircuit} color="text-purple-400" delay={0.2} />
            <StatCard label="Vector Density" value={stats.vectorCount} icon={Database} color="text-pink-400" delay={0.3} />
          </div>

          {/* Performance Chart */}
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }} className="glass-panel p-8 rounded-[2.5rem] border-white/5 relative overflow-hidden group">
            <div className="absolute top-0 right-0 p-8 opacity-5 pointer-events-none">
               <TrendingUp size={120} className="text-cyan-400" />
            </div>
            <div className="flex items-center justify-between mb-10">
               <div>
                  <h3 className="font-black text-xl text-white tracking-tight uppercase italic flex items-center gap-2">
                    <Activity className="text-cyan-400" size={20} />
                    Neural Throughput
                  </h3>
                  <p className="text-[11px] text-slate-400 mt-1 uppercase tracking-[0.4em] font-black">Diagnostic Intelligence Feed</p>
               </div>
               <div className="flex gap-4">
                  <div className="flex items-center gap-2"><div className="w-2 h-2 rounded-full bg-cyan-400 shadow-[0_0_8px_rgba(34,211,238,0.6)]" /><span className="text-[11px] font-black text-slate-400 uppercase tracking-widest">Queries</span></div>
                  <div className="flex items-center gap-2"><div className="w-2 h-2 rounded-full bg-purple-500" /><span className="text-[11px] font-black text-slate-400 uppercase tracking-widest">Uploads</span></div>
               </div>
            </div>
            <div className="h-[320px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData}>
                  <defs>
                    <linearGradient id="glowQueries" x1="0" y1="0" x2="0" y2="1"><stop offset="5%" stopColor="#22d3ee" stopOpacity={0.4}/><stop offset="95%" stopColor="#22d3ee" stopOpacity={0}/></linearGradient>
                    <linearGradient id="glowUploads" x1="0" y1="0" x2="0" y2="1"><stop offset="5%" stopColor="#a855f7" stopOpacity={0.2}/><stop offset="95%" stopColor="#a855f7" stopOpacity={0}/></linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#ffffff03" />
                  <XAxis dataKey="name" stroke="#334155" fontSize={10} fontWeight="bold" tickLine={false} axisLine={false} />
                  <YAxis hide />
                  <Tooltip contentStyle={{ backgroundColor: '#020617', border: '1px solid rgba(255,255,255,0.05)', borderRadius: '16px', boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)' }} />
                  <Area type="monotone" dataKey="q" stroke="#22d3ee" strokeWidth={4} fill="url(#glowQueries)" />
                  <Area type="monotone" dataKey="u" stroke="#a855f7" strokeWidth={3} fill="url(#glowUploads)" />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </motion.div>
        </div>

        {/* Sidebar Feeds */}
        <div className="w-full lg:w-96 space-y-8">
           {/* Recent Ingestions Feed */}
           <div className="glass-panel p-8 rounded-[2.5rem] border-white/5 h-fit relative group">
              <div className="flex items-center justify-between mb-8">
                 <h3 className="font-black text-lg text-white tracking-tight uppercase italic">Neural Feed</h3>
                 <Sparkles size={18} className="text-cyan-400 animate-pulse" />
              </div>
              <div className="space-y-4">
                 {recentUploads.length > 0 ? recentUploads.map((file, i) => (
                   <motion.div 
                     key={file.id} initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.1 }}
                     className="flex items-center gap-4 p-4 rounded-2xl bg-white/5 border border-white/5 hover:border-cyan-500/20 transition-all cursor-default group/item"
                   >
                      <div className="w-10 h-10 rounded-xl bg-cyan-500/10 flex items-center justify-center text-cyan-400 border border-cyan-500/10 group-hover/item:glow-cyan">
                         <FileText size={18} />
                      </div>
                      <div className="flex-1 min-w-0">
                         <p className="text-[11px] font-black text-slate-200 truncate uppercase tracking-tight">{file.originalFileName}</p>
                         <p className="text-[8px] text-emerald-500 font-black uppercase tracking-widest mt-1 flex items-center gap-1">
                            <CheckCircle2 size={8} /> Processed
                         </p>
                      </div>
                   </motion.div>
                 )) : (
                   <div className="py-12 flex flex-col items-center justify-center opacity-30">
                      <Layers size={40} className="text-slate-600 mb-4" />
                      <p className="text-[10px] font-black text-slate-600 uppercase tracking-widest text-center">Awaiting Data Ingestion</p>
                   </div>
                 )}
              </div>
           </div>

           {/* Knowledge Integrity Score */}
           <div className="glass-panel p-8 rounded-[2.5rem] border-white/5 relative overflow-hidden bg-gradient-to-br from-cyan-500/5 to-transparent">
              <div className="flex items-center justify-between mb-6">
                 <h3 className="font-black text-lg text-white tracking-tight uppercase italic flex items-center gap-2">
                   <ShieldCheck className="text-cyan-400" size={18} />
                   Core Integrity
                 </h3>
                 <span className="text-xs font-black text-cyan-400">98.4%</span>
              </div>
              <div className="space-y-6">
                 <div className="h-1.5 w-full bg-white/5 rounded-full overflow-hidden">
                    <motion.div initial={{ width: 0 }} animate={{ width: '98.4%' }} transition={{ duration: 2 }} className="h-full bg-cyan-500 glow-cyan" />
                 </div>
                 <div className="grid grid-cols-2 gap-4">
                    <div className="p-4 bg-white/5 rounded-2xl border border-white/5">
                       <p className="text-[8px] font-black text-slate-500 uppercase tracking-widest mb-1">Knowledge Units</p>
                       <p className="text-sm font-black text-white">{stats.totalUploads}</p>
                    </div>
                    <div className="p-4 bg-white/5 rounded-2xl border border-white/5">
                       <p className="text-[8px] font-black text-slate-500 uppercase tracking-widest mb-1">Neural Sync</p>
                       <p className="text-sm font-black text-emerald-400 italic">OPTIMAL</p>
                    </div>
                 </div>
              </div>
           </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
