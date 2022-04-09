/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

#ifndef OS_CPU_LINUX_X86_VM_ATOMIC_LINUX_X86_INLINE_HPP
#define OS_CPU_LINUX_X86_VM_ATOMIC_LINUX_X86_INLINE_HPP

#include "runtime/atomic.hpp"
#include "runtime/os.hpp"
#include "vm_version_x86.hpp"

#include "wei_log/WeiLog.hpp"

// Implementation of class atomic

inline void Atomic::store    (jbyte    store_value, jbyte*    dest) { *dest = store_value; }
inline void Atomic::store    (jshort   store_value, jshort*   dest) { *dest = store_value; }
inline void Atomic::store    (jint     store_value, jint*     dest) { *dest = store_value; }
inline void Atomic::store_ptr(intptr_t store_value, intptr_t* dest) { *dest = store_value; }
inline void Atomic::store_ptr(void*    store_value, void*     dest) { *(void**)dest = store_value; }

inline void Atomic::store    (jbyte    store_value, volatile jbyte*    dest) { *dest = store_value; }
inline void Atomic::store    (jshort   store_value, volatile jshort*   dest) { *dest = store_value; }
inline void Atomic::store    (jint     store_value, volatile jint*     dest) { *dest = store_value; }
inline void Atomic::store_ptr(intptr_t store_value, volatile intptr_t* dest) { *dest = store_value; }
inline void Atomic::store_ptr(void*    store_value, volatile void*     dest) { *(void* volatile *)dest = store_value; }


// Adding a lock prefix to an instruction on MP machine
#define LOCK_IF_MP(mp) "cmp $0, " #mp "; je 1f; lock; 1: "

inline jint     Atomic::add    (jint     add_value, volatile jint*     dest) {
  jint addend = add_value;
  int mp = os::is_MP();
  __asm__ volatile (  LOCK_IF_MP(%3) "xaddl %0,(%2)"
                    : "=r" (addend)
                    : "0" (addend), "r" (dest), "r" (mp)
                    : "cc", "memory");
  return addend + add_value;
}

inline void Atomic::inc    (volatile jint*     dest) {
  int mp = os::is_MP();
  __asm__ volatile (LOCK_IF_MP(%1) "addl $1,(%0)" :
                    : "r" (dest), "r" (mp) : "cc", "memory");
}

inline void Atomic::inc_ptr(volatile void*     dest) {
  inc_ptr((volatile intptr_t*)dest);
}

inline void Atomic::dec    (volatile jint*     dest) {
  int mp = os::is_MP();
  __asm__ volatile (LOCK_IF_MP(%1) "subl $1,(%0)" :
                    : "r" (dest), "r" (mp) : "cc", "memory");
}

inline void Atomic::dec_ptr(volatile void*     dest) {
  dec_ptr((volatile intptr_t*)dest);
}

inline jint     Atomic::xchg    (jint     exchange_value, volatile jint*     dest) {
  __asm__ volatile (  "xchgl (%2),%0"
                    : "=r" (exchange_value)
                    : "0" (exchange_value), "r" (dest)
                    : "memory");
  return exchange_value;
}

inline void*    Atomic::xchg_ptr(void*    exchange_value, volatile void*     dest) {
  return (void*)xchg_ptr((intptr_t)exchange_value, (volatile intptr_t*)dest);
}


/**
 * 
 * cmpxchgl的详细执行过程：
*  首先，输入是"r" (exchange_value), "a" (compare_value), "r" (dest), "r" (mp)，
*  表示compare_value存入eax寄存器，而exchange_value、dest、mp的值存入任意的通用寄存器。
*  嵌入式汇编规定把输出和输入寄存器按统一顺序编号，顺序是从输出寄存器序列从左到右从上到下以“%0”开始，
*  分别记为%0、%1···%9。也就是说，输出的eax是%0，输入的exchange_value、compare_value、dest、mp分别是%1、%2、%3、%4。
*  因此，cmpxchgl %1,(%3)实际上表示cmpxchgl exchange_value,(dest)，此处(dest)表示dest地址所存的值。需要注意的是cmpxchgl有个隐含操作数eax，
*  其实际过程是先比较eax的值(也就是compare_value)和dest地址所存的值是否相等，如果相等则把exchange_value的值写入dest指向的地址。如果不相等则把dest地址所存的值存入eax中。
*  输出是"=a" (exchange_value)，表示把eax中存的值写入exchange_value变量中。
*  Atomic::cmpxchg这个函数最终返回值是exchange_value，也就是说，
*  如果cmpxchgl执行时compare_value和dest指针指向内存值相等则会使得dest指针指向内存值变成exchange_value，
*  最终eax存的compare_value赋值给了exchange_value变量，即函数最终返回的值是原先的compare_value。

*>>>>> 即： 将dest指向的值与compare_value比较，如果相等，则将exchange_value写入到dest指向的地址，返回compare_value；如果不相等，则返回dest地址指向的值，即原值；
 * 
 */ 
inline jint     Atomic::cmpxchg    (jint     exchange_value, volatile jint*     dest, jint     compare_value) {
  
  // 打印一下日志
  wei_log_info(1,"<---------> atomic_linux_x86.inline.hpp::Atomic::cmpxchg");

  // // 判断是否是多处理器
  int mp = os::is_MP();

  /**
   * !!!注意，这里使用了两条汇编指令
   * LOCK_IF_MP 是一个宏定义，判断是否需要给汇编指令添加 lock前缀。
   * >>>  cmpxchgq 加lock前缀，通过学习JVM可以了解到，lock是将本处理器的缓存写入主存，并使得其他CPU缓存或者别的内核无效化以及锁住总线(带有lock前缀的指令在执行期间会锁住总线，使得其他处理器暂时无法通过总线访问内存)
   * 故 lock指令可以：1. 保证指令执行的原子性；2.禁止指令重排序
   */ 
  __asm__ volatile (LOCK_IF_MP(%4) "cmpxchgl %1,(%3)"
                    : "=a" (exchange_value)
                    : "r" (exchange_value), "a" (compare_value), "r" (dest), "r" (mp)
                    : "cc", "memory");
  return exchange_value;
}

