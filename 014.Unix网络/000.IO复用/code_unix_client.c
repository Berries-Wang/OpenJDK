#include <arpa/inet.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

char mess[1024];
int len;
int clientfile;

void *sendcli(void *arg);

int main(int argc, char *argv[]) {

  if (argc < 2) {

    printf("抱歉，用户名为空，正确打开方式： ./client username \n");
    exit(1);
  }

  struct sockaddr_in serveraddr;
  int res = -1;

  char ip[] = "127.0.0.1";
  int port = 9999;

  pthread_t pid2;

  bzero(&serveraddr, sizeof(serveraddr));
  serveraddr.sin_family = AF_INET;
  inet_pton(AF_INET, ip, &serveraddr.sin_addr.s_addr);
  serveraddr.sin_port = htons(port);

  clientfile = socket(AF_INET, SOCK_STREAM, 0);
tryagin:
  res = connect(clientfile, (struct sockaddr *)&serveraddr, sizeof(serveraddr));

  if (res == -1) {
    goto tryagin;
  }

  int jj = pthread_create(&pid2, NULL, sendcli, argv[1]);
  if (jj == 0) {

    printf("%s\n", "开始聊天");
  }
  pthread_detach(pid2);

  while (1)
    ;

  close(clientfile);

  return 0;
}

void *sendcli(void *arg) {

  char *argv = (char *)arg;
  while (1) {

    len = read(STDIN_FILENO, mess, sizeof(mess)); //从键盘读取数据
    char mess1[1024];
    int ii = strlen(argv);
    sprintf(mess1, "%s : %s", argv, mess);
    bzero(mess, sizeof(mess1));
    int i = write(clientfile, mess1, len + i); //向服务器发送数据
    fflush(stdin);
  }
}