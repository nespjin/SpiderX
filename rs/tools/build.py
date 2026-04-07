#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import argparse
from collections import OrderedDict
import os
import platform
import shutil


ANDROID_ARCHIVE_DIRS = {
    "arm": "armeabi-v7a",
    "arm64": "arm64-v8a",
}

ANDROID_ABIS = {
    "arm": "armv7-linux-androideabi",
    "arm64": "aarch64-linux-android",
}

ANDROID_LAUNCHER_ACTIVITY = "com.nesp.spiderx.android.example/.MainActivity"

def build_and_launch_android_app(work_dir):
    android_project_dir = os.path.join(work_dir,"..","android","app")
    os.system(f"cd {android_project_dir} && ../gradlew installDebug && adb shell am start -n {ANDROID_LAUNCHER_ACTIVITY}")

def prepare_android_compile_env(arch="arm64"):
    ndk_home = os.environ["ANDROID_NDK_HOME"]
    if not ndk_home:
        raise Exception("ANDROID_NDK_HOME is not set.")

    host_tag = ""
    if platform.system() == "Darwin":
        host_tag = "darwin-x86_64"
    elif platform.system() == "Linux":
        host_tag = "linux-x86_64"
    elif platform.system() == "Windows":
        host_tag = "windows-x86_64"
    else:
        raise Exception(f"Unsupported platform: {platform.system()}")

    ndk_prebuilt = f"{ndk_home}/toolchains/llvm/prebuilt/{host_tag}"
    ndk_prebuilt_bin = f"{ndk_prebuilt}/bin"

    ndk_arch = ""
    if arch == "arm64":
        ndk_arch = "aarch64"
    elif arch == "arm":
        ndk_arch = "armv7a"
    elif arch == "x86":
        ndk_arch = "i686"
    elif arch == "x86_64":
        ndk_arch = "x86_64"
    else:
        raise Exception(f"Unsupported arch: {arch}")

    clang_name_prefix = f"{ndk_arch}-linux-android"
    if ndk_arch == "armv7a":
        clang_name_prefix += "eabi"

    ndk_include = f"{ndk_prebuilt}/sysroot/usr/include"
    ndk_lib = f"{ndk_prebuilt}/sysroot/usr/lib/{clang_name_prefix.replace("v7a", "")}"

    list_of_lib = os.listdir(ndk_lib)
    list_of_lib.sort(reverse=True)
    for item in list_of_lib:
        item_path = os.path.join(ndk_lib, item)
        if not os.path.isdir(item_path):
            continue
        ndk_lib = item_path
        break

    print(f"build android include: {ndk_include}")
    print(f"build android lib: {ndk_lib}")

    compiler_prefix = ""

    # Pick the latest version of clang
    list_of_clang = os.listdir(ndk_prebuilt_bin)
    if len(list_of_clang) == 0:
        raise Exception(f"NDK prebuilt bin directory is empty: {ndk_prebuilt_bin}")
    list_of_clang.sort(reverse=True)

    for item in list_of_clang:
        if item.startswith(clang_name_prefix):
            if item.endswith("-clang"):
                compiler_prefix = item.replace("-clang", "")
            else:
                compiler_prefix = item.replace("-clang++", "")
            break

    if len(compiler_prefix) == 0:
        raise Exception(
            f"Failed to find clang compiler prefix in NDK prebuilt bin directory: {ndk_prebuilt_bin}"
        )

    compiler_prefix = os.path.join(ndk_prebuilt_bin, compiler_prefix)

    cc = f"{compiler_prefix}-clang"
    cxx = f"{compiler_prefix}-clang++"
    ar = os.path.join(ndk_prebuilt_bin, "llvm-ar")
    ld = os.path.join(ndk_prebuilt_bin, "ld")

    print(f"build android toolchain: {ndk_prebuilt}")
    print(f"build android cc: {cc}")
    print(f"build android cxx: {cxx}")
    print(f"build android ar: {ar}")

    os.environ["TOOLCHAIN"] = ndk_prebuilt
    os.environ["CC"] = cc
    os.environ["CXX"] = cxx
    os.environ["AR"] = ar
    os.environ["LD"] = ld
    os.environ["PATH"] = f"{ndk_prebuilt_bin}:{os.environ['PATH']}"
    os.environ["CFLAGS"] = "-I" + ndk_include
    os.environ["CXXFLAGS"] = "-I" + ndk_include
    os.environ["LDFLAGS"] = "-L" + ndk_lib

    if arch == "arm64":
        os.environ["CARGO_TARGET_AARCH64_LINUX_ANDROID_LINKER"] = cc
        os.environ["CARGO_TARGET_AARCH64_LINUX_ANDROID_AR"] = ar
    elif arch == "arm":
        os.environ["CARGO_TARGET_ARMV7_LINUX_ANDROIDEABI_LINKER"] = cc
        os.environ["CARGO_TARGET_ARMV7_LINUX_ANDROIDEABI_AR"] = ar
    # elif arch == "x86":
    # elif arch == "x86_64":
    else:
        raise Exception(f"Unsupported arch: {arch}")


