import { Outlet, useLocation } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Bot, Cpu, Database, Zap, FileText, Video, Mic, Globe, ShieldCheck, Sparkles, BrainCircuit } from 'lucide-react';
import { useEffect, useState } from 'react';

const ProjectHero = () => {
  const [index, setIndex] = useState(0);
  const subtitles = [
    "Upload PDFs, Audio & Video",
    "Ask Questions with AI",
    "Semantic Search with RAG",
    "Multimedia Timestamp Intelligence",
    "AI-Powered Knowledge Retrieval"
  ];

  useEffect(() => {
    const timer = setInterval(() => {
      setIndex((prev) => (prev + 1) % subtitles.length);
    }, 3000);
    return () => clearInterval(timer);
  }, []);

  const features = [
    { icon: FileText, label: "PDF Intelligence", color: "text-purple-400" },
    { icon: Video, label: "Video Timestamps", color: "text-cyan-400" },
    { icon: Mic, label: "Audio Analysis", color: "text-pink-400" },
    { icon: Database, label: "Vector Embeddings", color: "text-emerald-400" },
  ];

  const techStack = ["React", "Spring Boot", "Pinecone", "Groq", "OpenAI", "Docker", "MySQL", "Redis"];

  return (
    <div className="flex-1 flex flex-col justify-center px-8 lg:px-20 py-12 relative overflow-hidden h-full">
      {/* Neural Background Detail */}
      <div className="absolute top-0 left-0 w-full h-full opacity-10 pointer-events-none">
         <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-cyan-500 blur-[160px] rounded-full" />
      </div>

      <motion.div
        initial={{ opacity: 0, x: -50 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.8, ease: "easeOut" }}
        className="relative z-10"
      >
        <div className="flex items-center gap-3 mb-8">
          <div className="p-3 bg-gradient-to-br from-cyan-500 to-purple-600 rounded-2xl shadow-xl shadow-cyan-500/20">
            <Bot className="w-8 h-8 text-white" />
          </div>
          <span className="text-sm font-black uppercase tracking-[0.4em] text-slate-400">Project Alpha-Node</span>
        </div>

        <h1 className="text-4xl lg:text-6xl font-black text-white leading-[1.1] mb-6 tracking-tighter">
          AI Knowledge <br />
          <span className="bg-clip-text text-transparent bg-gradient-to-r from-cyan-400 via-purple-400 to-pink-400">
            Assistant Platform
          </span>
        </h1>

        <div className="h-8 mb-10">
          <AnimatePresence mode="wait">
            <motion.p
              key={index}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              className="text-lg lg:text-xl font-bold text-cyan-400 flex items-center gap-2"
            >
              <Sparkles className="w-5 h-5" />
              {subtitles[index]}
            </motion.p>
          </AnimatePresence>
        </div>

        <p className="text-slate-400 text-lg mb-12 max-w-xl leading-relaxed font-medium">
          A high-performance RAG platform that enables semantic interaction across all media formats. 
          Upload intelligence, analyze context, and interrogate your data.
        </p>

        {/* Feature Matrix */}
        <div className="grid grid-cols-2 gap-6 mb-16">
          {features.map((f, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5 + i * 0.1 }}
              className="flex items-center gap-4 group"
            >
              <div className={`p-3 bg-white/5 rounded-xl border border-white/5 group-hover:border-cyan-500/30 transition-colors ${f.color}`}>
                <f.icon size={20} />
              </div>
              <span className="text-sm font-bold text-slate-300">{f.label}</span>
            </motion.div>
          ))}
        </div>

      </motion.div>

      {/* Floating Visual Element */}
      <motion.div 
        animate={{ y: [0, -20, 0], rotate: [0, 5, 0] }}
        transition={{ duration: 6, repeat: Infinity, ease: "easeInOut" }}
        className="absolute bottom-20 right-0 opacity-20 pointer-events-none lg:opacity-40"
      >
         <BrainCircuit size={400} className="text-cyan-500/20" />
      </motion.div>
    </div>
  );
};

const AuthLayout = () => {
  return (
    <div className="min-h-screen bg-[#020617] flex flex-col lg:flex-row relative overflow-hidden">
      {/* Deep Space Atmosphere (Common Background) */}
      <div className="absolute inset-0 z-0 pointer-events-none overflow-hidden">
        <motion.div 
          animate={{ x: [0, 50, 0], y: [0, 30, 0] }}
          transition={{ duration: 30, repeat: Infinity, ease: "linear" }}
          className="absolute top-[-20%] left-[-20%] w-[60%] h-[60%] bg-cyan-500/5 blur-[160px] rounded-full" 
        />
        <motion.div 
          animate={{ x: [0, -50, 0], y: [0, -30, 0] }}
          transition={{ duration: 35, repeat: Infinity, ease: "linear" }}
          className="absolute bottom-[-20%] right-[-20%] w-[60%] h-[60%] bg-purple-600/5 blur-[160px] rounded-full" 
        />
        
        {/* Stars */}
        <div className="absolute inset-0 opacity-20">
          {[...Array(60)].map((_, i) => (
            <motion.div
              key={i}
              className="absolute w-0.5 h-0.5 bg-white rounded-full shadow-[0_0_8px_white]"
              initial={{ x: Math.random() * 100 + "%", y: Math.random() * 100 + "%" }}
              animate={{ opacity: [0.2, 0.8, 0.2] }}
              transition={{ duration: 2 + Math.random() * 2, repeat: Infinity, delay: Math.random() * 5 }}
            />
          ))}
        </div>
      </div>

      {/* Left Side: Cinematic Project Hero (Desktop Only) */}
      <div className="hidden lg:flex lg:w-3/5 z-10 border-r border-white/5">
         <ProjectHero />
      </div>

      {/* Right Side: Auth Interface */}
      <div className="flex-1 flex flex-col items-center justify-center relative z-20 p-6 lg:p-12">
        {/* Mobile Header (Only on mobile) */}
        <div className="lg:hidden w-full max-w-md text-center mb-10 pt-10">
           <div className="inline-flex p-3 bg-gradient-to-br from-cyan-500 to-purple-600 rounded-2xl mb-6">
              <Bot className="w-8 h-8 text-white" />
           </div>
           <h1 className="text-3xl font-black text-white tracking-tight mb-2">AI KNOWLEDGE</h1>
           <p className="text-cyan-400 font-bold text-[10px] uppercase tracking-[0.3em]">Multimedia RAG Platform</p>
        </div>

        <motion.div 
          initial={{ opacity: 0, y: 30, filter: 'blur(10px)' }}
          animate={{ opacity: 1, y: 0, filter: 'blur(0px)' }}
          transition={{ duration: 1, ease: "easeOut" }}
          className="w-full max-w-md relative group"
        >
          {/* Card Glow Background */}
          <div className="absolute -inset-1 bg-gradient-to-r from-cyan-500/20 to-purple-500/20 rounded-[3rem] blur-2xl opacity-0 group-hover:opacity-100 transition-all duration-1000" />
          
          <div className="relative glass-panel rounded-[3rem] shadow-2xl border-white/5 bg-[#020617]/40">
             <Outlet />
          </div>
        </motion.div>

        {/* Mobile Stats (Only on mobile) */}
        <div className="lg:hidden mt-12 flex flex-wrap justify-center gap-4 opacity-30 px-6">
           {["PDF", "AUDIO", "VIDEO", "RAG", "LLM"].map(tag => (
             <span key={tag} className="text-[9px] font-black tracking-widest text-white border border-white/20 px-3 py-1 rounded-full">{tag}</span>
           ))}
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;
