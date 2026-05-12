import { useState, useEffect, useCallback } from 'react';
import { 
  UploadCloud, FileText, CheckCircle2, AlertCircle, Sparkles, 
  Database, Music, Video, Zap, ShieldCheck, Cpu, Search, 
  Activity, Layers, Scan, Trash2, X, AlertTriangle, Loader2, Eye
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import api from '../api/api';
import { toast } from 'react-hot-toast';
import NeuralBackground from '../components/ui/NeuralBackground';
import { useAuthStore } from '../store/useAuthStore';

const Uploads = () => {
  const [files, setFiles] = useState([]);
  const [isDragging, setIsDragging] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [previewFile, setPreviewFile] = useState(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const fetchFiles = async () => {
    try {
      const response = await api.get('/files/list');
      setFiles(response.data);
    } catch (error) {
      toast.error('Failed to sync neural feed');
    }
  };

  useEffect(() => {
    fetchFiles();
  }, []);

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = useCallback((e) => {
    e.preventDefault();
    setIsDragging(false);
    const droppedFiles = Array.from(e.dataTransfer.files);
    handleFilesAdded(droppedFiles);
  }, []);

  const handleFileInput = (e) => {
    const selectedFiles = Array.from(e.target.files);
    handleFilesAdded(selectedFiles);
  };

  const handleFilesAdded = async (newFiles) => {
    const validExtensions = ['application/pdf', 'audio/mpeg', 'audio/wav', 'video/mp4', 'video/quicktime'];
    const validFiles = newFiles.filter(f => validExtensions.includes(f.type));

    if (validFiles.length === 0) {
      toast.error('Invalid neural format. Use PDF, Audio, or Video.');
      return;
    }

    setIsUploading(true);
    for (const file of validFiles) {
      const formData = new FormData();
      formData.append('file', file);
      try {
        await api.post('/files/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        toast.success(`Ingested: ${file.name}`);
      } catch (error) {
        toast.error(`Ingestion Failed: ${file.name}`);
      }
    }
    setIsUploading(false);
    fetchFiles();
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    setIsDeleting(true);
    try {
      await api.delete(`/files/${deleteConfirm.id}`);
      toast.success('Neural Purge Complete');
      setFiles(prev => prev.filter(f => f.id !== deleteConfirm.id));
      setDeleteConfirm(null);
    } catch (error) {
      toast.error('Purge Sequence Failed');
    } finally {
      setIsDeleting(false);
    }
  };

  const filteredFiles = files.filter(f => 
    f.originalFileName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const getStreamUrl = (id) => {
    const token = useAuthStore.getState().token;
    return `${api.defaults.baseURL}/files/stream/${id}?token=${token}`;
  };

  return (
    <div className="max-w-6xl mx-auto space-y-10 relative">
      <NeuralBackground />

      {/* Header HUD */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 relative z-10">
        <div>
           <h2 className="text-3xl font-black text-white tracking-tighter uppercase italic flex items-center gap-3">
             <Database className="text-cyan-400" size={32} />
             Knowledge Hub
           </h2>
           <p className="text-[10px] text-slate-500 mt-2 uppercase tracking-[0.4em] font-black">Contextual Ingestion Matrix</p>
        </div>
        
        <div className="flex items-center gap-4">
           <div className="relative group">
              <div className="absolute inset-0 bg-cyan-500 blur-md opacity-0 group-focus-within:opacity-20 transition-opacity" />
              <div className="relative flex items-center gap-3 px-5 py-3 bg-white/5 border border-white/10 rounded-2xl focus-within:border-cyan-500/50 transition-all">
                 <Search size={18} className="text-slate-500" />
                 <input 
                   type="text" placeholder="Filter Knowledge..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)}
                   className="bg-transparent border-none focus:ring-0 text-xs font-bold text-slate-200 placeholder:text-slate-700 w-48 sm:w-64"
                 />
              </div>
           </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 relative z-10">
        {/* Drop Zone Terminal */}
        <div className="lg:col-span-1">
          <motion.div
            onDragOver={handleDragOver} onDragLeave={handleDragLeave} onDrop={handleDrop}
            className={`
              h-[450px] rounded-[3rem] border-2 border-dashed transition-all flex flex-col items-center justify-center p-10 text-center relative overflow-hidden group
              ${isDragging ? 'border-cyan-500 bg-cyan-500/10' : 'border-white/10 bg-white/5 hover:border-white/20'}
            `}
          >
            <div className="absolute inset-0 bg-gradient-to-b from-cyan-500/5 to-transparent pointer-events-none" />
            
            <div className="relative z-10 space-y-6">
               <div className="w-24 h-24 rounded-full bg-slate-900 border border-white/10 flex items-center justify-center mx-auto shadow-2xl group-hover:scale-110 transition-transform">
                  {isUploading ? <Loader2 className="text-cyan-400 animate-spin" size={40} /> : <UploadCloud className="text-cyan-400" size={40} />}
               </div>
               <div>
                  <h3 className="text-lg font-black text-white uppercase italic tracking-tight">Initiate Ingestion</h3>
                  <p className="text-xs text-slate-500 mt-2 font-medium">Drag & Drop knowledge units here</p>
               </div>
               <div className="flex flex-wrap justify-center gap-2 pt-4">
                  {['PDF', 'MP3', 'MP4', 'WAV'].map(ext => (
                    <span key={ext} className="px-3 py-1 bg-white/5 rounded-lg border border-white/10 text-[9px] font-black text-slate-400">{ext}</span>
                  ))}
               </div>
               <label className="block pt-4">
                  <span className="px-8 py-4 bg-white/10 hover:bg-white/20 text-white rounded-2xl border border-white/10 text-xs font-black uppercase tracking-widest cursor-pointer transition-all active:scale-95 inline-block">
                    Browse Core
                  </span>
                  <input type="file" className="hidden" multiple onChange={handleFileInput} />
               </label>
            </div>
          </motion.div>
        </div>

        {/* Matrix Ingestion Feed */}
        <div className="lg:col-span-2 space-y-6">
           <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <AnimatePresence>
                 {filteredFiles.map((file, i) => {
                   const isVideo = file.fileType.startsWith('video/');
                   const isAudio = file.fileType.startsWith('audio/');
                   const isPdf = file.fileType === 'application/pdf';
                   
                   return (
                     <motion.div
                       layout key={file.id} initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.9 }}
                       className="glass-panel p-6 rounded-3xl border-white/5 group hover:border-cyan-500/20 transition-all relative overflow-hidden"
                     >
                        <div className="absolute top-0 right-0 p-4 opacity-0 group-hover:opacity-100 transition-opacity flex gap-2">
                           <button onClick={() => setPreviewFile(file)} className="p-2 bg-cyan-500/10 text-cyan-500 rounded-xl hover:bg-cyan-500 hover:text-white transition-all shadow-lg">
                              <Eye size={16} />
                           </button>
                           <button onClick={() => setDeleteConfirm(file)} className="p-2 bg-red-500/10 text-red-500 rounded-xl hover:bg-red-500 hover:text-white transition-all shadow-lg">
                              <Trash2 size={16} />
                           </button>
                        </div>

                        <div className="flex items-start gap-4">
                           <div className={`p-4 rounded-2xl bg-white/5 border border-white/10 group-hover:glow-cyan transition-all ${isPdf ? 'text-cyan-400' : isVideo ? 'text-purple-400' : 'text-emerald-400'}`}>
                              {isPdf ? <FileText size={24} /> : isVideo ? <Video size={24} /> : <Music size={24} />}
                           </div>
                           <div className="flex-1 min-w-0 pr-6">
                              <h4 className="text-sm font-black text-white uppercase italic truncate tracking-tight">{file.originalFileName}</h4>
                              <p className="text-[11px] text-slate-400 mt-1 uppercase font-bold">{(file.fileSize / 1024 / 1024).toFixed(2)} MB • {file.fileType.split('/')[1].toUpperCase()}</p>
                           </div>
                        </div>

                        <div className="mt-6 pt-6 border-t border-white/5 flex items-center justify-between">
                           <div className="flex items-center gap-2">
                              <div className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" />
                              <span className="text-[11px] font-black text-emerald-500 uppercase tracking-widest">Neural Link Active</span>
                           </div>
                           <div className="flex gap-2">
                              <Zap size={12} className="text-cyan-500" />
                              <Cpu size={12} className="text-purple-500" />
                           </div>
                        </div>
                     </motion.div>
                   );
                 })}
              </AnimatePresence>

              {filteredFiles.length === 0 && (
                <div className="col-span-full py-20 flex flex-col items-center justify-center glass-panel rounded-[3rem] border-white/5 opacity-30">
                   <Layers size={60} className="text-slate-700 mb-6" />
                   <p className="text-[10px] font-black text-slate-600 uppercase tracking-[0.5em]">Neural Grid Offline</p>
                </div>
              )}
           </div>
        </div>
      </div>

      {/* Delete Confirmation Modal */}
      <AnimatePresence>
        {deleteConfirm && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setDeleteConfirm(null)} className="absolute inset-0 bg-slate-950/80 backdrop-blur-sm" />
            <motion.div initial={{ scale: 0.9, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} exit={{ scale: 0.9, opacity: 0 }}
              className="relative w-full max-w-md glass-panel p-10 rounded-[3rem] border-red-500/20 text-center space-y-8"
            >
               <div className="w-20 h-20 bg-red-500/10 rounded-full flex items-center justify-center mx-auto text-red-500 animate-pulse">
                  <AlertTriangle size={40} />
               </div>
               <div>
                  <h3 className="text-xl font-black text-white uppercase italic tracking-tight">Neural Purge Protocol</h3>
                  <p className="text-xs text-slate-400 mt-4 leading-relaxed">
                     Are you sure you want to permanently decommission <span className="text-red-400 font-black">{deleteConfirm.originalFileName}</span>? This action will wipe all associated vector embeddings.
                  </p>
               </div>
               <div className="flex gap-4">
                  <button onClick={() => setDeleteConfirm(null)} className="flex-1 py-4 bg-white/5 hover:bg-white/10 text-white rounded-2xl border border-white/10 text-xs font-black uppercase tracking-widest transition-all">
                     Abort Purge
                  </button>
                  <button onClick={handleDelete} disabled={isDeleting} className="flex-1 py-4 bg-red-500 hover:bg-red-600 text-white rounded-2xl shadow-xl shadow-red-500/20 text-xs font-black uppercase tracking-widest transition-all flex items-center justify-center gap-2">
                     {isDeleting ? <Loader2 size={16} className="animate-spin" /> : <Trash2 size={16} />}
                     Confirm Purge
                  </button>
               </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      {/* Media Preview Modal */}
      <AnimatePresence>
        {previewFile && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setPreviewFile(null)} className="absolute inset-0 bg-slate-950/90 backdrop-blur-xl" />
            <motion.div initial={{ scale: 0.9, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} exit={{ scale: 0.9, opacity: 0 }}
              className="relative w-full max-w-5xl h-[85vh] glass-panel rounded-[3rem] border-cyan-500/20 overflow-hidden flex flex-col"
            >
               <div className="p-6 border-b border-white/5 flex items-center justify-between">
                  <div className="flex items-center gap-4">
                     <div className="p-3 rounded-xl bg-cyan-500/10 text-cyan-400">
                        <Eye size={20} />
                     </div>
                     <div>
                        <h3 className="text-sm font-black text-white uppercase tracking-tight">{previewFile.originalFileName}</h3>
                        <p className="text-[9px] text-slate-500 font-bold uppercase tracking-widest">Knowledge Stream Active</p>
                     </div>
                  </div>
                  <button onClick={() => setPreviewFile(null)} className="p-3 bg-white/5 text-slate-400 rounded-full hover:bg-white/10 hover:text-white transition-all">
                     <X size={20} />
                  </button>
               </div>

               <div className="flex-1 bg-black/40 relative overflow-hidden flex items-center justify-center">
                  {previewFile.fileType === 'application/pdf' ? (
                    <iframe 
                      key={previewFile.id}
                      src={`${getStreamUrl(previewFile.id)}#toolbar=0`}
                      className="w-full h-full border-none"
                      title="PDF Preview"
                    />
                  ) : previewFile.fileType.startsWith('video/') ? (
                    <video key={previewFile.id} controls autoPlay className="max-w-full max-h-full shadow-2xl">
                       <source src={getStreamUrl(previewFile.id)} type={previewFile.fileType} />
                       Your browser does not support the video tag.
                    </video>
                  ) : previewFile.fileType.startsWith('audio/') ? (
                    <div className="flex flex-col items-center gap-8 w-full max-w-xl">
                       <div className="w-32 h-32 rounded-full bg-cyan-500/10 flex items-center justify-center text-cyan-400 animate-pulse border-4 border-cyan-500/20">
                          <Music size={48} />
                       </div>
                       <audio key={previewFile.id} controls autoPlay className="w-full custom-audio-player">
                          <source src={getStreamUrl(previewFile.id)} type={previewFile.fileType} />
                          Your browser does not support the audio element.
                       </audio>
                    </div>
                  ) : (
                    <div className="text-center space-y-4">
                       <AlertCircle size={48} className="mx-auto text-slate-600" />
                       <p className="text-xs font-black text-slate-500 uppercase tracking-widest">Unsupported Neural Format for Live Preview</p>
                    </div>
                  )}
               </div>
               
               <div className="p-6 bg-white/5 border-t border-white/5 flex items-center justify-center gap-8">
                  <div className="flex items-center gap-2">
                     <ShieldCheck className="text-emerald-500" size={14} />
                     <span className="text-[9px] font-black text-slate-400 uppercase tracking-widest">Secure Link Verified</span>
                  </div>
                  <div className="flex items-center gap-2">
                     <Zap className="text-cyan-400" size={14} />
                     <span className="text-[9px] font-black text-slate-400 uppercase tracking-widest">High Speed Neural Stream</span>
                  </div>
               </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default Uploads;
