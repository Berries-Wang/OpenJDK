# include "wei_log/WeiLog.hpp"

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

void wei_print_klass_name(Symbol *klassName) {
  printf("正在加载的Klass: ");
  for (int index = 0; index < klassName->utf8_length(); index++) {
    printf("%c", (klassName->bytes())[index]);
  }
  printf("\n");
}

bool wei_string_equal(Symbol *klassName, char *klassNameStr) {
  bool equal = true;

  for (int index = 0; index < klassName->utf8_length(); index++) {
    if ((klassName->bytes())[index] != klassNameStr[index]) {
      equal = false;
      break;
    }
  }
  return equal;
}