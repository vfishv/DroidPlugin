//
//  RedirectAgent.h
//
//
//  Created by lody on 16/4/15.
//
//

#ifndef ____RedirectAgent__
#define ____RedirectAgent__
#include <iostream>
#include <map>
#include <string.h>
#include <stdio.h>

using namespace std;

class RedirectAgent {
public:
    void add(string originPath, string newPath);
    void remove(string originPath);
    string redirect(string path);
    bool isEmpty() const;
    void clear();
    
private:
    map<string, string> mRedirectMap;
};

#endif /* defined(____RedirectAgent__) */
