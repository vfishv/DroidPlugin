//
//  NativeIORedirect.cpp
//  
//
//  Created by lody on 16/4/15.
//
//

#include "NativeIORedirect.h"
#include "RedirectAgent.h"
#include "../Logger.h"
#include "HookCore.h"
#include <stdarg.h>




using namespace std;

static RedirectAgent agent;

static inline const char* redirectPath(const char * path) {
    	return agent.redirect(string(path)).c_str();
}

void NIR_add(const char* originPath, const char* newPath) {
	agent.add(string(originPath), string(newPath));
}

int (*org_execve)(const char *, char *const argv[], char *const envp[]);
char (*org_getcwd)(char *, size_t);
int (*org_access)(const char *, int);
int (*org_open)(const char *, int, ...);
int (*org_stat)(const char *, struct stat *);
int (*org_lstat)(const char *, struct stat *);
int (*org_chown)(const char *, uid_t, gid_t);
int (*org_chmod)(const char *path, mode_t mode);
int (*org_utimensat)(int, const char *,
              const struct timespec times[2], int);
int (*org_mknodat)(int, const char *, mode_t, dev_t);
int (*org_unlink)(const char *);
int (*org_rmdir)(const char *);
int (*org_linkat)(int, const char *,
           int, const char *, int);
int (*org_symlinkat)(const char *, int, const char *);
int (*org_readlinkat)(int, const char *,
               char *, size_t);
int (*org_statfs)(const char *, struct statfs *);
int (*org_unlinkat)(int, const char *, int);
int (*org_renameat)(int, const char *, int, const char *);
int (*org_mkdirat)(int, const char *, mode_t);
int (*org_chdir)(const char *);
int (*org_truncate)(const char *, off_t);
int (*org_fstatat)(int, const char *, struct stat *,int);
int (*org_faccessat)(int, const char *, int, int);
int (*org_rename)(const char *, const char *);


int my_rename(const char * oldpath, const char * newpath) {
	oldpath = redirectPath(oldpath);
	newpath = redirectPath(newpath);
	LOGD("#rename %s => %s.", oldpath, newpath);
	return org_rename(oldpath, newpath);
}

int my_faccessat(int dirfd, const char *path, int mode, int flags) {
	if(path) {
		path = redirectPath(path);
		LOGD("#faccessat %s.", path);
	}
	return org_faccessat(dirfd, path, mode, flags);
}

int my_fstatat(int dirfd, const char *path, struct stat *buf,
            int flags) {
	if(path) {
		path = redirectPath(path);
		LOGD("#fstatat %s.", path);
	}
	return org_fstatat(dirfd, path, buf, flags);
}

char * my_getcwd(char *buf, size_t size) {
	char* cwd = org_getcwd(buf, size);
	return redirectPath(cwd);
}

int my_truncate(const char *path, off_t length) {
	if(path) {
		path = redirectPath(path);
		LOGD("#truncate %s.", path);
	}
	return org_truncate(path, length);
}

int my_chdir(const char *path) {
	if(path) {
		path = redirectPath(path);
		LOGD("#chdir %s.", path);
	}
	return org_chdir(path);
}

int my_execve(const char *path, char *const argv[],
char *const envp[]) {
	if(path) {
		path = redirectPath(path);
		LOGD("#execve %s.", path);
	}
	return org_execve(path, argv, envp);
}

int my_access(const char *path, int mode) {
	if(path) {
		path = redirectPath(path);
		LOGD("#access %s.", path);
	}
    return org_access(path, mode);
}

int my_open(const char *path, int flags, ...) {
	va_list argp;
	va_start(argp, flags);
	int mode = va_arg(argp, int);
	va_end(argp);
	if(path) {
		path = redirectPath(path);
		LOGD("#open %s.", path);
	}
	
	return org_open(path, flags, mode);
}


int my_stat(const char *path, struct stat *buf) {
	if(path) {
		path = redirectPath(path);
		LOGD("#stat %s.", path);
	}
	
	return org_stat(path, buf);
}

