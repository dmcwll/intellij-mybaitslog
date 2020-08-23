package com.plugins.mybaitslog.util;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于SQL的还原
 *
 * @author lk
 * @version 1.0
 * @date 2020/8/23 17:14
 */
public class SqlProUtil {

    /**
     * 获取Sql语句类型
     * @param sql 语句
     * @return
     */
    public static String getSqlType(String sql) {
        if (StringUtils.isNotBlank(sql)) {
            String lowerLine = sql.toLowerCase().trim();
            if (lowerLine.startsWith("insert")) {
                return "insert";
            }
            if (lowerLine.startsWith("update")) {
                return "update";
            }
            if (lowerLine.startsWith("delete")) {
                return "delete";
            }
            if (lowerLine.startsWith("select")) {
                return "select";
            }
        }
        return "";
    }

    /**
     * Sql语句还原，整个插件的核心就是该方法
     *
     * @param preparingLine  sql
     * @param parametersLine 参数
     * @return
     */
    public static String[] restoreSql(Project project, final String preparingLine, final String parametersLine) {
        final String PREPARING = ConfigUtil.getPreparing(project);
        final String PARAMETERS = ConfigUtil.getParameters(project);
        final String[] preparingLineSplit = preparingLine.split(PREPARING);
        final String[] parametersLineSplit = parametersLine.split(PARAMETERS);
        final String[] preparing = getPreparing(preparingLineSplit);
        final Object[] parameters = getParameters(parametersLineSplit);
        return new String[]{preparing[0], String.format(preparing[1], parameters)};
    }

    private static String[] getPreparing(String[] preparingLineSplit) {
        String one = "";
        String tow = "";
        if (preparingLineSplit.length == 2) {
            one = preparingLineSplit[0];
            tow = preparingLineSplit[1].replace("?", "%s");
        }
        return new String[]{one, tow};
    }

    private static Object[] getParameters(String[] parametersLineSplit) {
        char c = '(';
        if (parametersLineSplit.length == 2) {
            final String[] split = parametersLineSplit[1].split(",");
            final List<String> params = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                final String s = split[i];
                if (s.endsWith(")")) {
                    final char[] value = s.toCharArray();
                    int find = -1;
                    for (int j = value.length - 1; j >= 0; j--) {
                        if (value[j] == c) {
                            find = j;
                            break;
                        }
                    }
                    if (find > 0) {
                        params.add(s.substring(0, find));
                    }
                } else {
                    params.add(s);
                }
            }
            return params.toArray(new String[0]);
        }
        return new String[]{};
    }
}
