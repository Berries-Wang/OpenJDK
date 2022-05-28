# I/O复用
&nbsp;&nbsp;I/O复用是一种预先告知内核的权利，使得内核一旦发现进程指定的一个或多个I/O条件就绪(输入已准备好被读取 或 描述符已能承受更多的输出)，他就通知进程.

&nbsp;&nbsp;I/O复用的支持:
1. select 
   ```c
      // man select 
      /* According to POSIX.1-2001, POSIX.1-2008 */
       #include <sys/select.h>

       /* According to earlier standards */
       #include <sys/time.h>
       #include <sys/types.h>
       #include <unistd.h>

       int select(int nfds, fd_set *readfds, fd_set *writefds,
                  fd_set *exceptfds, struct timeval *timeout);

   ```
2. poll 
   ```c
       #include <poll.h>

       int poll(struct pollfd *fds, nfds_t nfds, int timeout);
   ```
3. epoll
   ```c
      // Nginx 使用
       #include <sys/epoll.h>

       int epoll_create(int size);

   ```

## 系统
```txt
    Linux Wang 5.13.0-41-generic #46~20.04.1-Ubuntu SMP Wed Apr 20 13:16:21 UTC 2022 x86_64 x86_64 x86_64 GNU/Linux
```