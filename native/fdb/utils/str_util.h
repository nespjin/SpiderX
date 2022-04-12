//
// Created by jin on 2022/4/12.
//

#ifndef FDB_STR_UTIL_H
#define FDB_STR_UTIL_H

#include <vector>
#include <string>
#include <sstream>

std::vector<std::string> stringSplit(const std::string &str, char delim, std::vector<std::string> &result);

bool endWith(const std::string &str, const std::string &tail);

bool startWith(const std::string &str, const std::string &head);

#endif //FDB_STR_UTIL_H
