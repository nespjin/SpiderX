use jni::objects::JValueGen;

use crate::jni::{jni_handler::JniHandler, jni_thread};

///
/// Author: <a href="jinzhaoluns@qq.com">JinZhaolu</a>
///
pub fn jni_log(msg: &str) {
    let mut jni_handler = JniHandler::new();
    let out = jni_handler.get_static_field("java/lang/System", ("out", "Ljava/io/PrintStream;"));
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
