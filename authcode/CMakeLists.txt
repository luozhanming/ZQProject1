cmake_minimum_required(VERSION 3.4.2)

set(JNI_PATH ${CMAKE_SOURCE_DIR}/src/main/jni)

file(GLOB_RECURSE
        SOURCE_FILES
        ${JNI_PATH}/*.c
        ${JNI_PATH}/*.cpp)


add_library(native-lib
        SHARED
        ${SOURCE_FILES})

target_link_libraries(
        native-lib

)