import { useState, useRef, useEffect } from 'react';
import { 
  Send, Bot, User, Loader2, FileText, ChevronDown, Copy, 
  RefreshCw, Check, PlayCircle, Sparkles, Zap, BrainCircuit, 
  Mic, Video, Database, Quote, Info, Search, MessageSquare, 
  Terminal, Activity, Layers, Play, Cpu, Paperclip, X
} from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import api from '../api/api';
import { useAuthStore } from '../store/useAuthStore';
import clsx from 'clsx';
import { motion, AnimatePresence } from 'framer-motion';
import { toast } from 'react-hot-toast';
import NeuralBackground from '../components/ui/NeuralBackground';

const Chat = () => {
  const [messages, setMessages] = useState([
    { id: 1, role: 'ai', text: 'Hello! I am your AI Knowledge Assistant. Attach a knowledge unit to begin neural interrogation.', timestamps: [] }
  ]);
  const [copiedId, setCopiedId] = useState(null);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState('');
  const user = useAuthStore(state => state.user);
  const token = useAuthStore(state => state.token);
  
  const messagesEndRef = useRef(null);
  const mediaRef = useRef(null);
  const inputRef = useRef(null);
  const [nowPlaying, setNowPlaying] = useState(null);

  const selectedFileObj = files.find(f => String(f.id) === String(selectedFile));
  const isMedia = selectedFileObj && selectedFileObj.fileType && (selectedFileObj.fileType.startsWith('audio/') || selectedFileObj.fileType.startsWith('video/'));

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (selectedFile) {
      inputRef.current?.focus();
    }
  }, [selectedFile]);

  const fetchFiles = async () => {
    try {
      const response = await api.get('/files/list');
      setFiles(response.data.map(f => ({ id: f.id, name: f.originalFileName, fileType: f.fileType })));
    } catch (error) {
      console.error('Failed to fetch files', error);
    }
  };

  useEffect(() => {
    fetchFiles();
  }, []);

  const handleTimestampClick = (startTime, topic) => {
    if (mediaRef.current) {
      mediaRef.current.currentTime = startTime;
      mediaRef.current.play();
      setNowPlaying(`Syncing: ${topic} @ ${startTime}s`);
    }
  };

  const handleSend = async (e) => {
    if (e) e.preventDefault();
    if (!input.trim() || !selectedFile || isLoading) return;

    const userMessage = { id: Date.now(), role: 'user', text: input, attachedFile: selectedFileObj.name };
    setMessages(prev => [...prev, userMessage]);
    const currentInput = input;
    setInput('');
    setIsLoading(true);

    try {
      const response = await api.post('/chat/ask', {
        question: currentInput,
        fileId: selectedFile
      });
      
      const aiMessage = { 
        id: Date.now() + 1, 
        role: 'ai', 
        text: '', 
        fullText: response.data.answer,
        timestamps: response.data.timestamps || [],
        citations: response.data.citations || [],
        isStreaming: true
      };
      
      setMessages(prev => [...prev, aiMessage]);
      
      let currentText = "";
      const fullText = response.data.answer;
      const words = fullText.split(" ");
      let wordIndex = 0;

      const stream = () => {
        if (wordIndex < words.length) {
          currentText += (wordIndex === 0 ? "" : " ") + words[wordIndex];
          setMessages(prev => prev.map(m => m.id === aiMessage.id ? { ...m, text: currentText } : m));
          wordIndex++;
          setTimeout(stream, Math.random() * 40 + 20);
        } else {
          setMessages(prev => prev.map(m => m.id === aiMessage.id ? { ...m, isStreaming: false } : m));
        }
      };
      
      stream();
      
    } catch (error) {
      const errorMsg = error.response?.data?.message || error.message;
      setMessages(prev => [...prev, { id: Date.now(), role: 'ai', text: `Critical Failure: ${errorMsg}` }]);
      toast.error(`Neural Error: ${errorMsg}`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="flex flex-col h-[calc(100vh-8rem)] max-w-6xl mx-auto glass-panel rounded-[2.5rem] overflow-hidden relative border-white/5">
      <NeuralBackground />
      
      {/* Terminal Header */}
      <div className="px-8 py-5 border-b border-white/5 bg-slate-950/20 backdrop-blur-md flex flex-col sm:flex-row items-center justify-between gap-4 z-20">
        <div className="flex items-center gap-4">
          <div className="relative">
             <div className="absolute inset-0 bg-cyan-500 blur-md opacity-20 animate-pulse" />
             <div className="relative w-12 h-12 rounded-2xl bg-gradient-to-br from-cyan-500 to-purple-600 flex items-center justify-center text-white shadow-lg">
                <BrainCircuit size={24} />
             </div>
          </div>
          <div>
            <h2 className="font-black text-lg tracking-tight text-white uppercase italic">Neural Core Terminal</h2>
            <p className="text-[11px] uppercase tracking-[0.3em] font-bold text-cyan-400">Contextual Ingestion Active</p>
          </div>
        </div>

        <div className="relative group w-full sm:w-auto">
          <select 
            value={selectedFile}
            onChange={(e) => setSelectedFile(e.target.value)}
            className="w-full sm:w-72 appearance-none pl-12 pr-10 py-3 bg-white/5 border border-white/10 rounded-2xl text-[11px] text-slate-300 focus:outline-none focus:ring-2 focus:ring-cyan-500/50 transition-all cursor-pointer hover:bg-white/10 font-black uppercase tracking-widest"
          >
            <option value="" disabled>Select Knowledge Unit</option>
            {files.map(f => (
              <option key={f.id} value={f.id} className="bg-slate-900">{f.name}</option>
            ))}
          </select>
          <div className="absolute left-4 top-1/2 -translate-y-1/2 p-1.5 bg-white/5 rounded-lg text-cyan-400">
            <Paperclip size={16} />
          </div>
          <ChevronDown size={16} className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 pointer-events-none group-hover:text-cyan-400 transition-colors" />
        </div>
      </div>

      {/* Media Player Area */}
      <AnimatePresence>
        {isMedia && (
          <motion.div 
            initial={{ height: 0, opacity: 0 }} animate={{ height: 'auto', opacity: 1 }} exit={{ height: 0, opacity: 0 }}
            className="px-8 py-6 bg-slate-950/40 border-b border-white/5 relative z-10"
          >
            <div className="max-w-3xl mx-auto rounded-3xl overflow-hidden shadow-2xl border border-white/10 relative bg-black group">
              <video 
                ref={mediaRef} controls className="w-full max-h-[35vh]"
                src={`http://localhost:8081/api/v1/files/stream/${selectedFile}?token=${token}`}
              />
              <div className="absolute top-4 right-4 opacity-0 group-hover:opacity-100 transition-opacity">
                 <button onClick={() => setNowPlaying(null)} className="p-2 bg-black/50 text-white rounded-full hover:bg-red-500 transition-colors">
                    <X size={16} />
                 </button>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Chat Thread */}
      <div className="flex-1 overflow-y-auto px-8 py-10 space-y-12 custom-scrollbar relative z-10">
        {messages.map((msg, index) => (
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} key={msg.id} 
            className={clsx("flex gap-6 max-w-4xl mx-auto", msg.role === 'user' ? "flex-row-reverse" : "")}
          >
            <div className={clsx(
              "w-10 h-10 rounded-2xl flex items-center justify-center shrink-0 shadow-xl relative",
              msg.role === 'user' ? "bg-gradient-to-br from-cyan-400 to-cyan-600 text-white" : "bg-slate-800 text-purple-400 border border-white/10"
            )}>
              {msg.role === 'user' ? (user?.firstName?.charAt(0) || 'U') : <Bot size={22} />}
              {msg.role === 'ai' && <div className="absolute inset-0 bg-purple-500 blur-lg opacity-20" />}
            </div>
            
            <div className={clsx("flex flex-col gap-3", msg.role === 'user' ? "items-end" : "items-start")}>
              {msg.attachedFile && (
                <div className="flex items-center gap-2 px-3 py-1 bg-white/5 border border-white/10 rounded-full text-[11px] font-black text-slate-300 uppercase tracking-widest mb-1">
                   <Paperclip size={10} className="text-cyan-500" />
                   {msg.attachedFile}
                </div>
              )}
              <div className={clsx(
                "px-7 py-5 rounded-[2.5rem] shadow-2xl text-[15px] leading-relaxed relative",
                msg.role === 'user' ? "bg-cyan-600 text-white rounded-tr-none border border-cyan-400/20" : "glass-card text-white rounded-tl-none border-white/5"
              )}>
                <ReactMarkdown className="prose prose-invert max-w-none prose-sm font-medium">{msg.text}</ReactMarkdown>
                
                {msg.timestamps && msg.timestamps.length > 0 && (
                  <div className="mt-8 pt-6 border-t border-white/5">
                    <p className="text-[11px] font-black text-slate-400 uppercase tracking-[0.4em] mb-4">Neural Sync Points</p>
                    <div className="flex flex-wrap gap-2">
                      {msg.timestamps.map((ts, idx) => (
                        <button key={idx} onClick={() => handleTimestampClick(ts.startTime, ts.topic)}
                          className="px-4 py-2 bg-purple-500/10 text-purple-400 border border-purple-500/20 rounded-xl text-[11px] font-black hover:bg-purple-500/20 transition-all flex items-center gap-2 uppercase tracking-widest"
                        >
                          <PlayCircle size={12} />
                          {ts.topic} <span className="opacity-40">{ts.startTime}s</span>
                        </button>
                      ))}
                    </div>
                  </div>
                )}

                {msg.citations && msg.citations.length > 0 && !msg.isStreaming && (
                   <div className="mt-8 pt-6 border-t border-white/5">
                      <div className="flex flex-wrap gap-3">
                         {msg.citations.map((cit, i) => (
                           <div key={i} className="flex items-center gap-3 px-4 py-2 bg-white/5 rounded-2xl border border-white/5">
                              <span className="text-[10px] font-bold text-slate-400 uppercase tracking-tighter">Page {cit.page}</span>
                              <div className="w-px h-3 bg-white/10" />
                              <span className="text-[10px] font-black text-emerald-400">{(cit.similarity * 100).toFixed(1)}% Neural Match</span>
                           </div>
                         ))}
                      </div>
                   </div>
                )}
              </div>
            </div>
          </motion.div>
        ))}
        {isLoading && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex gap-6 max-w-4xl mx-auto">
            <div className="w-10 h-10 rounded-2xl bg-slate-800 text-purple-400 border border-white/10 flex items-center justify-center shrink-0">
               <Loader2 size={20} className="animate-spin" />
            </div>
            <div className="px-10 py-8 rounded-[2.5rem] glass-card border-white/5 w-80 shadow-2xl space-y-4">
               <div className="flex items-center gap-3">
                  <div className="w-2 h-2 rounded-full bg-cyan-500 animate-ping" />
                  <span className="text-[10px] font-black text-cyan-400 uppercase tracking-widest">Cognitive Processing</span>
               </div>
               <div className="h-2 bg-white/5 rounded-full w-full animate-pulse" />
               <div className="h-2 bg-white/5 rounded-full w-3/4 animate-pulse" />
            </div>
          </motion.div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Futuristic Textarea Bar */}
      <div className="px-8 py-10 relative z-20">
        <div className="max-w-4xl mx-auto relative group">
          <div className="absolute -inset-1 bg-gradient-to-r from-cyan-500/10 to-purple-600/10 blur opacity-0 group-focus-within:opacity-100 transition-opacity rounded-[2.5rem]" />
          
          <form onSubmit={handleSend} className="relative flex flex-col bg-slate-900/90 backdrop-blur-3xl border border-white/10 rounded-[2.5rem] p-4 shadow-2xl focus-within:border-cyan-500/40 transition-all">
            
            <AnimatePresence>
              {selectedFileObj && (
                <motion.div 
                  initial={{ opacity: 0, scale: 0.9, y: 10 }} animate={{ opacity: 1, scale: 1, y: 0 }} exit={{ opacity: 0, scale: 0.9, y: 10 }}
                  className="flex items-center gap-3 px-4 py-3 bg-white/5 border border-white/10 rounded-2xl w-fit mb-3 group/file relative overflow-hidden"
                >
                   <div className="absolute inset-0 bg-cyan-500/5 opacity-0 group-hover/file:opacity-100 transition-opacity" />
                   <div className="p-2 bg-cyan-500/10 rounded-xl text-cyan-400">
                      <FileText size={16} />
                   </div>
                   <div className="flex flex-col">
                      <span className="text-[10px] font-black text-white truncate max-w-[150px] uppercase tracking-tighter">{selectedFileObj.name}</span>
                      <span className="text-[8px] font-bold text-slate-500 uppercase tracking-widest">Active Context</span>
                   </div>
                   <button onClick={(e) => { e.preventDefault(); setSelectedFile(''); }} className="ml-2 p-1.5 hover:bg-red-500/20 hover:text-red-500 rounded-lg text-slate-600 transition-all">
                      <X size={14} />
                   </button>
                </motion.div>
              )}
            </AnimatePresence>

            <div className="flex items-start gap-4">
              <div className="p-2.5 text-slate-700 mt-1">
                 <Sparkles size={20} />
              </div>
              <textarea
                ref={inputRef}
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder={selectedFile ? "Ask anything about this knowledge unit..." : "Attach a knowledge unit first..."}
                disabled={!selectedFile || isLoading}
                rows="1"
                className="flex-1 bg-transparent border-none focus:ring-0 text-sm font-bold text-slate-200 placeholder:text-slate-800 py-3 resize-none max-h-48 custom-scrollbar"
              />
              <button
                type="submit" disabled={!input.trim() || isLoading || !selectedFile}
                className="p-4 bg-gradient-to-br from-cyan-500 via-purple-600 to-pink-500 rounded-2xl text-white shadow-xl shadow-cyan-500/20 hover:scale-105 active:scale-95 disabled:opacity-10 transition-all"
              >
                {isLoading ? <Loader2 className="animate-spin" size={24} /> : <Send size={24} />}
              </button>
            </div>
          </form>
          
          <div className="mt-4 flex items-center justify-center gap-10 opacity-30 group-focus-within:opacity-60 transition-opacity">
             <div className="flex items-center gap-2 text-[9px] font-black text-slate-500 uppercase tracking-widest">
                <Terminal size={12} /> ENTER to Establish Link
             </div>
             <div className="flex items-center gap-2 text-[9px] font-black text-slate-500 uppercase tracking-widest">
                <Activity size={12} className="text-cyan-400" /> Neural Sync Active
             </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Chat;
