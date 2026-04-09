use std::collections::HashMap;

use url::Url;

pub fn url_equals(a: &str, b: &str) -> bool {
    // 1. 解析 URL
    let Ok(url_a) = Url::parse(a) else {
        return false;
    };
    let Ok(url_b) = Url::parse(b) else {
        return false;
    };

    // 2. 提取域名（自动处理 www 前缀）
    let mut host_a = url_a.host_str().unwrap_or("");
    let mut host_b = url_b.host_str().unwrap_or("");

    // 3. 去掉 www. 统一域名
    if host_a.starts_with("www.") {
        host_a = &host_a[4..];
    }
    if host_b.starts_with("www.") {
        host_b = &host_b[4..];
    }

    // 4. 比较协议 + 标准化域名 + 路径
    url_a.scheme() == url_b.scheme() && host_a == host_b && url_a.path() == url_b.path()
}

pub fn url_replace_placeholders(url: &str, placeholders: &HashMap<String, String>) -> String {
    let mut url = url.to_string();
    for (key, value) in placeholders {
        url = url.replace(&format!("${{{}}}", key), value);
    }
    url
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_url_replace_placeholders() {
        let url = "https://www.example.com/${user_id}";
        let mut placeholders = HashMap::new();
        placeholders.insert("user_id".to_string(), "123".to_string());

        let result = url_replace_placeholders(url, &placeholders);
        assert_eq!(result, "https://www.example.com/123");
    }
}
