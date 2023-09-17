package server;

import java.util.regex.Pattern;

public enum Endpoint {
    GET_PRIORITIZED_TASKS(Pattern.compile("^/tasks/$")),
    GET_HISTORY(Pattern.compile("^/tasks/history/$")),
    GET_TASKS(Pattern.compile("^/tasks/task/$")),
    GET_SUBTASKS(Pattern.compile("^/tasks/subtask/$")),
    GET_EPICS(Pattern.compile("^/tasks/epic/$")),
    GET_TASK_BY_ID(Pattern.compile("^/tasks/task/\\?id=\\d+$")),
    GET_SUBTASK_BY_ID(Pattern.compile("^/tasks/subtask/\\?id=\\d+$")),
    GET_EPIC_BY_ID(Pattern.compile("^/tasks/epic/\\?id=\\d+$")),
    GET_EPIC_SUBTASKS(Pattern.compile("^/tasks/subtask/epic/\\?id=\\d+$")),
    ADD_TASK(Pattern.compile("^/tasks/task/$")),
    ADD_SUBTASK(Pattern.compile("^/tasks/subtask/$")),
    ADD_EPIC(Pattern.compile("^/tasks/epic/$")),
    UPDATE_TASK(Pattern.compile("^/tasks/task/\\?id=\\d+$")),
    UPDATE_EPIC(Pattern.compile("^/tasks/epic/\\?id=\\d+$")),
    UPDATE_SUBTASK(Pattern.compile("^/tasks/subtask/\\?id=\\d+$")),
    DELETE_ALL_TASKS(Pattern.compile("^/tasks/task/$")),
    DELETE_ALL_SUBTASKS(Pattern.compile("^/tasks/subtask/$")),
    DELETE_EPIC_SUBTASKS(Pattern.compile("^/tasks/subtask/epic/\\?id=\\d+$")),
    DELETE_TASK_BY_ID(Pattern.compile("^/tasks/task/\\?id=\\d+$")),
    DELETE_SUBTASK_BY_ID(Pattern.compile("^/tasks/subtask/\\?id=\\d+$")),
    DELETE_EPIC_BY_ID(Pattern.compile("^/tasks/subtask/epic/\\?id=\\d+$")),
    UNKNOWN(Pattern.compile(""));

    private final Pattern endpoint;
    Endpoint(Pattern endpoint) {
        this.endpoint = endpoint;
    }

    public static Endpoint getEndpoint(String requestPath, String requestMethod) {
        switch (requestMethod) {
            case "GET": {
                if (Pattern.matches(String.valueOf(GET_PRIORITIZED_TASKS.endpoint), requestPath)) {
                    return Endpoint.GET_PRIORITIZED_TASKS;
                }
                if (Pattern.matches(String.valueOf(GET_HISTORY.endpoint), requestPath)) {
                    return Endpoint.GET_HISTORY;
                }
                if (Pattern.matches(String.valueOf(GET_TASKS.endpoint), requestPath)) {
                    return Endpoint.GET_TASKS;
                } else if (Pattern.matches(String.valueOf(GET_TASK_BY_ID.endpoint), requestPath)) {
                    return Endpoint.GET_TASK_BY_ID;
                }
                if (Pattern.matches(String.valueOf(GET_SUBTASKS.endpoint), requestPath)) {
                    return Endpoint.GET_SUBTASKS;
                } else if (Pattern.matches(String.valueOf(GET_SUBTASK_BY_ID.endpoint), requestPath)) {
                    return Endpoint.GET_SUBTASK_BY_ID;
                }
                if (Pattern.matches(String.valueOf(GET_EPICS.endpoint), requestPath)) {
                    return Endpoint.GET_EPICS;
                } else if (Pattern.matches(String.valueOf(GET_EPIC_BY_ID.endpoint), requestPath)) {
                    return Endpoint.GET_EPIC_BY_ID;
                }
                if (Pattern.matches(String.valueOf(GET_EPIC_SUBTASKS.endpoint), requestPath)) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                }
                break;
            }
            case "POST": {
                if (Pattern.matches(String.valueOf(ADD_TASK.endpoint), requestPath)) {
                    return Endpoint.ADD_TASK;
                } else if (Pattern.matches(String.valueOf(UPDATE_TASK.endpoint), requestPath)) {
                    return Endpoint.UPDATE_TASK;
                }
                if (Pattern.matches(String.valueOf(ADD_SUBTASK.endpoint), requestPath)) {
                    return Endpoint.ADD_SUBTASK;
                } else if (Pattern.matches(String.valueOf(UPDATE_SUBTASK.endpoint), requestPath)) {
                    return Endpoint.UPDATE_SUBTASK;
                }
                if (Pattern.matches(String.valueOf(ADD_EPIC.endpoint), requestPath)) {
                    return Endpoint.ADD_EPIC;
                } else if (Pattern.matches(String.valueOf(UPDATE_EPIC.endpoint), requestPath)) {
                    return Endpoint.UPDATE_EPIC;
                }
                break;
            }
            case "DELETE": {
                if (Pattern.matches(String.valueOf(DELETE_ALL_TASKS.endpoint), requestPath)) {
                    return Endpoint.DELETE_ALL_TASKS;
                } else if (Pattern.matches(String.valueOf(DELETE_TASK_BY_ID.endpoint), requestPath)) {
                    return Endpoint.DELETE_TASK_BY_ID;
                }
                if (Pattern.matches(String.valueOf(DELETE_ALL_SUBTASKS.endpoint), requestPath)) {
                    return Endpoint.DELETE_ALL_SUBTASKS;
                } else if (Pattern.matches(String.valueOf(DELETE_SUBTASK_BY_ID.endpoint), requestPath)) {
                    return Endpoint.DELETE_SUBTASK_BY_ID;
                }
                if (Pattern.matches(String.valueOf(DELETE_EPIC_BY_ID.endpoint), requestPath)) {
                    return Endpoint.DELETE_EPIC_BY_ID;
                }
                if (Pattern.matches(String.valueOf(DELETE_EPIC_SUBTASKS.endpoint), requestPath)) {
                    return Endpoint.DELETE_EPIC_SUBTASKS;
                }
                break;
            }
            default:
                return Endpoint.UNKNOWN;
        }
        return Endpoint.UNKNOWN;
    }
}
