#include <stdio.h>
#include <string.h>
#include <strings.h>
#include <sys/epoll.h>

#include <arpa/inet.h>
#include <errno.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>

#define MAXLINE 4096
#define PROGRAM_PORT 9999
#define MAXCLIENTS 512
/**
 * TCP回射程序(epoll版)
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

  ssize_t msglen;
  char buff[MAXLINE];

  struct epoll_event sysevents[MAXCLIENTS + 1];

  // 创建监听套接字
  struct sockaddr_in cliaddr, servaddr;

  int listenfd = socket(AF_INET, SOCK_STREAM, 0);

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

  // 检测监听套接字和已连接套接字，判断是否有连接到达或是否有数据到达
  struct epoll_event servevent;
  servevent.events |= EPOLLIN;
  servevent.data.fd = listenfd;

  // 创建epoll
  int epollfd = epoll_create(MAXCLIENTS + 1);

  // 将监听套接字添加到epoll中，监测事件
  int ctlres = epoll_ctl(epollfd, EPOLL_CTL_ADD, listenfd, &servevent);

  if (ctlres < 0) {
    printf("epoll_ctl 执行失败\n");
    return ctlres;
  }

  // 监控连接建立&&处理数据
  for (;;) {

    int eventcount = epoll_wait(epollfd, sysevents, MAXCLIENTS + 1, 10000);

    // 当没有事件返回
    if (eventcount <= 0) {
      printf("epoll_wait 没有事件返回,函数返回值:[%d]\n", eventcount);
      continue;
    }

    // 处理指定数量的事件(由epoll_wait返回)
    for (int i = 0; i < eventcount; i++) {
      // 若有新连接到达
      if (sysevents[i].data.fd == listenfd) {
        socklen_t clilen = sizeof(cliaddr);

        printf("开始建立新连接\n");
        int connfd = accept(listenfd, (struct sockaddr *)&cliaddr, &clilen);
        printf("新连接建立完成,已连接套接字描述符:%d\n", connfd);

        // 将已连接套接字添加到epoll中
        struct epoll_event *connevent =
            (struct epoll_event *)malloc(sizeof(struct epoll_event));
        connevent->events |= EPOLLIN;
        connevent->data.fd = connfd;

        int ctlres = epoll_ctl(epollfd, EPOLL_CTL_ADD, connfd, connevent);

        if (ctlres < 0) {
          printf("epoll_ctl 执行失败,已连接套接字[%d]检测失败\n", connfd);
          continue;
        }
      } else { // 当已连接套接字有数据到达
        printf("已连接套接字有数据到达   ");
        memset(buff, 0, MAXLINE);

        struct epoll_event curevent = sysevents[i];

        int sockfd = curevent.data.fd;

        if ((msglen = read(sockfd, buff, MAXLINE)) < 0) {

          printf("[%d] read error", sockfd);

        } else if (msglen == 0) {
          close(sockfd);
          // 需要从epoll 中移出
          int remres = epoll_ctl(epollfd, EPOLL_CTL_DEL, sockfd, &curevent);
          printf("[%d] close , remove res:[%d]\n", sockfd, remres);
        } else {
          printf("有消息到达%d: ", sockfd);
          for (int index = 0; index < msglen; index++) {
            printf("%c", buff[index]);
          }
          printf("\n");
        }
      }
    }
  }

  // 程序结束,关闭epoll
  close(epollfd);

  return 0;
}