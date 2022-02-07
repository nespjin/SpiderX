package com.nesp.fishplugin.editor.plugin;

import com.nesp.fishplugin.editor.project.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PluginBuildTask {

    private final Map<String, Object> parameters = new HashMap<>();

    public Object getParameter(String name) {
        return parameters.get(name);
    }

    public void putParameter(String name, Object value) {
        parameters.put(name, value);
    }

    /**
     * @return name of task.
     */
    abstract String name();

    abstract Result run(Project workingProject, OnPrintListener onPrintListener, Object... parameters) throws Exception;

    abstract PluginBuildTask[] dependencies();

    public interface OnPrintListener {
        void print(String message);
    }

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

        public boolean isFailed() {
            return code == CODE_FAILED;
        }

        public boolean isSuccess() {
            return code == CODE_SUCCESS;
        }
    }
}
