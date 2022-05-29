#include <arpa/inet.h>
#include <errno.h>
#include <poll.h>
#include <stdio.h>
#include <strings.h>
#include <sys/poll.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>

#define MAXLINE 4096
#define PROGRAM_PORT 9999
#define OPEN_MAX 512

#define INFTIM -1

#ifndef __x86_64_POLL_H
#define __x86_64_POLL_H

/* The rest seem to be more-or-less nonstandard. Check them! */
#define POLLRDNORM 0x0040
#define POLLRDBAND 0x0080
#define POLLWRNORM 0x0100
#define POLLWRBAND 0x0200
#define POLLMSG 0x0400
#define POLLREMOVE 0x1000
#define POLLRDHUP 0x2000

#endif

/**
 * TCP 回射程序(poll版)
 *
 *
 * gcc code_unix_poll_server.c  -std=c99 -o server
 *
 * gcc code_unix_client.c  -pthread -o client
 *
 * 启动server: ./server
 * 启动多个client: ./client a , ./client b , ./client c   ，再通过
 * a,b,c发送消息,server端会将消息打印出来
 */

int main(int argc, char **argv) {

  int i, maxi, listenfd, connfd, sockfd;
  int nready;
  ssize_t msglen;
  char buff[MAXLINE];
  socklen_t clilen;
  struct pollfd client[OPEN_MAX];
  struct sockaddr_in cliaddr, servaddr;

  listenfd = socket(AF_INET, SOCK_STREAM, 0);

  bzero(&servaddr, sizeof(servaddr));

  servaddr.sin_family = AF_INET;
  servaddr.sin_addr.s_addr = htons(INADDR_ANY);
  servaddr.sin_port = htons(PROGRAM_PORT);

  // 套接字绑定
  bind(listenfd, (struct sockaddr *)&servaddr, sizeof(servaddr));

  /**
   * listen 函数仅由TCP服务器调用，主要做两件事:
   *   1. 将一个未连接的套接字转换为一个被动套接字
   *   2. 第二个参数规定了内核应该为相应套接字排队的最大连接个数
   * 《Unix 网络编程 第三版 卷一》  P84
   */
  listen(listenfd, 128);

  client[0].fd = listenfd;
  client[0].events = POLLRDNORM;

  for (i = 1; i < OPEN_MAX; i++) {
    client[i].fd = -1;
  }

  maxi = 0;

  for (;;) {
    /**
     *
     */
    nready = poll(client, maxi + 1, INFTIM);

    printf("wake up one time\n");

    if (client[0].revents & POLLRDNORM) { // new client connection
      clilen = sizeof(cliaddr);

      printf("开始建立新连接\n");
      connfd = accept(listenfd, (struct sockaddr *)&cliaddr, &clilen);
      printf("新连接建立完成,已连接套接字描述符:%d\n", connfd);

      for (i = 1; i < OPEN_MAX; i++) {
        if (client[i].fd < 0) {
          client[i].fd = connfd;
          break;
        }
      }

      if (i == OPEN_MAX) {
        printf("too many clients");
        return 0;
      }

      client[i].events = POLLRDNORM;
      if (i > maxi) {
        maxi = i;
      }

      if (--nready <= 0) {
        continue;
      }
    }

    /**
     * poll 缺点：
     *   无法明确知道哪些套接字有I/O事件发生，只能一个个轮询.
     *
     */
    for (i = 0; i <= maxi; i++) {
      if ((sockfd = client[i].fd) < 0) {
        continue;
      }

      if (client[i].revents & (POLLRDNORM | POLLERR)) {
        if ((msglen = read(sockfd, buff, MAXLINE)) < 0) {
          if (errno == ECONNRESET) {
            printf("[%d] ECONNRESET", sockfd);
            close(sockfd);
            client[i].fd = -1;
          } else {
            printf("[%d] read error", sockfd);
          }

        } else if (msglen == 0) {
          printf("[%d] close", sockfd);
          close(sockfd);
          client[i].fd = -1;
        } else {
          printf("有消息到达%d: ", sockfd);
          for (int index = 0; index < msglen; index++) {
            printf("%c", buff[index]);
          }
          printf("\n");
        }

        if (--nready <= 0) {
          break;
        }
      }
    }
  }

  return 0;
}