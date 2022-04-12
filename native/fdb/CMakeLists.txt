cmake_minimum_required(VERSION 3.21)
project(fdb)

set(CMAKE_CXX_STANDARD 14)

#option(HOST_OS "Use tutorial provided math implementation" OFF)
#option(FDB_TARGET "Use tutorial provided math implementation" OFF)

configure_file(config.h.in config.h)

IF (${CMAKE_HOST_SYSTEM_NAME} MATCHES "Linux")
    set(HOST_OS "linux")
    add_subdirectory(linux)
    list(APPEND EXTRA_INCLUDES "${PROJECT_SOURCE_DIR}/linux")
ELSEIF (${CMAKE_HOST_SYSTEM_NAME} MATCHES "Windows")
    set(HOST_OS 1)
    add_subdirectory(win)
    list(APPEND EXTRA_INCLUDES "${PROJECT_SOURCE_DIR}/win")
ELSEIF (${CMAKE_HOST_SYSTEM_NAME} MATCHES "Darwin")
    set(HOST_OS "osx")
    add_subdirectory(osx)
    list(APPEND EXTRA_INCLUDES "${PROJECT_SOURCE_DIR}/osx")
ENDIF ()


message(STATUS "operating system: ${HOST_OS} (FDB_TARGET=${FDB_TARGET})")

list(APPEND EXTRA_LIBS system)

add_library(fdbcore SHARED core/core.cpp core/core.h utils/str_util.h utils/str_util.cpp)

add_library(tcp SHARED tcp_server.h tcp_client.h tcp_common.h)
target_link_libraries(tcp ${EXTRA_LIBS})

add_executable(fdb fdb_client_main.cpp)
target_link_libraries(fdb fdbcore tcp ${EXTRA_LIBS})

add_executable(fdbserver fdb_server_main.cpp)
target_link_libraries(fdbserver fdbcore tcp ${EXTRA_LIBS})

add_executable(fdbd fdbd_main.cpp)
target_link_libraries(fdbd fdbcore tcp ${EXTRA_LIBS})

