/**
 *
 * 使用两个栈实现队列
 *
 * 队列: 先进先出。栈:先进后出。
 * 可以考虑将两个栈来将元素进行来回复制，取栈底的元素就是队列首部的元素.
 *
 */
#include <stdio.h>
#include <stdlib.h>

static unsigned int MAX_LENGTH = 10000;
typedef struct {
  // 队列长度
  unsigned int size;

  // 栈A，最大长度10000
  int *stackA;

  // 栈B,最大长度10000
  int *stackB;

  // 总空间
  int *stackAll;

} CQueue;

CQueue *cQueueCreate() {
  // 为队列分配内存空间
  CQueue *queue = (CQueue *)malloc(sizeof(CQueue));
  // 为栈分配内存空间
  queue->stackAll = (int *)malloc(sizeof(int) * MAX_LENGTH * 2);
  queue->stackA = queue->stackAll;
  queue->stackB = &(queue->stackAll[MAX_LENGTH]);
  queue->size = 0;

  return queue;
}

void cQueueAppendTail(CQueue *queue, int value) {
  if (queue->size >= MAX_LENGTH) {
    return;
  }

  queue->stackA[queue->size] = value;
  queue->size = 1 + queue->size;
}

int cQueueDeleteHead(CQueue *queue) {
  if (queue->size == 0) {
    return -1;
  }

  // 将StackA的数据复制到StackB
  int index;
  for (index = 0; index < queue->size; index++) {
    queue->stackB[index] = queue->stackA[queue->size - index - 1];
  }

  // 获取队首元素
  int queueHead = queue->stackB[queue->size-1];

  for (index = 0; index < queue->size-1; index++) {
    queue->stackA[index] = queue->stackB[queue->size - index - 2];
  }

  // 已出栈，元素数量减一
  queue->size = queue->size - 1;

  return queueHead;
}

void cQueueFree(CQueue *queue) {
  // 释放两个栈空间
  free(queue->stackAll);
  free(queue);
}

int main(int argc, char **argv) {

  CQueue *queue = cQueueCreate();
  int val = -2;

  val = cQueueDeleteHead(queue);
  printf("%d\n", val);

  cQueueAppendTail(queue, 5);
  cQueueAppendTail(queue, 2);

  val = cQueueDeleteHead(queue);
  printf("%d\n", val);
  val = cQueueDeleteHead(queue);
  printf("%d\n", val);
  return 0;
}