def build(
    is_android,
    android_arch,
    is_release,
    work_dir,
    include_dirs,
    output_dir,
    output_name="libspiderx_runtime",
):
    """
    Build the shared library.
    """

    if is_android:
        output_name = output_name + ".so"

    elif platform.system() == "Darwin":
        output_name = output_name + ".dylib"
    elif platform.system() == "Linux":
        output_name = output_name + ".so"
    elif platform.system() == "Windows":
        output_name = output_name + ".dll"
    else:
        raise Exception(f"Unsupported platform: {platform.system()}")

    for include_dir in include_dirs:
        os.environ["CFLAGS"] = os.getenv("CFLAGS", default="") + " -I" + include_dir
        os.environ["CXXFLAGS"] = os.getenv("CXXFLAGS", default="") + " -I" + include_dir

    target = ""
    if is_android:
        target = ANDROID_ABIS[android_arch]

    cmd = "cargo build"
    if is_release:
        cmd += " --release"
        # cmd = 'RUSTFLAGS="-C prefer-dynamic -C target-feature=-crt-static" ' + cmd

    if len(target) > 0:
        cmd += f" --target {target}"

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    if os.system(cmd) != 0:
        print(f"Failed to build for {target}")
        return

    output_file = os.path.join(output_dir, output_name)
    target_archive_dir = ''
    if is_android:
        target_archive_dir = f"{work_dir}/../android/runtime-android/src/main/jniLibs/{ANDROID_ARCHIVE_DIRS[android_arch]}"

    if len(target_archive_dir) > 0:
        # Copy the shared library to the current directory
        print(f"Copying {output_file} to {target_archive_dir}")
        os.makedirs(target_archive_dir, exist_ok=True)
        os.system(f'cp {output_file} {target_archive_dir}/')
        print(f"Successfully built for {target}")

def main():
    argparser = argparse.ArgumentParser()
    argparser.add_argument(
        "-r", "--release", action="store_true", help="Build a release version."
    )
    # The path to current directory
    scripts_dir = os.path.dirname(os.path.realpath(__file__))
    work_dir = os.path.abspath(os.path.join(scripts_dir, ".."))

    default_output_dir = os.path.abspath(os.path.join(work_dir, "target"))

    default_include_dirs = [
        os.path.abspath(os.path.join(work_dir, "main")),
        os.path.abspath(os.path.join(work_dir, "include")),
    ]

    argparser.add_argument(
        "-o", "--output", default=default_output_dir, help="The output directory."
    )
    argparser.add_argument(
        "--clear",
        action="store_true",
        help="Clear the output directory before building.",
    )
    argparser.add_argument(
        "--android",
        action="store_true",
        help="Build for Android.",
    )
    argparser.add_argument(
        "--archs",
        default=["arm64", "arm"],
        # choices=["arm64", "arm", "x86", "x86_64"],
        choices=["arm64", "arm"],
        nargs="+",
        help="The arch to build for Android.",
    )
    argparser.add_argument(
        "--include-dirs",
        nargs="+",
        default=default_include_dirs,
        help="The include directories.",
    )
    argparser.add_argument("--launch", action="store_true", help="Whether starts app")
    args = argparser.parse_args()
    output = args.output
    if args.clear and os.path.exists(output):
        try:
            shutil.rmtree(output)
        except Exception as e:
            print("an error occurs when clear output:\n" + e)


    for arch in args.archs:
        new_output = output
        print(f"Building for {'Android' if args.android else ''} {arch} {'release' if args.release else 'debug'}")
        if args.android:
            new_output = os.path.join(new_output, ANDROID_ABIS[arch])
            prepare_android_compile_env(arch)
 
        new_output = os.path.join(new_output, "release" if args.release else "debug")

        build(args.android, arch, args.release, work_dir, 
                args.include_dirs, new_output)
        print("")

    if args.android and args.launch:
        build_and_launch_android_app(work_dir)


if __name__ == "__main__":
    main()