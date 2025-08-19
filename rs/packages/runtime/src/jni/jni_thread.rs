use crate::jni::jni_handler::JniHandler;

///
/// Author: <a href="jinzhaoluns@qq.com">JinZhaolu</a>
///
pub fn current_thread_name() -> String {
    let mut jni_handler = JniHandler::new();
    let thread_obj = jni_handler.call_static_method(
        "java/lang/Thread",
        ("currentThread", "()Ljava/lang/Thread;"),
        &[],
    );
    let thread_name =
        jni_handler.call_method(&thread_obj, ("getName", "()Ljava/lang/String;"), &[]);
    let thread_name_str = jni_handler.get_string(&thread_name);
    return thread_name_str;
}
