#include <arpa/inet.h>
#include <stdio.h>
#include <strings.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>

#define MAXLINE 4096
#define PROGRAM_PORT 9999
/**
 * TCP 回射程序
 *
 * 
 * gcc code_unix_select_server.c  -std=c99 -o server
 * 
 * gcc code_unix_client.c  -pthread -o client
 * 
 * 启动server: ./server
 * 启动多个client: ./client a , ./client b , ./client c   ，再通过 a,b,c发送消息,server端会将消息打印出来 
 */

int main(int argc, char **argv) {

  int fdindex, maxfd, listenfd, connfd, sockfd;
  int nready, client[FD_SETSIZE];
  ssize_t msglen;
  fd_set rset, allset;
  char buff[MAXLINE];
  socklen_t clilen;
  struct sockaddr_in cliaddr, servaddr;

  // 创建监听套接字
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

  maxfd = listenfd;

  for (fdindex = 0; fdindex < FD_SETSIZE; fdindex++) {
    client[fdindex] = -1;
  }

  FD_ZERO(&allset);
  FD_SET(listenfd, &allset);


  for (;;) {
    rset = allset;

    // 等待数据准备完成
    nready = select(maxfd + 1, &rset, NULL, NULL, NULL);
    printf("wake up one time: %d\n", nready);

    // 如果连接请求
    if (FD_ISSET(listenfd, &rset)) {
      printf("有连接请求到达\n");

      clilen = sizeof(cliaddr);
      
      // 返回一个已完成的连接
      connfd = accept(listenfd, (struct sockaddr *)&cliaddr, &clilen);

      printf("--> %d\n", connfd);

      for (fdindex = 0; fdindex < FD_SETSIZE; fdindex++) {

        if (fdindex == FD_SETSIZE) {
          printf("too many clients");
          break;
        }

        if (client[fdindex] < 0) {
          client[fdindex] = connfd;
          printf("[%d] bind to select\n", connfd);
          FD_SET(connfd, &allset);

          if (connfd > maxfd) {
            maxfd = connfd;
          }

          break;
        }
      }
    }

    // 已连接套接字有数据到达
    for (fdindex = 0; fdindex < FD_SETSIZE; fdindex++) {
      if ((sockfd = client[fdindex]) < 0) {
        continue;
      }
      

      /**
       * select 缺点：
       *   无法明确知道哪些套接字有I/O事件发生，只能一个个轮询.
       * 
       */ 
      if (FD_ISSET(sockfd, &rset)) {
        if ((msglen = read(sockfd, buff, MAXLINE)) == 0) {
          printf("[%d] closed", sockfd);
          close(sockfd);
          FD_CLR(sockfd, &allset);
          client[fdindex] = -1;
        } else { // 输出内容
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