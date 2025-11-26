#! /usr/bin/env python3 --encoding:utf-8


def main():
    import os

    ANDROID_API = 35
    targets = ["aarch64-linux-android", "armv7-linux-androideabi"]
    toolchain_targets = {
        "armv7-linux-androideabi": "armv7a-linux-androideabi",
    }

    for target in targets:
        os.system(f'rustup target add {target}')

        # Set environment variables for cross-compilation
        env = {
            "TOOLCHAIN": f"{os.environ.get('ANDROID_NDK_HOME')}/toolchains/llvm/prebuilt/linux-x86_64",
        }
        toolchain_target = toolchain_targets.get(target, target)
        env.update({
            "CC": f"{env["TOOLCHAIN"]}/bin/{toolchain_target}{ANDROID_API}-clang",
            "CXX": f"{env["TOOLCHAIN"]}/bin/{toolchain_target}{ANDROID_API}-clang++",
            "AR": f"{env["TOOLCHAIN"]}/bin/llvm-ar",
            "PATH": f"{os.environ.get('PATH')}:{env["TOOLCHAIN"]}/bin",
        })
        os.environ.update(env)
        print(f"Building for {target}")
        if os.system(f'cargo build --target {target} --release') != 0:
            print(f"Failed to build for {target}")
            break
        else:
            print(f"Successfully built for {target}")

if __name__ == '__main__':
    main()