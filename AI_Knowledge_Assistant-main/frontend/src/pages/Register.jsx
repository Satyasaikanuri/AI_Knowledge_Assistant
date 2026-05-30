import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { Eye, EyeOff, Loader2, BrainCircuit, UserPlus, ShieldCheck, Sparkles, Zap, Fingerprint, Mail, Lock, User } from 'lucide-react';
import api from '../api/api';
import { motion, useMotionValue, useSpring } from 'framer-motion';

const Register = () => {
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

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

  const onSubmit = async (data) => {
    setIsLoading(true);
    try {
      await api.post('/auth/register', data);
      toast.success('Identity Created Successfully');
      navigate('/login');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Initialization Failed');
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
    <div className="w-full max-w-lg p-10 flex flex-col items-center">
      {/* Cinematic Header - Hidden on mobile to avoid duplication */}
      <motion.div 
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 1, type: "spring" }}
        className="hidden lg:flex flex-col items-center text-center mb-8 relative group"
      >
        <div className="absolute inset-0 bg-purple-500 blur-2xl opacity-10 animate-pulse" />
        <motion.div 
          animate={{ rotate: [0, 360] }}
          transition={{ duration: 20, repeat: Infinity, ease: "linear" }}
          className="relative inline-flex items-center justify-center w-20 h-20 rounded-[2rem] bg-gradient-to-br from-purple-500 to-cyan-600 text-white shadow-2xl p-0.5 mb-6"
        >
          <div className="w-full h-full bg-[#020617] rounded-[1.9rem] flex items-center justify-center">
             <UserPlus size={32} className="text-purple-400" />
          </div>
        </motion.div>

        <h1 className="text-3xl font-black text-white tracking-tighter">
          CREATE<span className="text-purple-400">IDENTITY</span>
        </h1>
      </motion.div>

      <form onSubmit={handleSubmit(onSubmit)} className="w-full space-y-6 relative z-10">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
          {/* First Name */}
          <motion.div initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.2 }} className="group">
            <label className="block text-xs font-bold text-slate-100 uppercase tracking-widest mb-2 px-1 flex items-center gap-2">
              <User size={14} className="text-purple-400" />
              First Name
            </label>
            <div className="relative">
               <div className="absolute -inset-0.5 bg-purple-500 rounded-xl blur opacity-0 group-focus-within:opacity-20 transition duration-500" />
               <input
                {...register('firstName', { required: 'Required' })}
                type="text"
                className="relative w-full px-5 py-3.5 bg-slate-900 border border-white/10 rounded-xl text-white placeholder:text-slate-600 focus:outline-none focus:ring-1 focus:ring-purple-500 transition-all font-medium text-sm"
                placeholder="John"
              />
            </div>
          </motion.div>

          {/* Last Name */}
          <motion.div initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.3 }} className="group">
            <label className="block text-xs font-bold text-slate-100 uppercase tracking-widest mb-2 px-1 flex items-center gap-2">
              <User size={14} className="text-purple-400" />
              Last Name
            </label>
            <div className="relative">
               <div className="absolute -inset-0.5 bg-purple-500 rounded-xl blur opacity-0 group-focus-within:opacity-20 transition duration-500" />
               <input
                {...register('lastName', { required: 'Required' })}
                type="text"
                className="relative w-full px-5 py-3.5 bg-slate-900 border border-white/10 rounded-xl text-white placeholder:text-slate-600 focus:outline-none focus:ring-1 focus:ring-purple-500 transition-all font-medium text-sm"
                placeholder="Doe"
              />
            </div>
          </motion.div>
        </div>

        {/* Email */}
        <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }} className="group">
          <label className="block text-xs font-bold text-slate-100 uppercase tracking-widest mb-2 px-1 flex items-center gap-2">
            <Mail size={14} className="text-cyan-400" />
            Email Address
          </label>
          <div className="relative">
             <div className="absolute -inset-0.5 bg-cyan-500 rounded-xl blur opacity-0 group-focus-within:opacity-20 transition duration-500" />
             <input
              {...register('email', {
                required: 'Email is required',
                pattern: { value: /^\S+@\S+$/i, message: 'Invalid ID' }
              })}
              type="email"
              className="relative w-full px-5 py-3.5 bg-slate-900 border border-white/10 rounded-xl text-white placeholder:text-slate-600 focus:outline-none focus:ring-1 focus:ring-cyan-500 transition-all font-medium text-sm"
              placeholder="john@example.com"
            />
          </div>
          {errors.email && <p className="text-red-400 text-[10px] font-bold mt-2 ml-1 uppercase">{errors.email.message}</p>}
        </motion.div>

        {/* Password */}
        <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.5 }} className="group">
          <label className="block text-xs font-bold text-slate-100 uppercase tracking-widest mb-2 px-1 flex items-center gap-2">
            <Lock size={14} className="text-pink-400" />
            Password
          </label>
          <div className="relative">
            <div className="absolute -inset-0.5 bg-pink-500 rounded-xl blur opacity-0 group-focus-within:opacity-20 transition duration-500" />
            <input
              {...register('password', {
                required: 'Password is required',
                minLength: { value: 6, message: 'Min 6 characters' }
              })}
              type={showPassword ? 'text' : 'password'}
              className="relative w-full px-5 py-3.5 bg-slate-900 border border-white/10 rounded-xl text-white placeholder:text-slate-600 focus:outline-none focus:ring-1 focus:ring-pink-500 transition-all font-medium text-sm"
              placeholder="••••••••••••"
            />
            <button
              type="button"
              className="absolute inset-y-0 right-0 pr-4 flex items-center text-slate-400 hover:text-white transition-colors"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
            </button>
          </div>
          {errors.password && <p className="text-red-500 text-[10px] font-bold mt-2 ml-1 uppercase">{errors.password.message}</p>}
        </motion.div>

        {/* Magnetic Button */}
        <motion.div
          style={{ x: mouseX, y: mouseY }}
          onMouseMove={handleMouseMove}
          onMouseLeave={handleMouseLeave}
          className="relative pt-4 group"
        >
          <div className="absolute -inset-1 bg-gradient-to-r from-purple-500 to-pink-500 rounded-xl blur-lg opacity-20 group-hover:opacity-50 transition duration-1000" />
          <button
            type="submit"
            disabled={isLoading}
            className="relative w-full py-4 bg-white text-slate-950 rounded-xl font-black text-xs uppercase tracking-[0.3em] shadow-2xl flex justify-center items-center gap-3 transition-colors hover:bg-purple-400 overflow-hidden group"
          >
            {isLoading ? (
              <Loader2 className="animate-spin" size={20} />
            ) : (
              <>
                <Fingerprint size={18} />
                <span>Initialize Identity</span>
              </>
            )}
            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/40 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-1000" />
          </button>
        </motion.div>
      </form>

      <motion.div 
        initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.8 }}
        className="mt-10 text-center"
      >
        <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">
          Already have an account?{' '}
          <Link
            to="/login"
            className="text-white hover:text-purple-400 underline underline-offset-8 transition-colors"
          >
            Sign In
          </Link>
        </p>
      </motion.div>
    </div>
  );
};

export default Register;
