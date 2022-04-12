//
// Created by jin on 2022/4/12.
//
#include "str_util.h"

void stringSplit(const std::string &str, char delim, std::vector<std::string> &result) {
    std::stringstream ss(str);
    std::string item;
    while (std::getline(ss, item, delim)) {
        if (!item.empty()) {
            result.push_back(item);
        }
    }
}


bool endWith(const std::string &str, const std::string &tail) {
    return str.compare(str.size() - tail.size(), tail.size(), tail) == 0;
}

bool startWith(const std::string &str, const std::string &head) {
    return str.compare(0, head.size(), head) == 0;
}