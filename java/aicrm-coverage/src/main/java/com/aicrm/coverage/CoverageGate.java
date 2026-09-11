package com.aicrm.coverage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Fails the build when the JaCoCo aggregate drops below the published release thresholds. */
public final class CoverageGate {
    private static final String SALES_GROUP = "aicrm-coverage/aicrm-sales";
    private static final double OVERALL_MINIMUM = 70.0d;
    private static final double SALES_MINIMUM = 80.0d;

    private CoverageGate() {
    }

    public static void main(String[] arguments) throws IOException {
        if (arguments.length != 1) {
            throw new IllegalArgumentException("需要传入 JaCoCo CSV 报告路径");
        }
        Path report = Path.of(arguments[0]);
        if (!Files.isRegularFile(report)) {
            throw new IllegalStateException("未生成 JaCoCo 聚合报告：" + report);
        }

        List<String> lines = Files.readAllLines(report);
        if (lines.size() < 2) {
            throw new IllegalStateException("JaCoCo 聚合报告没有覆盖率记录");
        }
        Map<String, Integer> headers = headers(lines.get(0));
        long allMissed = 0L;
        long allCovered = 0L;
        long salesMissed = 0L;
        long salesCovered = 0L;
        for (int row = 1; row < lines.size(); row++) {
            String[] values = lines.get(row).split(",", -1);
            long missed = metric(values, headers, "INSTRUCTION_MISSED");
            long covered = metric(values, headers, "INSTRUCTION_COVERED");
            allMissed += missed;
            allCovered += covered;
            if (SALES_GROUP.equals(value(values, headers, "GROUP"))) {
                salesMissed += missed;
                salesCovered += covered;
            }
        }

        double overall = percentage(allCovered, allMissed);
        double sales = percentage(salesCovered, salesMissed);
        System.out.printf("JaCoCo coverage gate: overall %.2f%% (minimum %.2f%%), sales %.2f%% (minimum %.2f%%)%n",
                overall, OVERALL_MINIMUM, sales, SALES_MINIMUM);
        if (overall < OVERALL_MINIMUM || sales < SALES_MINIMUM) {
            throw new IllegalStateException("覆盖率未达到发布门禁");
        }
    }

    private static Map<String, Integer> headers(String headerLine) {
        String[] names = headerLine.split(",", -1);
        Map<String, Integer> indexes = new HashMap<>();
        for (int index = 0; index < names.length; index++) {
            indexes.put(names[index], index);
        }
        for (String required : List.of("GROUP", "INSTRUCTION_MISSED", "INSTRUCTION_COVERED")) {
            if (!indexes.containsKey(required)) {
                throw new IllegalStateException("JaCoCo CSV 缺少列：" + required);
            }
        }
        return indexes;
    }

    private static long metric(String[] values, Map<String, Integer> headers, String name) {
        return Long.parseLong(value(values, headers, name));
    }

    private static String value(String[] values, Map<String, Integer> headers, String name) {
        int index = headers.get(name);
        if (index >= values.length) {
            throw new IllegalStateException("JaCoCo CSV 行缺少列：" + name);
        }
        return values[index];
    }

    private static double percentage(long covered, long missed) {
        long total = covered + missed;
        if (total == 0L) {
            throw new IllegalStateException("JaCoCo 覆盖率总数为零");
        }
        return 100.0d * covered / total;
    }
}
