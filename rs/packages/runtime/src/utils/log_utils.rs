use crate::jni::jni_log::jni_log;

///
/// Author: <a href="jinzhaoluns@qq.com">JinZhaolu</a>
///
pub fn logv(msg: &str) {
    _println(msg);
}

pub fn logd(msg: &str) {
    _println(msg);
}

pub fn logw(msg: &str) {
    _println(msg);
}

pub fn loge(msg: &str) {
    _println(msg);
}

pub fn _println(msg: &str) {
    jni_log(msg);
}
