//
// Created by jin on 2022/4/12.
//

#ifndef FDB_TCP_COMMON_H
#define FDB_TCP_COMMON_H

#define TCP_BUFFER_SIZE 1024

typedef void *(*OnReceiveListener)(char *, int);

#endif //FDB_TCP_COMMON_H