int my_lstat(const char *path, struct stat *buf) {
	if(path) {
		path = redirectPath(path);
		LOGD("#lstat %s.", path);
	}
	
	return org_lstat(path, buf);
}

int my_chown(const char *path, uid_t owner, gid_t group) {
	if(path) {
		path = redirectPath(path);
		LOGD("#chown %s.", path);
	}
	
	return org_chown(path, owner, group);
}

int my_chmod(const char *path, mode_t mode) {
	if(path) {
		path = redirectPath(path);
		LOGD("#chmod %s.", path);
	}
	
	return org_chmod(path, mode);
}

int my_utimensat(int dirfd, const char *path,
              const struct timespec times[2], int flags) {
	if(path) {
		path = redirectPath(path);
		LOGD("#utimensat %s.", path);
	}
	return org_utimensat(dirfd, path, times, flags);
}

int my_mknodat(int dirfd, const char *path, mode_t mode, dev_t dev) {
	if(path) {
		path = redirectPath(path);
		LOGD("#mknod %s.", path);
	}
	return org_mknodat(dirfd, path, mode, dev);
}

int my_unlink(const char *path) {
	if(path) {
		path = redirectPath(path);
		LOGD("#unlink %s.", path);
	}
	
	return org_unlink(path);
}

int my_rmdir(const char *path) {
	if(path) {
		path = redirectPath(path);
		LOGD("#unlink %s.", path);
	}
	
	return org_rmdir(path);
}

int my_linkat(int olddirfd, const char *oldpath,
           int newdirfd, const char *newpath, int flags) {
	oldpath = redirectPath(oldpath);
	newpath = redirectPath(newpath);
	LOGD("#linkat %s => %s.", oldpath, newpath);
	return org_linkat(olddirfd, oldpath, newdirfd, newpath, flags);
}

int my_symlinkat(const char *oldpath, int newdirfd, const char *newpath) {
	oldpath = redirectPath(oldpath);
	newpath = redirectPath(newpath);
	LOGD("#symlinkat %s => %s.", oldpath, newpath);
	return org_symlinkat(oldpath, newdirfd, newpath);
}

int my_readlinkat(int dirfd, const char *path,
               char *buf, size_t bufsiz) {
	if(path) {
		path = redirectPath(path);
		LOGD("#readlinkat %s.", path);
	}
	return org_readlinkat(dirfd, path, buf, bufsiz);
}

int my_statfs(const char *path, struct statfs *buf) {
	if(path) {
		path = redirectPath(path);
		LOGD("#statfs %s.", path);
	}
	return org_statfs(path, buf);
}

int my_unlinkat(int dirfd, const char *path, int flags) {
	if(path) {
		path = redirectPath(path);
		LOGD("#unlinkat %s.", path);
	}
	return org_unlinkat(dirfd, path, flags);
}

int my_renameat(int olddirfd, const char *oldpath, int newdirfd, const char *newpath) {
	oldpath = redirectPath(oldpath);
	newpath = redirectPath(newpath);
	LOGD("#renameat %s => %s.", oldpath, newpath);
	return org_renameat(olddirfd, oldpath, newdirfd, newpath);
}


int my_mkdirat(int dirfd, const char *path, mode_t mode) {
	if(path) {
		path = redirectPath(path);
		LOGD("#mkdirat %s.", path);
	}
	return org_mkdirat(dirfd, path, mode);
}


void NIR_open() {
	// HOOK(fstatat);
	// HOOK(faccessat);
	HOOK(truncate);
	HOOK(getcwd);
	HOOK(chdir);
	HOOK(execve);
	HOOK(access);
	HOOK(open);
	// HOOK(openat);
	HOOK(mkdirat);
	HOOK(rename);
	HOOK(renameat);
	HOOK(stat);
	HOOK(lstat);
	HOOK(chown);
	HOOK(chmod);
	HOOK(symlinkat);
	HOOK(statfs);
	HOOK(mknodat);
	HOOK(linkat);
	HOOK(unlinkat);
	HOOK(readlinkat);
	HOOK(utimensat);
}