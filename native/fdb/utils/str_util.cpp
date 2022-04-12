//
// Created by jin on 2022/4/12.
//
#include "str_util.h"

std::vector<std::string> stringSplit(const std::string &str, char delim, std::vector<std::string> &result) {
    std::stringstream ss(str);
    std::string item;
    std::vector<std::string> elems;
    while (std::getline(ss, item, delim)) {
        if (!item.empty()) {
            elems.push_back(item);
        }
    }
    return elems;
}


bool endWith(const std::string &str, const std::string &tail) {
    return str.compare(str.size() - tail.size(), tail.size(), tail) == 0;
}

bool startWith(const std::string &str, const std::string &head) {
    return str.compare(0, head.size(), head) == 0;
}