//
//  RedirectAgent.cpp
//  
//
//  Created by lody on 16/4/15.
//
//

#include "RedirectAgent.h"
#include "../Logger.h"

static inline bool startWith(string full, string start) {
    return !strncmp(full.c_str(), start.c_str(), start.length());
}


void RedirectAgent::add(string originPath, string newPath) {
    mRedirectMap.insert(pair<string, string>(originPath, newPath));
}

void RedirectAgent::remove(string originPath) {
    mRedirectMap.erase(originPath);
}

bool RedirectAgent::isEmpty() const {
    return mRedirectMap.empty();
}

void RedirectAgent::clear() {
    mRedirectMap.clear();
}


string RedirectAgent::redirect(string path) {
    map<string, string>::iterator iterator;
    for (iterator = mRedirectMap.begin(); iterator != mRedirectMap.end(); iterator++) {
        if (startWith(path, iterator->first)) {
            string newPath = string(path);
            newPath.replace(0, iterator->first.length(),
                            iterator->second.c_str());
            LOGD("-> %s", newPath.c_str());
            return newPath;
        }
    }
    return path;
}
