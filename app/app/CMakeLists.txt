cmake_minimum_required(VERSION 3.22.1)

project("ultracamera_native")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -ffast-math")

set(OpenCV_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/opencv/sdk/native/jni")
find_package(OpenCV REQUIRED COMPONENTS core imgproc photo)

add_library(
    ultracamera_native
    SHARED
    src/main/cpp/hdr_fusion.cpp
)

target_include_directories(
    ultracamera_native PRIVATE
    ${OpenCV_INCLUDE_DIRS}
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp
)

find_library(log-lib log)

target_link_libraries(
    ultracamera_native
    ${OpenCV_LIBS}
    ${log-lib}
    jnigraphics
)

