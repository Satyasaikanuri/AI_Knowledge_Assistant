import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { Eye, EyeOff, Loader2, BrainCircuit, Bot, ShieldCheck, Sparkles, Zap, Lock, Mail } from 'lucide-react';
import api from '../api/api';
import { useAuthStore } from '../store/useAuthStore';
import { motion, useMotionValue, useSpring, useTransform } from 'framer-motion';

const Login = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  // Magnetic Button Logic
  const x = useMotionValue(0);
  const y = useMotionValue(0);
  const mouseX = useSpring(x, { stiffness: 150, damping: 15 });
  const mouseY = useSpring(y, { stiffness: 150, damping: 15 });

  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm();

  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

  const onSubmit = async (data) => {
    setIsLoading(true);
    try {
      const response = await api.post('/auth/login', data);
      login(response.data.user, response.data.token);
      toast.success('Access Granted');
      navigate('/dashboard');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Authentication Failed');
    } finally {
      setIsLoading(false);
    }
  };

  const handleMouseMove = (e) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    x.set((e.clientX - centerX) * 0.2);
    y.set((e.clientY - centerY) * 0.2);
  };

  const handleMouseLeave = () => {
    x.set(0);
    y.set(0);
  };

  return (
    <div className="w-full max-w-md p-10 flex flex-col items-center">
      {/* Animated AI Mascot Header - Hidden on mobile to avoid duplication */}
      <motion.div 
        initial={{ opacity: 0, scale: 0.5 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.8, type: "spring" }}
        className="hidden lg:flex flex-col items-center text-center mb-10 relative group"
      >
        <div className="absolute inset-0 bg-cyan-500 blur-2xl opacity-20 animate-pulse" />
        <motion.div 
          animate={{ y: [0, -10, 0] }}
          transition={{ duration: 4, repeat: Infinity, ease: "easeInOut" }}
          className="relative inline-flex items-center justify-center w-20 h-20 rounded-[2rem] bg-gradient-to-br from-cyan-500 via-purple-600 to-pink-500 text-white shadow-2xl p-0.5"
        >
          <div className="w-full h-full bg-[#020617] rounded-[1.9rem] flex items-center justify-center">
             <BrainCircuit size={40} className="text-cyan-400" />
          </div>
        </motion.div>
        
        <motion.h1 
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="text-3xl font-black text-white tracking-tighter mt-6"
        >
          CORE<span className="text-cyan-400">ACCESS</span>
        </motion.h1>
      </motion.div>

      {/* Form Content */}
      <form onSubmit={handleSubmit(onSubmit)} className="w-full space-y-6">
        <div className="space-y-6">
          {/* Email Input */}
          <motion.div 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.4 }}
            className="group"
          >
            <div className="flex items-center justify-between mb-2 px-1">
               <label className="text-xs font-bold text-slate-100 uppercase tracking-widest flex items-center gap-2">
                 <Mail size={14} className="text-cyan-400" />
                 Email Address
               </label>
            </div>
            <div className="relative">
              <div className="absolute -inset-0.5 bg-gradient-to-r from-cyan-500 to-purple-600 rounded-2xl blur opacity-0 group-focus-within:opacity-40 transition duration-500" />
              <input
                {...register('email', { required: 'Email Address required' })}
                type="email"
                className="relative w-full px-6 py-4 bg-slate-900/80 border border-white/10 rounded-2xl text-white placeholder:text-slate-500 focus:outline-none focus:ring-1 focus:ring-cyan-500 transition-all font-medium"
                placeholder="your-email@example.com"
              />
            </div>
            {errors.email && <p className="text-red-400 text-[10px] font-bold mt-2 ml-1 uppercase">{errors.email.message}</p>}
          </motion.div>

          {/* Password Input */}
          <motion.div 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.5 }}
            className="group"
          >
             <div className="flex items-center justify-between mb-2 px-1">
               <label className="text-xs font-bold text-slate-100 uppercase tracking-widest flex items-center gap-2">
                 <Lock size={14} className="text-purple-400" />
                 Password
               </label>
            </div>
            <div className="relative">
              <div className="absolute -inset-0.5 bg-gradient-to-r from-purple-600 to-pink-600 rounded-2xl blur opacity-0 group-focus-within:opacity-40 transition duration-500" />
              <input
                {...register('password', { required: 'Password required' })}
                type={showPassword ? 'text' : 'password'}
                className="relative w-full px-6 py-4 bg-slate-900/80 border border-white/10 rounded-2xl text-white placeholder:text-slate-500 focus:outline-none focus:ring-1 focus:ring-purple-500 transition-all font-medium"
                placeholder="Enter your password"
              />
              <button
                type="button"
                className="absolute inset-y-0 right-0 pr-5 flex items-center text-slate-400 hover:text-white transition-colors"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            {errors.password && <p className="text-red-400 text-[10px] font-bold mt-2 ml-1 uppercase">{errors.password.message}</p>}
          </motion.div>
        </div>

        {/* Magnetic Submit Button */}
        <motion.div
          style={{ x: mouseX, y: mouseY }}
          onMouseMove={handleMouseMove}
          onMouseLeave={handleMouseLeave}
          className="relative group pt-4"
        >
           <div className="absolute -inset-1 bg-gradient-to-r from-cyan-400 via-purple-500 to-pink-500 rounded-2xl blur-lg opacity-20 group-hover:opacity-60 transition duration-1000 animate-pulse" />
           <button
            type="submit"
            disabled={isLoading}
            className="relative w-full py-5 bg-white text-slate-950 rounded-2xl font-black text-sm uppercase tracking-[0.2em] shadow-2xl flex justify-center items-center gap-3 overflow-hidden hover:bg-cyan-400 transition-colors"
          >
            {isLoading ? (
              <Loader2 className="animate-spin" size={24} />
            ) : (
              <>
                <Zap size={18} className="fill-current" />
                <span>Establish Link</span>
              </>
            )}
            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/40 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-1000" />
          </button>
        </motion.div>
      </form>

      {/* Footer Nav */}
      <motion.div 
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.8 }}
        className="mt-10 text-center"
      >
        <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">
          New to the Matrix?{' '}
          <Link
            to="/register"
            className="text-white hover:text-cyan-400 underline underline-offset-8 transition-colors"
          >
            Create Identity
          </Link>
        </p>
      </motion.div>
    </div>
  );
};

export default Login;