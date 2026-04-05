#[cfg(target_os = "android")]
use std::ffi::{CString, c_char};

use jni::objects::JValueGen;

use crate::jni::{jni_handler::JniHandler, jni_thread};

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
        let mut jni_handler = JniHandler::new();
        let out =
            jni_handler.get_static_field("java/lang/System", ("out", "Ljava/io/PrintStream;"));
        let msg_obj = jni_handler.new_string(format!(
            "[{} {}]\t{}",
            chrono::Local::now().format("%Y-%m-%d %H:%M:%S"),
            jni_thread::current_thread_name(),
            msg
        ));
        jni_handler.call_method(
            &out,
            ("println", "(Ljava/lang/String;)V"),
            &[JValueGen::Object(&msg_obj)],
        );
    }
}

#[cfg(target_os = "android")]
#[link(name = "log")]
unsafe extern "C" {
    fn __android_log_write(prio: i32, tag: *const c_char, text: *const c_char) -> i32;
}
