#include <stdarg.h>
#include <stdio.h>
#include <string.h>

#ifndef _WEI_LOG_WANG
#define _WEI_LOG_WANG

#include "oops/symbol.hpp"

void wei_log_info(int argc, ...);

void wei_print_klass_name(Symbol* klassName);

bool wei_string_equal(Symbol*,char*);

#endif // _WEI_LOG_WANG