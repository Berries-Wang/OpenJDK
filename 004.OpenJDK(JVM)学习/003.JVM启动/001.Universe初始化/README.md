# Universe 初始化
&nbsp;&nbsp;调用路径如下
JNI_CreateJavaVM(prims/jni.cpp)
  ->JNI_CreateJavaVM_inner
    ->Threads::create_vm(runtime/thread.cpp)
      ->init_globals(runtime/init.cpp)
        ->universe_init(memory/universe.cpp)
          ->Universe::initialize_heap()