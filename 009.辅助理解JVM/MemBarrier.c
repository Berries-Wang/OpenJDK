#include <stdio.h>

/**
 * 
 *  汇编指令测试
 * 
 * 内存屏障的分类
 * 硬件层提供了一系列的内存屏障 memory barrier / memory fence(Intel的提法)来提供一致性的能力。拿X86平台来说，有几种主要的内存屏障
 *>>>>1. lfence，是一种Load Barrier 读屏障。在读指令前插入读屏障，可以让高速缓存中的数据失效，重新从主内存加载数据
 *>>>>2. sfence, 是一种Store Barrier 写屏障。在写指令之后插入写屏障，能让写入缓存的最新数据写回到主内存
 *>>>>3. mfence, 是一种全能型的屏障，具备ifence和sfence的能力
 *>>>>4. Lock前缀，Lock不是一种内存屏障，但是它能完成类似内存屏障的功能。Lock会对CPU总线和高速缓存加锁，可以理解为CPU指令级的一种锁。它后面可以跟ADD, ADC, AND, BTC, BTR, BTS, CMPXCHG, CMPXCH8B, DEC, INC, NEG, NOT, OR, SBB, SUB, XOR, XADD, and XCHG等指令。
 
 *Lock前缀实现了类似的能力.
 *>>>>1. 它先对总线/缓存加锁，然后执行后面的指令，最后释放锁后会把高速缓存中的脏数据全部刷新回主内存。
 *>>>>2. 在Lock锁住总线的时候，其他CPU的读写请求都会被阻塞，直到锁释放。Lock后的写操作会让其他CPU相关的cache line失效，从而从新从内存加载最新的数据。这个是通过缓存一致性协议做的。
 * 
 */ 
int x = 0;
int r = 0;

void thread1_run(){
     x = 1;

     // 插入一条汇编指令 -> gcc -S MemBarrier.c 即可看到产生的汇编代码
    __asm__ __volatile__ ("mfence" : : : "memory");

     r = 2;

     printf("Hello World\n");
}


int main(int argc , char** argv){
      
      thread1_run();

      return 0;
}