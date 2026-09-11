package net.mgear.gregfoodexpansion.content.lint;

/** 单条 lint 结论:rule = 规则号(content-pipeline.md §5),severity 决定是否使构建失败。 */
public record LintIssue(int rule, Severity severity, String location, String message) {

    public enum Severity {
        /** 规则原文为"拒绝/构建失败":lint 任务以非零码退出。 */
        ERROR,
        /** 规则原文为"报警/报缺口":输出缺口报告,不阻断构建。 */
        WARNING,
        /** 监控曲线(如杠杆)只记录,不判级。 */
        INFO
    }

    @Override
    public String toString() {
        return "[%s] 规则%d (%s): %s".formatted(switch (severity) {
            case ERROR -> "错误";
            case WARNING -> "警告";
            case INFO -> "信息";
        }, rule, location, message);
    }
}