#ifdef AMD64
inline void Atomic::store    (jlong    store_value, jlong*    dest) { *dest = store_value; }
inline void Atomic::store    (jlong    store_value, volatile jlong*    dest) { *dest = store_value; }

inline intptr_t Atomic::add_ptr(intptr_t add_value, volatile intptr_t* dest) {
  intptr_t addend = add_value;
  bool mp = os::is_MP();
  __asm__ __volatile__ (LOCK_IF_MP(%3) "xaddq %0,(%2)"
                        : "=r" (addend)
                        : "0" (addend), "r" (dest), "r" (mp)
                        : "cc", "memory");
  return addend + add_value;
}

inline void*    Atomic::add_ptr(intptr_t add_value, volatile void*     dest) {
  return (void*)add_ptr(add_value, (volatile intptr_t*)dest);
}

inline void Atomic::inc_ptr(volatile intptr_t* dest) {
  bool mp = os::is_MP();
  __asm__ __volatile__ (LOCK_IF_MP(%1) "addq $1,(%0)"
                        :
                        : "r" (dest), "r" (mp)
                        : "cc", "memory");
}

inline void Atomic::dec_ptr(volatile intptr_t* dest) {
  bool mp = os::is_MP();
  __asm__ __volatile__ (LOCK_IF_MP(%1) "subq $1,(%0)"
                        :
                        : "r" (dest), "r" (mp)
                        : "cc", "memory");
}

inline intptr_t Atomic::xchg_ptr(intptr_t exchange_value, volatile intptr_t* dest) {
  __asm__ __volatile__ ("xchgq (%2),%0"
                        : "=r" (exchange_value)
                        : "0" (exchange_value), "r" (dest)
                        : "memory");
  return exchange_value;
}

inline jlong    Atomic::cmpxchg    (jlong    exchange_value, volatile jlong*    dest, jlong    compare_value) {
  bool mp = os::is_MP();
  __asm__ __volatile__ (LOCK_IF_MP(%4) "cmpxchgq %1,(%3)"
                        : "=a" (exchange_value)
                        : "r" (exchange_value), "a" (compare_value), "r" (dest), "r" (mp)
                        : "cc", "memory");
  return exchange_value;
}

inline intptr_t Atomic::cmpxchg_ptr(intptr_t exchange_value, volatile intptr_t* dest, intptr_t compare_value) {
  return (intptr_t)cmpxchg((jlong)exchange_value, (volatile jlong*)dest, (jlong)compare_value);
}

inline void*    Atomic::cmpxchg_ptr(void*    exchange_value, volatile void*     dest, void*    compare_value) {
  return (void*)cmpxchg((jlong)exchange_value, (volatile jlong*)dest, (jlong)compare_value);
}

inline jlong Atomic::load(volatile jlong* src) { return *src; }

#else // !AMD64

inline intptr_t Atomic::add_ptr(intptr_t add_value, volatile intptr_t* dest) {
  return (intptr_t)Atomic::add((jint)add_value, (volatile jint*)dest);
}

inline void*    Atomic::add_ptr(intptr_t add_value, volatile void*     dest) {
  return (void*)Atomic::add((jint)add_value, (volatile jint*)dest);
}


inline void Atomic::inc_ptr(volatile intptr_t* dest) {
  inc((volatile jint*)dest);
}

inline void Atomic::dec_ptr(volatile intptr_t* dest) {
  dec((volatile jint*)dest);
}

inline intptr_t Atomic::xchg_ptr(intptr_t exchange_value, volatile intptr_t* dest) {
  return (intptr_t)xchg((jint)exchange_value, (volatile jint*)dest);
}

extern "C" {
  // defined in linux_x86.s
  jlong _Atomic_cmpxchg_long(jlong, volatile jlong*, jlong, bool);
  void _Atomic_move_long(volatile jlong* src, volatile jlong* dst);
}

inline jlong    Atomic::cmpxchg    (jlong    exchange_value, volatile jlong*    dest, jlong    compare_value) {
  return _Atomic_cmpxchg_long(exchange_value, dest, compare_value, os::is_MP());
}

inline intptr_t Atomic::cmpxchg_ptr(intptr_t exchange_value, volatile intptr_t* dest, intptr_t compare_value) {
  return (intptr_t)cmpxchg((jint)exchange_value, (volatile jint*)dest, (jint)compare_value);
}

inline void*    Atomic::cmpxchg_ptr(void*    exchange_value, volatile void*     dest, void*    compare_value) {
  return (void*)cmpxchg((jint)exchange_value, (volatile jint*)dest, (jint)compare_value);
}

inline jlong Atomic::load(volatile jlong* src) {
  volatile jlong dest;
  _Atomic_move_long(src, &dest);
  return dest;
}

inline void Atomic::store(jlong store_value, jlong* dest) {
  _Atomic_move_long((volatile jlong*)&store_value, (volatile jlong*)dest);
}

inline void Atomic::store(jlong store_value, volatile jlong* dest) {
  _Atomic_move_long((volatile jlong*)&store_value, dest);
}

#endif // AMD64

#endif // OS_CPU_LINUX_X86_VM_ATOMIC_LINUX_X86_INLINE_HPP
