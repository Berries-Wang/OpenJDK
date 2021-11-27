#include <stdarg.h>
#include <stdio.h>

#ifndef _WEI_LOG_WANG
#define _WEI_LOG_WANG

void wei_log_info(int argc, ...) {
  // 保存可变长数组参数
  va_list var_arg;
  // 将argc之外的函数参数保存到va_arg上
  va_start(var_arg, argc);

  for (int count = 0; count < argc; count++) {

    // 从var_arg中取出一个参数，以char*方式解析
    char *logInfo = va_arg(var_arg, char *);
    printf("%s", logInfo);

    if (count == argc - 1) {
      printf("\n");
    }
  }

  // 完成处理可变参数
  va_end(var_arg);
}

#endif // _WEI_LOG_WANG