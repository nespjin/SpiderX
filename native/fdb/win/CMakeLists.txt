add_library(system tcp_server.cpp tcp_client.cpp)

find_library(ws2_32 ws2_32.lib)
find_library(Mswsock Mswsock.lib)
find_library(Advapi32 Advapi32.lib)
find_library(Advapi32 Advapi32.lib)

target_link_libraries(system ws2_32 Mswsock Advapi32)
