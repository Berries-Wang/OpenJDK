# 一些调试小建议
## 1. 日志打印
```c
    // 005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/memory/collectorPolicy.cpp
    gclog_or_tty->print_cr("Wei Say: Minimum heap(MB) " SIZE_FORMAT "  Initial heap(MB) "
      SIZE_FORMAT "  Maximum heap(MB) " SIZE_FORMAT,
      _min_heap_byte_size/WEI_BYTE_TO_MB, _initial_heap_byte_size/WEI_BYTE_TO_MB, _max_heap_byte_size/WEI_BYTE_TO_MB);
```