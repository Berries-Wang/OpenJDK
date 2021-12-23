/*
 * Copyright (c) 1997, 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

#ifndef CPU_X86_VM_FRAME_X86_HPP
#define CPU_X86_VM_FRAME_X86_HPP

#include "runtime/synchronizer.hpp"
#include "utilities/top.hpp"

// A frame represents a physical stack frame (an activation).  Frames can be
// C or Java frames, and the Java frames can be interpreted or compiled.
// In contrast, vframes represent source-level activations, so that one physical frame
// can correspond to multiple source level frames because of inlining.
// A frame is comprised of {pc, fp, sp}

// ------------------------------------------------------栈帧字段说明------------------------------------------------
// | constant pool cache                     方法所属类的常量池缓存。
// | expression stack                        操作数栈，用于支持字节码执行时的栈使用需求。使用栈顶缓存技术（TOS）将栈操作转化为寄存器操作，提高执行效率。
// | byte code index/pointr (bcx)            表示字节码指针，指向待执行的字节码。
// | pointer to locals                       局部变量表起始地址
// | Method*                                 存储栈帧所属方法的 C++ 对象的地址。
// | locals and parameters                   局部变量和入参共用，C 语言函数调用只会将入参入栈，而 hotspot 将这块内存区域扩展到能够存放局部变量的大小，以使得局部变量和入参能共享此区域。
// | return pc                               返回地址
// | methodData                              指向 MethodData 对象的 _data 成员变量，其中记录着 data entries，详情查询 MethodData。
// | last sp                                 在调用者栈帧中，存储调用者跳转时的 sp 指针（仅限 java 函数间跳转）。 调用者栈帧中的 last sp 和被调用者栈帧中的 sender sp 指向同一位置
// | old stack pointer(sender_sp)            在被调用者栈帧中，存储调用者在跳转时的 sp 指针。
// | ----------------部分名词解释----------------------
// | bp 、 sp：x86 硬件有专用栈寄存器（bp、sp）
// ------------------------------------------------------栈帧字段说明------------------------------------------------

// ------------------------------ Asm interpreter (默认模板解释器栈帧)----------------------------------------
// Layout of asm interpreter frame:
//    [expression stack      ] * <- sp
//    [monitors              ]   \
//     ...                        | monitor block size
//    [monitors              ]   /
//    [monitor block size    ]
//    [byte code index/pointr]                   = bcx()                bcx_offset
//    [pointer to locals     ]                   = locals()             locals_offset
//    [constant pool cache   ]                   = cache()              cache_offset
//    [methodData            ]                   = mdp()                mdx_offset
//    [Method*               ]                   = method()             method_offset
//    [last sp               ]                   = last_sp()            last_sp_offset
//    [old stack pointer     ]                     (sender_sp)          sender_sp_offset
//    [old frame pointer     ]   <- fp           = link()
//    [return pc             ]
//    [oop temp              ]                     (only for native calls)
//    [locals and parameters ]
//                               <- sender sp
// ------------------------------ Asm interpreter(ASM 汇编解释器) ----------------------------------------

// ------------------------------ C++ interpreter(C++解释器)----------------------------------------
//
// expression stack 就是操作数栈,即 “后进先出(Last-In-First-out）的操作数栈”，
// 
// Layout of C++ interpreter frame: (While executing in BytecodeInterpreter::run)
//
//                             <- SP (current esp/rsp)
//    [local variables         ] BytecodeInterpreter::run local variables
//    ...                        BytecodeInterpreter::run local variables
//    [local variables         ] BytecodeInterpreter::run local variables
//    [old frame pointer       ]   fp [ BytecodeInterpreter::run's ebp/rbp ]
//    [return pc               ]  (return to frame manager)
//    [interpreter_state*      ]  (arg to BytecodeInterpreter::run)   --------------
//    [expression stack        ] <- last_Java_sp                           |
//    [...                     ] * <- interpreter_state.stack              |
//    [expression stack        ] * <- interpreter_state.stack_base         |
//    [monitors                ]   \                                       |
//     ...                          | monitor block size                   |
//    [monitors                ]   / <- interpreter_state.monitor_base     |
//    [struct interpretState   ] <-----------------------------------------|
//    [return pc               ] (return to callee of frame manager [1]
//    [locals and parameters   ]
//                               <- sender sp

// [1] When the c++ interpreter calls a new method it returns to the frame
//     manager which allocates(分配，分派) a new frame on the stack. In that case there
//     is no real callee(被召者) of this newly allocated frame. The frame manager is
//     aware(知道，明白) of the  additional(附加的，额外的) frame(s) and will pop them as nested(嵌套的) calls
//     complete. Howevers tTo make it look good in the debugger the frame
//     manager actually installs a dummy pc pointing to RecursiveInterpreterActivation
//     with a fake interpreter_state* parameter to make it easy to debug
//     nested calls.

// Note that contrary to the layout for the assembly interpreter the
// expression stack allocated for the C++ interpreter is full sized.
// However this is not as bad as it seems as the interpreter frame_manager
// will truncate the unused space on succesive method calls.
//
// ------------------------------ C++ interpreter ----------------------------------------

 public:
  enum {
    pc_return_offset                                 =  0,
    // All frames
    link_offset                                      =  0,
    return_addr_offset                               =  1,
    // non-interpreter frames
    sender_sp_offset                                 =  2,

#ifndef CC_INTERP

    // Interpreter frames
    interpreter_frame_result_handler_offset          =  3, // for native calls only
    interpreter_frame_oop_temp_offset                =  2, // for native calls only

    interpreter_frame_sender_sp_offset               = -1,
    // outgoing sp before a call to an invoked method
    interpreter_frame_last_sp_offset                 = interpreter_frame_sender_sp_offset - 1,
    interpreter_frame_method_offset                  = interpreter_frame_last_sp_offset - 1,
    interpreter_frame_mdx_offset                     = interpreter_frame_method_offset - 1,
    interpreter_frame_cache_offset                   = interpreter_frame_mdx_offset - 1,
    interpreter_frame_locals_offset                  = interpreter_frame_cache_offset - 1,
    interpreter_frame_bcx_offset                     = interpreter_frame_locals_offset - 1,
    interpreter_frame_initial_sp_offset              = interpreter_frame_bcx_offset - 1,

    interpreter_frame_monitor_block_top_offset       = interpreter_frame_initial_sp_offset,
    interpreter_frame_monitor_block_bottom_offset    = interpreter_frame_initial_sp_offset,

#endif // CC_INTERP

    // Entry frames
#ifdef AMD64
#ifdef _WIN64
    entry_frame_after_call_words                     =  28,
    entry_frame_call_wrapper_offset                  =  2,

    arg_reg_save_area_bytes                          = 32, // Register argument save area
#else
    entry_frame_after_call_words                     = 13,
    entry_frame_call_wrapper_offset                  = -6,

    arg_reg_save_area_bytes                          =  0,
#endif // _WIN64
#else
    entry_frame_call_wrapper_offset                  =  2,
#endif // AMD64

    // Native frames

    native_frame_initial_param_offset                =  2

  };

  intptr_t ptr_at(int offset) const {
    return *ptr_at_addr(offset);
  }

  void ptr_at_put(int offset, intptr_t value) {
    *ptr_at_addr(offset) = value;
  }

 private:
  // an additional field beyond _sp and _pc:
  intptr_t*   _fp; // frame pointer
  // The interpreter and adapters will extend the frame of the caller.
  // Since oopMaps are based on the sp of the caller before extension
  // we need to know that value. However in order to compute the address
  // of the return address we need the real "raw" sp. Since sparc already
  // uses sp() to mean "raw" sp and unextended_sp() to mean the caller's
  // original sp we use that convention.

  intptr_t*     _unextended_sp;
  void adjust_unextended_sp();

  intptr_t* ptr_at_addr(int offset) const {
    return (intptr_t*) addr_at(offset);
  }

#ifdef ASSERT
  // Used in frame::sender_for_{interpreter,compiled}_frame
  static void verify_deopt_original_pc(   nmethod* nm, intptr_t* unextended_sp, bool is_method_handle_return = false);
  static void verify_deopt_mh_original_pc(nmethod* nm, intptr_t* unextended_sp) {
    verify_deopt_original_pc(nm, unextended_sp, true);
  }
#endif

 public:
  // Constructors

  frame(intptr_t* sp, intptr_t* fp, address pc);

  frame(intptr_t* sp, intptr_t* unextended_sp, intptr_t* fp, address pc);

  frame(intptr_t* sp, intptr_t* fp);

  // accessors for the instance variables
  // Note: not necessarily the real 'frame pointer' (see real_fp)
  intptr_t*   fp() const { return _fp; }

  inline address* sender_pc_addr() const;

  // return address of param, zero origin index.
  inline address* native_param_addr(int idx) const;

  // expression stack tos if we are nested in a java call
  intptr_t* interpreter_frame_last_sp() const;

  // helper to update a map with callee-saved RBP
  static void update_map_with_saved_link(RegisterMap* map, intptr_t** link_addr);

#ifndef CC_INTERP
  // deoptimization support
  void interpreter_frame_set_last_sp(intptr_t* sp);
#endif // CC_INTERP

#ifdef CC_INTERP
  inline interpreterState get_interpreterState() const;
#endif // CC_INTERP

#endif // CPU_X86_VM_FRAME_X86_HPP
