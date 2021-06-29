# build之前，需要安装freetype,使用如下命令安装
# sudo apt-get install libpng12-dev zlib1g-dev
# sudo apt-get install libpng-dev
# sudo apt-get install libx11-dev libxext-dev libxrender-dev libxtst-dev libxt-dev
# sudo apt-get install libfreetype6-dev

# Step1. 清除之前构建的
make clean 

# freetype路径配置
FREETYPEINCLUDE='/usr/include/freetype2'
FREETYPELIB='/usr/lib/x86_64-linux-gnu/'
# BootJDK配置
BOOTJDK='/home/wei/workspace/Temp/jdk7/jdk1.7.0_80'
# 构建文件输出目录
BUILDOUTPUTDIR=`pwd`/build

# Step2. 运行configure
./configure  --with-debug-level=slowdebug   \
             --with-boot-jdk=${BOOTJDK}  \
             --with-freetype-include=${FREETYPEINCLUDE} \
             --with-freetype-lib=${FREETYPELIB} \
             --enable-debug-symbols ZIP_DEBUGINFO_FILES=0  
             #--with-native-debug-symbols=internal \  无效选项,执行configure时会报错

# Step3. 构建OpenJdk
#语言选项，这个必须设置，否则编译好后会出现一个HashTable的NPE错
export LANG=C
# java文件编码设置
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
#Bootstrap JDK的安装路径。必须设置
export ALT_BOOTDIR=${BOOTJDK}
#允许自动下载依赖
export ALLOW_DOWNLOADS=true
#并行编译的线程数，设置为和CPU内核数量一致即可
export HOTSPOT_BUILD_JOBS=4
export ALT_PARALLEL_COMPILE_JOBS=4
#比较本次build出来的映像与先前版本的差异。这对我们来说没有意义，
#必须设置为false，否则sanity检查会报缺少先前版本JDK的映像的错误提示。
#如果已经设置dev或者DEV_ONLY=true，这个不显式设置也行
export SKIP_COMPARE_IMAGES=true
#使用预编译头文件，不加这个编译会更慢一些
export USE_PRECOMPILED_HEADER=true
#要编译的内容
export BUILD_LANGTOOLS=true
export BUILD_HOTSPOT=true
export BUILD_JDK=true
#把它设置为false可以避开javaws和浏览器Java插件之类的部分的build
BUILD_DEPLOY=false
#把它设置为false就不会build出安装包。因为安装包里有些奇怪的依赖，但即便不build出它也已经能得到完整的JDK映像，所以还是别build它好了
BUILD_INSTALL=false
#编译结果所存放的路径
export ALT_OUTPUTDIR=${BUILDOUTPUTDIR}
#这两个环境变量必须去掉，不然会有很诡异的事情发生，Makefile脚本检查到有这2个变量就会提示警告）
unset JAVA_HOME
unset CLASSPATH
# ZIP_DEBUGINFO_FILES=0 参数很重要，即不对debuginfo进行压缩，只有不进行压缩，才可以进行源码调试
make all ZIP_DEBUGINFO_FILES=0  2>&1|tee $ALT_OUTPUTDIR/build.log