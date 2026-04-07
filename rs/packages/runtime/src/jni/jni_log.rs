#[cfg(target_os = "android")]
use std::ffi::{CString, c_char};

use jni::objects::JValue;

///
/// Author: <a href="jinzhaoluns@qq.com">JinZhaolu</a>
///
pub fn jni_log(msg: &str) {
    #[cfg(target_os = "android")]
    unsafe {
        let tag = CString::new("com.nesp.spiderx.runtime").expect("failed to create CString");
        let msg = CString::new(msg).expect("failed to create CString");
        __android_log_write(4, tag.as_ptr(), msg.as_ptr());
    }

    #[cfg(not(target_os = "android"))]
    {
        use crate::jni::jni_utils;

        jni_utils::attach_current_thread(|env| -> Result<(), jni::errors::Error> {
            use jni::{jni_sig, jni_str};

            let out = env.get_static_field(
                jni_str!("java/lang/System"),
                jni_str!("out"),
                jni_sig!(java.io.PrintStream),
            )?;
            let out_obj = out.l()?;

            let thread_name = jni_utils::current_thread_name(env);
            let msg_obj = env.new_string(format!(
                "[{} {}]\t{}",
                chrono::Local::now().format("%Y-%m-%d %H:%M:%S"),
                thread_name,
                msg
            ))?;

            env.call_method(
                &out_obj,
                jni_str!("println"),
                jni_sig!((java.lang.String) -> void),
                &[JValue::Object(&msg_obj)],
            )?;
            Ok(())
        })
        .expect("Failed to print message");
    }
}

#[cfg(target_os = "android")]
#[link(name = "log")]
unsafe extern "C" {
    fn __android_log_write(prio: i32, tag: *const c_char, text: *const c_char) -> i32;
}
