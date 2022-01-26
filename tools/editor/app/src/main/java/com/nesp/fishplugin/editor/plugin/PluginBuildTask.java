package com.nesp.fishplugin.editor.plugin;

import com.nesp.fishplugin.editor.project.Project;

import java.util.ArrayList;
import java.util.List;

public interface PluginBuildTask {

    /**
     * @return name of task.
     */
    String name();

    Result run(Project workingProject, Object... parameters) throws Exception;

    PluginBuildTask[] dependencies();

    record Result(int code, String msg, Object data, List<Result> printMessages) {
        public static final int CODE_FAILED = -1;
        public static final int CODE_SUCCESS = 0;
        public static final int CODE_MES = 1;

        public static Result fail() {
            return fail("");
        }

        public static Result fail(String msg) {
            return new Result(msg);
        }

        public static Result success() {
            return success(null);
        }

        public static Result success(Object data) {
            return new Result(data);
        }

        public static Result msg(String msg) {
            return new Result(CODE_MES, msg, null, new ArrayList<>());
        }

        Result(String msg) {
            this(CODE_FAILED, msg, null, new ArrayList<>());
        }

        Result(Object data) {
            this(CODE_SUCCESS, "", data, new ArrayList<>());
        }
    }
}
