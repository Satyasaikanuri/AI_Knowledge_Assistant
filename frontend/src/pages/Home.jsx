import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Link } from 'react-router-dom';
import { Bot, FileText, MessageSquare, Play, Sparkles, Shield, Cpu, Zap, X, Info, Database, Layers, BrainCircuit } from 'lucide-react';

const Home = () => {
  const [showAbout, setShowAbout] = useState(false);

  const presentationSteps = [
    {
      icon: <Database className="text-cyan-400" size={40} />,
      title: "Multimedia Ingestion",
      desc: "Our neural core processes high-fidelity PDFs, Audio transcripts, and Video frames, converting them into a unified knowledge stream.",
      color: "from-cyan-500/20 to-transparent"
    },
    {
      icon: <Layers className="text-purple-400" size={40} />,
      title: "Vector Matrix",
      desc: "Data is fragmented into semantic chunks and embedded into a multidimensional vector space for hyper-accurate retrieval.",
      color: "from-purple-500/20 to-transparent"
    },
    {
      icon: <BrainCircuit className="text-pink-400" size={40} />,
      title: "Cognitive Interrogation",
      desc: "Advanced RAG (Retrieval-Augmented Generation) allows you to interrogate your knowledge base with natural language, getting cited answers in real-time.",
      color: "from-pink-500/20 to-transparent"
    }
  ];

  return (
    <div className="min-h-screen bg-mesh overflow-hidden relative selection:bg-cyan-500/30">
      {/* About Application Presentation Modal */}
      <AnimatePresence>
        {showAbout && (
          <motion.div 
            initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-10 overflow-y-auto"
          >
            <div className="fixed inset-0 bg-black/95 backdrop-blur-3xl" onClick={() => setShowAbout(false)} />
            <motion.div 
              initial={{ scale: 0.9, y: 30 }} animate={{ scale: 1, y: 0 }} exit={{ scale: 0.9, y: 30 }}
              className="relative w-full max-w-6xl glass-panel rounded-[3rem] border border-white/10 shadow-[0_0_100px_rgba(34,211,238,0.1)] overflow-hidden"
            >
              <div className="absolute top-0 left-0 w-full h-[2px] bg-gradient-to-r from-transparent via-cyan-500 to-transparent opacity-50" />
              
              <div className="p-8 sm:p-16">
                 <div className="flex items-center justify-between mb-16">
                    <div>
                       <h2 className="text-4xl sm:text-5xl font-black text-white italic tracking-tighter uppercase mb-4">Neural Architecture</h2>
                       <p className="text-[11px] font-black text-cyan-400 uppercase tracking-[0.4em]">The Science Behind the Assistant</p>
                    </div>
                    <button onClick={() => setShowAbout(false)} className="p-4 bg-white/5 hover:bg-red-500 text-white rounded-full transition-all group">
                       <X size={24} className="group-hover:rotate-90 transition-transform" />
                    </button>
                 </div>

                 <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {presentationSteps.map((step, i) => (
                      <motion.div 
                        initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.2 }}
                        key={i} className={`p-10 rounded-[2.5rem] bg-gradient-to-br ${step.color} border border-white/5 relative group hover:border-white/10 transition-all`}
                      >
                         <div className="mb-8 p-5 bg-white/5 rounded-2xl w-fit group-hover:scale-110 transition-transform">
                            {step.icon}
                         </div>
                         <h3 className="text-2xl font-black text-white mb-4 uppercase italic tracking-tight">{step.title}</h3>
                         <p className="text-slate-400 font-medium leading-relaxed">{step.desc}</p>
                         <div className="mt-8 flex items-center gap-2 opacity-30">
                            <div className="w-1.5 h-1.5 rounded-full bg-cyan-500" />
                            <div className="h-px flex-1 bg-white/10" />
                         </div>
                      </motion.div>
                    ))}
                 </div>

                 <div className="mt-16 p-8 glass-card rounded-[2.5rem] border-cyan-500/10 flex flex-col sm:flex-row items-center justify-between gap-8">
                    <div className="flex items-center gap-6">
                       <div className="p-4 bg-cyan-500/10 rounded-2xl text-cyan-400">
                          <Bot size={32} />
                       </div>
                       <div>
                          <p className="text-xl font-bold text-white tracking-tight">Ready to Begin Interrogation?</p>
                          <p className="text-sm text-slate-500 mt-1 uppercase tracking-widest font-black">Secure Link Protocols Ready</p>
                       </div>
                    </div>
                    <Link to="/register" className="px-10 py-5 bg-gradient-to-r from-cyan-500 to-purple-600 rounded-2xl font-black text-[13px] uppercase tracking-widest text-white italic shadow-lg shadow-cyan-500/20 hover:scale-105 transition-all">
                       Establish Neural Link
                    </Link>
                 </div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Animated Background Particles */}
      <div className="absolute inset-0 z-0">
        {[...Array(20)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute h-1 w-1 bg-cyan-400 rounded-full opacity-20"
            initial={{ 
              x: Math.random() * window.innerWidth, 
              y: Math.random() * window.innerHeight 
            }}
            animate={{
              y: [0, -20, 0],
              opacity: [0.2, 0.5, 0.2],
            }}
            transition={{
              duration: 3 + Math.random() * 2,
              repeat: Infinity,
              delay: Math.random() * 2,
            }}
          />
        ))}
      </div>

      {/* Navbar */}
      <nav className="relative z-50 flex items-center justify-between px-8 py-6 max-w-7xl mx-auto">
        <motion.div 
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="flex items-center gap-2 group cursor-pointer"
        >
          <div className="p-2 bg-gradient-to-br from-cyan-500 to-purple-600 rounded-xl group-hover:rotate-12 transition-transform duration-300 shadow-lg shadow-cyan-500/20">
            <Bot className="w-6 h-6 text-white" />
          </div>
          <span className="text-xl font-black italic bg-clip-text text-transparent bg-gradient-to-r from-white to-slate-400 uppercase tracking-tighter">
            Neural AI
          </span>
        </motion.div>
        
        <motion.div 
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="flex items-center gap-6"
        >
          <Link to="/login" className="text-[11px] font-black uppercase tracking-widest text-slate-400 hover:text-white transition-colors">
            Sign In
          </Link>
          <Link to="/register" className="px-6 py-2.5 bg-cyan-500/10 hover:bg-cyan-500/20 backdrop-blur-md border border-cyan-500/20 rounded-xl text-[11px] font-black uppercase tracking-widest text-cyan-400 transition-all hover:scale-105 active:scale-95 glow-cyan">
            Sign Up
          </Link>
        </motion.div>
      </nav>

      {/* Hero Section */}
      <main className="relative z-10 max-w-7xl mx-auto px-8 pt-20 pb-32 flex flex-col lg:flex-row items-center justify-between gap-16">
        <div className="flex-1 text-center lg:text-left">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-cyan-500/10 border border-cyan-500/20 text-cyan-400 text-[11px] font-black uppercase tracking-widest mb-6 glow-cyan"
          >
            <Sparkles className="w-3 h-3" />
            <span>AI-Powered Multimedia RAG Platform</span>
          </motion.div>

          <motion.h1 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="text-6xl lg:text-8xl font-black mb-8 leading-none tracking-tighter italic"
          >
            Upload. Analyze. <br />
            <span className="bg-clip-text text-transparent bg-gradient-to-r from-cyan-400 via-purple-400 to-pink-400">
              Ask Anything.
            </span>
          </motion.h1>

          <motion.p
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="text-lg font-medium text-slate-400 mb-10 max-w-xl mx-auto lg:mx-0 leading-relaxed"
          >
            The ultimate knowledge assistant that understands your documents, audio, and video files. 
            Experience the future of information retrieval.
          </motion.p>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
            className="flex flex-col sm:flex-row items-center gap-4 justify-center lg:justify-start"
          >
            <button 
              onClick={() => setShowAbout(true)}
              className="px-12 py-6 bg-gradient-to-r from-cyan-500 via-purple-600 to-blue-600 rounded-3xl font-black text-[15px] uppercase tracking-widest text-white shadow-2xl shadow-cyan-500/30 hover:shadow-cyan-500/50 hover:scale-105 transition-all duration-300 italic flex items-center gap-4 group"
            >
              <Info size={24} className="group-hover:rotate-12 transition-transform" />
              About This Application
            </button>
          </motion.div>
        </div>

        {/* Mascot / Visual Side */}
        <motion.div
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 1, ease: "easeOut" }}
          className="flex-1 relative"
        >
          {/* AI Generated Nexus Hero */}
          <motion.div
            animate={{ y: [0, -20, 0] }}
            transition={{ duration: 4, repeat: Infinity, ease: "easeInOut" }}
            className="relative z-20 w-80 h-80 lg:w-[500px] lg:h-[500px] mx-auto"
          >
            <div className="absolute inset-0 bg-gradient-to-br from-cyan-500/20 to-purple-600/30 rounded-full blur-[100px]" />
            <div className="w-full h-full glass-panel rounded-[3rem] flex items-center justify-center p-4 border-cyan-500/20 overflow-hidden shadow-[0_0_50px_rgba(34,211,238,0.2)]">
               <img 
                 src="https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&q=80&w=1000" 
                 alt="AI Knowledge Nexus"
                 className="w-full h-full object-cover rounded-[2rem] opacity-80 mix-blend-screen"
               />
               <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-transparent to-transparent opacity-60" />
            </div>
            
            {/* Orbiting Elements */}
            <motion.div
              animate={{ rotate: 360 }}
              transition={{ duration: 30, repeat: Infinity, ease: "linear" }}
              className="absolute inset-0"
            >
              <div className="absolute -top-4 left-1/2 -translate-x-1/2 p-4 glass-panel rounded-2xl glow-cyan">
                <FileText className="w-6 h-6 text-cyan-400" />
              </div>
              <div className="absolute -bottom-4 left-1/2 -translate-x-1/2 p-4 glass-panel rounded-2xl glow-blue">
                <Zap className="w-6 h-6 text-blue-400" />
              </div>
              <div className="absolute top-1/2 -left-4 -translate-y-1/2 p-4 glass-panel rounded-2xl glow-purple">
                <MessageSquare className="w-6 h-6 text-purple-400" />
              </div>
            </motion.div>
          </motion.div>
          
          <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-cyan-500/10 blur-[150px] rounded-full z-0" />
        </motion.div>
      </main>

      {/* Feature Section */}
      <section className="relative z-10 max-w-7xl mx-auto px-8 pb-32">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {[
            { icon: Zap, title: "Instant Analysis", desc: "Process massive PDFs and media files in seconds." },
            { icon: Cpu, title: "Multi-Modal AI", desc: "Supports text, audio, and video for total context." },
            { icon: Shield, title: "Secure & Private", desc: "Enterprise-grade encryption for all your data." }
          ].map((feature, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.1 }}
              className="glass-panel p-10 rounded-[2.5rem] border-white/5 hover:border-cyan-500/20 transition-all group"
            >
              <div className="p-4 bg-cyan-500/10 rounded-2xl w-fit mb-8 group-hover:glow-cyan transition-all">
                <feature.icon className="w-8 h-8 text-cyan-400" />
              </div>
              <h3 className="text-2xl font-black mb-4 uppercase italic tracking-tighter">{feature.title}</h3>
              <p className="text-slate-400 font-medium leading-relaxed">{feature.desc}</p>
            </motion.div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default Home;
